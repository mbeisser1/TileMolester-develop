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

package tm.threads;

import java.io.*;

import tm.utils.TMLog;

/**
 * Thread for reading a file into a buffer.
 **/
public class FileLoaderThread extends ProgressThread {

    private static final int CHUNK_SIZE = 16384;
    BufferedInputStream bis=null;
    private int bytesLeft;
    private byte[] contents;

    /**
     * Allocates a buffer sized to the file and opens an input stream for chunked reading.
     * @param file source file to load
     * @throws OutOfMemoryError if the file cannot fit in a byte array
     * @throws FileNotFoundException if the file does not exist or cannot be opened
     **/
    public FileLoaderThread(File file) throws OutOfMemoryError, FileNotFoundException {
        super();
        contents = new byte[(int)file.length()];
        bis = new BufferedInputStream(new FileInputStream(file));
        bytesLeft = contents.length;
        this.setPriority(NORM_PRIORITY);
    }

    /**
     * Returns how much of the file has been read so far.
     * @return completion percentage from 0 to 100
     **/
    public int getPercentageCompleted() {
        int result = (int)((long)(((long)contents.length - (long)bytesLeft) * 100) / (long)contents.length);
        return result;
    }

    /**
     * Reads the file in {@link #CHUNK_SIZE} chunks until the buffer is full.
     **/
    public void run() {
        while (bytesLeft > 0) {
            if (bytesLeft > CHUNK_SIZE) {
                try {
                    bis.read(contents, contents.length - bytesLeft, CHUNK_SIZE);
                }
                catch (IOException e) {
                    TMLog.logException("File load read error", e);
                }
                bytesLeft -= CHUNK_SIZE;
            }
            else {
                try {
                    bis.read(contents, contents.length - bytesLeft, bytesLeft);
                }
                catch (IOException e) {
                    TMLog.logException("File load read error", e);
                }
                bytesLeft = 0;
            }
            ProgressThread.yield();
        }
        try {
            bis.close();
        } catch (IOException e) {
            TMLog.logException("File load close error", e);
        }
        // done loading data
    }

    /**
     * Returns the loaded file contents.
     * @return byte array holding the entire file
     **/
    public byte[] getContents() {
        return contents;
    }

    /**
     * Drops the reference to the loaded buffer so it can be garbage collected.
     **/
    public void killContentsRef() {
        contents = null;
    }

}
