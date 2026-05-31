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

import javax.swing.*;
import java.awt.*;

/**
 * Toolbar toggle button with compact insets around its icon or text.
 **/
public class TMToolToggleButton extends JToggleButton {

	private Insets insets;

	/**
	 * Creates a toolbar toggle button with the given label.
	 * @param text button label
	 **/
	public TMToolToggleButton(String text) {
		super(text);
		if (TMUI.isWindows) {
			insets = new Insets(6, 6, 8, 8);
		} else {
			insets = new Insets(4, 4, 5, 5);
		}
	}

	/**
	 * Creates a toolbar toggle button with the given icon.
	 * @param icon button icon
	 **/
	public TMToolToggleButton(ImageIcon icon) {
		super(icon);
		if (TMUI.isWindows) {
			insets = new Insets(8, 8, 8, 8);
		} else {
			insets = new Insets(6, 6, 7, 7);
		}
	}

	/**
	 * Gets the insets.
	 * @return insets
	 **/
	@Override
	public Insets getInsets() {
		return insets;
	}
}
