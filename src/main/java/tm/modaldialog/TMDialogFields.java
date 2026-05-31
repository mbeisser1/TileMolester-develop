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

package tm.modaldialog;

import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * Sizing helpers for modal dialog text fields (avoids collapsed inputs on Mac/Windows).
 **/
public final class TMDialogFields {

	private static final int FIELD_HEIGHT = 26;

	private TMDialogFields() {
	}

	/**
	 * Sets column count and minimum/preferred size so the field stays visible and editable.
	 **/
	public static void configure(JTextField field, int columns) {
		field.setColumns(columns);
		int width = Math.max(72, columns * 10);
		Dimension dim = new Dimension(width, FIELD_HEIGHT);
		field.setMinimumSize(dim);
		field.setPreferredSize(dim);
	}
}
