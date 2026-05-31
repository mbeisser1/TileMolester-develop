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
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Custom button UI that paints no border.
 **/
public class TMCButtonUI extends ButtonUI {

	/**
	 * Creates the custom button UI delegate.
	 * @return UI delegate for the custom button
	 * @param c Swing component requesting the UI delegate
	 **/
	public static ComponentUI createUI(JComponent c) {
		return new TMCButtonUI();
	}

	/**
	 * Paints no border for the custom toolbar button UI.
	 * @param g graphics context used for drawing
	 **/
	public void paintBorder(Graphics g) {
	}
}
