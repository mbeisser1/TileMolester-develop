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
import tm.canvases.TMTileCanvas;
import tm.treenodes.BookmarkItemNode;

/**
 * Allows undo/redo of making a new selection.
 * Undo is two-phase when no float is visible: reveal, then clear.
 **/
public class ReversibleNewSelectionAction extends ReversibleAction {
    private static final String NAME_NEW = "New_Selection";
    private static final String NAME_SHOW = "Show_Selection";
    private static final String NAME_CLEAR = "Clear_Selection";

    private final TMSelectionCanvas newSelection;
    private final TMEditorCanvas owner;
    private final BookmarkItemNode bookmark;
    private final int newX, newY;

    private boolean awaitingClearUndo;

    /**
     * Records the new selection and its grid position for undo/redo.
     * @param newSelection selection canvas that was created
     * @param owner editor canvas that received the selection
     **/
    public ReversibleNewSelectionAction(TMSelectionCanvas newSelection, TMEditorCanvas owner) {
        super(NAME_NEW);   // i18n
        this.newSelection = newSelection;
        this.owner = owner;
        this.bookmark = owner.getView().createBookmark("");
        newX = newSelection.getX() / newSelection.getScaledTileDim();
        newY = newSelection.getY() / newSelection.getScaledTileDim();
    }

    /**
     * Whether the first undo phase (reveal) has run and a second undo should clear.
     * @return true if the selection was shown by undo and is awaiting clear
     **/
    public boolean isAwaitingClearUndo() {
        return awaitingClearUndo;
    }

    /**
     * @param sel visible selection canvas
     * @return whether this action owns the given floating selection
     **/
    public boolean ownsSelection(TMSelectionCanvas sel) {
        return sel == newSelection;
    }

    /**
     * Navigates to the selection and shows the floating marquee.
     **/
    public void undoReveal() {
        owner.getView().gotoBookmark(bookmark);
        owner.reattachFloatingSelection(newSelection, newX, newY);
        awaitingClearUndo = true;
        owner.repaint();
    }

    /**
     * Restores tiles under the selection and removes the float.
     **/
    public void undoClear() {
        owner.getView().gotoBookmark(bookmark);
        owner.cancelFloatingSelection(newSelection, newX, newY);
        awaitingClearUndo = false;
        owner.repaint();
    }

    /**
     * Re-creates the floating selection.
     **/
    public void redo() {
        owner.getView().gotoBookmark(bookmark);
        owner.reattachFloatingSelection(newSelection, newX, newY);
        awaitingClearUndo = false;
        owner.repaint();
    }

    /**
     * Not used directly; {@link tm.ui.view.TMView#undo()} drives two-phase undo.
     **/
    @Override
    public void undo() {
        if (awaitingClearUndo) {
            undoClear();
        } else {
            undoReveal();
        }
    }

    @Override
    public String getPresentationName() {
        if (awaitingClearUndo) {
            return NAME_CLEAR;
        }
        if (owner.hasSelection()) {
            TMTileCanvas visible = owner.getSelectionCanvas();
            if (visible instanceof TMSelectionCanvas sel && ownsSelection(sel)) {
                return NAME_CLEAR;
            }
        }
        return NAME_SHOW;
    }

    /**
     * New selection can always be undone.
     * @return {@code true}
     **/
    public boolean canUndo() { return true; }

    /**
     * New selection can always be redone.
     * @return {@code true}
     **/
    public boolean canRedo() { return true; }

}
