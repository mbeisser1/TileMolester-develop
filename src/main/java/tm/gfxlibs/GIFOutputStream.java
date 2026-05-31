/*
   Class: org.shetline.io.GIFOutputStream

   Copyright (c) 2000, 2001 by Kerry Shetline, kerry@shetline.com.

   This code is free for public use in any non-commercial application. All
   other uses are restricted without prior consent of the author, Kerry
   Shetline. The author assumes no liability for the suitability of this
   code in any application.

   Note: This code does *NOT* implement LZW compression. While the output
   of the compression routine is compatible with LZW, only a simple run-
   length compression is performed. The degree of compression as compared
   to LZW is not as high, but execution time is faster, and LZW licensing
   issues are avoided. Depending on image size and image complexity, the
   differences in compression may or may not have practical significance
   for particular applications.

   Date           Comments
   -----------    --------
   2000 SEP 30    First released version.
   2001 MAR 18    Replaced byte-by-byte specification of 256-color table with
                  a short code segment to generate the same table.
   2001 JUN 10    Added DITHERED_216_COLORS option.
   2001 AUG 21    Fixed a bug where single-color images would produce an
                  invalid GIF stream when using ORIGINAL_COLOR mode. GIF color
                  tables need to have at least two entries, so if the image only
                  has one color, an unused entry of either black or white is
                  added to the table to make it a valid length.
*/

package tm.gfxlibs;

import tm.utils.TMLog;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Writes GIF89a image data to an output stream with optional palette reduction.
 * Compression is run-length based (GIF-compatible, not LZW). See the file header
 * comment for authorship and licensing.
 **/
public class GIFOutputStream extends FilterOutputStream
{
   /** Keep original image colors (up to 256 unique RGB values). **/
   public static final int ORIGINAL_COLOR = 0;
   /** Map pixels to black and white. **/
   public static final int BLACK_AND_WHITE = 1;
   /** Map pixels to a 16-level grayscale palette. **/
   public static final int GRAYSCALE_16 = 2;
   /** Map pixels to a 256-level grayscale palette. **/
   public static final int GRAYSCALE_256 = 3;
   /** Map pixels to the built-in 16-color VGA palette. **/
   public static final int STANDARD_16_COLORS = 4;
   /** Map pixels to the built-in 256-color palette. **/
   public static final int STANDARD_256_COLORS = 5;
   /** Map pixels to 216 colors with ordered dithering. **/
   public static final int DITHERED_216_COLORS = 6;

   /** Last write completed without error. **/
   public static final int NO_ERROR = 0;
   /** Pixel grab failed or was aborted. **/
   public static final int IMAGE_LOAD_FAILED = 1;
   /** ORIGINAL_COLOR mode had more than 256 distinct colors. **/
   public static final int TOO_MANY_COLORS = 2;
   /** Unrecognized colorMode argument. **/
   public static final int INVALID_COLOR_MODE = 3;

   /** Palette index for black in {@link #BLACK_AND_WHITE} mode. **/
   protected static final int BLACK_INDEX = 0;
   /** Palette index for white in {@link #BLACK_AND_WHITE} mode. **/
   protected static final int WHITE_INDEX = 1;

   /** Built-in 16-color VGA palette (0xRRGGBB). **/
   protected static final int[] standard16 =
   {
      0x000000,
      0xFF0000, 0x00FF00, 0x0000FF,
      0x00FFFF, 0xFF00FF, 0xFFFF00,
      0x800000, 0x008000, 0x000080,
      0x008080, 0x800080, 0x808000,
      0x808080, 0xC0C0C0,
      0xFFFFFF
   };

   /** Built-in 256-color palette; populated in the static initializer. **/
   protected static final int[] standard256 = new int[256];

   /** 4×4 ordered-dither thresholds for {@link #DITHERED_216_COLORS}. **/
   protected static int ditherPattern[][] = {{  8, 184, 248, 216},
                                             {120,  56, 152,  88},
                                             { 40, 232,  24, 200},
                                             {168, 104, 136,  72}};

   /** Result code from the most recent encode ({@link #NO_ERROR} initially). **/
   protected int     errorStatus = NO_ERROR;

   static
   {
      // Set up a standard 256-color table.

      int   n, j, r, g, b;

      standard256[0] = 0x000000;

      n = 40;

      // 0x33 multiples, starting at index 41 (black stored at index 40 gets replaced with 0xEE0000
      for (r = 0; r < 6; ++r)
         for (g = 0; g < 6; ++g)
            for (b = 0; b < 6; ++b)
               standard256[n++] = 0x330000 * r | 0x003300 * g | 0x000033 * b;

      n = 1;

      for (j = 0; j < 10; ++j) {
         // Shades of gray w/o 0x33 multiples, starting at index 1
         standard256[j +  1] = 0x111111 * n;
         // Shades of blue w/o 0x33 multiples, starting at index 11
         standard256[j + 11] = 0x000011 * n;
         // Shades of green w/o 0x33 multiples, starting at index 21
         standard256[j + 21] = 0x001100 * n;
         // Shades of red w/o 0x33 multiples, starting at index 31
         standard256[j + 31] = 0x110000 * n;

         ++n;

         if (n % 3 == 0)
            ++n;
      }
   }

   /**
    * Writes a GIF to the stream using {@link #ORIGINAL_COLOR} and no transparency.
    * @param out destination stream (closed after write)
    * @param image source image
    * @return error status ({@link #NO_ERROR} on success)
    **/
   public static int writeGIF(OutputStream out, Image image) throws IOException
   {
      return writeGIF(out, image, ORIGINAL_COLOR, null);
   }

   /**
    * Writes a GIF to the stream with the given color reduction mode.
    * @param out destination stream (closed after write)
    * @param image source image
    * @param colorMode one of the ORIGINAL_COLOR … DITHERED_216_COLORS constants
    * @return error status ({@link #NO_ERROR} on success)
    **/
   public static int writeGIF(OutputStream out, Image image, int colorMode) throws IOException
   {
      return writeGIF(out, image, colorMode, null);
   }

   /**
    * Writes a GIF to the stream with color mode and optional transparent color.
    * @param out destination stream (closed after write)
    * @param image source image
    * @param colorMode one of the ORIGINAL_COLOR … DITHERED_216_COLORS constants
    * @param transparentColor color treated as transparent, or null for opaque
    * @return error status ({@link #NO_ERROR} on success)
    **/
   public static int writeGIF(OutputStream out, Image image, int colorMode, Color transparentColor) throws IOException
   {
      GIFOutputStream   gifOut = new GIFOutputStream(out);

      gifOut.write(image, colorMode, transparentColor);

       int res = gifOut.getErrorStatus();

       gifOut.close();

       return res;
   }

   /**
    * Creates a GIF writer wrapping the given output stream.
    * @param out destination stream
    **/
   public GIFOutputStream(OutputStream out)
   {
      super(out);
   }

   /**
    * Returns the error code from the last {@link #write(Image, int, Color)} call.
    * @return {@link #NO_ERROR} or another NO_ERROR … INVALID_COLOR_MODE constant
    **/
   public int getErrorStatus() { return errorStatus; }

   /**
    * Encodes the image as a GIF using {@link #ORIGINAL_COLOR}.
    * @param image source image
    **/
   public void write(Image image) throws IOException
   {
      write(image, ORIGINAL_COLOR, null);
   }

   /**
    * Encodes the image as a GIF with the given color reduction mode.
    * @param image source image
    * @param colorMode one of the ORIGINAL_COLOR … DITHERED_216_COLORS constants
    **/
   public void write(Image image, int colorMode) throws IOException
   {
      write(image, colorMode, null);
   }

   /**
    * Encodes the image as a GIF with optional transparency.
    * @param image source image
    * @param transparentColor color treated as transparent, or null for opaque
    **/
   public void write(Image image, Color transparentColor) throws IOException
   {
      write(image, ORIGINAL_COLOR, transparentColor);
   }

   /**
    * Encodes the image as a GIF stream (header, palette, compressed raster).
    * Sets {@link #errorStatus} when pixel grab fails, too many colors, or mode is invalid.
    * @param image source image
    * @param colorMode one of the ORIGINAL_COLOR … DITHERED_216_COLORS constants
    * @param transparentColor color treated as transparent, or null for opaque
    **/
   public void write(Image image, int colorMode, Color transparentColor) throws IOException
   {
      errorStatus = NO_ERROR;

      if (image == null)
         return;

      PixelGrabber   pg = new PixelGrabber(image, 0, 0, -1, -1, true);

      try {
         pg.grabPixels();
      } catch (InterruptedException e) {
         TMLog.logException("GIF image pixel grab interrupted", e);
         errorStatus = IMAGE_LOAD_FAILED;
         return;
      }

      if ((pg.status() & ImageObserver.ABORT) != 0) {
         errorStatus = IMAGE_LOAD_FAILED;
         return;
      }

      int[]    pixels = (int[]) pg.getPixels();
      int      width = pg.getWidth();
      int      height = pg.getHeight();
      int      colorCount = 0;
      int[]    colorTable = null;
      byte[]   bytePixels = null;

////// do color reduction
/*      int[][] pixxies = new int[height][width];
      for (int i=0; i<height; i++) {
          for (int j=0; j<width; j++) {
              pixxies[i][j] = pixels[(i * width) + j];
          }
      }
*/
//////

      switch (colorMode) {
         case ORIGINAL_COLOR:
            Map<Integer, Integer>   colorSet = getColorSet(pixels);
            colorCount = colorSet.size();
            if (colorCount > 256) {
               errorStatus = TOO_MANY_COLORS;
               return;
            }
            colorTable = createColorTable(colorSet, colorCount);
            bytePixels = createBytePixels(pixels, colorSet);
            break;

         case BLACK_AND_WHITE:
            colorCount = 2;
            colorTable = createBWTable();
            bytePixels = createBWBytePixels(pixels);
            break;

         case GRAYSCALE_16:
            colorCount = 16;
            colorTable = create16GrayTable();
            bytePixels = create16GrayBytePixels(pixels);
            break;

         case GRAYSCALE_256:
            colorCount = 256;
            colorTable = create256GrayTable();
            bytePixels = create256GrayBytePixels(pixels);
            break;

         case STANDARD_16_COLORS:
            colorCount = 16;
            colorTable = createStd16ColorTable();
            bytePixels = createStd16ColorBytePixels(pixels);
            break;

         case STANDARD_256_COLORS:
            colorCount = 256;
            colorTable = createStd256ColorTable();
            bytePixels = createStd256ColorBytePixels(pixels, width, false);
            break;

         case DITHERED_216_COLORS:
            colorCount = 216;
            colorTable = createStd216ColorTable();
            bytePixels = createStd256ColorBytePixels(pixels, width, true);
            break;

         default:
            errorStatus = INVALID_COLOR_MODE;
            return;
      }

      pixels = null;

      int   cc1 = colorCount - 1;
      int   bitsPerPixel = 0;

      while (cc1 != 0) {
         ++bitsPerPixel;
         cc1 >>= 1;
      }

      writeGIFHeader(width, height, bitsPerPixel);

      writeColorTable(colorTable, bitsPerPixel);

      if (transparentColor != null)
         writeGraphicControlExtension(transparentColor, colorTable);

      writeImageDescriptor(width, height);

      writeCompressedImageData(bytePixels, bitsPerPixel);

      write(0x00); // Terminate picture data.

      write(0x3B); // GIF file terminator.
   }

   /**
    * Collects distinct 24-bit RGB colors from pixel data and assigns palette indices.
    * @param pixels ARGB pixel array from a PixelGrabber
    * @return map from RGB integer to palette index (0 … n-1)
    **/
   protected Map<Integer, Integer> getColorSet(int[] pixels)
   {
      Map<Integer, Integer>   colorSet = new HashMap<>();
      boolean[]   checked = new boolean[pixels.length];
      int         needsChecking = pixels.length;
      int         color;
      int         colorIndex = 0;

      for (int j = 0; j < pixels.length && needsChecking > 0; ++j) {
         if (!checked[j]) {
            color = pixels[j] & 0x00FFFFFF;
            checked[j] = true;
            --needsChecking;

            colorSet.put(color, colorIndex);
            if (++colorIndex > 256)
               break;

            for (int j2 = j + 1; j2 < pixels.length; ++j2) {
               if ((pixels[j2] & 0x00FFFFFF) == color) {
                  checked[j2] = true;
                  --needsChecking;
               }
            }
         }
      }

      if (colorIndex == 1) {
         if (colorSet.get(0) == null)
            colorSet.put(0, 1);
         else
            colorSet.put(0xFFFFFF, 1);
      }

      return colorSet;
   }

   /**
    * Builds a GIF color table from a color-set map.
    * @param colorSet RGB to index map from {@link #getColorSet}
    * @param colorCount number of entries in the table
    * @return palette entries as 0xRRGGBB integers
    **/
   protected int[] createColorTable(Map<Integer, Integer> colorSet, int colorCount)
   {
      int[]    colorTable = new int[colorCount];

      for (Map.Entry<Integer, Integer> entry : colorSet.entrySet()) {
         colorTable[entry.getValue()] = entry.getKey();
      }

      return colorTable;
   }

   /**
    * Converts true-color pixels to palette indices using a color-set map.
    * @param pixels ARGB source pixels
    * @param colorSet RGB to index map from {@link #getColorSet}
    * @return one byte per pixel (palette index)
    **/
   protected byte[] createBytePixels(int[] pixels, Map<Integer, Integer> colorSet)
   {
      byte[]   bytePixels = new byte[pixels.length];
      int      colorIndex;

      for (int j = 0; j < pixels.length; ++j) {
         colorIndex = colorSet.get(pixels[j] & 0x00FFFFFF);
         bytePixels[j] = (byte) colorIndex;
      }

      return bytePixels;
   }

   /**
    * Returns the two-entry black/white palette.
    * @return color table of length 2
    **/
   protected int[] createBWTable()
   {
      int[]    colorTable = new int[2];

      colorTable[BLACK_INDEX] = 0x000000;
      colorTable[WHITE_INDEX] = 0xFFFFFF;

      return colorTable;
   }

   /**
    * Quantizes pixels to black or white by luminance threshold.
    * @param pixels ARGB source pixels
    * @return palette indices (BLACK_INDEX or WHITE_INDEX)
    **/
   protected byte[] createBWBytePixels(int[] pixels)
   {
      byte[]   bytePixels = new byte[pixels.length];

      for (int j = 0; j < pixels.length; ++j) {
         if (grayscaleValue(pixels[j]) < 0x80)
            bytePixels[j] = (byte) BLACK_INDEX;
         else
            bytePixels[j] = (byte) WHITE_INDEX;
      }

      return bytePixels;
   }

   /**
    * Returns the 16-level grayscale palette (0x000000 … 0xEEEEEE step 0x111111).
    * @return color table of length 16
    **/
   protected int[] create16GrayTable()
   {
      int[]    colorTable = new int[16];

      for (int j = 0; j < 16; ++j)
         colorTable[j] = 0x111111 * j;

      return colorTable;
   }

   /**
    * Maps each pixel to one of 16 gray levels.
    * @param pixels ARGB source pixels
    * @return palette indices 0 … 15
    **/
   protected byte[] create16GrayBytePixels(int[] pixels)
   {
      byte[]   bytePixels = new byte[pixels.length];

      for (int j = 0; j < pixels.length; ++j) {
         bytePixels[j] = (byte) (grayscaleValue(pixels[j]) / 16);
      }

      return bytePixels;
   }

   /**
    * Returns the 256-level grayscale palette.
    * @return color table of length 256
    **/
   protected int[] create256GrayTable()
   {
      int[]    colorTable = new int[256];

      for (int j = 0; j < 256; ++j)
         colorTable[j] = 0x010101 * j;

      return colorTable;
   }

   /**
    * Maps each pixel to an 8-bit gray value.
    * @param pixels ARGB source pixels
    * @return palette indices 0 … 255
    **/
   protected byte[] create256GrayBytePixels(int[] pixels)
   {
      byte[]   bytePixels = new byte[pixels.length];

      for (int j = 0; j < pixels.length; ++j) {
         bytePixels[j] = (byte) grayscaleValue(pixels[j]);
      }

      return bytePixels;
   }

   /**
    * Returns a copy of the built-in 16-color VGA palette.
    * @return color table of length 16
    **/
   protected int[] createStd16ColorTable()
   {
      int[]    colorTable = new int[16];

      System.arraycopy(standard16, 0, colorTable, 0, 16);

      return colorTable;
   }

   /**
    * Maps each pixel to the nearest standard 16-color entry.
    * @param pixels ARGB source pixels
    * @return palette indices 0 … 15
    **/
   protected byte[] createStd16ColorBytePixels(int[] pixels)
   {
      byte[]   bytePixels = new byte[pixels.length];
      int      color;
      int      minError = 0;
      int      error;
      int      minIndex;

      for (int j = 0; j < pixels.length; ++j) {
         color = pixels[j] & 0xFFFFFF;
         minIndex = -1;

         for (int k = 0; k < 16; ++k) {
            error = colorMatchError(color, standard16[k]);
            if (error < minError || minIndex < 0) {
               minError = error;
               minIndex = k;
            }
         }

         bytePixels[j] = (byte) minIndex;
      }

      return bytePixels;
   }

   /**
    * Returns a copy of the built-in 256-color palette.
    * @return color table of length 256
    **/
   protected int[] createStd256ColorTable()
   {
      int[]    colorTable = new int[256];

      System.arraycopy(standard256, 0, colorTable, 0, 256);

      return colorTable;
   }

   /**
    * Returns the 216-color web-safe subset used for dithered output.
    * @return color table of length 216
    **/
   protected int[] createStd216ColorTable()
   {
      int[]    colorTable = new int[216];

      colorTable[0] = 0x000000;

      System.arraycopy(standard256, 41, colorTable, 1, 215);

      return colorTable;
   }

   /**
    * Maps pixels to the standard 256-color palette, optionally with 4×4 ordered dithering.
    * @param pixels ARGB source pixels
    * @param width image width in pixels (for dither pattern indexing)
    * @param dither when true, use {@link #DITHERED_216_COLORS} cube mapping; otherwise nearest match
    * @return palette indices
    **/
   protected byte[] createStd256ColorBytePixels(int[] pixels, int width, boolean dither)
   {
      byte[]   bytePixels = new byte[pixels.length];
      int      color;
      int      minError = 0;
      int      error;
      int      minIndex;
      int      sampleIndex;
      int      r, g, b;
      int      r2, g2, b2;
      int      x, y;
      int      threshold;

      for (int j = 0; j < pixels.length; ++j) {
         color = pixels[j] & 0xFFFFFF;
         minIndex = -1;

         r = (color & 0xFF0000) >> 16;
         g = (color & 0x00FF00) >> 8;
         b =  color & 0x0000FF;

         r2 = r / 0x33;
         g2 = g / 0x33;
         b2 = b / 0x33;

         if (dither) {
            x = j % width;
            y = j / width;
            threshold = ditherPattern[x % 4][y % 4] / 5;

            if (r2 < 5 && r % 0x33 >= threshold)
               ++r2;

            if (g2 < 5 && g % 0x33 >= threshold)
               ++g2;

            if (b2 < 5 && b % 0x33 >= threshold)
               ++b2;

            bytePixels[j] = (byte) (r2 * 36 + g2 * 6 + b2);
         }
         else {
            // Try to match color to a 0x33-multiple color.

            for (int r0 = r2; r0 <= r2 + 1 && r0 < 6; ++r0) {
               for (int g0 = g2; g0 <= g2 + 1 && g0 < 6; ++g0) {
                  for (int b0 = b2; b0 <= b2 + 1 && b0 < 6; ++b0) {
                     sampleIndex = 40 + r0 * 36 + g0 * 6 + b0;
                     if (sampleIndex == 40)
                        sampleIndex = 0;

                     error = colorMatchError(color, standard256[sampleIndex]);
                     if (error < minError || minIndex < 0) {
                        minError = error;
                        minIndex = sampleIndex;
                     }
                  }
               }
            }

            int   shadeBase;
            int   shadeIndex;

            // Try to match color to a 0x11-multiple pure primary shade.

            if (r > g && r > b) {
               shadeBase = 30;
               shadeIndex = (r + 8) / 0x11;
            }
            else if (g > r && g > b) {
               shadeBase = 20;
               shadeIndex = (g + 8) / 0x11;
            }
            else {
               shadeBase = 10;
               shadeIndex = (b + 8) / 0x11;
            }

            if (shadeIndex > 0) {
               shadeIndex -= (shadeIndex / 3);
               sampleIndex = shadeBase + shadeIndex;
               error = colorMatchError(color, standard256[sampleIndex]);
               if (error < minError || minIndex < 0) {
                  minError = error;
                  minIndex = sampleIndex;
               }
            }

            // Try to match color to a 0x11-multiple gray.

            shadeIndex = (grayscaleValue(color) + 8) / 0x11;
            if (shadeIndex > 0) {
               shadeIndex -= (shadeIndex / 3);
               sampleIndex = shadeIndex;
               error = colorMatchError(color, standard256[sampleIndex]);
               if (error < minError || minIndex < 0) {
                  minError = error;
                  minIndex = sampleIndex;
               }
            }

            bytePixels[j] = (byte) minIndex;
         }
      }

      return bytePixels;
   }

   /**
    * Computes weighted luminance (30% R, 59% G, 11% B) for an RGB color.
    * @param color 0xRRGGBB color (alpha ignored)
    * @return gray level 0 … 255
    **/
   protected int grayscaleValue(int color)
   {
      int   r = (color & 0xFF0000) >> 16;
      int   g = (color & 0x00FF00) >> 8;
      int   b =  color & 0x0000FF;

      return (r * 30 + g * 59 + b * 11) / 100;
   }

   /**
    * Returns a weighted squared distance between two RGB colors (perceptual match metric).
    * @param color1 first 0xRRGGBB color
    * @param color2 second 0xRRGGBB color
    * @return error value; lower is a closer match
    **/
   protected int colorMatchError(int color1, int color2)
   {
      int   r1 = (color1 & 0xFF0000) >> 16;
      int   g1 = (color1 & 0x00FF00) >> 8;
      int   b1 =  color1 & 0x0000FF;
      int   r2 = (color2 & 0xFF0000) >> 16;
      int   g2 = (color2 & 0x00FF00) >> 8;
      int   b2 =  color2 & 0x0000FF;
      int   dr = (r2 - r1) * 30;
      int   dg = (g2 - g1) * 59;
      int   db = (b2 - b1) * 11;

      return (dr * dr + dg * dg + db * db) / 100;
   }

   /**
    * Writes the GIF89a signature, logical screen descriptor, and global color-table flag.
    * @param width image width in pixels
    * @param height image height in pixels
    * @param bitsPerPixel color depth (log2 of palette size)
    **/
   protected void writeGIFHeader(int width, int height, int bitsPerPixel) throws IOException
   {
      write((int) 'G');
      write((int) 'I');
      write((int) 'F');
      write((int) '8');
      write((int) '9');
      write((int) 'a');

      writeGIFWord(width);
      writeGIFWord(height);

      int   packedBits = 0x80; // Yes, there is a global color table, not ordered.

      packedBits |= ((bitsPerPixel - 1) << 4) | (bitsPerPixel - 1);

      write(packedBits);

      write(0); // Background color index -- not used.

      write(0); // Aspect ratio index -- not specified.
   }

   /**
    * Writes the global color table, padding to 2^bitsPerPixel entries.
    * @param colorTable palette RGB values
    * @param bitsPerPixel color depth determining table size
    **/
   protected void writeColorTable(int[] colorTable, int bitsPerPixel) throws IOException
   {
      int   colorCount = 1 << bitsPerPixel;

      for (int j = 0; j < colorCount; ++j) {
         if (j < colorTable.length)
            writeGIFColor(colorTable[j]);
         else
            writeGIFColor(0);
      }
   }

   /**
    * Writes a graphic control extension if transparentColor appears in the palette.
    * @param transparentColor RGB color to mark transparent
    * @param colorTable palette used for the image
    **/
   protected void writeGraphicControlExtension(Color transparentColor,
      int[] colorTable) throws IOException
   {
      for (int j = 0; j < colorTable.length; ++j) {
         if (colorTable[j] == (transparentColor.getRGB() & 0xFFFFFF)) {
            write(0x21); // Extension identifier.
            write(0xF9); // Graphic Control Extension identifier.
            write(0x04); // Block size, always 4.
            write(0x01); // Sets transparent color bit. Other bits in this
                         //   packed field should be zero.
            write(0x00); // Two bytes of delay time -- not used.
            write(0x00);
            write(j);    // Index of transparent color.
            write(0x00); // Block terminator.
         }
      }
   }

   /**
    * Writes the image descriptor (position, size, no local color table).
    * @param width image width in pixels
    * @param height image height in pixels
    **/
   protected void writeImageDescriptor(int width, int height) throws IOException
   {
      write(0x2C); // Image descriptor identifier;

      writeGIFWord(0); // left postion;
      writeGIFWord(0); // top postion;
      writeGIFWord(width);
      writeGIFWord(height);

      write(0); // No local color table, not interlaced.
   }

   /**
    * Writes a 16-bit little-endian value.
    * @param word value to write
    **/
   protected void writeGIFWord(short word) throws IOException
   {
      writeGIFWord((int) word);
   }

   /**
    * Writes a 16-bit little-endian value.
    * @param word value to write
    **/
   protected void writeGIFWord(int word) throws IOException
   {
      write(word & 0xFF);
      write((word & 0xFF00) >> 8);
   }

   /**
    * Writes one palette entry as three RGB bytes.
    * @param color AWT color (alpha ignored)
    **/
   protected void writeGIFColor(Color color) throws IOException
   {
      writeGIFColor(color.getRGB());
   }

   /**
    * Writes one palette entry as three RGB bytes.
    * @param color 0xRRGGBB value
    **/
   protected void writeGIFColor(int color) throws IOException
   {
      write((color & 0xFF0000) >> 16);
      write((color & 0xFF00) >> 8);
      write(color & 0xFF);
   }


   /********************************************************************\
   |                                                                    |
   |  The following code is based on C code for GIF compression         |
   |  obtained from http://www.boutell.com                              |
   |                                                                    |
   |  Based on GIFENCOD by David Rowley <mgardi@watdscu.waterloo.edu>   |
   |  Modified by Marcel Wijkstra <wijkstra@fwi.uva.nl>                 |
   |  One version, Copyright (C) 1989 by Jef Poskanzer.                 |
   |  Heavily modified by Mouse, 1998-02-12.                            |
   |                                                                    |
   |  And now, modified and rendered in Java by Kerry Shetline, 2000,   |
   |  kerry@shetline.com                                                |
   |                                                                    |
   \********************************************************************/
   protected int        rl_pixel;
   protected int        rl_basecode;
   protected int        rl_count;
   protected int        rl_table_pixel;
   protected int        rl_table_max;
   protected boolean    just_cleared;
   protected int        out_bits;
   protected int        out_bits_init;
   protected int        out_count;
   protected int        out_bump;
   protected int        out_bump_init;
   protected int        out_clear;
   protected int        out_clear_init;
   protected int        max_ocodes;
   protected int        code_clear;
   protected int        code_eof;
   protected int        obuf;
   protected int        obits;
   protected byte[]     oblock = new byte[256];
   protected int        oblen;

   protected final static int GIFBITS = 12;

   /**
    * Compresses and writes image data using the imported run-length GIF encoder below.
    * @param bytePixels palette indices, one byte per pixel
    * @param bitsPerPixel color depth passed to the LZW-style code stream
    **/
   protected void writeCompressedImageData(byte[] bytePixels, int bitsPerPixel)
      throws IOException
   {
      int   init_bits = bitsPerPixel;

      if (init_bits < 2)
         init_bits = 2;

      write(init_bits);

      int      c;

      obuf = 0;
      obits = 0;
      oblen = 0;
      code_clear = 1 << init_bits;
      code_eof = code_clear + 1;
      rl_basecode = code_eof + 1;
      out_bump_init = (1 << init_bits) - 1;
      /* for images with a lot of runs, making out_clear_init larger will
         give better compression. */
      out_clear_init = (init_bits <= 2) ? 9 : (out_bump_init - 1);
      out_bits_init = init_bits + 1;
      max_ocodes = (1 << GIFBITS) - ((1 << (out_bits_init - 1)) + 3);
      did_clear();
      output(code_clear);
      rl_count = 0;

      for (int j = 0; j < bytePixels.length; ++j) {
         c = (int) bytePixels[j];
         if (c < 0)
            c += 256;

         if ((rl_count > 0) && (c != rl_pixel))
            rl_flush();

         if (rl_pixel == c) {
            rl_count++;
         }
         else {
            rl_pixel = c;
            rl_count = 1;
         }
      }

      if (rl_count > 0)
         rl_flush();

      output(code_eof);
      output_flush();
   }


   /**
    * Flushes the current 255-byte compression sub-block to the stream.
    **/
   protected void write_block() throws IOException
   {
      write(oblen);
      write(oblock, 0, oblen);
      oblen = 0;
   }

   /**
    * Appends one byte to the compression sub-block buffer.
    * @param c byte value
    **/
   protected void block_out(int c) throws IOException
   {
      oblock[oblen++] = (byte) c;
      if (oblen >= 255)
         write_block();
   }

   /**
    * Writes any remaining bytes in the compression sub-block buffer.
    **/
   protected void block_flush() throws IOException
   {
      if (oblen > 0)
         write_block();
   }

   /**
    * Queues a variable-width code bit pattern into the output buffer.
    * @param val code value to emit
    **/
   protected void output(int val) throws IOException
   {
      obuf |= val << obits;
      obits += out_bits;
      while (obits >= 8) {
         block_out(obuf & 0xFF);
         obuf >>= 8;
         obits -= 8;
      }
   }

   /**
    * Flushes pending bits and the current sub-block after compression.
    **/
   protected void output_flush() throws IOException
   {
      if (obits > 0)
         block_out(obuf);
      block_flush();
   }

   /**
    * Resets encoder state after a clear code.
    **/
   protected void did_clear() throws IOException
   {
      out_bits = out_bits_init;
      out_bump = out_bump_init;
      out_clear = out_clear_init;
      out_count = 0;
      rl_table_max = 0;
      just_cleared = true;
   }

   /**
    * Emits a literal code and updates dynamic code-size state.
    * @param c code value to emit
    **/
   protected void output_plain(int c) throws IOException
   {
      just_cleared = false;
      output(c);
      out_count++;
      if (out_count >= out_bump) {
         out_bits++;
         out_bump += 1 << (out_bits - 1);
      }
      if (out_count >= out_clear) {
         output(code_clear);
         did_clear();
      }
   }

   /**
    * Integer square root used by run-length cost estimation.
    * @param x non-negative value
    * @return floor(sqrt(x)) for x >= 2, else x
    **/
   protected int isqrt(int x)
   {
      int   r;
      int   v;

      if (x < 2)
         return x;

      for (v = x, r = 1; v != 0; v >>= 2, r <<= 1);

      while (true) {
         v = ((x / r) + r) / 2;
         if ((v == r) || (v == r + 1))
            return r;
         r = v;
      }
   }

   /**
    * Estimates output codes needed to represent a run of identical pixels.
    * @param count run length
    * @param nrepcodes number of repeat codes available
    * @return estimated code count
    **/
   protected int compute_triangle_count(int count, int nrepcodes)
   {
      int   perrep;
      int   cost;

      cost = 0;
      perrep = (nrepcodes * (nrepcodes +1 )) / 2;
      while (count >= perrep) {
         cost += nrepcodes;
         count -= perrep;
      }
      if (count > 0) {
         int      n = isqrt(count);
         while ((n * (n + 1)) >= 2 * count)
            n--;
         while ((n * (n + 1)) < 2 * count)
            n++;
         cost += n;
      }

      return cost;
   }

   /**
    * Raises the clear threshold to the maximum opcode limit.
    **/
   protected void max_out_clear()
   {
      out_clear = max_ocodes;
   }

   /**
    * Restores the clear threshold and emits a clear code if the table is full.
    **/
   protected void reset_out_clear() throws IOException
   {
      out_clear = out_clear_init;
      if (out_count >= out_clear) {
         output(code_clear);
         did_clear();
      }
   }

   /**
    * Flushes a run immediately after a clear code using plain pixel codes.
    * @param count run length to encode
    **/
   protected void rl_flush_fromclear(int count) throws IOException
   {
      int   n;

      max_out_clear();
      rl_table_pixel = rl_pixel;
      n = 1;
      while (count > 0) {
         if (n == 1) {
            rl_table_max = 1;
            output_plain(rl_pixel);
            count--;
         }
         else if (count >= n) {
            rl_table_max = n;
            output_plain(rl_basecode + n - 2);
            count -= n;
         }
         else if (count == 1) {
            rl_table_max++;
            output_plain(rl_pixel);
            count = 0;
         }
         else {
            rl_table_max++;
            output_plain(rl_basecode + count - 2);
            count = 0;
         }

         if (out_count == 0)
            n = 1;
         else
            n++;
      }

      reset_out_clear();
   }

   /**
    * Flushes a run by either issuing a clear code or repeating plain pixel codes.
    * @param count run length to encode
    **/
   protected void rl_flush_clearorrep(int count) throws IOException
   {
      int   withclr;

      withclr = 1 + compute_triangle_count(count, max_ocodes);
      if (withclr < count) {
         output(code_clear);
         did_clear();
         rl_flush_fromclear(count);
      }
      else {
         for (; count > 0; count--)
            output_plain(rl_pixel);
      }
   }

   /**
    * Flushes a run using the repeat-code table when cheaper than clear+plain.
    * @param count run length to encode
    **/
   protected void rl_flush_withtable(int count) throws IOException
   {
      int   repmax;
      int   repleft;
      int   leftover;

      repmax = count / rl_table_max;
      leftover = count % rl_table_max;
      repleft = (leftover != 0 ? 1 : 0);
      if (out_count + repmax + repleft > max_ocodes) {
         repmax = max_ocodes - out_count;
         leftover = count - (repmax * rl_table_max);
         repleft = 1 + compute_triangle_count(leftover, max_ocodes);
      }

      if (1 + compute_triangle_count(count,max_ocodes) < repmax + repleft) {
         output(code_clear);
         did_clear();
         rl_flush_fromclear(count);
         return;
      }

      max_out_clear();
      for (; repmax > 0; repmax--)
         output_plain(rl_basecode + rl_table_max - 2);
      if (leftover != 0) {
         if (just_cleared) {
            rl_flush_fromclear(leftover);
         }
         else if (leftover == 1) {
            output_plain(rl_pixel);
         }
         else {
            output_plain(rl_basecode + leftover - 2);
         }
      }
      reset_out_clear();
   }

   /**
    * Encodes and clears the current run-length pixel sequence ({@link #rl_count}).
    **/
   protected void rl_flush() throws IOException
   {
      int   table_reps;
      int   table_extra;

      if (rl_count == 1) {
         output_plain(rl_pixel);
         rl_count = 0;
         return;
      }
      if (just_cleared) {
         rl_flush_fromclear(rl_count);
      }
      else if ((rl_table_max < 2) || (rl_table_pixel != rl_pixel)) {
         rl_flush_clearorrep(rl_count);
      }
      else {
         rl_flush_withtable(rl_count);
      }

      rl_count = 0;
   }

   // END OF IMPORTED GIF COMPRESSION CODE
}
