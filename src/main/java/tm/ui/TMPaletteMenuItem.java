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

import tm.TMPalette;
import tm.treenodes.PaletteItemNode;
import javax.swing.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Menu item that represents a palette.
 **/
public class TMPaletteMenuItem extends JRadioButtonMenuItem {

	private final PaletteItemNode paletteNode;

	/**
	 * Creates a menu item for the given palette node.
	 * @param paletteNode palette tree node
	 * @param onSelect callback invoked when the user selects this palette
	 **/
	public TMPaletteMenuItem(PaletteItemNode paletteNode, Consumer<TMPalette> onSelect) {
		super(paletteNode.getDescription());
		this.paletteNode = paletteNode;
		addActionListener(new ActionListener() {
			/**
			 * Forwards the selection to the select palette command handler.
			 * @param e event from the AWT/Swing listener
			 **/
			public void actionPerformed(ActionEvent e) {
				onSelect.accept(((TMPaletteMenuItem) e.getSource()).getPalette());
			}
		});
		setToolTipText(paletteNode.getToolTipText());
	}

	/**
	 * Gets the palette.
	 * @return active palette
	 **/
	public TMPalette getPalette() {
		return paletteNode.getPalette();
	}
}
