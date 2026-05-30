/*
*
*    Copyright (C) 2003 Kent Hansen.
*
*    This file is part of Tile Molester.
*
*    Tile Molester is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    Tile Molester is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*/

package tm.tilecodecs;

/**
 * Abstract base for 8×8 tile codecs.
 * Subclasses implement {@link #decode} and {@link #encode} for a specific ROM layout.
 **/
public abstract class TileCodec {

    public static final int MODE_1D=1;
    public static final int MODE_2D=2;

    private String id;
    private String description;
    protected int[] pixels;     // destination for DEcoded tile data
    protected int bitsPerPixel;
    protected int bytesPerRow;  // row = 8 pixels
    protected long colorCount;
    protected int tileSize;     // size of one encoded tile

    /**
     * Initializes tile geometry for this codec.
     * @param id short codec identifier (from tmspec)
     * @param bitsPerPixel palette index bits per pixel (1, 2, 4, 8, or direct-color byte width)
     * @param description human-readable label shown in the UI
     **/
    public TileCodec(String id, int bitsPerPixel, String description) {
        this.id = id;
        this.bitsPerPixel = bitsPerPixel;
        this.description = description;
        bytesPerRow = bitsPerPixel; // because (bitsPerPixel*8)/8 = bitsPerPixel
        tileSize = bytesPerRow*8;
        colorCount = 1 << bitsPerPixel;
        pixels = new int[8*8];
    }

    /**
     * Decodes one 8×8 tile from raw file bytes into {@link #pixels}.
     * @param bits file buffer containing encoded tile data
     * @param ofs byte offset where this tile starts in {@code bits}
     * @param stride extra bytes between encoded rows (0 if rows are contiguous); multiplied by {@link #bytesPerRow} internally
     * @return the same {@code pixels} array filled with palette indices or ARGB values
     **/
    public abstract int[] decode(byte[] bits, int ofs, int stride);

    /**
     * Writes one decoded 8×8 tile from {@code pixels} into the file buffer.
     * @param pixels 64 values (indices or ARGB) in row-major order
     * @param bits destination file buffer
     * @param ofs byte offset where this tile starts in {@code bits}
     * @param stride extra bytes between encoded rows (0 if rows are contiguous); multiplied by {@link #bytesPerRow} internally
     **/
    public abstract void encode(int[] pixels, byte[] bits, int ofs, int stride);

    /**
     * Returns bits per pixel for this tile format.
     * @return bits per pixel
     **/
    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    /**
     * Returns encoded bytes per 8-pixel row.
     * @return bytes per row
     **/
    public int getBytesPerRow() {
        return bytesPerRow;
    }

/*
    public long getColorCount() {
        return colorCount;
    }
*/
// TEMP!!!!!!!!!!
    /**
     * Returns the number of distinct palette indices for this format.
     * For depths below 8 bpp this is {@code 2^bitsPerPixel}; for 8 bpp and direct formats returns 256.
     * @return palette entry count usable by the editor
     **/
    public int getColorCount() {
        if (bitsPerPixel < 8) return (1 << bitsPerPixel);
        return 256;
    }

    /**
     * Returns the encoded size of one tile in bytes.
     * @return tile byte length in the file
     **/
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Returns the human-readable codec description.
     * @return description text
     **/
    public String getDescription() {
        return description;
    }

    /**
     * Returns the short codec identifier.
     * @return codec id string
     **/
    public String getID() {
        return id;
    }

    /**
     * Returns the description (same as {@link #getDescription()}).
     * @return description text
     **/
    public String toString() {
        return description;
    }

}
