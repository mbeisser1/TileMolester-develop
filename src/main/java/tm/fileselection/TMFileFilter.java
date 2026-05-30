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

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.StringTokenizer;

/**
 * A file filter that allows you to register a set of extensions with a common
 * description.
 **/
public class TMFileFilter extends FileFilter {

    private String description;
    private String[] extensions;
    private String extlist;

    /**
     * Creates an empty file filter.
     **/
    public TMFileFilter() {
        super();
    }

    /**
     * Creates a file filter from a comma-separated extension list and description.
     * @param extlist comma-separated list of extensions
     * @param description human-readable filter label
     **/
    public TMFileFilter(String extlist, String description) {
        super();
        setExtensions(extlist);
        setDescription(description);
    }

    /**
     * Parses and stores the comma-separated extension list.
     * @param extlist comma-separated list of extensions
     **/
    public void setExtensions(String extlist) {
        this.extlist = extlist;
        StringTokenizer st = new StringTokenizer(extlist, ",");
        extensions = new String[st.countTokens()];
        for (int i=0; i<extensions.length; i++) {
            extensions[i] = st.nextToken();
        }
    }

    /**
     * Sets the human-readable description shown in the file chooser.
     * @param description filter label text
     **/
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the registered filename extensions.
     * @return extension strings without leading dots
     **/
    public String[] getExtensions() {
        return extensions;
    }

    /**
     * Returns the original comma-separated extension list.
     * @return extension list as passed to the constructor or {@link #setExtensions}
     **/
    public String getExtlist() {
        return extlist;
    }

    /**
     * Returns the first registered extension, used as the default when appending.
     * @return default extension string
     **/
    public String getDefaultExtension() {
        return extensions[0];
    }

    /**
     * Returns the human-readable filter description.
     * @return description text
     **/
    public String getDescription() {
        return description;
    }

    /**
     * Accepts directories and files whose extension matches one of the registered
     * patterns ({@code ?} matches any character).
     * @param f file or directory to test
     * @return {@code true} if the file should be shown in the chooser
     **/
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String ext = getExtension(f);
        for (int i=0; i<extensions.length; i++) {
            String suppExt = extensions[i];
            if (suppExt.length() != ext.length()) {
                continue;   // can't possibly match if length different
            }
            boolean match = true;
            for (int j=0; j<ext.length(); j++) {
                if (suppExt.charAt(j) == '?') {
                    continue;   // '?' matches any character
                }
                if (suppExt.charAt(j) != ext.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the file extension.
     * @param f file whose extension is needed
     * @return lowercase extension without dot, or empty string if none
     **/
    public static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

}
