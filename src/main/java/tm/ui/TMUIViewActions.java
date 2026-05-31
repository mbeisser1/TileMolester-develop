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

import tm.ui.settings.TMTheme;
import tm.*;
import tm.tilecodecs.*;
import javax.swing.*;

/** View menu command handlers for {@link TMUI}. */
public class TMUIViewActions extends TMUICommandGroup {
	public final Action zoomIn;
	public final Action zoomOut;
	public final Action sizeBlockToCanvas;
	public final Action customBlockSize;
	public final Action rowInterleaveBlocks;
	public final Action blockGrid;
	public final Action tileGrid;
	public final Action pixelGrid;
	public final Action statusBar;
	public final Action toolBar;
	public final Action darkMode;
	public final Action decreaseWidth;
	public final Action increaseWidth;
	public final Action decreaseHeight;
	public final Action increaseHeight;
	public final Action zoom100;
	public final Action zoom200;
	public final Action zoom400;
	public final Action zoom800;
	public final Action zoom1600;
	public final Action zoom3200;
	public final Action mode1D;
	public final Action mode2D;

	public TMUIViewActions(TMUI ui) {
		super(ui);
		zoomIn = command(this::doZoomInCommand);
		zoomOut = command(this::doZoomOutCommand);
		sizeBlockToCanvas = command(this::doSizeBlockToCanvasCommand);
		customBlockSize = command(this::doCustomBlockSizeCommand);
		rowInterleaveBlocks = command(this::doRowInterleaveBlocksCommand);
		blockGrid = command(this::doBlockGridCommand);
		tileGrid = command(this::doTileGridCommand);
		pixelGrid = command(this::doPixelGridCommand);
		statusBar = command(this::doStatusBarCommand);
		toolBar = command(this::doToolBarCommand);
		darkMode = command(this::doDarkModeCommand);
		decreaseWidth = command(this::doDecreaseWidthCommand);
		increaseWidth = command(this::doIncreaseWidthCommand);
		decreaseHeight = command(this::doDecreaseHeightCommand);
		increaseHeight = command(this::doIncreaseHeightCommand);
		zoom100 = command(() -> doZoomCommand(1.0));
		zoom200 = command(() -> doZoomCommand(2.0));
		zoom400 = command(() -> doZoomCommand(4.0));
		zoom800 = command(() -> doZoomCommand(8.0));
		zoom1600 = command(() -> doZoomCommand(16.0));
		zoom3200 = command(() -> doZoomCommand(32.0));
		mode1D = command(() -> {
			doModeCommand(TileCodec.MODE_1D);
			ui.widgets._1DimensionalMenuItem.setSelected(true);
		});
		mode2D = command(() -> {
			doModeCommand(TileCodec.MODE_2D);
			ui.widgets._2DimensionalMenuItem.setSelected(true);
		});
	}


	public void doTileCodecCommand(TileCodec codec) {
		ui.withSelectedView(view -> {
			view.setTileCodec(codec);
			ui.refresh.refreshPalettePane();
			ui.refresh.refreshStatusBar();
			ui.refresh.refreshTileCodecSelection(view);
		});
	}

	public void doZoomCommand(double scale) {
		ui.withSelectedView(view -> view.setScale(scale));
	}

	public void doZoomInCommand() {
		ui.withSelectedView(view -> view.setScale(view.getScale() + 1.0));
	}

	public void doZoomOutCommand() {
		ui.withSelectedView(view -> view.setScale(view.getScale() - 1.0));
	}

	public void doBlockGridCommand() {
		ui.withSelectedView(view -> {
			view.setBlockGridVisible(!view.isBlockGridVisible());
			ui.widgets.blockGridMenuItem.setSelected(view.isBlockGridVisible());
			view.repaint();
		});
	}

	public void doTileGridCommand() {
		ui.withSelectedView(view -> {
			view.setTileGridVisible(!view.isTileGridVisible());
			ui.widgets.tileGridMenuItem.setSelected(view.isTileGridVisible());
			view.repaint();
		});
	}

	public void doPixelGridCommand() {
		ui.withSelectedView(view -> {
			view.setPixelGridVisible(!view.isPixelGridVisible());
			ui.widgets.pixelGridMenuItem.setSelected(view.isPixelGridVisible());
			view.repaint();
		});
	}

	public void doStatusBarCommand() {
		ui.viewStatusBar = !ui.viewStatusBar;
		TileMolester.settings.setViewStatusBar(ui.viewStatusBar);
		ui.widgets.statusBar.setVisible(ui.viewStatusBar);
		ui.widgets.statusBarMenuItem.setSelected(ui.viewStatusBar);
	}

	public void doToolBarCommand() {
		ui.viewToolBar = !ui.viewToolBar;
		TileMolester.settings.setViewToolBar(ui.viewToolBar);
		ui.widgets.toolBarPane.setVisible(ui.viewToolBar);
		ui.widgets.toolBarMenuItem.setSelected(ui.viewToolBar);
	}

	public void doDarkModeCommand() {
		ui.darkMode = !TMTheme.darkMode;
		ui.widgets.darkModeMenuItem.setSelected(ui.darkMode);
		TMTheme.setDarkMode(ui.darkMode);
	}

	public void doModeCommand(int mode) {
		ui.withSelectedView(view -> {
			view.setMode(mode);
			ui.refresh.refreshStatusBar();
		});
	}

	public void doSizeBlockToCanvasCommand() {
		ui.withSelectedView(view -> {
			view.setSizeBlockToCanvas(!view.getSizeBlockToCanvas());
			ui.widgets.sizeBlockToCanvasMenuItem.setSelected(view.getSizeBlockToCanvas());
		});
	}

	public void doCustomBlockSizeCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.widgets.blockSizeDialog.showDialog(view.getBlockWidth(), view.getBlockHeight());
			if (retVal == JOptionPane.OK_OPTION) {
				view.setSizeBlockToCanvas(false);
				ui.widgets.sizeBlockToCanvasMenuItem.setSelected(false);
				view.setBlockDimensions(ui.widgets.blockSizeDialog.getCols(), ui.widgets.blockSizeDialog.getRows());
			}
		});
	}

	public void doRowInterleaveBlocksCommand() {
		ui.withSelectedView(view -> {
			view.setRowInterleaveBlocks(!view.getRowInterleaveBlocks());
			ui.widgets.rowInterleaveBlocksMenuItem.setSelected(view.getRowInterleaveBlocks());
		});
	}

	public void doCustomCodecCommand() {
		ui.withSelectedView(view -> {
			ui.widgets.customCodecDialog.setVisible(true);
			int retVal = 0; // TODO
			if (retVal == JOptionPane.OK_OPTION) {
				int bpp = ui.widgets.customCodecDialog.getBitsPerPixel();
				int rmask = ui.widgets.customCodecDialog.getRedMask();
				int gmask = ui.widgets.customCodecDialog.getBlueMask();
				int bmask = ui.widgets.customCodecDialog.getGreenMask();
				int amask = ui.widgets.customCodecDialog.getAlphaMask();
				String desc = ui.widgets.customCodecDialog.getDescription();
				DirectColorTileCodec codec = new DirectColorTileCodec("", bpp, rmask, gmask, bmask, amask, desc);
				ui.addTileCodec(codec);
				view.setTileCodec(codec);
			}
		});
	}

	public void doDecreaseWidthCommand() {
		ui.withSelectedView(view -> {
			view.setGridSize(view.getCols() - 1, view.getRows());
			view.setScale(view.getScale());
		});
	}

	public void doIncreaseWidthCommand() {
		ui.withSelectedView(view -> {
			view.setGridSize(view.getCols() + 1, view.getRows());
			view.setScale(view.getScale());
		});
	}

	public void doDecreaseHeightCommand() {
		ui.withSelectedView(view -> {
			view.setGridSize(view.getCols(), view.getRows() - 1);
			view.setScale(view.getScale());
		});
	}

	public void doIncreaseHeightCommand() {
		ui.withSelectedView(view -> {
			view.setGridSize(view.getCols(), view.getRows() + 1);
			view.setScale(view.getScale());
		});
	}

}
