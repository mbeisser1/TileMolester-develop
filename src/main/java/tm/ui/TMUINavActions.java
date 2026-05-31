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

/** Navigation and bookmark command handlers for {@link TMUI}. */
public class TMUINavActions {

	private final TMUI ui;

	public TMUINavActions(TMUI ui) {
		this.ui = ui;
	}

	public void doHomeCommand() {
		ui.withSelectedView(view -> view.setAbsoluteOffset(view.getMinOffset()));
	}

	public void doMinusPageCommand() {
		ui.withSelectedView(view -> view.setRelativeOffset(-view.getEditorCanvas().getPageIncrement()));
	}

	public void doMinusRowCommand() {
		ui.withSelectedView(view -> view.setRelativeOffset(-view.getEditorCanvas().getRowIncrement()));
	}

	public void doMinusTileCommand() {
		ui.withSelectedView(view -> view.setRelativeOffset(-view.getEditorCanvas().getTileIncrement()));
	}

	public void doMinusByteCommand() {
		ui.adjustOffset(-1);
	}

	public void doPlusByteCommand() {
		ui.adjustOffset(1);
	}

	public void doPlusTileCommand() {
		ui.withSelectedView(view -> view.setRelativeOffset(view.getEditorCanvas().getTileIncrement()));
	}

	public void doPlusRowCommand() {
		ui.withSelectedView(view -> view.setRelativeOffset(view.getEditorCanvas().getRowIncrement()));
	}

	public void doPlusPageCommand() {
		ui.withSelectedView(view -> view.setRelativeOffset(view.getEditorCanvas().getPageIncrement()));
	}

	public void doEndCommand() {
		ui.withSelectedView(view -> view.setRelativeOffset(view.getMaxOffset()));
	}

	public void doAddToBookmarksCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.addBookmarkDialog.showDialog(view.getFileImage().getResources().getBookmarksRoot());
			if (retVal == JOptionPane.OK_OPTION) {
				FolderNode folder = ui.addBookmarkDialog.getFolder();
				BookmarkItemNode bookmark = view.createBookmark(ui.addBookmarkDialog.getDescription());
				folder.add(bookmark);
				ui.refreshBookmarksMenu();
			}
		});
	}

	public void doOrganizeBookmarksCommand() {
		ui.withSelectedView(view -> {
			ui.organizeBookmarksDialog.showDialog(view.getFileImage().getResources().getBookmarksRoot());
			ui.refreshBookmarksMenu();
		});
	}

	public void doGotoBookmarkCommand(BookmarkItemNode bookmark) {
		ui.withSelectedView(view -> view.gotoBookmark(bookmark));
	}

}
