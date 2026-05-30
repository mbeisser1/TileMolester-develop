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

import tm.canvases.TMSelectionCanvas;
import tm.canvases.TMEditorCanvas;

/**
 * Allows undo/redo of the Clear operation.
 **/
public class ReversibleClearAction extends ReversibleAction {

    private TMSelectionCanvas selection;
    private TMEditorCanvas owner;
    private int x, y;

    /**
     * Records the cleared selection and its grid position for undo/redo.
     * @param selection selection canvas that was cleared
     * @param owner editor canvas that owned the selection
     **/
    public ReversibleClearAction(TMSelectionCanvas selection, TMEditorCanvas owner) {
        super("Clear Selection");   // i18n
        this.selection = selection;
        this.owner = owner;
        x = selection.getX() / selection.getScaledTileDim();
        y = selection.getY() / selection.getScaledTileDim();
    }

    /**
     * Restores the selection on the editor canvas.
     **/
    public void undo() {
        owner.showSelection(selection, x, y);
    }

    /**
     * Removes the selection from the editor canvas again.
     **/
    public void redo() {
        owner.remove(selection);
        owner.repaint();
    }

    /**
     * Clear can always be undone.
     * @return {@code true}
     **/
    public boolean canUndo() { return true; }

    /**
     * Clear can always be redone.
     * @return {@code true}
     **/
    public boolean canRedo() { return true; }

}
