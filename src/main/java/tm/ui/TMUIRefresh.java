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
import tm.colorcodecs.ColorCodec;
import tm.tilecodecs.TileCodec;
import java.awt.*;

/** MDI mode and UI refresh helpers for {@link TMUI}. */
public class TMUIRefresh {

	private final TMUI ui;

	public TMUIRefresh(TMUI ui) {
		this.ui = ui;
	}

	public void setMdiMode(boolean enabled) {
		if (enabled) {
		// Show MDI menus
				ui.menuBar.remove(ui.helpMenu);
				ui.menuBar.add(ui.editMenu);
				ui.menuBar.add(ui.viewMenu);
				ui.menuBar.add(ui.imageMenu);
				ui.menuBar.add(ui.navigateMenu);
				ui.menuBar.add(ui.paletteMenu);
				ui.menuBar.add(ui.windowMenu);
				ui.menuBar.add(ui.helpMenu);
				// Show File menu items
				ui.closeMenuItem.setVisible(true);
				ui.closeAllMenuItem.setVisible(true);
				ui.saveMenuItem.setVisible(true);
				ui.saveAsMenuItem.setVisible(true);
				ui.saveAllMenuItem.setVisible(true);
				ui.saveAllMenuItem.setEnabled(false);
		
				Component[] menuComponents = ui.fileMenu.getMenuComponents();
				if (menuComponents.length >= 6) {
		            menuComponents[5].setVisible(true);
		        }
		
				// TODO: Enable previously hidden menu items w/ key accelerators
				// Show Toolbar buttons
				ui.saveButton.setVisible(true);
				ui.cutButton.setVisible(true);
				ui.copyButton.setVisible(true);
				ui.pasteButton.setVisible(true);
				ui.undoButton.setVisible(true);
				ui.redoButton.setVisible(true);
				ui.gotoButton.setVisible(true);
				ui.addBookmarkButton.setVisible(true);
				ui.decWidthButton.setVisible(true);
				ui.incWidthButton.setVisible(true);
				ui.decHeightButton.setVisible(true);
				ui.incHeightButton.setVisible(true);
				// disable some buttons
				ui.saveButton.setEnabled(false);
				ui.pasteButton.setEnabled(false);
				ui.pasteMenuItem.setEnabled(false);
				ui.undoButton.setEnabled(false);
				ui.redoButton.setEnabled(false);
				// Show navigation bar
				ui.navBar.setVisible(true);
				// Show tool pane
				ui.toolPane.setVisible(true);
				// Maybe show statusbar
				ui.statusBar.setVisible(ui.viewStatusBar);
				// show bottom pane
				ui.bottomPane.setVisible(true);
				} else {
		// Hide MDI menus
				ui.menuBar.remove(ui.editMenu);
				ui.menuBar.remove(ui.viewMenu);
				ui.menuBar.remove(ui.imageMenu);
				ui.menuBar.remove(ui.navigateMenu);
				ui.menuBar.remove(ui.paletteMenu);
				ui.menuBar.remove(ui.windowMenu);
				// Hide some File menu items
				ui.closeMenuItem.setVisible(false);
				ui.closeAllMenuItem.setVisible(false);
				ui.saveMenuItem.setVisible(false);
				ui.saveAsMenuItem.setVisible(false);
				ui.saveAllMenuItem.setVisible(false);
		
		
				Component[] menuComponents = ui.fileMenu.getMenuComponents();
				if (menuComponents.length >= 6) {
		            menuComponents[5].setVisible(false);
		        }
		
				// Hide some Toolbar buttons
				ui.saveButton.setVisible(false);
				ui.cutButton.setVisible(false);
				ui.copyButton.setVisible(false);
				ui.pasteButton.setVisible(false);
				ui.undoButton.setVisible(false);
				ui.redoButton.setVisible(false);
				ui.gotoButton.setVisible(false);
				ui.addBookmarkButton.setVisible(false);
				ui.decWidthButton.setVisible(false);
				ui.incWidthButton.setVisible(false);
				ui.decHeightButton.setVisible(false);
				ui.incHeightButton.setVisible(false);
				// Hide navigation bar
				ui.navBar.setVisible(false);
				// Hide tool pane
				ui.toolPane.setVisible(false);
				// hide bottom pane
				ui.bottomPane.setVisible(false);
				}
	}

	public void refreshBlockSizeSelection(TMView view) {
		ui.sizeBlockToCanvasMenuItem.setSelected(view.getSizeBlockToCanvas());
	}

	public void refreshModeSelection(TMView view) {
		// select the correct mode menu item
		if (view.getMode() == TileCodec.MODE_1D) {
			ui._1DimensionalMenuItem.setSelected(true);
		} else {
			ui._2DimensionalMenuItem.setSelected(true);
		}
	}

	public void refreshTileCodecSelection(TMView view) {
		ui.tileCodecButtonHashtable.get(view.getTileCodec()).setSelected(true);
	}

	public void refreshPalettePane() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.palettePane.viewSelected(view);
		}
	}

	public void refreshUndoRedo() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.setUndoButtonsEnabled(view.canUndo());
			if (view.canUndo()) {
				ui.undoMenuItem.setText(ui.xlate("Undo") + " " + ui.xlate(view.getFirstUndoableAction().getPresentationName()));
			} else {
				ui.undoMenuItem.setText(ui.xlate("Cant_Undo"));
			}
			ui.undoButton.setToolTipText(ui.undoMenuItem.getText());

			ui.setRedoButtonsEnabled(view.canRedo());
			if (view.canRedo()) {
				ui.redoMenuItem.setText(ui.xlate("Redo") + " " + ui.xlate(view.getFirstRedoableAction().getPresentationName()));
			} else {
				ui.redoMenuItem.setText(ui.xlate("Cant_Redo"));
			}
			ui.redoButton.setToolTipText(ui.redoMenuItem.getText());
		}
	}

	public void refreshStatusBar() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.statusBar.viewSelected(view);
		}
	}

	public void refreshBookmarksMenu() {
		TMView view = ui.getSelectedView();
		if (view != null && view.getFileImage().getResources() != null) {
			ui.treeMenuBuilder.buildBookmarksMenu(view.getFileImage().getResources().getBookmarksRoot());
		}
	}

	public void refreshPalettesMenu() {
		TMView view = ui.getSelectedView();
		if (view != null && view.getFileImage().getResources() != null) {
			ui.treeMenuBuilder.buildPalettesMenu(view.getFileImage().getResources().getPalettesRoot());
			refreshPaletteSelection(view);
			refreshPaletteEndiannessSelection(view);
			refreshColorCodecSelection(view);
		}
	}

	public void refreshPaletteSelection(TMView view) {
		TMPaletteMenuItem item = ui.paletteButtonHashtable.get(view.getPalette());
		if (item != null) {
			item.setSelected(true);
		} else {
			ui.dummyPaletteMenuItem.setSelected(true);
		}
	}

	public void refreshPaletteEndiannessSelection(TMView view) {
		if (view.getPalette().getEndianness() == ColorCodec.LITTLE_ENDIAN) {
			ui.paletteLittleEndianMenuItem.setSelected(true);
		} else {
			ui.paletteBigEndianMenuItem.setSelected(true);
		}
	}

	public void refreshColorCodecSelection(TMView view) {
		ui.colorCodecButtonHashtable.get(view.getPalette().getCodec()).setSelected(true);
	}

}
