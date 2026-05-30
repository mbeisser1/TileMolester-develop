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
 * Allows undo/redo for the Paste operation.
 **/
public class ReversiblePasteSelectionAction extends ReversibleAction{
    private TMSelectionCanvas pastedSel;
    private TMEditorCanvas owner;

    /**
     * Records the pasted selection and displays it on the editor canvas.
     * @param pastedSel selection canvas created by paste
     * @param owner editor canvas receiving the pasted selection
     **/
    public ReversiblePasteSelectionAction(TMSelectionCanvas pastedSel, TMEditorCanvas owner)
    {
        super("Paste");
        this.pastedSel = pastedSel;
        this.owner = owner;

        owner.showSelection(pastedSel, 0, 0);
    }

    /**
     * Paste can always be undone.
     * @return {@code true}
     **/
    public boolean canUndo()
    {
        return true;
    }

    /**
     * Paste can always be redone.
     * @return {@code true}
     **/
    public boolean canRedo()
    {
        return true;
    }

    /**
     * Removes the pasted selection from the editor canvas.
     **/
    public void undo()
    {
        owner.remove(pastedSel);
        owner.redraw();
    }

    /**
     * Redisplays the pasted selection on the editor canvas.
     **/
    public void redo()
    {
        owner.showSelection(pastedSel,0,0);
    }
}
