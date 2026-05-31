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

import tm.treenodes.BookmarkItemNode;
import javax.swing.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Menu item that represents a bookmark.
 **/
public class TMBookmarkMenuItem extends JMenuItem {

	private final BookmarkItemNode bookmark;

	/**
	 * Creates a menu item for the given bookmark.
	 * @param bookmark bookmark node to jump to
	 * @param onSelect callback invoked when the user selects this bookmark
	 **/
	public TMBookmarkMenuItem(BookmarkItemNode bookmark, Consumer<BookmarkItemNode> onSelect) {
		super(bookmark.getDescription());
		this.bookmark = bookmark;
		addActionListener(new ActionListener() {
			/**
			 * Forwards the selection to the goto bookmark command handler.
			 * @param e event from the AWT/Swing listener
			 **/
			public void actionPerformed(ActionEvent e) {
				onSelect.accept(((TMBookmarkMenuItem) e.getSource()).getBookmark());
			}
		});
		setToolTipText(bookmark.getToolTipText());
	}

	/**
	 * Gets the bookmark.
	 * @return bookmark
	 **/
	public BookmarkItemNode getBookmark() {
		return bookmark;
	}
}
