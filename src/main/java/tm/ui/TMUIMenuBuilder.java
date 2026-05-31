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

import tm.colorcodecs.ColorCodec;
import tm.tilecodecs.TileCodec;
import javax.swing.*;
import java.awt.event.*;

/**
 * Builds Tile Molester menu bar items and wires them to {@link TMUI} commands.
 **/
public class TMUIMenuBuilder {

	private final TMUI ui;

	public TMUIMenuBuilder(TMUI ui) {
		this.ui = ui;
	}

	public void buildMenuBar() {
		// File menu
		ui.fileMenu.setMnemonic(KeyEvent.VK_F);
		// New
		ui.newMenuItem.setMnemonic(KeyEvent.VK_N);
		ui.newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		ui.newMenuItem.addActionListener(e -> { ui.doNewCommand(); });
		ui.fileMenu.add(ui.newMenuItem);
		// Open
		ui.openMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		ui.openMenuItem.addActionListener(e -> { ui.doOpenCommand(); });
		ui.fileMenu.add(ui.openMenuItem);
		// Reopen
		ui.reopenMenu.setMnemonic(KeyEvent.VK_R);
		ui.fileMenu.add(ui.reopenMenu);
		// Close
		ui.closeMenuItem.setMnemonic(KeyEvent.VK_C);
		ui.closeMenuItem.addActionListener(e -> { ui.doCloseCommand(); });
		ui.fileMenu.add(ui.closeMenuItem);
		// Close All
		ui.closeAllMenuItem.setMnemonic(KeyEvent.VK_E);
		ui.closeAllMenuItem.addActionListener(e -> { ui.doCloseAllCommand(); });
		ui.fileMenu.add(ui.closeAllMenuItem);
		//
		ui.fileMenu.addSeparator();
		// Save
		ui.saveMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		ui.saveMenuItem.addActionListener(e -> { ui.doSaveCommand(); });
		ui.fileMenu.add(ui.saveMenuItem);
		// Save As
		ui.saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.saveAsMenuItem.addActionListener(e -> { ui.doSaveAsCommand(); });
		ui.fileMenu.add(ui.saveAsMenuItem);
		// Save All
		ui.saveAllMenuItem.setMnemonic(KeyEvent.VK_L);
		ui.saveAllMenuItem.addActionListener(e -> { ui.doSaveAllCommand(); });
		ui.fileMenu.add(ui.saveAllMenuItem);
		//
		ui.fileMenu.addSeparator();
		// Exit
		ui.exitMenuItem.setMnemonic(KeyEvent.VK_X);
		ui.exitMenuItem.addActionListener(e -> { ui.doExitCommand(); });
		ui.fileMenu.add(ui.exitMenuItem);
		ui.menuBar.add(ui.fileMenu);
		// Edit menu
		ui.editMenu.setMnemonic(KeyEvent.VK_E);
		// Undo
		ui.undoMenuItem.setMnemonic(KeyEvent.VK_U);
		ui.undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		ui.undoMenuItem.addActionListener(e -> { ui.doUndoCommand(); });
		ui.editMenu.add(ui.undoMenuItem);
		// Redo
		ui.redoMenuItem.setMnemonic(KeyEvent.VK_R);
		ui.redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		ui.redoMenuItem.addActionListener(e -> { ui.doRedoCommand(); });
		ui.editMenu.add(ui.redoMenuItem);
		//
		ui.editMenu.addSeparator();
		// Cut
		ui.cutMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		ui.cutMenuItem.addActionListener(e -> { ui.doCutCommand(); });
		ui.editMenu.add(ui.cutMenuItem);
		// Copy
		ui.copyMenuItem.setMnemonic(KeyEvent.VK_C);
		ui.copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		ui.copyMenuItem.addActionListener(e -> { ui.doCopyCommand(); });
		ui.editMenu.add(ui.copyMenuItem);
		// Paste
		ui.pasteMenuItem.setMnemonic(KeyEvent.VK_P);
		ui.pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		ui.pasteMenuItem.addActionListener(e -> { ui.doPasteCommand(); });
		ui.editMenu.add(ui.pasteMenuItem);
		// Clear
		ui.clearMenuItem.setMnemonic(KeyEvent.VK_L);
		ui.clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		ui.clearMenuItem.addActionListener(e -> { ui.doClearCommand(); });
		ui.editMenu.add(ui.clearMenuItem);
		//
		ui.editMenu.addSeparator();
		// Select All
		ui.selectAllMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		ui.selectAllMenuItem.addActionListener(e -> { ui.doSelectAllCommand(); });
		ui.editMenu.add(ui.selectAllMenuItem);
		//
		ui.editMenu.addSeparator();
		// Save Selection As...
		ui.copyToMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.copyToMenuItem.addActionListener(e -> { ui.doCopyToCommand(); });
		ui.editMenu.add(ui.copyToMenuItem);
		// Paste From...
		ui.pasteFromMenuItem.setMnemonic(KeyEvent.VK_F);
		ui.pasteFromMenuItem.addActionListener(e -> { ui.doPasteFromCommand(); });
		ui.editMenu.add(ui.pasteFromMenuItem);

		ui.menuBar.add(ui.editMenu);
		// View menu
		ui.viewMenu.setMnemonic(KeyEvent.VK_V);
		// Codec submenu
		ui.viewMenu.add(ui.tileCodecMenu);
		// Zoom submenu
		ui.zoomMenu.setMnemonic(KeyEvent.VK_Z);
		// In
		ui.zoomInMenuItem.setMnemonic(KeyEvent.VK_I);
		ui.zoomInMenuItem.addActionListener(e -> { ui.doZoomInCommand(); });
		ui.zoomMenu.add(ui.zoomInMenuItem);
		// Out
		ui.zoomOutMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.zoomOutMenuItem.addActionListener(e -> { ui.doZoomOutCommand(); });
		ui.zoomMenu.add(ui.zoomOutMenuItem);
		//
		ui.zoomMenu.addSeparator();
		// 100%
		ui._100MenuItem.setMnemonic(KeyEvent.VK_1);
		ui._100MenuItem.addActionListener(e -> { ui.doZoomCommand(1.0); });
		ui.zoomMenu.add(ui._100MenuItem);
		// 200%
		ui._200MenuItem.setMnemonic(KeyEvent.VK_2);
		ui._200MenuItem.addActionListener(e -> { ui.doZoomCommand(2.0); });
		ui.zoomMenu.add(ui._200MenuItem);
		// 400%
		ui._400MenuItem.setMnemonic(KeyEvent.VK_4);
		ui._400MenuItem.addActionListener(e -> { ui.doZoomCommand(4.0); });
		ui.zoomMenu.add(ui._400MenuItem);
		// 800%
		ui._800MenuItem.setMnemonic(KeyEvent.VK_8);
		ui._800MenuItem.addActionListener(e -> { ui.doZoomCommand(8.0); });
		ui.zoomMenu.add(ui._800MenuItem);
		// 1600%
		ui._1600MenuItem.setMnemonic(KeyEvent.VK_6);
		ui._1600MenuItem.addActionListener(e -> { ui.doZoomCommand(16.0); });
		ui.zoomMenu.add(ui._1600MenuItem);
		// 3200%
		ui._3200MenuItem.setMnemonic(KeyEvent.VK_3);
		ui._3200MenuItem.addActionListener(e -> { ui.doZoomCommand(32.0); });
		ui.zoomMenu.add(ui._3200MenuItem);
		ui.viewMenu.add(ui.zoomMenu);
		// Mode submenu
		ui.modeMenu.setMnemonic(KeyEvent.VK_M);
		// 1-Dimensional
		ui._1DimensionalMenuItem.addActionListener(e -> { ui.doModeCommand(TileCodec.MODE_1D);
						ui._1DimensionalMenuItem.setSelected(true); });
		ui.modeMenu.add(ui._1DimensionalMenuItem);
		// 2-Dimensional
		ui._2DimensionalMenuItem.addActionListener(e -> { ui.doModeCommand(TileCodec.MODE_2D);
						ui._2DimensionalMenuItem.setSelected(true); });
		ui.modeMenu.add(ui._2DimensionalMenuItem);
		ui.viewMenu.add(ui.modeMenu);
		// create button group for modes
		ui.modeButtonGroup.add(ui._1DimensionalMenuItem);
		ui.modeButtonGroup.add(ui._2DimensionalMenuItem);
		//
		ui.viewMenu.addSeparator();
		// Block Size
		ui.blockSizeMenu.setMnemonic(KeyEvent.VK_B);
		// Full Canvas
		ui.sizeBlockToCanvasMenuItem.setMnemonic(KeyEvent.VK_F);
		ui.sizeBlockToCanvasMenuItem.addActionListener(e -> { ui.doSizeBlockToCanvasCommand(); });
		ui.blockSizeMenu.add(ui.sizeBlockToCanvasMenuItem);
		//
		ui.blockSizeMenu.addSeparator();
		// Custom Block Size
		ui.customBlockSizeMenuItem.setMnemonic(KeyEvent.VK_C);
		ui.customBlockSizeMenuItem.addActionListener(e -> { ui.doCustomBlockSizeCommand(); });
		ui.blockSizeMenu.add(ui.customBlockSizeMenuItem);
		ui.viewMenu.add(ui.blockSizeMenu);
		// Row-interleave Blocks
		ui.rowInterleaveBlocksMenuItem.setMnemonic(KeyEvent.VK_R);
		ui.rowInterleaveBlocksMenuItem.addActionListener(e -> { ui.doRowInterleaveBlocksCommand(); });
		ui.viewMenu.add(ui.rowInterleaveBlocksMenuItem);
		//
		ui.viewMenu.addSeparator();
		// Block Grid
		ui.blockGridMenuItem.setMnemonic(KeyEvent.VK_V);
		ui.blockGridMenuItem.addActionListener(e -> { ui.doBlockGridCommand(); });
		ui.viewMenu.add(ui.blockGridMenuItem);
		// Tile Grid
		ui.tileGridMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.tileGridMenuItem.addActionListener(e -> { ui.doTileGridCommand(); });
		ui.viewMenu.add(ui.tileGridMenuItem);
		// Pixel Grid
		ui.pixelGridMenuItem.setMnemonic(KeyEvent.VK_P);
		ui.pixelGridMenuItem.addActionListener(e -> { ui.doPixelGridCommand(); });
		ui.viewMenu.add(ui.pixelGridMenuItem);
		//
		ui.viewMenu.addSeparator();
		// Statusbar
		ui.statusBarMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.statusBarMenuItem.setSelected(ui.viewStatusBar);
		ui.statusBarMenuItem.addActionListener(e -> { ui.doStatusBarCommand(); });
		ui.viewMenu.add(ui.statusBarMenuItem);
		// Toolbar
		ui.toolBarMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.toolBarMenuItem.setSelected(ui.viewToolBar);
		ui.toolBarMenuItem.addActionListener(e -> { ui.doToolBarCommand(); });
		ui.viewMenu.add(ui.toolBarMenuItem);
		ui.darkModeMenuItem.setMnemonic(KeyEvent.VK_K);
		ui.darkModeMenuItem.setSelected(ui.darkMode);
		ui.darkModeMenuItem.addActionListener(e -> { ui.doDarkModeCommand(); });
		ui.viewMenu.add(ui.darkModeMenuItem);
		ui.menuBar.add(ui.viewMenu);
		// Image menu
		ui.imageMenu.setMnemonic(KeyEvent.VK_I);
		// Mirror
		ui.mirrorMenuItem.setMnemonic(KeyEvent.VK_M);
		ui.mirrorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
		ui.mirrorMenuItem.addActionListener(e -> { ui.doMirrorCommand(); });
		ui.imageMenu.add(ui.mirrorMenuItem);
		// Flip
		ui.flipMenuItem.setMnemonic(KeyEvent.VK_F);
		ui.flipMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		ui.flipMenuItem.addActionListener(e -> { ui.doFlipCommand(); });
		ui.imageMenu.add(ui.flipMenuItem);
		//
		ui.imageMenu.addSeparator();
		// Rotate Right
		ui.rotateRightMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.rotateRightMenuItem.addActionListener(e -> { ui.doRotateRightCommand(); });
		ui.imageMenu.add(ui.rotateRightMenuItem);
		// Rotate Left
		ui.rotateLeftMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.rotateLeftMenuItem.addActionListener(e -> { ui.doRotateLeftCommand(); });
		ui.imageMenu.add(ui.rotateLeftMenuItem);
		//
		ui.imageMenu.addSeparator();
		// Shift Left
		ui.shiftLeftMenuItem.setMnemonic(KeyEvent.VK_L);
		// ui.shiftLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
		// Event.SHIFT_MASK));
		ui.shiftLeftMenuItem.addActionListener(e -> { ui.doShiftLeftCommand(); });
		ui.imageMenu.add(ui.shiftLeftMenuItem);
		// Shift Right
		ui.shiftRightMenuItem.setMnemonic(KeyEvent.VK_R);
		// ui.shiftRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
		// Event.SHIFT_MASK));
		ui.shiftRightMenuItem.addActionListener(e -> { ui.doShiftRightCommand(); });
		ui.imageMenu.add(ui.shiftRightMenuItem);
		// Shift Up
		ui.shiftUpMenuItem.setMnemonic(KeyEvent.VK_U);
		// ui.shiftUpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
		// Event.SHIFT_MASK));
		ui.shiftUpMenuItem.addActionListener(e -> { ui.doShiftUpCommand(); });
		ui.imageMenu.add(ui.shiftUpMenuItem);
		// Shift Down
		ui.shiftDownMenuItem.setMnemonic(KeyEvent.VK_D);
		// ui.shiftDownMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
		// Event.SHIFT_MASK));
		ui.shiftDownMenuItem.addActionListener(e -> { ui.doShiftDownCommand(); });
		ui.imageMenu.add(ui.shiftDownMenuItem);
		//
		ui.imageMenu.addSeparator();
		// Canvas Size...
		ui.canvasSizeMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.canvasSizeMenuItem.addActionListener(e -> { ui.doCanvasSizeCommand(); });
		ui.imageMenu.add(ui.canvasSizeMenuItem);
		// Stretch...
		ui.stretchMenuItem.setMnemonic(KeyEvent.VK_E);
		ui.stretchMenuItem.addActionListener(e -> { ui.doStretchCommand(); });
		ui.imageMenu.add(ui.stretchMenuItem);
		ui.menuBar.add(ui.imageMenu);
		// Navigate menu
		ui.navigateMenu.setMnemonic(KeyEvent.VK_N);
		// Go To
		ui.goToMenuItem.setMnemonic(KeyEvent.VK_G);
		ui.goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		ui.goToMenuItem.addActionListener(e -> { ui.doGoToCommand(); });
		ui.navigateMenu.add(ui.goToMenuItem);
		// Go To Again
		ui.goToAgainMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.goToAgainMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		ui.goToAgainMenuItem.addActionListener(e -> { ui.doGoToAgainCommand(); });
		ui.navigateMenu.add(ui.goToAgainMenuItem);
		//
		ui.navigateMenu.addSeparator();
		// Add To Bookmarks
		ui.addToBookmarksMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.addToBookmarksMenuItem.addActionListener(e -> { ui.doAddToBookmarksCommand(); });
		ui.navigateMenu.add(ui.addToBookmarksMenuItem);
		// Organize Bookmarks
		ui.organizeBookmarksMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.organizeBookmarksMenuItem.addActionListener(e -> { ui.doOrganizeBookmarksCommand(); });
		ui.navigateMenu.add(ui.organizeBookmarksMenuItem);
		//
		ui.menuBar.add(ui.navigateMenu);
		// Palette menu
		ui.paletteMenu.setMnemonic(KeyEvent.VK_P);
		
		// Edit Colors...
		ui.editColorsMenuItem.setMnemonic(KeyEvent.VK_E);
		ui.editColorsMenuItem.addActionListener(e -> { ui.doEditColorsCommand(); });
		ui.paletteMenu.add(ui.editColorsMenuItem);
		// Format submenu
		ui.colorCodecMenu.setMnemonic(KeyEvent.VK_F);
		ui.paletteMenu.add(ui.colorCodecMenu);
		// Endianness submenu
		ui.paletteEndiannessMenu.setMnemonic(KeyEvent.VK_N);
		// Little endian
		ui.paletteLittleEndianMenuItem.setMnemonic(KeyEvent.VK_L);
		ui.paletteLittleEndianMenuItem.addActionListener(e -> { ui.doPaletteEndiannessCommand(ColorCodec.LITTLE_ENDIAN); });
		ui.paletteEndiannessMenu.add(ui.paletteLittleEndianMenuItem);
		// Big endian
		ui.paletteBigEndianMenuItem.setMnemonic(KeyEvent.VK_B);
		ui.paletteBigEndianMenuItem.addActionListener(e -> { ui.doPaletteEndiannessCommand(ColorCodec.BIG_ENDIAN); });
		ui.paletteEndiannessMenu.add(ui.paletteBigEndianMenuItem);
		// create button group for palette endianness
		ui.paletteEndiannessButtonGroup.add(ui.paletteLittleEndianMenuItem);
		ui.paletteEndiannessButtonGroup.add(ui.paletteBigEndianMenuItem);
		//
		ui.paletteMenu.add(ui.paletteEndiannessMenu);
		// Size...
		ui.paletteSizeMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.paletteSizeMenuItem.addActionListener(e -> { ui.doPaletteSizeCommand(); });
		ui.paletteMenu.add(ui.paletteSizeMenuItem);
		//
		ui.paletteMenu.addSeparator();
		// New...
		ui.newPaletteMenuItem.setMnemonic(KeyEvent.VK_N);
		ui.newPaletteMenuItem.addActionListener(e -> { ui.doNewPaletteCommand(); });
		ui.paletteMenu.add(ui.newPaletteMenuItem);
		// Import From submenu
		ui.importPaletteMenu.setMnemonic(KeyEvent.VK_I);
		// Import From This File...
		ui.importInternalPaletteMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.importInternalPaletteMenuItem.addActionListener(e -> { ui.doImportInternalPaletteCommand(); });
		ui.importPaletteMenu.add(ui.importInternalPaletteMenuItem);
		// Import From Another File...
		ui.importExternalPaletteMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.importExternalPaletteMenuItem.addActionListener(e -> { ui.doImportExternalPaletteCommand(); });
		ui.importPaletteMenu.add(ui.importExternalPaletteMenuItem);
		ui.paletteMenu.add(ui.importPaletteMenu);
		//
		ui.paletteMenu.addSeparator();
		// Add To Palettes...
		ui.addToPalettesMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.addToPalettesMenuItem.addActionListener(e -> { ui.doAddToPalettesCommand(); });
		ui.paletteMenu.add(ui.addToPalettesMenuItem);
		// Organize Palettes...
		ui.organizePalettesMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.organizePalettesMenuItem.addActionListener(e -> { ui.doOrganizePalettesCommand(); });
		ui.paletteMenu.add(ui.organizePalettesMenuItem);
		ui.menuBar.add(ui.paletteMenu);
		// Window menu
		ui.windowMenu.setMnemonic(KeyEvent.VK_W);
		// New Window
		ui.newWindowMenuItem.setMnemonic(KeyEvent.VK_N);
		ui.newWindowMenuItem.addActionListener(e -> { ui.doNewWindowCommand(); });
		ui.windowMenu.add(ui.newWindowMenuItem);
		//
		ui.windowMenu.addSeparator();
		// Tile
		ui.tileMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.tileMenuItem.addActionListener(e -> { ui.doTileCommand(); });
		ui.windowMenu.add(ui.tileMenuItem);
		// Cascade
		ui.cascadeMenuItem.setMnemonic(KeyEvent.VK_C);
		ui.cascadeMenuItem.addActionListener(e -> { ui.doCascadeCommand(); });
		ui.windowMenu.add(ui.cascadeMenuItem);
		// Arrange Icons
		ui.arrangeIconsMenuItem.setMnemonic(KeyEvent.VK_I);
		ui.arrangeIconsMenuItem.addActionListener(e -> { ui.doArrangeIconsCommand(); });
		ui.windowMenu.add(ui.arrangeIconsMenuItem);
		ui.menuBar.add(ui.windowMenu);
		// Help menu
		ui.helpMenu.setMnemonic(KeyEvent.VK_H);
		// Help Topics
		ui.helpTopicsMenuItem.setMnemonic(KeyEvent.VK_H);
		ui.helpTopicsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		ui.helpTopicsMenuItem.addActionListener(e -> { ui.doHelpTopicsCommand(); });
		ui.helpMenu.add(ui.helpTopicsMenuItem);

		// About
		ui.aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.aboutMenuItem.addActionListener(e -> { ui.doAboutCommand(); });
		ui.helpMenu.add(ui.aboutMenuItem);
		ui.menuBar.add(ui.helpMenu);
	}
}
