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

import tm.utils.TMTools;
import javax.swing.*;
import java.awt.event.*;

/**
 * Builds Tile Molester toolbars and wires them to {@link TMUI} commands.
 **/
public class TMUIToolbarBuilder {

	private final TMUI ui;

	public TMUIToolbarBuilder(TMUI ui) {
		this.ui = ui;
	}

	public void buildToolBar() {
		// ui.toolBar.setBorder(null);

		// New
		ui.newButton.setToolTipText(ui.newMenuItem.getText());
		ui.newButton.setFocusable(false);
		ui.newButton.addActionListener(e -> { ui.doNewCommand(); });
		ui.toolBar.add(ui.newButton);
		// Open
		ui.openButton.setToolTipText(ui.openMenuItem.getText());
		ui.openButton.setFocusable(false);
		ui.openButton.addActionListener(e -> { ui.doOpenCommand(); });
		ui.toolBar.add(ui.openButton);
		// Save
		ui.saveButton.setToolTipText(ui.saveMenuItem.getText());
		ui.saveButton.setFocusable(false);
		ui.saveButton.addActionListener(e -> { ui.doSaveCommand(); });
		ui.toolBar.add(ui.saveButton);
		//
		ui.toolBarMDI.addSeparator();
		// Cut
		ui.cutButton.setToolTipText(ui.cutMenuItem.getText());
		ui.cutButton.setFocusable(false);
		ui.cutButton.addActionListener(e -> { ui.doCutCommand(); });
		ui.toolBarMDI.add(ui.cutButton);
		// Copy
		ui.copyButton.setToolTipText(ui.copyMenuItem.getText());
		ui.copyButton.setFocusable(false);
		ui.copyButton.addActionListener(e -> { ui.doCopyCommand(); });
		ui.toolBarMDI.add(ui.copyButton);
		// Paste
		ui.pasteButton.setToolTipText(ui.pasteMenuItem.getText());
		ui.pasteButton.setFocusable(false);
		ui.pasteButton.addActionListener(e -> { ui.doPasteCommand(); });
		ui.toolBarMDI.add(ui.pasteButton);
		//
		ui.toolBarMDI.addSeparator();
		// Undo
		ui.undoButton.setToolTipText(ui.undoMenuItem.getText());
		ui.undoButton.setFocusable(false);
		ui.undoButton.addActionListener(e -> { ui.doUndoCommand(); });
		ui.toolBarMDI.add(ui.undoButton);
		// Redo
		ui.redoButton.setToolTipText(ui.redoMenuItem.getText());
		ui.redoButton.setFocusable(false);
		ui.redoButton.addActionListener(e -> { ui.doRedoCommand(); });
		ui.toolBarMDI.add(ui.redoButton);
		//
		ui.toolBarMDI.addSeparator();
		// Go To
		ui.gotoButton.setToolTipText(ui.goToMenuItem.getText());
		ui.gotoButton.setFocusable(false);
		ui.gotoButton.addActionListener(e -> { ui.doGoToCommand(); });
		ui.toolBarMDI.add(ui.gotoButton);
		// Add To Bookmarks...
		ui.addBookmarkButton.setToolTipText(ui.addToBookmarksMenuItem.getText());
		ui.addBookmarkButton.setFocusable(false);
		ui.addBookmarkButton.addActionListener(e -> { ui.doAddToBookmarksCommand(); });
		ui.toolBarMDI.add(ui.addBookmarkButton);
		//
		ui.toolBarMDI.addSeparator();
		// Decrease Width
		ui.decWidthButton.setToolTipText(ui.xlate("Decrease_Width"));
		ui.decWidthButton.setFocusable(false);
		ui.decWidthButton.addActionListener(e -> { ui.doDecreaseWidthCommand(); });
		ui.toolBarMDI.add(ui.decWidthButton);
		// Increase Width
		ui.incWidthButton.setToolTipText(ui.xlate("Increase_Width"));
		ui.incWidthButton.setFocusable(false);
		ui.incWidthButton.addActionListener(e -> { ui.doIncreaseWidthCommand(); });
		ui.toolBarMDI.add(ui.incWidthButton);
		// Decrease Height
		ui.decHeightButton.setToolTipText(ui.xlate("Decrease_Height"));
		ui.decHeightButton.setFocusable(false);
		ui.decHeightButton.addActionListener(e -> { ui.doDecreaseHeightCommand(); });
		ui.toolBarMDI.add(ui.decHeightButton);
		// Increase Height
		ui.incHeightButton.setToolTipText(ui.xlate("Increase_Height"));
		ui.incHeightButton.setFocusable(false);
		ui.incHeightButton.addActionListener(e -> { ui.doIncreaseHeightCommand(); });
		ui.toolBarMDI.add(ui.incHeightButton);
		//
		ui.toolBar.setFocusable(false);
		ui.toolBar.setFloatable(false);
		ui.toolBarMDI.setFocusable(false);
		ui.toolBarMDI.setFloatable(false);
	}

	public void buildNavBar() {
		// ui.navBar.setBorder(null);

		ui.navBar.addSeparator();

		// Page Back
		ui.minusPageButton.setToolTipText(ui.xlate("Page_Back"));
		ui.minusPageButton.setFocusable(false);
		ui.minusPageButton.addActionListener(e -> { ui.doMinusPageCommand(); });
		ui.navBar.add(ui.minusPageButton);
		// Page Forward
		ui.plusPageButton.setToolTipText(ui.xlate("Page_Forward"));
		ui.plusPageButton.setFocusable(false);
		ui.plusPageButton.addActionListener(e -> { ui.doPlusPageCommand(); });
		ui.navBar.add(ui.plusPageButton);
		// Row Back
		ui.minusRowButton.setToolTipText(ui.xlate("Row_Back"));
		ui.minusRowButton.setFocusable(false);
		ui.minusRowButton.addActionListener(e -> { ui.doMinusRowCommand(); });
		ui.navBar.add(ui.minusRowButton);
		// Row Forward
		ui.plusRowButton.setToolTipText(ui.xlate("Row_Forward"));
		ui.plusRowButton.setFocusable(false);
		ui.plusRowButton.addActionListener(e -> { ui.doPlusRowCommand(); });
		ui.navBar.add(ui.plusRowButton);
		// Tile Back
		ui.minusTileButton.setToolTipText(ui.xlate("Tile_Back"));
		ui.minusTileButton.setFocusable(false);
		ui.minusTileButton.addActionListener(e -> { ui.doMinusTileCommand(); });
		ui.navBar.add(ui.minusTileButton);
		// Tile Forward
		ui.plusTileButton.setToolTipText(ui.xlate("Tile_Forward"));
		ui.plusTileButton.setFocusable(false);
		ui.plusTileButton.addActionListener(e -> { ui.doPlusTileCommand(); });
		ui.navBar.add(ui.plusTileButton);
		// Byte Back
		ui.minusByteButton.setToolTipText(ui.xlate("Byte_Back"));
		ui.minusByteButton.setFocusable(false);
		ui.minusByteButton.addActionListener(e -> { ui.doMinusByteCommand(); });
		ui.navBar.add(ui.minusByteButton);
		// Byte Forward
		ui.plusByteButton.setToolTipText(ui.xlate("Byte_Forward"));
		ui.plusByteButton.setFocusable(false);
		ui.plusByteButton.addActionListener(e -> { ui.doPlusByteCommand(); });
		ui.navBar.add(ui.plusByteButton);
		//
		ui.navBar.setFloatable(false);
		ui.navBar.setFocusable(false);
	}

	public void buildToolPalette() {
		ui.toolPalette.setBorder(null);
		// Selection
		ui.selectButton.setToolTipText(ui.xlate("Selection"));
		ui.selectButton.setFocusable(false);
		ui.selectButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.SELECT_TOOL;
						ui.deselectToolPalette();
						ui.selectButton.setSelected(true); });
		ui.toolPalette.add(ui.selectButton);
		// Zoom
		ui.zoomButton.setToolTipText(ui.xlate("Zoom"));
		ui.zoomButton.setFocusable(false);
		ui.zoomButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.ZOOM_TOOL;
						ui.deselectToolPalette();
						ui.zoomButton.setSelected(true); });
		ui.toolPalette.add(ui.zoomButton);
		// Dropper
		ui.pickupButton.setToolTipText(ui.xlate("Dropper"));
		ui.pickupButton.setFocusable(false);
		ui.pickupButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.PICKUP_TOOL;
						ui.deselectToolPalette();
						ui.pickupButton.setSelected(true); });
		ui.toolPalette.add(ui.pickupButton);
		// Brush
		ui.brushButton.setToolTipText(ui.xlate("Brush"));
		ui.brushButton.setFocusable(false);
		ui.brushButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.BRUSH_TOOL;
						ui.deselectToolPalette();
						ui.brushButton.setSelected(true); });
		ui.toolPalette.add(ui.brushButton);
		// Line
		ui.lineButton.setToolTipText(ui.xlate("Line"));
		ui.lineButton.setFocusable(false);
		ui.lineButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.LINE_TOOL;
						ui.deselectToolPalette();
						ui.lineButton.setSelected(true); });
		ui.toolPalette.add(ui.lineButton);
		// Flood Fill
		ui.fillButton.setToolTipText(ui.xlate("Flood_Fill"));
		ui.fillButton.setFocusable(false);
		ui.fillButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.FILL_TOOL;
						ui.deselectToolPalette();
						ui.fillButton.setSelected(true); });
		ui.toolPalette.add(ui.fillButton);
		// Color Replacer
		ui.replaceButton.setToolTipText(ui.xlate("Color_Replacer"));
		ui.replaceButton.setFocusable(false);
		ui.replaceButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.REPLACE_TOOL;
						ui.deselectToolPalette();
						ui.replaceButton.setSelected(true); });
		ui.toolPalette.add(ui.replaceButton);
		// Mover
		ui.moveButton.setToolTipText(ui.xlate("Mover"));
		ui.moveButton.setFocusable(false);
		ui.moveButton.addActionListener(e -> { ui.toolType = TMTools.ToolType.MOVE_TOOL;
						ui.deselectToolPalette();
						ui.moveButton.setSelected(true); });
		ui.toolPalette.add(ui.moveButton);
		ui.toolPalette.setFloatable(false);
		ui.toolPalette.setFocusable(false);

		ui.selectButton.setSelected(true); // starting tool
	}

	public void buildSelectionToolBar() {
		ui.selectionToolBar.setBorder(null);
		// Mirror
		ui.mirrorButton.setToolTipText(ui.mirrorMenuItem.getText());
		ui.mirrorButton.setFocusable(false);
		ui.mirrorButton.addActionListener(e -> { ui.doMirrorCommand(); });
		ui.selectionToolBar.add(ui.mirrorButton);
		// Flip
		ui.flipButton.setToolTipText(ui.flipMenuItem.getText());
		ui.flipButton.setFocusable(false);
		ui.flipButton.addActionListener(e -> { ui.doFlipCommand(); });
		ui.selectionToolBar.add(ui.flipButton);
		// Rotate Right
		ui.rotateRightButton.setToolTipText(ui.rotateRightMenuItem.getText());
		ui.rotateRightButton.setFocusable(false);
		ui.rotateRightButton.addActionListener(e -> { ui.doRotateRightCommand(); });
		ui.selectionToolBar.add(ui.rotateRightButton);
		// Rotate Left
		ui.rotateLeftButton.setToolTipText(ui.rotateLeftMenuItem.getText());
		ui.rotateLeftButton.setFocusable(false);
		ui.rotateLeftButton.addActionListener(e -> { ui.doRotateLeftCommand(); });
		ui.selectionToolBar.add(ui.rotateLeftButton);
		// Shift Left
		ui.shiftLeftButton.setToolTipText(ui.shiftLeftMenuItem.getText());
		ui.shiftLeftButton.setFocusable(false);
		ui.shiftLeftButton.addActionListener(e -> { ui.doShiftLeftCommand(); });
		ui.selectionToolBar.add(ui.shiftLeftButton);
		// Shift Right
		ui.shiftRightButton.setToolTipText(ui.shiftRightMenuItem.getText());
		ui.shiftRightButton.setFocusable(false);
		ui.shiftRightButton.addActionListener(e -> { ui.doShiftRightCommand(); });
		ui.selectionToolBar.add(ui.shiftRightButton);
		// Shift Up
		ui.shiftUpButton.setToolTipText(ui.shiftUpMenuItem.getText());
		ui.shiftUpButton.setFocusable(false);
		ui.shiftUpButton.addActionListener(e -> { ui.doShiftUpCommand(); });
		ui.selectionToolBar.add(ui.shiftUpButton);
		// Shift Down
		ui.shiftDownButton.setToolTipText(ui.shiftDownMenuItem.getText());
		ui.shiftDownButton.setFocusable(false);
		ui.shiftDownButton.addActionListener(e -> { ui.doShiftDownCommand(); });
		ui.selectionToolBar.add(ui.shiftDownButton);
		//
		ui.selectionToolBar.setFloatable(false);
		ui.selectionToolBar.setFocusable(false);
		// ui.selectionToolBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
	}

}
