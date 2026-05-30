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

package tm.reversibleaction;

import tm.treenodes.*;

/**
 * Allows undo/redo of a bookmark addition.
 **/
public class ReversibleAddBookmarkAction extends ReversibleAction {

    private BookmarkItemNode bookmark;
    private FolderNode folder;

    /**
     * Records the bookmark and its parent folder for undo/redo.
     * @param bookmark bookmark node that was added
     **/
    public ReversibleAddBookmarkAction(BookmarkItemNode bookmark) {
        super("Add Bookmark");  // i18n
        this.bookmark = bookmark;
        this.folder = (FolderNode)bookmark.getParent();
    }

    /**
     * Removes the bookmark from its parent folder.
     **/
    public void undo() {
        folder.remove(bookmark);
        // ui.buildBookmarksMenu();
    }

    /**
     * Re-adds the bookmark to its parent folder.
     **/
    public void redo() {
        folder.add(bookmark);
        // ui.buildBookmarksMenu();
    }

    /**
     * Add bookmark can always be undone.
     * @return {@code true}
     **/
    public boolean canUndo() { return true; }

    /**
     * Add bookmark can always be redone.
     * @return {@code true}
     **/
    public boolean canRedo() { return true; }

}
