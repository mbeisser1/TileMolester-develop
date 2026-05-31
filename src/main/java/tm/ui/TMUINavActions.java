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

import tm.ui.TMUI;
import tm.treenodes.*;
import javax.swing.*;

/** Navigation and bookmark command handlers for {@link TMUI}. */
public class TMUINavActions extends TMUICommandGroup {
	public final Action minusPage;
	public final Action plusPage;
	public final Action minusRow;
	public final Action plusRow;
	public final Action minusTile;
	public final Action plusTile;
	public final Action minusByte;
	public final Action plusByte;
	public final Action addToBookmarks;
	public final Action organizeBookmarks;

	public TMUINavActions(TMUI ui) {
		super(ui);
		minusPage = command(this::doMinusPageCommand);
		plusPage = command(this::doPlusPageCommand);
		minusRow = command(this::doMinusRowCommand);
		plusRow = command(this::doPlusRowCommand);
		minusTile = command(this::doMinusTileCommand);
		plusTile = command(this::doPlusTileCommand);
		minusByte = command(this::doMinusByteCommand);
		plusByte = command(this::doPlusByteCommand);
		addToBookmarks = command(this::doAddToBookmarksCommand);
		organizeBookmarks = command(this::doOrganizeBookmarksCommand);
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
			int retVal = ui.widgets.addBookmarkDialog.showDialog(view.getFileImage().getResources().getBookmarksRoot());
			if (retVal == JOptionPane.OK_OPTION) {
				FolderNode folder = ui.widgets.addBookmarkDialog.getFolder();
				BookmarkItemNode bookmark = view.createBookmark(ui.widgets.addBookmarkDialog.getDescription());
				folder.add(bookmark);
				ui.refreshBookmarksMenu();
			}
		});
	}

	public void doOrganizeBookmarksCommand() {
		ui.withSelectedView(view -> {
			ui.widgets.organizeBookmarksDialog.showDialog(view.getFileImage().getResources().getBookmarksRoot());
			ui.refreshBookmarksMenu();
		});
	}

	public void doGotoBookmarkCommand(BookmarkItemNode bookmark) {
		ui.withSelectedView(view -> view.gotoBookmark(bookmark));
	}

}
