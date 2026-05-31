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

import tm.treenodes.*;
import javax.swing.*;

/**
 * Builds dynamic bookmark and palette submenus.
 **/
public class TMUITreeMenuBuilder {

	private final TMUI ui;

	public TMUITreeMenuBuilder(TMUI ui) {
		this.ui = ui;
	}

	public void buildBookmarksMenu(FolderNode root) {
		// remove old bookmark menuitems, if any
		while (ui.navigateMenu.getItemCount() > 5) {
			ui.navigateMenu.remove(5);
		}

		TMTreeNode[] children = root.getChildren();
		if (children.length == 0) {
			// no bookmarks exist
		} else {
			// add all the bookmarks
			ui.navigateMenu.addSeparator();
			for (int i = 0; i < children.length; i++) {
				addToBookmarksMenu(children[i], ui.navigateMenu);
			}
		}
	}

	public void addToBookmarksMenu(TMTreeNode node, JMenu menu) {
		if (node instanceof BookmarkItemNode) {
			menu.add(new TMBookmarkMenuItem((BookmarkItemNode) node, ui::doGotoBookmarkCommand));
		} else {
			// folder
			JMenu subMenu = new JMenu(node.toString());
			TMTreeNode[] children = node.getChildren();
			if (children.length == 0) {
				// no bookmarks exist in this folder
				JMenuItem emptyItem = new JMenuItem("(" + ui.xlate("Empty") + ")");
				emptyItem.setEnabled(false);
				subMenu.add(emptyItem);
			} else {
				// add all the child bookmarks/folders
				for (int i = 0; i < children.length; i++) {
					addToBookmarksMenu(children[i], subMenu);
				}
			}
			menu.add(subMenu);
		}
	}

	public void buildPalettesMenu(FolderNode root) {
		// remove old palette menuitems, if any
		while (ui.paletteMenu.getItemCount() > 10) {
			ui.paletteMenu.remove(10);
		}

		ui.paletteButtonHashtable.clear();
		ui.paletteButtonGroup = new ButtonGroup();

		TMTreeNode[] children = root.getChildren();
		if (children.length == 0) {
			// no palettes exist (shouldn't be possible)
		} else {
			// add all the palettes
			ui.paletteMenu.addSeparator();
			for (int i = 0; i < children.length; i++) {
				addToPalettesMenu(children[i], ui.paletteMenu);
			}
		}
		ui.paletteButtonGroup.add(ui.dummyPaletteMenuItem);
	}

	public void addToPalettesMenu(TMTreeNode node, JMenu menu) {
		if (node instanceof PaletteItemNode) {
			// palette
			PaletteItemNode paletteNode = (PaletteItemNode) node;
			TMPaletteMenuItem paletteMenuItem = new TMPaletteMenuItem(paletteNode, ui::doSelectPaletteCommand);
			menu.add(paletteMenuItem);
			ui.paletteButtonGroup.add(paletteMenuItem);
			ui.paletteButtonHashtable.put(paletteNode.getPalette(), paletteMenuItem);
		} else {
			// folder
			JMenu subMenu = new JMenu(node.toString());
			TMTreeNode[] children = node.getChildren();
			if (children.length == 0) {
				// no palettes exist in this folder
				JMenuItem emptyItem = new JMenuItem("(" + ui.xlate("Empty") + ")");
				emptyItem.setEnabled(false);
				subMenu.add(emptyItem);
			} else {
				// add all the child palettes/folders
				for (int i = 0; i < children.length; i++) {
					addToPalettesMenu(children[i], subMenu);
				}
			}
			menu.add(subMenu);
		}
	}
}
