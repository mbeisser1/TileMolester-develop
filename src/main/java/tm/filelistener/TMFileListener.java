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

package tm.filelistener;

/**
 * Abstract class that defines the interface for filelisteners.
 * A <code>filelistener</code> is an object that is notified when a file has
 * been fully loaded into memory, as well as when it is about to be saved.
 * At these times it may perform various operations on the data being saved,
 * such as repairing checksums.
 * The fileformatlistener also has to implement a method that determines if
 * the file being saved is indeed of a supported format. This usually involves
 * checking the header (verifying ID strings and such).
 **/
public abstract class TMFileListener {

    /**
     * Invoked after a file is loaded to detect whether this listener handles the format.
     * When this returns {@code true}, subsequent {@link #fileLoaded} and
     * {@link #fileSaving} calls are routed to this listener.
     * @param data file contents in memory
     * @param extension lowercase filename extension without dot
     * @return {@code true} if this listener should handle the file
     **/
    public abstract boolean doFormatDetect(final byte[] data, String extension);

    /**
     * Invoked after the file has been loaded and {@link #doFormatDetect} returned
     * {@code true}.
     * @param data file contents in memory
     * @param extension lowercase filename extension without dot
     **/
    public abstract void fileLoaded(byte[] data, String extension);

    /**
     * Invoked immediately before the file is written to disk.
     * @param data file contents about to be saved
     * @param extension lowercase filename extension without dot
     **/
    public abstract void fileSaving(byte[] data, String extension);

}
