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

/**
 * Allows undo/redo for applying a selection to the tile data.
 **/
public class ReversibleApplySelectionAction extends ReversibleAction {
    private TMSelectionCanvas selection;
    private TMEditorCanvas owner;
    private ReversibleTileModifyAction modifiedTiles;

    /**
     * Captures the tile modifications made when the selection is applied.
     * @param selection selection being applied
     * @param owner editor canvas receiving the applied pixels
     **/
    public ReversibleApplySelectionAction(TMSelectionCanvas selection, TMEditorCanvas owner)
    {
        super("Apply_Selection");
        this.selection = selection;
        this.owner = owner;
        this.modifiedTiles = owner.encodeSelection(false);
    }

    /**
     * Restores tile data and redisplays the selection.
     **/
    public void undo()
    {
        int x = selection.getX() / selection.getScaledTileDim();
        int y = selection.getY() / selection.getScaledTileDim();
        modifiedTiles.undo();
        owner.showSelection(selection, x, y);
    }

    /**
     * Re-applies the selection encoding to the tile data.
     **/
    public void redo()
    {
        owner.encodeSelection(false);
    }

    /**
     * Apply selection can always be undone.
     * @return {@code true}
     **/
    public boolean canUndo()
    {
        return true;
    }

    /**
     * Apply selection can always be redone.
     * @return {@code true}
     **/
    public boolean canRedo()
    {
        return true;
    }
}
