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
				ui.widgets.menuBar.remove(ui.widgets.helpMenu);
				ui.widgets.menuBar.add(ui.widgets.editMenu);
				ui.widgets.menuBar.add(ui.widgets.viewMenu);
				ui.widgets.menuBar.add(ui.widgets.imageMenu);
				ui.widgets.menuBar.add(ui.widgets.navigateMenu);
				ui.widgets.menuBar.add(ui.widgets.paletteMenu);
				ui.widgets.menuBar.add(ui.widgets.windowMenu);
				ui.widgets.menuBar.add(ui.widgets.helpMenu);
				// Show File menu items
				ui.widgets.closeMenuItem.setVisible(true);
				ui.widgets.closeAllMenuItem.setVisible(true);
				ui.widgets.saveMenuItem.setVisible(true);
				ui.widgets.saveAsMenuItem.setVisible(true);
				ui.widgets.saveAllMenuItem.setVisible(true);
				ui.widgets.saveAllMenuItem.setEnabled(false);
		
				Component[] menuComponents = ui.widgets.fileMenu.getMenuComponents();
				if (menuComponents.length >= 6) {
		            menuComponents[5].setVisible(true);
		        }
		
				// TODO: Enable previously hidden menu items w/ key accelerators
				// Show Toolbar buttons
				ui.widgets.saveButton.setVisible(true);
				ui.widgets.cutButton.setVisible(true);
				ui.widgets.copyButton.setVisible(true);
				ui.widgets.pasteButton.setVisible(true);
				ui.widgets.undoButton.setVisible(true);
				ui.widgets.redoButton.setVisible(true);
				ui.widgets.gotoButton.setVisible(true);
				ui.widgets.addBookmarkButton.setVisible(true);
				ui.widgets.decWidthButton.setVisible(true);
				ui.widgets.incWidthButton.setVisible(true);
				ui.widgets.decHeightButton.setVisible(true);
				ui.widgets.incHeightButton.setVisible(true);
				// disable some buttons
				ui.fileActions.save.setEnabled(false);
				ui.fileActions.saveAll.setEnabled(false);
				ui.editActions.paste.setEnabled(false);
				ui.editActions.undo.setEnabled(false);
				ui.editActions.redo.setEnabled(false);
				// Show navigation bar
				ui.widgets.navBar.setVisible(true);
				// Show tool pane
				ui.widgets.toolPane.setVisible(true);
				// Maybe show statusbar
				ui.widgets.statusBar.setVisible(ui.viewStatusBar);
				// show bottom pane
				ui.widgets.bottomPane.setVisible(true);
				} else {
		// Hide MDI menus
				ui.widgets.menuBar.remove(ui.widgets.editMenu);
				ui.widgets.menuBar.remove(ui.widgets.viewMenu);
				ui.widgets.menuBar.remove(ui.widgets.imageMenu);
				ui.widgets.menuBar.remove(ui.widgets.navigateMenu);
				ui.widgets.menuBar.remove(ui.widgets.paletteMenu);
				ui.widgets.menuBar.remove(ui.widgets.windowMenu);
				// Hide some File menu items
				ui.widgets.closeMenuItem.setVisible(false);
				ui.widgets.closeAllMenuItem.setVisible(false);
				ui.widgets.saveMenuItem.setVisible(false);
				ui.widgets.saveAsMenuItem.setVisible(false);
				ui.widgets.saveAllMenuItem.setVisible(false);
		
		
				Component[] menuComponents = ui.widgets.fileMenu.getMenuComponents();
				if (menuComponents.length >= 6) {
		            menuComponents[5].setVisible(false);
		        }
		
				// Hide some Toolbar buttons
				ui.widgets.saveButton.setVisible(false);
				ui.widgets.cutButton.setVisible(false);
				ui.widgets.copyButton.setVisible(false);
				ui.widgets.pasteButton.setVisible(false);
				ui.widgets.undoButton.setVisible(false);
				ui.widgets.redoButton.setVisible(false);
				ui.widgets.gotoButton.setVisible(false);
				ui.widgets.addBookmarkButton.setVisible(false);
				ui.widgets.decWidthButton.setVisible(false);
				ui.widgets.incWidthButton.setVisible(false);
				ui.widgets.decHeightButton.setVisible(false);
				ui.widgets.incHeightButton.setVisible(false);
				// Hide navigation bar
				ui.widgets.navBar.setVisible(false);
				// Hide tool pane
				ui.widgets.toolPane.setVisible(false);
				// hide bottom pane
				ui.widgets.bottomPane.setVisible(false);
				}
	}

	public void refreshBlockSizeSelection(TMView view) {
		ui.widgets.sizeBlockToCanvasMenuItem.setSelected(view.getSizeBlockToCanvas());
	}

	public void refreshModeSelection(TMView view) {
		// select the correct mode menu item
		if (view.getMode() == TileCodec.MODE_1D) {
			ui.widgets._1DimensionalMenuItem.setSelected(true);
		} else {
			ui.widgets._2DimensionalMenuItem.setSelected(true);
		}
	}

	public void refreshTileCodecSelection(TMView view) {
		ui.widgets.tileCodecButtonHashtable.get(view.getTileCodec()).setSelected(true);
	}

	public void refreshPalettePane() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.widgets.palettePane.viewSelected(view);
		}
	}

	public void refreshUndoRedo() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.setUndoButtonsEnabled(view.canUndo());
			if (view.canUndo()) {
				ui.widgets.undoMenuItem.setText(ui.xlate("Undo") + " " + ui.xlate(view.getFirstUndoableAction().getPresentationName()));
			} else {
				ui.widgets.undoMenuItem.setText(ui.xlate("Cant_Undo"));
			}
			ui.widgets.undoButton.setToolTipText(ui.widgets.undoMenuItem.getText());

			ui.setRedoButtonsEnabled(view.canRedo());
			if (view.canRedo()) {
				ui.widgets.redoMenuItem.setText(ui.xlate("Redo") + " " + ui.xlate(view.getFirstRedoableAction().getPresentationName()));
			} else {
				ui.widgets.redoMenuItem.setText(ui.xlate("Cant_Redo"));
			}
			ui.widgets.redoButton.setToolTipText(ui.widgets.redoMenuItem.getText());
		}
	}

	public void refreshStatusBar() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.widgets.statusBar.viewSelected(view);
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
		TMPaletteMenuItem item = ui.widgets.paletteButtonHashtable.get(view.getPalette());
		if (item != null) {
			item.setSelected(true);
		} else {
			ui.widgets.dummyPaletteMenuItem.setSelected(true);
		}
	}

	public void refreshPaletteEndiannessSelection(TMView view) {
		if (view.getPalette().getEndianness() == ColorCodec.LITTLE_ENDIAN) {
			ui.widgets.paletteLittleEndianMenuItem.setSelected(true);
		} else {
			ui.widgets.paletteBigEndianMenuItem.setSelected(true);
		}
	}

	public void refreshColorCodecSelection(TMView view) {
		ui.widgets.colorCodecButtonHashtable.get(view.getPalette().getCodec()).setSelected(true);
	}

}
