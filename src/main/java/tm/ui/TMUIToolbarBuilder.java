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
		TMUIBind.addToolBarButton(ui.widgets.toolBar, ui.widgets.newButton, ui.widgets.newMenuItem.getText(), ui.fileActions.newFile);
		TMUIBind.addToolBarButton(ui.widgets.toolBar, ui.widgets.openButton, ui.widgets.openMenuItem.getText(), ui.fileActions.open);
		TMUIBind.addToolBarButton(ui.widgets.toolBar, ui.widgets.saveButton, ui.widgets.saveMenuItem.getText(), ui.fileActions.save);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.cutButton, ui.widgets.cutMenuItem.getText(), ui.editActions.cut);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.copyButton, ui.widgets.copyMenuItem.getText(), ui.editActions.copy);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.pasteButton, ui.widgets.pasteMenuItem.getText(), ui.editActions.paste);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.undoButton, ui.widgets.undoMenuItem.getText(), ui.editActions.undo);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.redoButton, ui.widgets.redoMenuItem.getText(), ui.editActions.redo);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.gotoButton, ui.widgets.goToMenuItem.getText(), ui.editActions.goTo);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.addBookmarkButton, ui.widgets.addToBookmarksMenuItem.getText(),
				ui.navActions.addToBookmarks);

		ui.widgets.toolBarMDI.addSeparator();
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.decWidthButton, ui.xlate("Decrease_Width"),
				ui.viewActions.decreaseWidth);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.incWidthButton, ui.xlate("Increase_Width"),
				ui.viewActions.increaseWidth);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.decHeightButton, ui.xlate("Decrease_Height"),
				ui.viewActions.decreaseHeight);
		TMUIBind.addToolBarButton(ui.widgets.toolBarMDI, ui.widgets.incHeightButton, ui.xlate("Increase_Height"),
				ui.viewActions.increaseHeight);

		ui.widgets.toolBar.setFocusable(false);
		ui.widgets.toolBar.setFloatable(false);
		ui.widgets.toolBarMDI.setFocusable(false);
		ui.widgets.toolBarMDI.setFloatable(false);
	}

	public void buildNavBar() {
		ui.widgets.navBar.addSeparator();

		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusPageButton, ui.xlate("Page_Back"), ui.navActions.minusPage);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusPageButton, ui.xlate("Page_Forward"), ui.navActions.plusPage);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusRowButton, ui.xlate("Row_Back"), ui.navActions.minusRow);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusRowButton, ui.xlate("Row_Forward"), ui.navActions.plusRow);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusTileButton, ui.xlate("Tile_Back"), ui.navActions.minusTile);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusTileButton, ui.xlate("Tile_Forward"), ui.navActions.plusTile);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.minusByteButton, ui.xlate("Byte_Back"), ui.navActions.minusByte);
		TMUIBind.addToolBarButton(ui.widgets.navBar, ui.widgets.plusByteButton, ui.xlate("Byte_Forward"), ui.navActions.plusByte);

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
				ui.imageActions.mirror);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.flipButton, ui.widgets.flipMenuItem.getText(), ui.imageActions.flip);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.rotateRightButton, ui.widgets.rotateRightMenuItem.getText(),
				ui.imageActions.rotateRight);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.rotateLeftButton, ui.widgets.rotateLeftMenuItem.getText(),
				ui.imageActions.rotateLeft);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftLeftButton, ui.widgets.shiftLeftMenuItem.getText(),
				ui.imageActions.shiftLeft);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftRightButton, ui.widgets.shiftRightMenuItem.getText(),
				ui.imageActions.shiftRight);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftUpButton, ui.widgets.shiftUpMenuItem.getText(),
				ui.imageActions.shiftUp);
		TMUIBind.addToolBarButton(ui.widgets.selectionToolBar, ui.widgets.shiftDownButton, ui.widgets.shiftDownMenuItem.getText(),
				ui.imageActions.shiftDown);

		ui.widgets.selectionToolBar.setFloatable(false);
		ui.widgets.selectionToolBar.setFocusable(false);
	}

}
