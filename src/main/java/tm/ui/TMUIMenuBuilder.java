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
		ui.widgets.fileMenu.setMnemonic(KeyEvent.VK_F);
		ui.widgets.newMenuItem.setMnemonic(KeyEvent.VK_N);
		ui.widgets.newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.newMenuItem, ui::doNewCommand);
		ui.widgets.fileMenu.add(ui.widgets.newMenuItem);
		ui.widgets.openMenuItem.setMnemonic(KeyEvent.VK_O);
		ui.widgets.openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.openMenuItem, ui::doOpenCommand);
		ui.widgets.fileMenu.add(ui.widgets.openMenuItem);
		ui.widgets.reopenMenu.setMnemonic(KeyEvent.VK_R);
		ui.widgets.fileMenu.add(ui.widgets.reopenMenu);
		ui.widgets.closeMenuItem.setMnemonic(KeyEvent.VK_C);
		TMUIBind.bind(ui.widgets.closeMenuItem, ui::doCloseCommand);
		ui.widgets.fileMenu.add(ui.widgets.closeMenuItem);
		ui.widgets.closeAllMenuItem.setMnemonic(KeyEvent.VK_E);
		TMUIBind.bind(ui.widgets.closeAllMenuItem, ui::doCloseAllCommand);
		ui.widgets.fileMenu.add(ui.widgets.closeAllMenuItem);
		ui.widgets.fileMenu.addSeparator();
		ui.widgets.saveMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.widgets.saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.saveMenuItem, ui::doSaveCommand);
		ui.widgets.fileMenu.add(ui.widgets.saveMenuItem);
		ui.widgets.saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.widgets.saveAsMenuItem, ui::doSaveAsCommand);
		ui.widgets.fileMenu.add(ui.widgets.saveAsMenuItem);
		ui.widgets.saveAllMenuItem.setMnemonic(KeyEvent.VK_L);
		TMUIBind.bind(ui.widgets.saveAllMenuItem, ui::doSaveAllCommand);
		ui.widgets.fileMenu.add(ui.widgets.saveAllMenuItem);
		ui.widgets.fileMenu.addSeparator();
		ui.widgets.exitMenuItem.setMnemonic(KeyEvent.VK_X);
		TMUIBind.bind(ui.widgets.exitMenuItem, ui::doExitCommand);
		ui.widgets.fileMenu.add(ui.widgets.exitMenuItem);
		ui.widgets.menuBar.add(ui.widgets.fileMenu);

		// Edit menu
		ui.widgets.editMenu.setMnemonic(KeyEvent.VK_E);
		ui.widgets.undoMenuItem.setMnemonic(KeyEvent.VK_U);
		ui.widgets.undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.undoMenuItem, ui::doUndoCommand);
		ui.widgets.editMenu.add(ui.widgets.undoMenuItem);
		ui.widgets.redoMenuItem.setMnemonic(KeyEvent.VK_R);
		ui.widgets.redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.redoMenuItem, ui::doRedoCommand);
		ui.widgets.editMenu.add(ui.widgets.redoMenuItem);
		ui.widgets.editMenu.addSeparator();
		ui.widgets.cutMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.widgets.cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.cutMenuItem, ui::doCutCommand);
		ui.widgets.editMenu.add(ui.widgets.cutMenuItem);
		ui.widgets.copyMenuItem.setMnemonic(KeyEvent.VK_C);
		ui.widgets.copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.copyMenuItem, ui::doCopyCommand);
		ui.widgets.editMenu.add(ui.widgets.copyMenuItem);
		ui.widgets.pasteMenuItem.setMnemonic(KeyEvent.VK_P);
		ui.widgets.pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.pasteMenuItem, ui::doPasteCommand);
		ui.widgets.editMenu.add(ui.widgets.pasteMenuItem);
		ui.widgets.clearMenuItem.setMnemonic(KeyEvent.VK_L);
		ui.widgets.clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		TMUIBind.bind(ui.widgets.clearMenuItem, ui::doClearCommand);
		ui.widgets.editMenu.add(ui.widgets.clearMenuItem);
		ui.widgets.editMenu.addSeparator();
		ui.widgets.selectAllMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.widgets.selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.selectAllMenuItem, ui::doSelectAllCommand);
		ui.widgets.editMenu.add(ui.widgets.selectAllMenuItem);
		ui.widgets.editMenu.addSeparator();
		ui.widgets.copyToMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.widgets.copyToMenuItem, ui::doCopyToCommand);
		ui.widgets.editMenu.add(ui.widgets.copyToMenuItem);
		ui.widgets.pasteFromMenuItem.setMnemonic(KeyEvent.VK_F);
		TMUIBind.bind(ui.widgets.pasteFromMenuItem, ui::doPasteFromCommand);
		ui.widgets.editMenu.add(ui.widgets.pasteFromMenuItem);
		ui.widgets.menuBar.add(ui.widgets.editMenu);

		// View menu
		ui.widgets.viewMenu.setMnemonic(KeyEvent.VK_V);
		ui.widgets.viewMenu.add(ui.widgets.tileCodecMenu);
		ui.widgets.zoomMenu.setMnemonic(KeyEvent.VK_Z);
		ui.widgets.zoomInMenuItem.setMnemonic(KeyEvent.VK_I);
		TMUIBind.bind(ui.widgets.zoomInMenuItem, ui::doZoomInCommand);
		ui.widgets.zoomMenu.add(ui.widgets.zoomInMenuItem);
		ui.widgets.zoomOutMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.widgets.zoomOutMenuItem, ui::doZoomOutCommand);
		ui.widgets.zoomMenu.add(ui.widgets.zoomOutMenuItem);
		ui.widgets.zoomMenu.addSeparator();
		ui.widgets._100MenuItem.setMnemonic(KeyEvent.VK_1);
		TMUIBind.bind(ui.widgets._100MenuItem, () -> ui.doZoomCommand(1.0));
		ui.widgets.zoomMenu.add(ui.widgets._100MenuItem);
		ui.widgets._200MenuItem.setMnemonic(KeyEvent.VK_2);
		TMUIBind.bind(ui.widgets._200MenuItem, () -> ui.doZoomCommand(2.0));
		ui.widgets.zoomMenu.add(ui.widgets._200MenuItem);
		ui.widgets._400MenuItem.setMnemonic(KeyEvent.VK_4);
		TMUIBind.bind(ui.widgets._400MenuItem, () -> ui.doZoomCommand(4.0));
		ui.widgets.zoomMenu.add(ui.widgets._400MenuItem);
		ui.widgets._800MenuItem.setMnemonic(KeyEvent.VK_8);
		TMUIBind.bind(ui.widgets._800MenuItem, () -> ui.doZoomCommand(8.0));
		ui.widgets.zoomMenu.add(ui.widgets._800MenuItem);
		ui.widgets._1600MenuItem.setMnemonic(KeyEvent.VK_6);
		TMUIBind.bind(ui.widgets._1600MenuItem, () -> ui.doZoomCommand(16.0));
		ui.widgets.zoomMenu.add(ui.widgets._1600MenuItem);
		ui.widgets._3200MenuItem.setMnemonic(KeyEvent.VK_3);
		TMUIBind.bind(ui.widgets._3200MenuItem, () -> ui.doZoomCommand(32.0));
		ui.widgets.zoomMenu.add(ui.widgets._3200MenuItem);
		ui.widgets.viewMenu.add(ui.widgets.zoomMenu);
		ui.widgets.modeMenu.setMnemonic(KeyEvent.VK_M);
		TMUIBind.bind(ui.widgets._1DimensionalMenuItem, () -> {
			ui.doModeCommand(TileCodec.MODE_1D);
			ui.widgets._1DimensionalMenuItem.setSelected(true);
		});
		ui.widgets.modeMenu.add(ui.widgets._1DimensionalMenuItem);
		TMUIBind.bind(ui.widgets._2DimensionalMenuItem, () -> {
			ui.doModeCommand(TileCodec.MODE_2D);
			ui.widgets._2DimensionalMenuItem.setSelected(true);
		});
		ui.widgets.modeMenu.add(ui.widgets._2DimensionalMenuItem);
		ui.widgets.viewMenu.add(ui.widgets.modeMenu);
		ui.widgets.modeButtonGroup.add(ui.widgets._1DimensionalMenuItem);
		ui.widgets.modeButtonGroup.add(ui.widgets._2DimensionalMenuItem);
		ui.widgets.viewMenu.addSeparator();
		ui.widgets.blockSizeMenu.setMnemonic(KeyEvent.VK_B);
		ui.widgets.sizeBlockToCanvasMenuItem.setMnemonic(KeyEvent.VK_F);
		TMUIBind.bind(ui.widgets.sizeBlockToCanvasMenuItem, ui::doSizeBlockToCanvasCommand);
		ui.widgets.blockSizeMenu.add(ui.widgets.sizeBlockToCanvasMenuItem);
		ui.widgets.blockSizeMenu.addSeparator();
		ui.widgets.customBlockSizeMenuItem.setMnemonic(KeyEvent.VK_C);
		TMUIBind.bind(ui.widgets.customBlockSizeMenuItem, ui::doCustomBlockSizeCommand);
		ui.widgets.blockSizeMenu.add(ui.widgets.customBlockSizeMenuItem);
		ui.widgets.viewMenu.add(ui.widgets.blockSizeMenu);
		ui.widgets.rowInterleaveBlocksMenuItem.setMnemonic(KeyEvent.VK_R);
		TMUIBind.bind(ui.widgets.rowInterleaveBlocksMenuItem, ui::doRowInterleaveBlocksCommand);
		ui.widgets.viewMenu.add(ui.widgets.rowInterleaveBlocksMenuItem);
		ui.widgets.viewMenu.addSeparator();
		ui.widgets.blockGridMenuItem.setMnemonic(KeyEvent.VK_V);
		TMUIBind.bind(ui.widgets.blockGridMenuItem, ui::doBlockGridCommand);
		ui.widgets.viewMenu.add(ui.widgets.blockGridMenuItem);
		ui.widgets.tileGridMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.widgets.tileGridMenuItem, ui::doTileGridCommand);
		ui.widgets.viewMenu.add(ui.widgets.tileGridMenuItem);
		ui.widgets.pixelGridMenuItem.setMnemonic(KeyEvent.VK_P);
		TMUIBind.bind(ui.widgets.pixelGridMenuItem, ui::doPixelGridCommand);
		ui.widgets.viewMenu.add(ui.widgets.pixelGridMenuItem);
		ui.widgets.viewMenu.addSeparator();
		ui.widgets.statusBarMenuItem.setMnemonic(KeyEvent.VK_S);
		ui.widgets.statusBarMenuItem.setSelected(ui.viewStatusBar);
		TMUIBind.bind(ui.widgets.statusBarMenuItem, ui::doStatusBarCommand);
		ui.widgets.viewMenu.add(ui.widgets.statusBarMenuItem);
		ui.widgets.toolBarMenuItem.setMnemonic(KeyEvent.VK_T);
		ui.widgets.toolBarMenuItem.setSelected(ui.viewToolBar);
		TMUIBind.bind(ui.widgets.toolBarMenuItem, ui::doToolBarCommand);
		ui.widgets.viewMenu.add(ui.widgets.toolBarMenuItem);
		ui.widgets.darkModeMenuItem.setMnemonic(KeyEvent.VK_K);
		ui.widgets.darkModeMenuItem.setSelected(ui.darkMode);
		TMUIBind.bind(ui.widgets.darkModeMenuItem, ui::doDarkModeCommand);
		ui.widgets.viewMenu.add(ui.widgets.darkModeMenuItem);
		ui.widgets.menuBar.add(ui.widgets.viewMenu);

		// Image menu
		ui.widgets.imageMenu.setMnemonic(KeyEvent.VK_I);
		ui.widgets.mirrorMenuItem.setMnemonic(KeyEvent.VK_M);
		ui.widgets.mirrorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.mirrorMenuItem, ui::doMirrorCommand);
		ui.widgets.imageMenu.add(ui.widgets.mirrorMenuItem);
		ui.widgets.flipMenuItem.setMnemonic(KeyEvent.VK_F);
		ui.widgets.flipMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.flipMenuItem, ui::doFlipCommand);
		ui.widgets.imageMenu.add(ui.widgets.flipMenuItem);
		ui.widgets.imageMenu.addSeparator();
		ui.widgets.rotateRightMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.widgets.rotateRightMenuItem, ui::doRotateRightCommand);
		ui.widgets.imageMenu.add(ui.widgets.rotateRightMenuItem);
		ui.widgets.rotateLeftMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.widgets.rotateLeftMenuItem, ui::doRotateLeftCommand);
		ui.widgets.imageMenu.add(ui.widgets.rotateLeftMenuItem);
		ui.widgets.imageMenu.addSeparator();
		ui.widgets.shiftLeftMenuItem.setMnemonic(KeyEvent.VK_L);
		TMUIBind.bind(ui.widgets.shiftLeftMenuItem, ui::doShiftLeftCommand);
		ui.widgets.imageMenu.add(ui.widgets.shiftLeftMenuItem);
		ui.widgets.shiftRightMenuItem.setMnemonic(KeyEvent.VK_R);
		TMUIBind.bind(ui.widgets.shiftRightMenuItem, ui::doShiftRightCommand);
		ui.widgets.imageMenu.add(ui.widgets.shiftRightMenuItem);
		ui.widgets.shiftUpMenuItem.setMnemonic(KeyEvent.VK_U);
		TMUIBind.bind(ui.widgets.shiftUpMenuItem, ui::doShiftUpCommand);
		ui.widgets.imageMenu.add(ui.widgets.shiftUpMenuItem);
		ui.widgets.shiftDownMenuItem.setMnemonic(KeyEvent.VK_D);
		TMUIBind.bind(ui.widgets.shiftDownMenuItem, ui::doShiftDownCommand);
		ui.widgets.imageMenu.add(ui.widgets.shiftDownMenuItem);
		ui.widgets.imageMenu.addSeparator();
		ui.widgets.canvasSizeMenuItem.setMnemonic(KeyEvent.VK_S);
		TMUIBind.bind(ui.widgets.canvasSizeMenuItem, ui::doCanvasSizeCommand);
		ui.widgets.imageMenu.add(ui.widgets.canvasSizeMenuItem);
		ui.widgets.stretchMenuItem.setMnemonic(KeyEvent.VK_E);
		TMUIBind.bind(ui.widgets.stretchMenuItem, ui::doStretchCommand);
		ui.widgets.imageMenu.add(ui.widgets.stretchMenuItem);
		ui.widgets.menuBar.add(ui.widgets.imageMenu);

		// Navigate menu
		ui.widgets.navigateMenu.setMnemonic(KeyEvent.VK_N);
		ui.widgets.goToMenuItem.setMnemonic(KeyEvent.VK_G);
		ui.widgets.goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		TMUIBind.bind(ui.widgets.goToMenuItem, ui::doGoToCommand);
		ui.widgets.navigateMenu.add(ui.widgets.goToMenuItem);
		ui.widgets.goToAgainMenuItem.setMnemonic(KeyEvent.VK_A);
		ui.widgets.goToAgainMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		TMUIBind.bind(ui.widgets.goToAgainMenuItem, ui::doGoToAgainCommand);
		ui.widgets.navigateMenu.add(ui.widgets.goToAgainMenuItem);
		ui.widgets.navigateMenu.addSeparator();
		ui.widgets.addToBookmarksMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.widgets.addToBookmarksMenuItem, ui::doAddToBookmarksCommand);
		ui.widgets.navigateMenu.add(ui.widgets.addToBookmarksMenuItem);
		ui.widgets.organizeBookmarksMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.widgets.organizeBookmarksMenuItem, ui::doOrganizeBookmarksCommand);
		ui.widgets.navigateMenu.add(ui.widgets.organizeBookmarksMenuItem);
		ui.widgets.menuBar.add(ui.widgets.navigateMenu);

		// Palette menu
		ui.widgets.paletteMenu.setMnemonic(KeyEvent.VK_P);
		ui.widgets.editColorsMenuItem.setMnemonic(KeyEvent.VK_E);
		TMUIBind.bind(ui.widgets.editColorsMenuItem, ui::doEditColorsCommand);
		ui.widgets.paletteMenu.add(ui.widgets.editColorsMenuItem);
		ui.widgets.colorCodecMenu.setMnemonic(KeyEvent.VK_F);
		ui.widgets.paletteMenu.add(ui.widgets.colorCodecMenu);
		ui.widgets.paletteEndiannessMenu.setMnemonic(KeyEvent.VK_N);
		ui.widgets.paletteLittleEndianMenuItem.setMnemonic(KeyEvent.VK_L);
		TMUIBind.bind(ui.widgets.paletteLittleEndianMenuItem,
				() -> ui.doPaletteEndiannessCommand(ColorCodec.LITTLE_ENDIAN));
		ui.widgets.paletteEndiannessMenu.add(ui.widgets.paletteLittleEndianMenuItem);
		ui.widgets.paletteBigEndianMenuItem.setMnemonic(KeyEvent.VK_B);
		TMUIBind.bind(ui.widgets.paletteBigEndianMenuItem, () -> ui.doPaletteEndiannessCommand(ColorCodec.BIG_ENDIAN));
		ui.widgets.paletteEndiannessMenu.add(ui.widgets.paletteBigEndianMenuItem);
		ui.widgets.paletteEndiannessButtonGroup.add(ui.widgets.paletteLittleEndianMenuItem);
		ui.widgets.paletteEndiannessButtonGroup.add(ui.widgets.paletteBigEndianMenuItem);
		ui.widgets.paletteMenu.add(ui.widgets.paletteEndiannessMenu);
		ui.widgets.paletteSizeMenuItem.setMnemonic(KeyEvent.VK_S);
		TMUIBind.bind(ui.widgets.paletteSizeMenuItem, ui::doPaletteSizeCommand);
		ui.widgets.paletteMenu.add(ui.widgets.paletteSizeMenuItem);
		ui.widgets.paletteMenu.addSeparator();
		ui.widgets.newPaletteMenuItem.setMnemonic(KeyEvent.VK_N);
		TMUIBind.bind(ui.widgets.newPaletteMenuItem, ui::doNewPaletteCommand);
		ui.widgets.paletteMenu.add(ui.widgets.newPaletteMenuItem);
		ui.widgets.importPaletteMenu.setMnemonic(KeyEvent.VK_I);
		ui.widgets.importInternalPaletteMenuItem.setMnemonic(KeyEvent.VK_T);
		TMUIBind.bind(ui.widgets.importInternalPaletteMenuItem, ui::doImportInternalPaletteCommand);
		ui.widgets.importPaletteMenu.add(ui.widgets.importInternalPaletteMenuItem);
		ui.widgets.importExternalPaletteMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.widgets.importExternalPaletteMenuItem, ui::doImportExternalPaletteCommand);
		ui.widgets.importPaletteMenu.add(ui.widgets.importExternalPaletteMenuItem);
		ui.widgets.paletteMenu.add(ui.widgets.importPaletteMenu);
		ui.widgets.paletteMenu.addSeparator();
		ui.widgets.addToPalettesMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.widgets.addToPalettesMenuItem, ui::doAddToPalettesCommand);
		ui.widgets.paletteMenu.add(ui.widgets.addToPalettesMenuItem);
		ui.widgets.organizePalettesMenuItem.setMnemonic(KeyEvent.VK_O);
		TMUIBind.bind(ui.widgets.organizePalettesMenuItem, ui::doOrganizePalettesCommand);
		ui.widgets.paletteMenu.add(ui.widgets.organizePalettesMenuItem);
		ui.widgets.menuBar.add(ui.widgets.paletteMenu);

		// Window menu
		ui.widgets.windowMenu.setMnemonic(KeyEvent.VK_W);
		ui.widgets.newWindowMenuItem.setMnemonic(KeyEvent.VK_N);
		TMUIBind.bind(ui.widgets.newWindowMenuItem, ui::doNewWindowCommand);
		ui.widgets.windowMenu.add(ui.widgets.newWindowMenuItem);
		ui.widgets.windowMenu.addSeparator();
		ui.widgets.tileMenuItem.setMnemonic(KeyEvent.VK_T);
		TMUIBind.bind(ui.widgets.tileMenuItem, ui::doTileCommand);
		ui.widgets.windowMenu.add(ui.widgets.tileMenuItem);
		ui.widgets.cascadeMenuItem.setMnemonic(KeyEvent.VK_C);
		TMUIBind.bind(ui.widgets.cascadeMenuItem, ui::doCascadeCommand);
		ui.widgets.windowMenu.add(ui.widgets.cascadeMenuItem);
		ui.widgets.arrangeIconsMenuItem.setMnemonic(KeyEvent.VK_I);
		TMUIBind.bind(ui.widgets.arrangeIconsMenuItem, ui::doArrangeIconsCommand);
		ui.widgets.windowMenu.add(ui.widgets.arrangeIconsMenuItem);
		ui.widgets.menuBar.add(ui.widgets.windowMenu);

		// Help menu
		ui.widgets.helpMenu.setMnemonic(KeyEvent.VK_H);
		ui.widgets.helpTopicsMenuItem.setMnemonic(KeyEvent.VK_H);
		ui.widgets.helpTopicsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		TMUIBind.bind(ui.widgets.helpTopicsMenuItem, ui::doHelpTopicsCommand);
		ui.widgets.helpMenu.add(ui.widgets.helpTopicsMenuItem);
		ui.widgets.aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		TMUIBind.bind(ui.widgets.aboutMenuItem, ui::doAboutCommand);
		ui.widgets.helpMenu.add(ui.widgets.aboutMenuItem);
		ui.widgets.menuBar.add(ui.widgets.helpMenu);
	}
}
