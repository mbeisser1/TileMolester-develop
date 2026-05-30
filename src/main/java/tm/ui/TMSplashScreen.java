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

package tm.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tile Molester splash screen.
 **/
public class TMSplashScreen extends JWindow {

    /**
     * Creates and displays the splash screen centered on the display.
     * @param owner owning frame
     **/
    public TMSplashScreen(Frame owner) {
        super(owner);
        ClassLoader cl = getClass().getClassLoader();
        JLabel l = new JLabel(new ImageIcon(cl.getResource("tm/splash.gif")));
        getContentPane().add(l, BorderLayout.CENTER);
        pack();

        // center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        int insetx = (screenSize.width - labelSize.width) / 2;
        int insety = (screenSize.height - labelSize.height) / 2;
        setLocation(insetx, insety);

        addMouseListener(new MouseAdapter() {
            /**
             * Dismisses the splash screen on double-click.
             * @param e mouse event from the splash window
             **/
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setVisible(false);
                    dispose();
                }
            }
        });

        final Runnable closerRunner = new Runnable() {
            /**
             * Closes the splash screen after the display delay.
             **/
            public void run() {
                setVisible(false);
                dispose();
            }
        };

        Runnable waitRunner = new Runnable() {
            /**
             * Waits for the splash delay, then closes the splash on the EDT.
             **/
            public void run() {
                try {
                    Thread.sleep(4000);     // 4 secs
                    SwingUtilities.invokeAndWait(closerRunner);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    // can catch InvocationTargetException
                    // can catch InterruptedException
                }
            }
        };

        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.setPriority(Thread.MIN_PRIORITY);
        splashThread.start();
    }

}