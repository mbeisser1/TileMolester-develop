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

package tm.colorcodecs;

/**
 * Abstract superclass for color codecs.
 * A "color codec" defines how values of a particular format are translated
 * to 32-bit ARGB pixel values (decoded), and back again (encoded).
 **/
public abstract class ColorCodec {

    public static final int LITTLE_ENDIAN=1;
    public static final int BIG_ENDIAN=2;
    public static final int MIDDLE_ENDIAN=3;

    protected int bitsPerPixel;
    protected int bytesPerPixel;
    protected String id;
    protected String description;

    protected int endianness;
    private int startShift;
    private int shiftStep;

    /**
     * Initializes palette entry encoding for a ROM color format.
     * @param id short codec identifier (from tmspec)
     * @param bitsPerPixel bits per palette entry in the file
     * @param description human-readable label
     **/
    public ColorCodec(String id, int bitsPerPixel, String description) {
        this.id = id;
        this.bitsPerPixel = bitsPerPixel;
        this.description = description;
        bytesPerPixel = getBytesRequired(bitsPerPixel);
        setEndianness(ColorCodec.LITTLE_ENDIAN);   // default
    }

    /**
     * Sets byte order for {@link #toBytes} and {@link #fromBytes}.
     * @param endianness {@link #LITTLE_ENDIAN}, {@link #BIG_ENDIAN}, or {@link #MIDDLE_ENDIAN} (TODO: middle endian use)
     **/
    public void setEndianness(int endianness) {
        this.endianness = endianness;
        if (endianness == ColorCodec.LITTLE_ENDIAN) {
            startShift = 0;
            shiftStep = 8;
        }
        else {
            // ColorCodec.BIG_ENDIAN
            startShift = (bytesPerPixel-1) * 8;
            shiftStep = -8;
        }
    }

    /**
     * Returns the short codec identifier.
     * @return codec id
     **/
    public String getID() {
        return id;
    }

    /**
     * Returns bits per palette entry in the ROM.
     * @return bits per pixel
     **/
    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    /**
     * Returns whole bytes used to store one palette entry.
     * @return bytes per entry
     **/
    public int getBytesPerPixel() {
        return bytesPerPixel;
    }

    /**
     * Returns the human-readable codec description.
     * @return description text
     **/
    public String getDescription() {
        return description;
    }

    /**
     * Converts a native palette value to Java ARGB.
     * @param value encoded entry from the file (already assembled into an int)
     * @return 32-bit ARGB color
     **/
    public abstract int decode(int value);

    /**
     * Converts Java ARGB to a native palette value.
     * @param argb 32-bit ARGB color
     * @return encoded entry for the file
     **/
    public abstract int encode(int argb);

    /**
     * Writes a native value into a byte array using the current endianness.
     * @param value encoded palette value
     * @param bytes destination array
     * @param offset start index in {@code bytes}
     * @return {@code bytes} for chaining
     **/
    public byte[] toBytes(int value, byte[] bytes, int offset) {
        int shift = startShift;
        for (int i=0; i<bytesPerPixel; i++) {
            bytes[offset+i] = (byte)(value >> shift);
            shift += shiftStep;
        }
        return bytes;
    }

    /**
     * Reads a native palette value from a byte array using the current endianness.
     * @param bytes source array
     * @param offset start index in {@code bytes}
     * @return assembled encoded value
     **/
    public int fromBytes(byte[] bytes, int offset) {
        int shift = startShift;
        int value = 0;
        for (int i=0; i<bytesPerPixel; i++) {
            value |= (bytes[offset+i] & 0xFF) << shift;
            shift += shiftStep;
        }
        return value;
    }

    /**
     * Gets the least number of whole bytes that are required to store
     * <code>bits</code> bits of information.
     **/
    private static int getBytesRequired(int bits) {
        int bytes = bits / 8;
        int extrabits = bits % 8;
        if(extrabits != 0) {
            bytes++;
        }
        return bytes;
    }

    /**
     * Returns the description (same as {@link #getDescription()}).
     * @return description text
     **/
    public String toString() {
        return description;
    }

}