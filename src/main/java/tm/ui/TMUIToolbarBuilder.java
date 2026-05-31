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
		TMUIBind.addToolBarButton(ui.widgets.toolBar, ui.widgets.newButton, ui.widgets.newMenuItem.getText(), ui::doNewCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBar, ui.widgets.openButton, ui.widgets.openMenuItem.getText(), ui::doOpenCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBar, ui.widgets.saveButton, ui.widgets.saveMenuItem.getText(), ui::doSaveCommand);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.cutButton, ui.widgets.cutMenuItem.getText(), ui::doCutCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.copyButton, ui.widgets.copyMenuItem.getText(), ui::doCopyCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.pasteButton, ui.widgets.pasteMenuItem.getText(), ui::doPasteCommand);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.undoButton, ui.widgets.undoMenuItem.getText(), ui::doUndoCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.redoButton, ui.widgets.redoMenuItem.getText(), ui::doRedoCommand);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.gotoButton, ui.widgets.goToMenuItem.getText(), ui::doGoToCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.addBookmarkButton, ui.widgets.addToBookmarksMenuItem.getText(),
				ui::doAddToBookmarksCommand);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.decWidthButton, ui.xlate("Decrease_Width"),
				ui::doDecreaseWidthCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.incWidthButton, ui.xlate("Increase_Width"),
				ui::doIncreaseWidthCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.decHeightButton, ui.xlate("Decrease_Height"),
				ui::doDecreaseHeightCommand);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.incHeightButton, ui.xlate("Increase_Height"),
				ui::doIncreaseHeightCommand);

		ui.widgets.toolBar.setFocusable(false);
		ui.widgets.toolBar.setFloatable(false);
		ui.widgets.toolBarMDI.setFocusable(false);
		ui.widgets.toolBarMDI.setFloatable(false);
	}

	public void buildNavBar() {
		ui.widgets.navBar.addSeparator();

		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusPageButton, ui.xlate("Page_Back"), ui::doMinusPageCommand);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusPageButton, ui.xlate("Page_Forward"), ui::doPlusPageCommand);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusRowButton, ui.xlate("Row_Back"), ui::doMinusRowCommand);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusRowButton, ui.xlate("Row_Forward"), ui::doPlusRowCommand);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusTileButton, ui.xlate("Tile_Back"), ui::doMinusTileCommand);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusTileButton, ui.xlate("Tile_Forward"), ui::doPlusTileCommand);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusByteButton, ui.xlate("Byte_Back"), ui::doMinusByteCommand);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusByteButton, ui.xlate("Byte_Forward"), ui::doPlusByteCommand);

		ui.widgets.navBar.setFloatable(false);
		ui.widgets.navBar.setFocusable(false);
	}

	public void buildToolPalette() {
		ui.widgets.toolPalette.setBorder(null);

		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.selectButton, ui.xlate("Selection"), () -> {
			ui.toolType = TMTools.ToolType.SELECT_TOOL;
			ui.deselectToolPalette();
			ui.widgets.selectButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.zoomButton, ui.xlate("Zoom"), () -> {
			ui.toolType = TMTools.ToolType.ZOOM_TOOL;
			ui.deselectToolPalette();
			ui.widgets.zoomButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.pickupButton, ui.xlate("Dropper"), () -> {
			ui.toolType = TMTools.ToolType.PICKUP_TOOL;
			ui.deselectToolPalette();
			ui.widgets.pickupButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.brushButton, ui.xlate("Brush"), () -> {
			ui.toolType = TMTools.ToolType.BRUSH_TOOL;
			ui.deselectToolPalette();
			ui.widgets.brushButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.lineButton, ui.xlate("Line"), () -> {
			ui.toolType = TMTools.ToolType.LINE_TOOL;
			ui.deselectToolPalette();
			ui.widgets.lineButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.fillButton, ui.xlate("Flood_Fill"), () -> {
			ui.toolType = TMTools.ToolType.FILL_TOOL;
			ui.deselectToolPalette();
			ui.widgets.fillButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.replaceButton, ui.xlate("Color_Replacer"), () -> {
			ui.toolType = TMTools.ToolType.REPLACE_TOOL;
			ui.deselectToolPalette();
			ui.widgets.replaceButton.setSelected(true);
		});
		TMUIBind.addToolBarButton(ui.widgets.toolPalette, ui.widgets.moveButton, ui.xlate("Mover"), () -> {
			ui.toolType = TMTools.ToolType.MOVE_TOOL;
			ui.deselectToolPalette();
			ui.widgets.moveButton.setSelected(true);
		});

		ui.widgets.toolPalette.setFloatable(false);
		ui.widgets.toolPalette.setFocusable(false);
		ui.widgets.selectButton.setSelected(true);
	}

	public void buildSelectionToolBar() {
		ui.widgets.selectionToolBar.setBorder(null);

		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.mirrorButton, ui.widgets.mirrorMenuItem.getText(),
				ui::doMirrorCommand);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.flipButton, ui.widgets.flipMenuItem.getText(), ui::doFlipCommand);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.rotateRightButton, ui.widgets.rotateRightMenuItem.getText(),
				ui::doRotateRightCommand);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.rotateLeftButton, ui.widgets.rotateLeftMenuItem.getText(),
				ui::doRotateLeftCommand);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftLeftButton, ui.widgets.shiftLeftMenuItem.getText(),
				ui::doShiftLeftCommand);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftRightButton, ui.widgets.shiftRightMenuItem.getText(),
				ui::doShiftRightCommand);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftUpButton, ui.widgets.shiftUpMenuItem.getText(),
				ui::doShiftUpCommand);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftDownButton, ui.widgets.shiftDownMenuItem.getText(),
				ui::doShiftDownCommand);

		ui.widgets.selectionToolBar.setFloatable(false);
		ui.widgets.selectionToolBar.setFocusable(false);
	}

}
