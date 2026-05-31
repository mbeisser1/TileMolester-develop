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

import tm.canvases.TMEditorCanvas;
import tm.canvases.TMSelectionCanvas;
import tm.treenodes.BookmarkItemNode;

/**
 * Allows undo/redo of moving a selection.
 **/
public class ReversibleMoveSelectionAction extends ReversibleAction {

    private TMEditorCanvas owner;
    private TMSelectionCanvas selection;
    private BookmarkItemNode bookmark;
    private int oldX, oldY;
    private int newX, newY;

    /**
     * Records the selection and its old and new grid positions.
     * @param owner editor canvas that owns the selection
     * @param selection selection canvas that was moved
     * @param oldX previous tile column
     * @param oldY previous tile row
     * @param newX new tile column
     * @param newY new tile row
     **/
    public ReversibleMoveSelectionAction(
            TMEditorCanvas owner,
            TMSelectionCanvas selection,
            int oldX,
            int oldY,
            int newX,
            int newY) {
        super("Move Selection");   // i18n
        this.owner = owner;
        this.selection = selection;
        this.bookmark = owner.getView().createBookmark("");
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

    /**
     * Moves the selection back to its previous grid position.
     **/
    public void undo() {
        owner.getView().gotoBookmark(bookmark);
        moveSelectionTo(oldX, oldY);
    }

    /**
     * Moves the selection to its new grid position.
     **/
    public void redo() {
        owner.getView().gotoBookmark(bookmark);
        moveSelectionTo(newX, newY);
    }

    private void moveSelectionTo(int tileX, int tileY) {
        int dim = selection.getScaledTileDim();
        selection.setLocation(tileX * dim, tileY * dim);
        owner.syncParkedMetadataAfterMove(tileX, tileY);
        selection.repaint();
        owner.repaint();
    }

    /**
     * Move can always be undone.
     * @return {@code true}
     **/
    public boolean canUndo() { return true; }

    /**
     * Move can always be redone.
     * @return {@code true}
     **/
    public boolean canRedo() { return true; }

}
