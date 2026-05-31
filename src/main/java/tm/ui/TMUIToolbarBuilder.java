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

/**
 * Builds Tile Molester toolbars and wires them to {@link TMUI} commands.
 **/
public class TMUIToolbarBuilder {

	private final TMUI ui;

	public TMUIToolbarBuilder(TMUI ui) {
		this.ui = ui;
	}

	public void buildToolBar() {
		TMUIBind.addToolBarButton(ui.toolBar, ui.newButton, ui.newMenuItem.getText(), ui::doNewCommand);
		TMUIBind.addToolBarButton(ui.toolBar, ui.openButton, ui.openMenuItem.getText(), ui::doOpenCommand);
		TMUIBind.addToolBarButton(ui.toolBar, ui.saveButton, ui.saveMenuItem.getText(), ui::doSaveCommand);

		ui.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.cutButton, ui.cutMenuItem.getText(), ui::doCutCommand);
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.copyButton, ui.copyMenuItem.getText(), ui::doCopyCommand);
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.pasteButton, ui.pasteMenuItem.getText(), ui::doPasteCommand);

		ui.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.undoButton, ui.undoMenuItem.getText(), ui::doUndoCommand);
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.redoButton, ui.redoMenuItem.getText(), ui::doRedoCommand);

		ui.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.gotoButton, ui.goToMenuItem.getText(), ui::doGoToCommand);
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.addBookmarkButton, ui.addToBookmarksMenuItem.getText(),
				ui::doAddToBookmarksCommand);

		ui.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.decWidthButton, ui.xlate("Decrease_Width"),
				ui::doDecreaseWidthCommand);
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.incWidthButton, ui.xlate("Increase_Width"),
				ui::doIncreaseWidthCommand);
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.decHeightButton, ui.xlate("Decrease_Height"),
				ui::doDecreaseHeightCommand);
		TMUIBind.addToolBarButton(ui.toolBarMDI, ui.incHeightButton, ui.xlate("Increase_Height"),
				ui::doIncreaseHeightCommand);

		ui.toolBar.setFocusable(false);
		ui.toolBar.setFloatable(false);
		ui.toolBarMDI.setFocusable(false);
		ui.toolBarMDI.setFloatable(false);
	}

	public void buildNavBar() {
		ui.navBar.addSeparator();

		TMUIBind.addToolBarButton(ui.navBar, ui.minusPageButton, ui.xlate("Page_Back"), ui::doMinusPageCommand);
		TMUIBind.addToolBarButton(ui.navBar, ui.plusPageButton, ui.xlate("Page_Forward"), ui::doPlusPageCommand);
		TMUIBind.addToolBarButton(ui.navBar, ui.minusRowButton, ui.xlate("Row_Back"), ui::doMinusRowCommand);
		TMUIBind.addToolBarButton(ui.navBar, ui.plusRowButton, ui.xlate("Row_Forward"), ui::doPlusRowCommand);
		TMUIBind.addToolBarButton(ui.navBar, ui.minusTileButton, ui.xlate("Tile_Back"), ui::doMinusTileCommand);
		TMUIBind.addToolBarButton(ui.navBar, ui.plusTileButton, ui.xlate("Tile_Forward"), ui::doPlusTileCommand);
		TMUIBind.addToolBarButton(ui.navBar, ui.minusByteButton, ui.xlate("Byte_Back"), ui::doMinusByteCommand);
		TMUIBind.addToolBarButton(ui.navBar, ui.plusByteButton, ui.xlate("Byte_Forward"), ui::doPlusByteCommand);

		ui.navBar.setFloatable(false);
		ui.navBar.setFocusable(false);
	}

	public void buildToolPalette() {
		ui.toolPalette.setBorder(null);

		TMUIBind.addToolBarButton(ui.toolPalette, ui.selectButton, ui.xlate("Selection"), () -> {
			ui.toolType = TMTools.ToolType.SELECT_TOOL;
			ui.deselectToolPalette();
			ui.selectButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.toolPalette, ui.zoomButton, ui.xlate("Zoom"), () -> {
			ui.toolType = TMTools.ToolType.ZOOM_TOOL;
			ui.deselectToolPalette();
			ui.zoomButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.toolPalette, ui.pickupButton, ui.xlate("Dropper"), () -> {
			ui.toolType = TMTools.ToolType.PICKUP_TOOL;
			ui.deselectToolPalette();
			ui.pickupButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.toolPalette, ui.brushButton, ui.xlate("Brush"), () -> {
			ui.toolType = TMTools.ToolType.BRUSH_TOOL;
			ui.deselectToolPalette();
			ui.brushButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.toolPalette, ui.lineButton, ui.xlate("Line"), () -> {
			ui.toolType = TMTools.ToolType.LINE_TOOL;
			ui.deselectToolPalette();
			ui.lineButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.toolPalette, ui.fillButton, ui.xlate("Flood_Fill"), () -> {
			ui.toolType = TMTools.ToolType.FILL_TOOL;
			ui.deselectToolPalette();
			ui.fillButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.toolPalette, ui.replaceButton, ui.xlate("Color_Replacer"), () -> {
			ui.toolType = TMTools.ToolType.REPLACE_TOOL;
			ui.deselectToolPalette();
			ui.replaceButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.toolPalette, ui.moveButton, ui.xlate("Mover"), () -> {
			ui.toolType = TMTools.ToolType.MOVE_TOOL;
			ui.deselectToolPalette();
			ui.moveButton.setSelected(true);
		});

		ui.toolPalette.setFloatable(false);
		ui.toolPalette.setFocusable(false);
		ui.selectButton.setSelected(true);
	}

	public void buildSelectionToolBar() {
		ui.selectionToolBar.setBorder(null);

		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.mirrorButton, ui.mirrorMenuItem.getText(),
				ui::doMirrorCommand);
		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.flipButton, ui.flipMenuItem.getText(), ui::doFlipCommand);
		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.rotateRightButton, ui.rotateRightMenuItem.getText(),
				ui::doRotateRightCommand);
		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.rotateLeftButton, ui.rotateLeftMenuItem.getText(),
				ui::doRotateLeftCommand);
		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.shiftLeftButton, ui.shiftLeftMenuItem.getText(),
				ui::doShiftLeftCommand);
		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.shiftRightButton, ui.shiftRightMenuItem.getText(),
				ui::doShiftRightCommand);
		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.shiftUpButton, ui.shiftUpMenuItem.getText(),
				ui::doShiftUpCommand);
		TMUIBind.addToolBarButton(ui.selectionToolBar, ui.shiftDownButton, ui.shiftDownMenuItem.getText(),
				ui::doShiftDownCommand);

		ui.selectionToolBar.setFloatable(false);
		ui.selectionToolBar.setFocusable(false);
	}

}
