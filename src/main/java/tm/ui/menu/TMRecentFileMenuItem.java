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

package tm.ui.menu;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.function.Consumer;

/**
 * Menu item that represents a recently opened (closed) file.
 **/
public class TMRecentFileMenuItem extends JMenuItem {

	private final File recentFile;

	/**
	 * Creates a menu item for the given recent file.
	 * @param recentFile file to reopen
	 * @param onSelect callback invoked when the user selects this file
	 **/
	public TMRecentFileMenuItem(File recentFile, Consumer<File> onSelect) {
		super(recentFile.getName());
		setToolTipText(recentFile.getAbsolutePath());
		this.recentFile = recentFile;
		addActionListener(new ActionListener() {
			/**
			 * Forwards the selection to the reopen command handler.
			 * @param e event from the AWT/Swing listener
			 **/
			public void actionPerformed(ActionEvent e) {
				onSelect.accept(((TMRecentFileMenuItem) e.getSource()).getRecentFile());
			}
		});
	}

	/**
	 * Gets the recent file.
	 * @return recent file
	 **/
	public File getRecentFile() {
		return recentFile;
	}
}
