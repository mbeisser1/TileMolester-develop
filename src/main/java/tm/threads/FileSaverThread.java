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
 * Thread for writing a buffer to a file.
 **/
public class FileSaverThread extends ProgressThread {

    private static final int CHUNK_SIZE = 16384;
    private RandomAccessFile raf=null;
    private int bytesLeft;
    private byte[] contents;

    /**
     * Opens the destination file for writing and prepares chunked output.
     * @param contents data to write
     * @param file destination file
     * @throws FileNotFoundException if the file cannot be opened
     * @throws IOException if seeking or opening fails
     **/
    public FileSaverThread(byte[] contents, File file)
        throws FileNotFoundException, IOException {
        super();
        this.contents = contents;
        raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        bytesLeft = contents.length;
        this.setPriority(NORM_PRIORITY);
    }

    /**
     * Returns how much of the buffer has been written so far.
     * @return completion percentage from 0 to 100
     **/
    public int getPercentageCompleted() {
        int result = (int)(((long)contents.length - (long)bytesLeft) * 100 / (long)contents.length);
        return result;
    }

    /**
     * Writes the buffer in {@link #CHUNK_SIZE} chunks until complete, then closes the file.
     **/
    public void run() {
        while (bytesLeft > 0) {
            if (bytesLeft > CHUNK_SIZE) {
                try {
                    raf.write(contents, contents.length - bytesLeft, CHUNK_SIZE);
                }
                catch (IOException e) {
                    TMLog.severe("File save write error", e);
                    bytesLeft = 0;
                    break;
                }
                bytesLeft -= CHUNK_SIZE;
            }
            else {
                try {
                    raf.write(contents, contents.length - bytesLeft, bytesLeft);
                }
                catch (IOException e) {
                    TMLog.severe("File save write error", e);
                    bytesLeft = 0;
                    break;
                }
                bytesLeft = 0;
            }
            ProgressThread.yield();
        }
        try {
            raf.close();
        } catch (IOException e) {
            TMLog.logException("File save close error", e);
        }
        // done saving data
    }

}
