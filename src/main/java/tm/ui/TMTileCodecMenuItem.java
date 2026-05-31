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

import tm.tilecodecs.TileCodec;
import javax.swing.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Menu item that represents a tile codec.
 **/
public class TMTileCodecMenuItem extends JRadioButtonMenuItem {

	private final TileCodec codec;

	/**
	 * Creates a menu item for the given tile codec.
	 * @param codec tile codec used for encode/decode
	 * @param onSelect callback invoked when the user selects this codec
	 **/
	public TMTileCodecMenuItem(TileCodec codec, Consumer<TileCodec> onSelect) {
		super(codec.getDescription());
		this.codec = codec;
		addActionListener(new ActionListener() {
			/**
			 * Forwards the selection to the tile codec command handler.
			 * @param e event from the AWT/Swing listener
			 **/
			public void actionPerformed(ActionEvent e) {
				onSelect.accept(((TMTileCodecMenuItem) e.getSource()).getCodec());
				setSelected(true);
			}
		});
	}

	/**
	 * Gets the codec that the menu item represents.
	 * @return active tile codec
	 **/
	public TileCodec getCodec() {
		return codec;
	}
}
