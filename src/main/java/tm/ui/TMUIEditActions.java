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

import tm.*;
import tm.canvases.*;
import tm.modaldialog.TMGoToDialog;
import javax.swing.*;
import java.io.*;

/** Edit-menu command handlers for {@link TMUI}. */
public class TMUIEditActions {

	private final TMUI ui;

	public TMUIEditActions(TMUI ui) {
		this.ui = ui;
	}

	public void doUndoCommand() {
		ui.withSelectedView(view -> {
			view.undo();
			ui.refreshUndoRedo();
			ui.fileImageModified(view.getFileImage());
		});
	}

	public void doRedoCommand() {
		ui.withSelectedView(view -> {
			view.redo();
			ui.refreshUndoRedo();
			ui.fileImageModified(view.getFileImage());
		});
	}

	public void doCutCommand() {
		ui.withSelectedView(view -> {
			ui.copiedSelection = view.getEditorCanvas().cutSelection();
			ui.pasteButton.setEnabled(true);
			ui.pasteMenuItem.setEnabled(true);
		});
	}

	public void doCopyCommand() {
		ui.withSelectedView(view -> {
			ui.copiedSelection = view.getEditorCanvas().copySelection();
			ui.pasteButton.setEnabled(true);
			ui.pasteMenuItem.setEnabled(true);
		});
	}

	public void doPasteCommand() {
		ui.withSelectedView(view -> {
			if (ui.copiedSelection != null) {
				view.getEditorCanvas().paste(ui.copiedSelection);
			}
		});
	}

	public void doClearCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().clearSelection());
	}

	public void doGoToCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.goToDialog.showDialog();
			if (retVal == JOptionPane.OK_OPTION) {
				if (ui.goToDialog.getMode() == TMGoToDialog.ABSOLUTE_MODE) {
					view.setAbsoluteOffset(ui.goToDialog.getOffset());
				} else {
					view.setRelativeOffset(ui.goToDialog.getOffset());
				}
				view.repaint();
			}
		});
	}

	public void doGoToAgainCommand() {
		ui.withSelectedView(view -> {
			if (ui.goToDialog.getMode() == TMGoToDialog.ABSOLUTE_MODE) {
				view.setAbsoluteOffset(ui.goToDialog.getOffset());
			} else {
				view.setRelativeOffset(ui.goToDialog.getOffset());
			}
			view.repaint();
		});
	}

	public void doSelectAllCommand() {
		ui.withSelectedView(view -> view.getEditorCanvas().selectAll());
	}

	public boolean exportSelectionAs() {
		TMView view = ui.getSelectedView();
		if (view == null) {
			return false;
		}
		ui.bitmapSaveChooser.setFileFilter(ui.bmf.bmp);
		int retVal = ui.bitmapSaveChooser.showSaveDialog(ui);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = ui.bitmapSaveChooser.getSelectedFile();
			try {
				TMBitmapExporter.saveTileCanvasToFile(view.getEditorCanvas().getSelectionCanvas(), file);
				// Keep selection and palette in sync after export.
				view.refreshPaletteDisplay();
				return true;
			} catch (IOException e) {
				ui.showError("Save_Bitmap_Error", e);
				return false;
			}
		}
		return false;
	}

	public void doCopyToCommand() {
		ui.exportSelectionAs();
	}

	public void doCutAsCommand() {
		if (ui.exportSelectionAs()) {
			doCutCommand();
		}
	}

	public void doPasteFromCommand() {
		ui.withSelectedView(view -> {
			if (new File(ui.lastPath).exists()) {
				ui.bitmapOpenChooser.setCurrentDirectory(new File(ui.lastPath));
			} else {
				ui.bitmapOpenChooser.setCurrentDirectory(new File("."));
			}
			int retVal = ui.bitmapOpenChooser.showOpenDialog(ui);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = ui.bitmapOpenChooser.getSelectedFile();
				TMTileCanvas bitmapCanvas;
				try {
					bitmapCanvas = TMBitmapImporter.loadTileCanvasFromFile(file);
				} catch (InterruptedException | IOException e) {
					ui.showError("Load_Bitmap_Error", e);
					return;
				}
				TMSelectionCanvas selCanvas = new TMSelectionCanvas(
						ui, bitmapCanvas, 0, 0,
						bitmapCanvas.getCols(),
						bitmapCanvas.getRows());
				view.getEditorCanvas().paste(selCanvas);
			}
		});
	}

}
