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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Reads comma-separated RRGGBB hex values from a CSV palette file.
 **/
public class PaletteCsvReader {

    private static final Pattern HEX6 = Pattern.compile("^[0-9A-Fa-f]{6}$");

    /**
     * Creates a PaletteCsvReader instance.
     **/
    private PaletteCsvReader() {
    }

    /**
     * @return 24-bit RGB values (0x00RRGGBB) in file order
     **/
    public static int[] read(File file) throws Exception {
        ArrayList<Integer> colors = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split(",");
                for (String token : tokens) {
                    String t = token.trim();
                    if (t.isEmpty()) {
                        continue;
                    }
                    int entryNumber = colors.size() + 1;
                    colors.add(parseHexColor(entryNumber, t));
                }
            }
        }
        int[] rgb = new int[colors.size()];
        for (int i = 0; i < colors.size(); i++) {
            rgb[i] = colors.get(i).intValue();
        }
        return rgb;
    }

    /**
     * Parses a single hex color token from a CSV palette entry.
     * @param entryNumber 1-based palette entry number for error reporting
     * @param token hex color token from the CSV file
     * @return 24-bit RGB value from the hex token
     **/
    private static int parseHexColor(int entryNumber, String token) throws PaletteCsvParseException {
        String hex = token;
        if (hex.length() >= 2
                && hex.charAt(0) == '0'
                && (hex.charAt(1) == 'x' || hex.charAt(1) == 'X')) {
            hex = hex.substring(2);
        }
        if (!HEX6.matcher(hex).matches()) {
            throw new PaletteCsvParseException(entryNumber, token);
        }
        return Integer.parseInt(hex, 16) & 0xFFFFFF;
    }
}
