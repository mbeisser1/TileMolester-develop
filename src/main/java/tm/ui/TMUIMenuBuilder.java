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
		ui.newMenuItem.setMnemonic(KeyEvent.VK_N);
		ui.newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.newMenuItem, ui::doNewCommand);
		ui.fileMenu.add(ui.newMenuItem);
		ui.openMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.openMenuItem, ui::doOpenCommand);
		ui.fileMenu.add(ui.openMenuItem);
		ui.reopenMenu.setMnemonic(KeyEvent.VK_R);
		ui.fileMenu.add(ui.reopenMenu);
		ui.closeMenuItem.setMnemonic(KeyEvent.VK_C);
		TMUIBind.bind(ui.closeMenuItem, ui::doCloseCommand);
		ui.fileMenu.add(ui.closeMenuItem);
		ui.closeAllMenuItem.setMnemonic(KeyEvent.VK_E);
		TMUIBind.bind(ui.closeAllMenuItem, ui::doCloseAllCommand);
		ui.fileMenu.add(ui.closeAllMenuItem);
		ui.fileMenu.addSeparator();
		ui.saveMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.saveMenuItem, ui::doSaveCommand);
		ui.fileMenu.add(ui.saveMenuItem);
		ui.saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.saveAsMenuItem, ui::doSaveAsCommand);
		ui.fileMenu.add(ui.saveAsMenuItem);
		ui.saveAllMenuItem.setMnemonic(KeyEvent.VK_L);
		TMUIBind.bind(ui.saveAllMenuItem, ui::doSaveAllCommand);
		ui.fileMenu.add(ui.saveAllMenuItem);
		ui.fileMenu.addSeparator();
		ui.exitMenuItem.setMnemonic(KeyEvent.VK_X);
		TMUIBind.bind(ui.exitMenuItem, ui::doExitCommand);
		ui.fileMenu.add(ui.exitMenuItem);
		ui.menuBar.add(ui.fileMenu);

		// Edit menu
		ui.editMenu.setMnemonic(KeyEvent.VK_E);
		ui.undoMenuItem.setMnemonic(KeyEvent.VK_U);
		ui.undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.undoMenuItem, ui::doUndoCommand);
		ui.editMenu.add(ui.undoMenuItem);
		ui.redoMenuItem.setMnemonic(KeyEvent.VK_R);
		ui.redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.redoMenuItem, ui::doRedoCommand);
		ui.editMenu.add(ui.redoMenuItem);
		ui.editMenu.addSeparator();
		ui.cutMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.cutMenuItem, ui::doCutCommand);
		ui.editMenu.add(ui.cutMenuItem);
		ui.copyMenuItem.setMnemonic(KeyEvent.VK_C);
		ui.copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.copyMenuItem, ui::doCopyCommand);
		ui.editMenu.add(ui.copyMenuItem);
		ui.pasteMenuItem.setMnemonic(KeyEvent.VK_P);
		ui.pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.pasteMenuItem, ui::doPasteCommand);
		ui.editMenu.add(ui.pasteMenuItem);
		ui.clearMenuItem.setMnemonic(KeyEvent.VK_L);
		ui.clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		TMUIBind.bind(ui.clearMenuItem, ui::doClearCommand);
		ui.editMenu.add(ui.clearMenuItem);
		ui.editMenu.addSeparator();
		ui.selectAllMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.selectAllMenuItem, ui::doSelectAllCommand);
		ui.editMenu.add(ui.selectAllMenuItem);
		ui.editMenu.addSeparator();
		ui.copyToMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.copyToMenuItem, ui::doCopyToCommand);
		ui.editMenu.add(ui.copyToMenuItem);
		ui.pasteFromMenuItem.setMnemonic(KeyEvent.VK_F);
		TMUIBind.bind(ui.pasteFromMenuItem, ui::doPasteFromCommand);
		ui.editMenu.add(ui.pasteFromMenuItem);
		ui.menuBar.add(ui.editMenu);

		// View menu
		ui.viewMenu.setMnemonic(KeyEvent.VK_V);
		ui.viewMenu.add(ui.tileCodecMenu);
		ui.zoomMenu.setMnemonic(KeyEvent.VK_Z);
		ui.zoomInMenuItem.setMnemonic(KeyEvent.VK_I);
		TMUIBind.bind(ui.zoomInMenuItem, ui::doZoomInCommand);
		ui.zoomMenu.add(ui.zoomInMenuItem);
		ui.zoomOutMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.zoomOutMenuItem, ui::doZoomOutCommand);
		ui.zoomMenu.add(ui.zoomOutMenuItem);
		ui.zoomMenu.addSeparator();
		ui._100MenuItem.setMnemonic(KeyEvent.VK_1);
		TMUIBind.bind(ui._100MenuItem, () -> ui.doZoomCommand(1.0));
		ui.zoomMenu.add(ui._100MenuItem);
		ui._200MenuItem.setMnemonic(KeyEvent.VK_2);
		TMUIBind.bind(ui._200MenuItem, () -> ui.doZoomCommand(2.0));
		ui.zoomMenu.add(ui._200MenuItem);
		ui._400MenuItem.setMnemonic(KeyEvent.VK_4);
		TMUIBind.bind(ui._400MenuItem, () -> ui.doZoomCommand(4.0));
		ui.zoomMenu.add(ui._400MenuItem);
		ui._800MenuItem.setMnemonic(KeyEvent.VK_8);
		TMUIBind.bind(ui._800MenuItem, () -> ui.doZoomCommand(8.0));
		ui.zoomMenu.add(ui._800MenuItem);
		ui._1600MenuItem.setMnemonic(KeyEvent.VK_6);
		TMUIBind.bind(ui._1600MenuItem, () -> ui.doZoomCommand(16.0));
		ui.zoomMenu.add(ui._1600MenuItem);
		ui._3200MenuItem.setMnemonic(KeyEvent.VK_3);
		TMUIBind.bind(ui._3200MenuItem, () -> ui.doZoomCommand(32.0));
		ui.zoomMenu.add(ui._3200MenuItem);
		ui.viewMenu.add(ui.zoomMenu);
		ui.modeMenu.setMnemonic(KeyEvent.VK_M);
		TMUIBind.bind(ui._1DimensionalMenuItem, () -> {
			ui.doModeCommand(TileCodec.MODE_1D);
			ui._1DimensionalMenuItem.setSelected(true);
		});
		ui.modeMenu.add(ui._1DimensionalMenuItem);
		TMUIBind.bind(ui._2DimensionalMenuItem, () -> {
			ui.doModeCommand(TileCodec.MODE_2D);
			ui._2DimensionalMenuItem.setSelected(true);
		});
		ui.modeMenu.add(ui._2DimensionalMenuItem);
		ui.viewMenu.add(ui.modeMenu);
		ui.modeButtonGroup.add(ui._1DimensionalMenuItem);
		ui.modeButtonGroup.add(ui._2DimensionalMenuItem);
		ui.viewMenu.addSeparator();
		ui.blockSizeMenu.setMnemonic(KeyEvent.VK_B);
		ui.sizeBlockToCanvasMenuItem.setMnemonic(KeyEvent.VK_F);
		TMUIBind.bind(ui.sizeBlockToCanvasMenuItem, ui::doSizeBlockToCanvasCommand);
		ui.blockSizeMenu.add(ui.sizeBlockToCanvasMenuItem);
		ui.blockSizeMenu.addSeparator();
		ui.customBlockSizeMenuItem.setMnemonic(KeyEvent.VK_C);
		TMUIBind.bind(ui.customBlockSizeMenuItem, ui::doCustomBlockSizeCommand);
		ui.blockSizeMenu.add(ui.customBlockSizeMenuItem);
		ui.viewMenu.add(ui.blockSizeMenu);
		ui.rowInterleaveBlocksMenuItem.setMnemonic(KeyEvent.VK_R);
		TMUIBind.bind(ui.rowInterleaveBlocksMenuItem, ui::doRowInterleaveBlocksCommand);
		ui.viewMenu.add(ui.rowInterleaveBlocksMenuItem);
		ui.viewMenu.addSeparator();
		ui.blockGridMenuItem.setMnemonic(KeyEvent.VK_V);
		TMUIBind.bind(ui.blockGridMenuItem, ui::doBlockGridCommand);
		ui.viewMenu.add(ui.blockGridMenuItem);
		ui.tileGridMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.tileGridMenuItem, ui::doTileGridCommand);
		ui.viewMenu.add(ui.tileGridMenuItem);
		ui.pixelGridMenuItem.setMnemonic(KeyEvent.VK_P);
		TMUIBind.bind(ui.pixelGridMenuItem, ui::doPixelGridCommand);
		ui.viewMenu.add(ui.pixelGridMenuItem);
		ui.viewMenu.addSeparator();
		ui.statusBarMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.statusBarMenuItem.setSelected(ui.viewStatusBar);
		TMUIBind.bind(ui.statusBarMenuItem, ui::doStatusBarCommand);
		ui.viewMenu.add(ui.statusBarMenuItem);
		ui.toolBarMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.toolBarMenuItem.setSelected(ui.viewToolBar);
		TMUIBind.bind(ui.toolBarMenuItem, ui::doToolBarCommand);
		ui.viewMenu.add(ui.toolBarMenuItem);
		ui.darkModeMenuItem.setMnemonic(KeyEvent.VK_K);
		ui.darkModeMenuItem.setSelected(ui.darkMode);
		TMUIBind.bind(ui.darkModeMenuItem, ui::doDarkModeCommand);
		ui.viewMenu.add(ui.darkModeMenuItem);
		ui.menuBar.add(ui.viewMenu);

		// Image menu
		ui.imageMenu.setMnemonic(KeyEvent.VK_I);
		ui.mirrorMenuItem.setMnemonic(KeyEvent.VK_M);
		ui.mirrorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.mirrorMenuItem, ui::doMirrorCommand);
		ui.imageMenu.add(ui.mirrorMenuItem);
		ui.flipMenuItem.setMnemonic(KeyEvent.VK_F);
		ui.flipMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.flipMenuItem, ui::doFlipCommand);
		ui.imageMenu.add(ui.flipMenuItem);
		ui.imageMenu.addSeparator();
		ui.rotateRightMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.rotateRightMenuItem, ui::doRotateRightCommand);
		ui.imageMenu.add(ui.rotateRightMenuItem);
		ui.rotateLeftMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.rotateLeftMenuItem, ui::doRotateLeftCommand);
		ui.imageMenu.add(ui.rotateLeftMenuItem);
		ui.imageMenu.addSeparator();
		ui.shiftLeftMenuItem.setMnemonic(KeyEvent.VK_L);
		TMUIBind.bind(ui.shiftLeftMenuItem, ui::doShiftLeftCommand);
		ui.imageMenu.add(ui.shiftLeftMenuItem);
		ui.shiftRightMenuItem.setMnemonic(KeyEvent.VK_R);
		TMUIBind.bind(ui.shiftRightMenuItem, ui::doShiftRightCommand);
		ui.imageMenu.add(ui.shiftRightMenuItem);
		ui.shiftUpMenuItem.setMnemonic(KeyEvent.VK_U);
		TMUIBind.bind(ui.shiftUpMenuItem, ui::doShiftUpCommand);
		ui.imageMenu.add(ui.shiftUpMenuItem);
		ui.shiftDownMenuItem.setMnemonic(KeyEvent.VK_D);
		TMUIBind.bind(ui.shiftDownMenuItem, ui::doShiftDownCommand);
		ui.imageMenu.add(ui.shiftDownMenuItem);
		ui.imageMenu.addSeparator();
		ui.canvasSizeMenuItem.setMnemonic(KeyEvent.VK_S);
		TMUIBind.bind(ui.canvasSizeMenuItem, ui::doCanvasSizeCommand);
		ui.imageMenu.add(ui.canvasSizeMenuItem);
		ui.stretchMenuItem.setMnemonic(KeyEvent.VK_E);
		TMUIBind.bind(ui.stretchMenuItem, ui::doStretchCommand);
		ui.imageMenu.add(ui.stretchMenuItem);
		ui.menuBar.add(ui.imageMenu);

		// Navigate menu
		ui.navigateMenu.setMnemonic(KeyEvent.VK_N);
		ui.goToMenuItem.setMnemonic(KeyEvent.VK_G);
		ui.goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.goToMenuItem, ui::doGoToCommand);
		ui.navigateMenu.add(ui.goToMenuItem);
		ui.goToAgainMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.goToAgainMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		TMUIBind.bind(ui.goToAgainMenuItem, ui::doGoToAgainCommand);
		ui.navigateMenu.add(ui.goToAgainMenuItem);
		ui.navigateMenu.addSeparator();
		ui.addToBookmarksMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.addToBookmarksMenuItem, ui::doAddToBookmarksCommand);
		ui.navigateMenu.add(ui.addToBookmarksMenuItem);
		ui.organizeBookmarksMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.organizeBookmarksMenuItem, ui::doOrganizeBookmarksCommand);
		ui.navigateMenu.add(ui.organizeBookmarksMenuItem);
		ui.menuBar.add(ui.navigateMenu);

		// Palette menu
		ui.paletteMenu.setMnemonic(KeyEvent.VK_P);
		ui.editColorsMenuItem.setMnemonic(KeyEvent.VK_E);
		TMUIBind.bind(ui.editColorsMenuItem, ui::doEditColorsCommand);
		ui.paletteMenu.add(ui.editColorsMenuItem);
		ui.colorCodecMenu.setMnemonic(KeyEvent.VK_F);
		ui.paletteMenu.add(ui.colorCodecMenu);
		ui.paletteEndiannessMenu.setMnemonic(KeyEvent.VK_N);
		ui.paletteLittleEndianMenuItem.setMnemonic(KeyEvent.VK_L);
		TMUIBind.bind(ui.paletteLittleEndianMenuItem,
				() -> ui.doPaletteEndiannessCommand(ColorCodec.LITTLE_ENDIAN));
		ui.paletteEndiannessMenu.add(ui.paletteLittleEndianMenuItem);
		ui.paletteBigEndianMenuItem.setMnemonic(KeyEvent.VK_B);
		TMUIBind.bind(ui.paletteBigEndianMenuItem, () -> ui.doPaletteEndiannessCommand(ColorCodec.BIG_ENDIAN));
		ui.paletteEndiannessMenu.add(ui.paletteBigEndianMenuItem);
		ui.paletteEndiannessButtonGroup.add(ui.paletteLittleEndianMenuItem);
		ui.paletteEndiannessButtonGroup.add(ui.paletteBigEndianMenuItem);
		ui.paletteMenu.add(ui.paletteEndiannessMenu);
		ui.paletteSizeMenuItem.setMnemonic(KeyEvent.VK_S);
		TMUIBind.bind(ui.paletteSizeMenuItem, ui::doPaletteSizeCommand);
		ui.paletteMenu.add(ui.paletteSizeMenuItem);
		ui.paletteMenu.addSeparator();
		ui.newPaletteMenuItem.setMnemonic(KeyEvent.VK_N);
		TMUIBind.bind(ui.newPaletteMenuItem, ui::doNewPaletteCommand);
		ui.paletteMenu.add(ui.newPaletteMenuItem);
		ui.importPaletteMenu.setMnemonic(KeyEvent.VK_I);
		ui.importInternalPaletteMenuItem.setMnemonic(KeyEvent.VK_T);
		TMUIBind.bind(ui.importInternalPaletteMenuItem, ui::doImportInternalPaletteCommand);
		ui.importPaletteMenu.add(ui.importInternalPaletteMenuItem);
		ui.importExternalPaletteMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.importExternalPaletteMenuItem, ui::doImportExternalPaletteCommand);
		ui.importPaletteMenu.add(ui.importExternalPaletteMenuItem);
		ui.paletteMenu.add(ui.importPaletteMenu);
		ui.paletteMenu.addSeparator();
		ui.addToPalettesMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.addToPalettesMenuItem, ui::doAddToPalettesCommand);
		ui.paletteMenu.add(ui.addToPalettesMenuItem);
		ui.organizePalettesMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.organizePalettesMenuItem, ui::doOrganizePalettesCommand);
		ui.paletteMenu.add(ui.organizePalettesMenuItem);
		ui.menuBar.add(ui.paletteMenu);

		// Window menu
		ui.windowMenu.setMnemonic(KeyEvent.VK_W);
		ui.newWindowMenuItem.setMnemonic(KeyEvent.VK_N);
		TMUIBind.bind(ui.newWindowMenuItem, ui::doNewWindowCommand);
		ui.windowMenu.add(ui.newWindowMenuItem);
		ui.windowMenu.addSeparator();
		ui.tileMenuItem.setMnemonic(KeyEvent.VK_T);
		TMUIBind.bind(ui.tileMenuItem, ui::doTileCommand);
		ui.windowMenu.add(ui.tileMenuItem);
		ui.cascadeMenuItem.setMnemonic(KeyEvent.VK_C);
		TMUIBind.bind(ui.cascadeMenuItem, ui::doCascadeCommand);
		ui.windowMenu.add(ui.cascadeMenuItem);
		ui.arrangeIconsMenuItem.setMnemonic(KeyEvent.VK_I);
		TMUIBind.bind(ui.arrangeIconsMenuItem, ui::doArrangeIconsCommand);
		ui.windowMenu.add(ui.arrangeIconsMenuItem);
		ui.menuBar.add(ui.windowMenu);

		// Help menu
		ui.helpMenu.setMnemonic(KeyEvent.VK_H);
		ui.helpTopicsMenuItem.setMnemonic(KeyEvent.VK_H);
		ui.helpTopicsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		TMUIBind.bind(ui.helpTopicsMenuItem, ui::doHelpTopicsCommand);
		ui.helpMenu.add(ui.helpTopicsMenuItem);
		ui.aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.aboutMenuItem, ui::doAboutCommand);
		ui.helpMenu.add(ui.aboutMenuItem);
		ui.menuBar.add(ui.helpMenu);
	}
}
