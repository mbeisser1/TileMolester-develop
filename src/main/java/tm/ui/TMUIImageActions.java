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
import javax.swing.*;

/** Image menu command handlers for {@link TMUI}. */
public class TMUIImageActions extends TMUICommandGroup {
	public final Action mirror;
	public final Action flip;
	public final Action rotateRight;
	public final Action rotateLeft;
	public final Action shiftLeft;
	public final Action shiftRight;
	public final Action shiftUp;
	public final Action shiftDown;
	public final Action stretch;
	public final Action canvasSize;

	public TMUIImageActions(TMUI ui) {
		super(ui);
		mirror = command(this::doMirrorCommand);
		flip = command(this::doFlipCommand);
		rotateRight = command(this::doRotateRightCommand);
		rotateLeft = command(this::doRotateLeftCommand);
		shiftLeft = command(this::doShiftLeftCommand);
		shiftRight = command(this::doShiftRightCommand);
		shiftUp = command(this::doShiftUpCommand);
		shiftDown = command(this::doShiftDownCommand);
		stretch = command(this::doStretchCommand);
		canvasSize = command(this::doCanvasSizeCommand);
	}


	public void doMirrorCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().flipSelectionHorizontally());
	}

	public void doFlipCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().flipSelectionVertically());
	}

	public void doRotateRightCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().rotateSelectionClockwise());
	}

	public void doRotateLeftCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().rotateSelectionCounterClockwise());
	}

	public void doShiftLeftCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().shiftSelectionLeft());
	}

	public void doShiftRightCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().shiftSelectionRight());
	}

	public void doShiftUpCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().shiftSelectionUp());
	}

	public void doShiftDownCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().shiftSelectionDown());
	}

	public void doStretchCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.widgets.stretchDialog.showDialog(view.getEditorCanvas().getSelectionCanvas().getCols(),
					view.getEditorCanvas().getSelectionCanvas().getRows());
			if (retVal == JOptionPane.OK_OPTION) {
				view.getEditorCanvas().stretchSelection(ui.widgets.stretchDialog.getCols(), ui.widgets.stretchDialog.getRows());
			}
		});
	}

	public void doCanvasSizeCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.widgets.canvasSizeDialog.showDialog(view.getCols(), view.getRows());
			if (retVal == JOptionPane.OK_OPTION) {
				view.setGridSize(ui.widgets.canvasSizeDialog.getCols(), ui.widgets.canvasSizeDialog.getRows());
				view.setScale(view.getScale());
			}
		});
	}

}
