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

package tm.fileselection;

/**
 * File filter that associates accepted extensions with palette import parameters.
 **/
public class TMPaletteFileFilter extends TMFileFilter {

    private String codecID; // color codec
    private int size;
    private int offset;
    private int endianness;

    /**
     * Creates a palette file filter with codec and layout metadata.
     * @param extlist comma-separated list of extensions
     * @param description human-readable filter label
     * @param codecID color codec identifier from tmspec
     * @param size palette data size in bytes
     * @param offset byte offset of palette data in the file
     * @param endianness byte order constant (TODO: document endianness values)
     **/
    public TMPaletteFileFilter(String extlist, String description, String codecID, int size, int offset, int endianness) {
        super(extlist, description);
        this.codecID = codecID;
        this.size = size;
        this.offset = offset;
        this.endianness = endianness;
    }

    /**
     * Returns the color codec identifier for palette data in matching files.
     * @return codec id string
     **/
    public String getCodecID() {
        return codecID;
    }

    /**
     * Returns the size in bytes of the palette region in matching files.
     * @return palette data size
     **/
    public int getSize() {
        return size;
    }

    /**
     * Returns the byte offset of palette data within matching files.
     * @return palette offset
     **/
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the byte order used when reading palette entries.
     * @return endianness constant
     **/
    public int getEndianness() {
        return endianness;
    }

}
