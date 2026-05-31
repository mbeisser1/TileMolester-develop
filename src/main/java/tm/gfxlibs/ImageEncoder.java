// ImageEncoder - abstract class for writing out an image
//
// Copyright (C) 1996 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
// OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
// LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
// OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
//
// Visit the ACME Labs Java page for up-to-date versions of this and other
// fine Java utilities: http://www.acme.com/java/

package tm.gfxlibs;

import tm.utils.TMLog;

import java.util.*;
import java.io.*;
import java.awt.Image;
import java.awt.image.*;

/**
 * Abstract base for image encoders that consume ImageProducer pixels.
 **/
public abstract class ImageEncoder implements ImageConsumer
    {

    protected OutputStream out;

    private ImageProducer producer;
    private int width = -1;
    private int height = -1;
    private int hintflags = 0;
    private boolean started = false;
    private boolean encoding;
    private IOException iox;
    private static final ColorModel rgbModel = ColorModel.getRGBdefault();
    private Map<?, ?> props = null;

    /**
     * Creates an image encoder for the given image and output stream.
     * @param img image whose pixels are encoded
     * @param out destination output stream
     **/
    public ImageEncoder( Image img, OutputStream out ) throws IOException
	{
	this( img.getSource(), out );
	}

    /**
     * Creates an image encoder for the given producer and output stream.
     * @param producer image producer supplying pixel data
     * @param out destination output stream
     **/
    public ImageEncoder( ImageProducer producer, OutputStream out ) throws IOException
	{
	this.producer = producer;
	this.out = out;
	}


    /**
     * Subclass hook to write the image format header.
     * @param w image width in pixels
     * @param h image height in pixels
     **/
    abstract void encodeStart( int w, int h ) throws IOException;

    /**
     * Subclass hook to write a rectangle of ARGB pixels.
     * Pixels arrive in top-down left-right order using the RGBdefault color model.
     * @param x horizontal pixel origin
     * @param y vertical pixel origin
     * @param w pixel region width
     * @param h pixel region height
     * @param rgbPixels ARGB pixel values
     * @param off offset into the pixel array
     * @param scansize row stride in the pixel array
     **/
    abstract void encodePixels(
	int x, int y, int w, int h, int[] rgbPixels, int off, int scansize )
	throws IOException;

    /**
     * Subclass hook to finalize the encoded image stream.
     **/
    abstract void encodeDone() throws IOException;


    // Our own methods.

    /**
     * Starts image production and blocks until encoding completes.
     **/
    public synchronized void encode() throws IOException
	{
	encoding = true;
	iox = null;
	producer.startProduction( this );
	while ( encoding )
	    try
		{
		wait();
		}
	    catch ( InterruptedException e ) {
		TMLog.logException("Image encoding interrupted", e);
	    }
	if( iox != null ) {
		throw iox;
	}
	}

    private boolean accumulate = false;
    private int[] accumulator;

    /**
     * Buffers or forwards pixel rows to the encoder.
     **/
    private void encodePixelsWrapper(
	int x, int y, int w, int h, int[] rgbPixels, int off, int scansize )
	throws IOException
	{
	if ( ! started )
	    {
	    started = true;
	    encodeStart( width, height );
	    if ( ( hintflags & TOPDOWNLEFTRIGHT ) == 0 )
		{
		accumulate = true;
		accumulator = new int[width * height];
		}
	    }
	if( accumulate ) {
	    for ( int row = 0; row < h; ++row ) {
		System.arraycopy(
		    rgbPixels, row * scansize + off,
		    accumulator, ( y + row ) * width + x,
		    w );
	    }
	} else {
	    encodePixels( x, y, w, h, rgbPixels, off, scansize );
	}
	}

    /**
     * Flushes accumulated pixels before encoding completes.
     **/
    private void encodeFinish() throws IOException
	{
	if ( accumulate )
	    {
	    encodePixels( 0, 0, width, height, accumulator, 0, width );
	    accumulator = null;
	    accumulate = false;
	    }
	}

    /**
     * Stops the encode loop and notifies waiting threads.
     **/
    private synchronized void stop()
	{
	encoding = false;
	notifyAll();
	}


    // Methods from ImageConsumer.

    /**
     * Receives image width and height from the image producer.
     * @param width image width in pixels
     * @param height image height in pixels
     **/
    public void setDimensions( int width, int height )
	{
	this.width = width;
	this.height = height;
	}

    /**
     * Receives image properties from the image producer.
     * @param props image property hashtable
     **/
    public void setProperties( Map<?, ?> props )
	{
	this.props = props;
	}

    /**
     * Receives the color model from the image producer.
     * @param model source color model for pixel data
     **/
    public void setColorModel( ColorModel model )
	{
	// Ignore.
	}

    /**
     * Receives delivery hints from the image producer.
     * @param hintflags image delivery hint flags
     **/
    public void setHints( int hintflags )
	{
	this.hintflags = hintflags;
	}

    /**
     * Receives pixel data from the image producer.
     **/
    public void setPixels(
	int x, int y, int w, int h, ColorModel model, byte[] pixels,
	int off, int scansize )
	{
	int[] rgbPixels = new int[w];
	for ( int row = 0; row < h; ++row )
	    {
	    int rowOff = off + row * scansize;
	    for ( int col = 0; col < w; ++col )
		rgbPixels[col] = model.getRGB( pixels[rowOff + col] & 0xFF );
	    try
		{
		encodePixelsWrapper( x, y + row, w, 1, rgbPixels, 0, w );
		}
	    catch ( IOException e )
		{
		TMLog.logException("Image encoding I/O error", e);
		iox = e;
		stop();
		return;
		}
	    }
	}

    /**
     * Receives pixel data from the image producer.
     **/
    public void setPixels(
	int x, int y, int w, int h, ColorModel model, int[] pixels,
	int off, int scansize )
	{
	if ( model == rgbModel )
	    {
	    try
		{
		encodePixelsWrapper( x, y, w, h, pixels, off, scansize );
		}
	    catch ( IOException e )
		{
		TMLog.logException("Image encoding I/O error", e);
		iox = e;
		stop();
		return;
		}
	    }
	else
	    {
	    int[] rgbPixels = new int[w];
            for ( int row = 0; row < h; ++row )
		{
		int rowOff = off + row * scansize;
                for ( int col = 0; col < w; ++col )
                    rgbPixels[col] = model.getRGB( pixels[rowOff + col] );
		try
		    {
		    encodePixelsWrapper( x, y + row, w, 1, rgbPixels, 0, w );
		    }
		catch ( IOException e )
		    {
		    TMLog.logException("Image encoding I/O error", e);
		    iox = e;
		    stop();
		    return;
		    }
		}
	    }
	}

    /**
     * Called when image production completes or aborts.
     * @param status image production completion status
     **/
    public void imageComplete( int status )
	{
	producer.removeConsumer( this );
	if ( status == ImageConsumer.IMAGEABORTED ) {
	    iox = new IOException( "image aborted" );
	    TMLog.logException("Image encoding aborted", iox);
	}
	else
	    {
	    try
		{
		encodeFinish();
		encodeDone();
		}
	    catch ( IOException e )
		{
		TMLog.logException("Image encoding finish failed", e);
		iox = e;
		}
	    }
	stop();
	}

    }
