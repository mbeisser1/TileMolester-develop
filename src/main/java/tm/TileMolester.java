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

package tm;

import tm.ui.TMUI;
import tm.ui.settings.TMSettings;
import tm.ui.settings.TMTheme;
import tm.utils.TMLog;

import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Toolkit;
import com.formdev.flatlaf.util.SystemInfo;


/**
 * Tile Molester main class.
 * A quite pointless class really. The application is very UI-centric,
 * so the TMUI class evolved into the real application backbone.
 * This class just gets the show started.
 **/
public class TileMolester {

	public static TMSettings settings;

	/**
	 * Constructor.
	 * Initializes platform-specific settings and starts the main UI.
	 **/
	public TileMolester() {
		if (SystemInfo.isMacOS) {
			System.setProperty( "apple.awt.application.appearance", "system" );
			System.setProperty( "apple.laf.useScreenMenuBar", "true" );
			System.setProperty( "apple.awt.application.name", "Tile Molester" );

			final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
			final Image macIcon = defaultToolkit.getImage(getClass().getClassLoader().getResource("icons/TMIcon_dock.png"));
			final Taskbar taskbar = Taskbar.getTaskbar();
	
			try {
				taskbar.setIconImage(macIcon);
			} catch (final UnsupportedOperationException e) {
				TMLog.warning("taskbar.setIconImage not supported", e);
			} catch (final SecurityException e) {
				TMLog.warning("Security exception for taskbar.setIconImage", e);
			}
		}
		
		settings = new TMSettings();
		new TMTheme();
		new TMUI();
	}

	/**
	 * Starts up the program.
	 * @param args command-line arguments (currently unused)
	 **/
	public static void main(String[] args) {
		TMLog.init();
		new TileMolester();
	}

}