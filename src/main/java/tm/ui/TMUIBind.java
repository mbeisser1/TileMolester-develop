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

/**
 * Binds Swing menu and toolbar controls to command handlers.
 **/
public final class TMUIBind {

	private TMUIBind() {
	}

	/**
	 * Binds a menu item to a command handler.
	 * @param item menu item to wire
	 * @param action command to run when selected
	 **/
	public static void bind(JMenuItem item, Runnable action) {
		item.addActionListener(e -> action.run());
	}

	/**
	 * Binds an abstract button to a command handler.
	 * @param button button to wire
	 * @param action command to run when clicked
	 **/
	public static void bind(AbstractButton button, Runnable action) {
		button.addActionListener(e -> action.run());
	}

	/**
	 * Adds a toolbar button with tooltip text and command handler.
	 * @param bar toolbar to add the button to
	 * @param button button to configure and add
	 * @param toolTip tooltip text
	 * @param action command to run when clicked
	 **/
	public static void addToolBarButton(JToolBar bar, AbstractButton button, String toolTip, Runnable action) {
		button.setToolTipText(toolTip);
		button.setFocusable(false);
		bind(button, action);
		bar.add(button);
	}
}
