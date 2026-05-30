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

package tm.utils;

/**
 * Thrown when a CSV palette file contains a non-hex or malformed color entry.
 **/
public class PaletteCsvParseException extends Exception {

    private final int entryNumber;
    private final String value;

    /**
     * Creates a PaletteCsvParseException instance.
     * @param entryNumber 1-based palette entry number for error reporting
     * @param value invalid CSV color token
     **/
    public PaletteCsvParseException(int entryNumber, String value) {
        super("Entry " + entryNumber + ": \"" + value + "\" is invalid");
        this.entryNumber = entryNumber;
        this.value = value;
    }

    /**
     * Gets the 1-based entry number that failed parsing.
     * @return 1-based entry index that failed parsing
     **/
    public int getEntryNumber() {
        return entryNumber;
    }

    /**
     * Gets the invalid token value from the CSV file.
     * @return malformed token from the CSV file
     **/
    public String getValue() {
        return value;
    }
}
