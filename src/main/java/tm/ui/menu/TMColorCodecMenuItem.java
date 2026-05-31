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

import tm.colorcodecs.ColorCodec;
import javax.swing.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Menu item that represents a color codec.
 **/
public class TMColorCodecMenuItem extends JRadioButtonMenuItem {

	private final ColorCodec codec;

	/**
	 * Creates a menu item for the given color codec.
	 * @param codec color codec used for palette encode/decode
	 * @param onSelect callback invoked when the user selects this codec
	 **/
	public TMColorCodecMenuItem(ColorCodec codec, Consumer<ColorCodec> onSelect) {
		super(codec.getDescription());
		this.codec = codec;
		addActionListener(new ActionListener() {
			/**
			 * Forwards the selection to the color codec command handler.
			 * @param e event from the AWT/Swing listener
			 **/
			public void actionPerformed(ActionEvent e) {
				onSelect.accept(((TMColorCodecMenuItem) e.getSource()).getCodec());
				setSelected(true);
			}
		});
	}

	/**
	 * Gets the codec that the menu item represents.
	 * @return active color codec
	 **/
	public ColorCodec getCodec() {
		return codec;
	}
}
