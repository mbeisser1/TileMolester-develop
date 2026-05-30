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
 * File filter that associates accepted extensions with a tile codec and default mode.
 **/
public class TMTileCodecFileFilter extends TMFileFilter {

    private int defaultMode;
    private String codecID;

    /**
     * Creates a tile-codec file filter.
     * @param extlist comma-separated list of extensions
     * @param description human-readable filter label
     * @param codecID tile codec identifier from tmspec
     * @param defaultMode default tile layout mode for this filter
     **/
    public TMTileCodecFileFilter(String extlist, String description, String codecID, int defaultMode) {
        super(extlist, description);
        setCodecID(codecID);
        setDefaultMode(defaultMode);
    }

    /**
     * Sets the tile codec identifier for files accepted by this filter.
     * @param codecID tile codec identifier from tmspec
     **/
    public void setCodecID(String codecID) {
        this.codecID = codecID;
    }

    /**
     * Sets the default tile layout mode used when this filter is selected.
     * @param defaultMode default mode value (TODO: document mode constants)
     **/
    public void setDefaultMode(int defaultMode) {
        this.defaultMode = defaultMode;
    }

    /**
     * Returns the default tile layout mode for this filter.
     * @return default mode value
     **/
    public int getDefaultMode() {
        return defaultMode;
    }

    /**
     * Returns the tile codec identifier for this filter.
     * @return codec id string
     **/
    public String getCodecID() {
        return codecID;
    }

}
