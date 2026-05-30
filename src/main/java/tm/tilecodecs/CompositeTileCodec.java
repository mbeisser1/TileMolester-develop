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
 * Composite tile codec.
 * A <code>composite</code> tile is a tile that is built up of several
 * standard tiles. As an example, consider a 3bpp tile that consists
 * of a single 2bpp non-interleaved tile followed by a single 1bpp tile.
 * Such a format cannot be accommodated by the standard planar tile codec
 * (PlanarTileCodec). However, it can be accommodated by instantiating several
 * PlanarTileCodecs, decoding planar tiles separately and then "overlaying" them
 * on top of each other. This class provides just this kind of functionality.
 * It allows more flexibility in the tile formats, but is probably a bit
 * slower.
 **/
public class CompositeTileCodec extends TileCodec {

    private TileCodec[] codecs;

    /**
     * Builds a composite codec from low-to-high bitplane sub-codecs.
     * @param id short codec identifier
     * @param bpp total bits per pixel for the combined tile
     * @param codecs sub-codecs applied in order from least to most significant bitplanes
     * @param description human-readable label
     **/
    public CompositeTileCodec(String id, int bpp, TileCodec[] codecs, String description) {
        super(id, bpp, description);
        this.codecs = codecs;
    }

    /**
     * Decodes a composite tile by decoding each sub-tile and OR-ing bitplanes together.
     * @param bits file buffer containing encoded tile data
     * @param ofs byte offset where this tile starts
     * @param stride row padding passed to each sub-codec (see {@link TileCodec#decode})
     * @return 64 combined palette indices in {@link #pixels}
     **/
    public int[] decode(byte[] bits, int ofs, int stride) {
        // decode the first sub-tile
        int[] tilePixels = codecs[0].decode(bits, ofs, stride);
        System.arraycopy(tilePixels, 0, pixels, 0, tilePixels.length);
        // decode remaining sub-tiles
        int p = codecs[0].getBitsPerPixel();
        for (int i=1; i<codecs.length; i++) {
            ofs += (stride+1) * codecs[i-1].getTileSize();
            tilePixels = codecs[i].decode(bits, ofs, stride);
            // "overlay" the tile
            for (int j=0; j<64; j++) {
                pixels[j] |= tilePixels[j] << p;
            }
            p += codecs[i].getBitsPerPixel();
        }
        return pixels;
    }

    /**
     * Encodes a composite tile by shifting and encoding each bitplane sub-tile.
     * @param pixels 64 combined palette indices
     * @param bits destination file buffer
     * @param ofs byte offset where this tile starts
     * @param stride row padding passed to each sub-codec (see {@link TileCodec#encode})
     **/
    public void encode(int[] pixels, byte[] bits, int ofs, int stride) {
        // encode the first sub-tile
        codecs[0].encode(pixels, bits, ofs, stride);
        // encode remaining sub-tiles
        int p = codecs[0].getBitsPerPixel();
        for (int i=1; i<codecs.length; i++) {
            ofs += (stride+1) * codecs[i-1].getTileSize();
            // shift the tile pixels
            for (int j=0; j<64; j++) {
                pixels[j] >>= p;
            }
            codecs[i].encode(pixels, bits, ofs, stride);
            p += codecs[i].getBitsPerPixel();
        }
    }

}