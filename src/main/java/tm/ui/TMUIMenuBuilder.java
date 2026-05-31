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

import javax.swing.*;
import java.awt.event.*;

/**
 * Builds Tile Molester menu bar items and wires them to command actions.
 **/
public class TMUIMenuBuilder {

	private final TMUI ui;
	private final TMUIWidgets w;

	public TMUIMenuBuilder(TMUI ui) {
		this.ui = ui;
		this.w = ui.widgets;
	}

	public void buildMenuBar() {
		buildFileMenu();
		buildEditMenu();
		buildViewMenu();
		buildImageMenu();
		buildNavigateMenu();
		buildPaletteMenu();
		buildWindowMenu();
		buildHelpMenu();
	}

	private void buildFileMenu() {
		JMenu menu = w.fileMenu;
		menu.setMnemonic(KeyEvent.VK_F);

		add(menu, w.newMenuItem, KeyEvent.VK_N, ctrl(KeyEvent.VK_N), ui.fileActions.newFile);
		add(menu, w.openMenuItem, KeyEvent.VK_O, ctrl(KeyEvent.VK_O), ui.fileActions.open);
		add(menu, w.reopenMenu, KeyEvent.VK_R);
		add(menu, w.closeMenuItem, KeyEvent.VK_C, ui.fileActions.close);
		add(menu, w.closeAllMenuItem, KeyEvent.VK_E, ui.fileActions.closeAll);
		menu.addSeparator();
		add(menu, w.saveMenuItem, KeyEvent.VK_S, ctrl(KeyEvent.VK_S), ui.fileActions.save);
		add(menu, w.saveAsMenuItem, KeyEvent.VK_A, ui.fileActions.saveAs);
		add(menu, w.saveAllMenuItem, KeyEvent.VK_L, ui.fileActions.saveAll);
		menu.addSeparator();
		add(menu, w.exitMenuItem, KeyEvent.VK_X, ui.fileActions.exit);

		w.menuBar.add(menu);
	}

	private void buildEditMenu() {
		JMenu menu = w.editMenu;
		menu.setMnemonic(KeyEvent.VK_E);

		add(menu, w.undoMenuItem, KeyEvent.VK_U, ctrl(KeyEvent.VK_Z), ui.editActions.undo);
		add(menu, w.redoMenuItem, KeyEvent.VK_R, ctrl(KeyEvent.VK_Y), ui.editActions.redo);
		menu.addSeparator();
		add(menu, w.cutMenuItem, KeyEvent.VK_T, ctrl(KeyEvent.VK_X), ui.editActions.cut);
		add(menu, w.copyMenuItem, KeyEvent.VK_C, ctrl(KeyEvent.VK_C), ui.editActions.copy);
		add(menu, w.pasteMenuItem, KeyEvent.VK_P, ctrl(KeyEvent.VK_V), ui.editActions.paste);
		add(menu, w.clearMenuItem, KeyEvent.VK_L, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ui.editActions.clear);
		menu.addSeparator();
		add(menu, w.selectAllMenuItem, KeyEvent.VK_S, ctrl(KeyEvent.VK_A), ui.editActions.selectAll);
		menu.addSeparator();
		add(menu, w.copyToMenuItem, KeyEvent.VK_O, ui.editActions.copyTo);
		add(menu, w.pasteFromMenuItem, KeyEvent.VK_F, ui.editActions.pasteFrom);

		w.menuBar.add(menu);
	}

	private void buildViewMenu() {
		JMenu menu = w.viewMenu;
		menu.setMnemonic(KeyEvent.VK_V);

		menu.add(w.tileCodecMenu);
		buildZoomMenu();
		menu.add(w.zoomMenu);
		buildModeMenu();
		menu.add(w.modeMenu);
		w.modeButtonGroup.add(w._1DimensionalMenuItem);
		w.modeButtonGroup.add(w._2DimensionalMenuItem);
		menu.addSeparator();
		buildBlockSizeMenu();
		menu.add(w.blockSizeMenu);
		add(menu, w.rowInterleaveBlocksMenuItem, KeyEvent.VK_R, ui.viewActions.rowInterleaveBlocks);
		menu.addSeparator();
		add(menu, w.blockGridMenuItem, KeyEvent.VK_V, ui.viewActions.blockGrid);
		add(menu, w.tileGridMenuItem, KeyEvent.VK_A, ui.viewActions.tileGrid);
		add(menu, w.pixelGridMenuItem, KeyEvent.VK_P, ui.viewActions.pixelGrid);
		menu.addSeparator();
		addChecked(menu, w.statusBarMenuItem, KeyEvent.VK_S, ui.viewStatusBar, ui.viewActions.statusBar);
		addChecked(menu, w.toolBarMenuItem, KeyEvent.VK_T, ui.viewToolBar, ui.viewActions.toolBar);
		addChecked(menu, w.darkModeMenuItem, KeyEvent.VK_K, ui.darkMode, ui.viewActions.darkMode);

		w.menuBar.add(menu);
	}

	private void buildZoomMenu() {
		JMenu menu = w.zoomMenu;
		menu.setMnemonic(KeyEvent.VK_Z);

		add(menu, w.zoomInMenuItem, KeyEvent.VK_I, ui.viewActions.zoomIn);
		add(menu, w.zoomOutMenuItem, KeyEvent.VK_O, ui.viewActions.zoomOut);
		menu.addSeparator();
		add(menu, w._100MenuItem, KeyEvent.VK_1, ui.viewActions.zoom100);
		add(menu, w._200MenuItem, KeyEvent.VK_2, ui.viewActions.zoom200);
		add(menu, w._400MenuItem, KeyEvent.VK_4, ui.viewActions.zoom400);
		add(menu, w._800MenuItem, KeyEvent.VK_8, ui.viewActions.zoom800);
		add(menu, w._1600MenuItem, KeyEvent.VK_6, ui.viewActions.zoom1600);
		add(menu, w._3200MenuItem, KeyEvent.VK_3, ui.viewActions.zoom3200);
	}

	private void buildModeMenu() {
		JMenu menu = w.modeMenu;
		menu.setMnemonic(KeyEvent.VK_M);

		add(menu, w._1DimensionalMenuItem, ui.viewActions.mode1D);
		add(menu, w._2DimensionalMenuItem, ui.viewActions.mode2D);
	}

	private void buildBlockSizeMenu() {
		JMenu menu = w.blockSizeMenu;
		menu.setMnemonic(KeyEvent.VK_B);

		add(menu, w.sizeBlockToCanvasMenuItem, KeyEvent.VK_F, ui.viewActions.sizeBlockToCanvas);
		menu.addSeparator();
		add(menu, w.customBlockSizeMenuItem, KeyEvent.VK_C, ui.viewActions.customBlockSize);
	}

	private void buildImageMenu() {
		JMenu menu = w.imageMenu;
		menu.setMnemonic(KeyEvent.VK_I);

		add(menu, w.mirrorMenuItem, KeyEvent.VK_M, ctrl(KeyEvent.VK_M), ui.imageActions.mirror);
		add(menu, w.flipMenuItem, KeyEvent.VK_F, ctrl(KeyEvent.VK_I), ui.imageActions.flip);
		menu.addSeparator();
		add(menu, w.rotateRightMenuItem, KeyEvent.VK_O, ui.imageActions.rotateRight);
		add(menu, w.rotateLeftMenuItem, KeyEvent.VK_A, ui.imageActions.rotateLeft);
		menu.addSeparator();
		add(menu, w.shiftLeftMenuItem, KeyEvent.VK_L, ui.imageActions.shiftLeft);
		add(menu, w.shiftRightMenuItem, KeyEvent.VK_R, ui.imageActions.shiftRight);
		add(menu, w.shiftUpMenuItem, KeyEvent.VK_U, ui.imageActions.shiftUp);
		add(menu, w.shiftDownMenuItem, KeyEvent.VK_D, ui.imageActions.shiftDown);
		menu.addSeparator();
		add(menu, w.canvasSizeMenuItem, KeyEvent.VK_S, ui.imageActions.canvasSize);
		add(menu, w.stretchMenuItem, KeyEvent.VK_E, ui.imageActions.stretch);

		w.menuBar.add(menu);
	}

	private void buildNavigateMenu() {
		JMenu menu = w.navigateMenu;
		menu.setMnemonic(KeyEvent.VK_N);

		add(menu, w.goToMenuItem, KeyEvent.VK_G, ctrl(KeyEvent.VK_G), ui.editActions.goTo);
		add(menu, w.goToAgainMenuItem, KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), ui.editActions.goToAgain);
		menu.addSeparator();
		add(menu, w.addToBookmarksMenuItem, KeyEvent.VK_A, ui.navActions.addToBookmarks);
		add(menu, w.organizeBookmarksMenuItem, KeyEvent.VK_O, ui.navActions.organizeBookmarks);

		w.menuBar.add(menu);
	}

	private void buildPaletteMenu() {
		JMenu menu = w.paletteMenu;
		menu.setMnemonic(KeyEvent.VK_P);

		add(menu, w.editColorsMenuItem, KeyEvent.VK_E, ui.paletteActions.editColors);
		w.colorCodecMenu.setMnemonic(KeyEvent.VK_F);
		menu.add(w.colorCodecMenu);
		buildPaletteEndiannessMenu();
		menu.add(w.paletteEndiannessMenu);
		add(menu, w.paletteSizeMenuItem, KeyEvent.VK_S, ui.paletteActions.paletteSize);
		menu.addSeparator();
		add(menu, w.newPaletteMenuItem, KeyEvent.VK_N, ui.paletteActions.newPalette);
		buildImportPaletteMenu();
		menu.add(w.importPaletteMenu);
		menu.addSeparator();
		add(menu, w.addToPalettesMenuItem, KeyEvent.VK_A, ui.paletteActions.addToPalettes);
		add(menu, w.organizePalettesMenuItem, KeyEvent.VK_O, ui.paletteActions.organizePalettes);

		w.menuBar.add(menu);
	}

	private void buildPaletteEndiannessMenu() {
		JMenu menu = w.paletteEndiannessMenu;
		menu.setMnemonic(KeyEvent.VK_N);

		add(menu, w.paletteLittleEndianMenuItem, KeyEvent.VK_L, ui.paletteActions.paletteLittleEndian);
		add(menu, w.paletteBigEndianMenuItem, KeyEvent.VK_B, ui.paletteActions.paletteBigEndian);
		w.paletteEndiannessButtonGroup.add(w.paletteLittleEndianMenuItem);
		w.paletteEndiannessButtonGroup.add(w.paletteBigEndianMenuItem);
	}

	private void buildImportPaletteMenu() {
		JMenu menu = w.importPaletteMenu;
		menu.setMnemonic(KeyEvent.VK_I);

		add(menu, w.importInternalPaletteMenuItem, KeyEvent.VK_T, ui.paletteActions.importInternalPalette);
		add(menu, w.importExternalPaletteMenuItem, KeyEvent.VK_A, ui.paletteActions.importExternalPalette);
	}

	private void buildWindowMenu() {
		JMenu menu = w.windowMenu;
		menu.setMnemonic(KeyEvent.VK_W);

		add(menu, w.newWindowMenuItem, KeyEvent.VK_N, ui.windowActions.newWindow);
		menu.addSeparator();
		add(menu, w.tileMenuItem, KeyEvent.VK_T, ui.windowActions.tile);
		add(menu, w.cascadeMenuItem, KeyEvent.VK_C, ui.windowActions.cascade);
		add(menu, w.arrangeIconsMenuItem, KeyEvent.VK_I, ui.windowActions.arrangeIcons);

		w.menuBar.add(menu);
	}

	private void buildHelpMenu() {
		JMenu menu = w.helpMenu;
		menu.setMnemonic(KeyEvent.VK_H);

		add(menu, w.helpTopicsMenuItem, KeyEvent.VK_H, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), ui.helpActions.helpTopics);
		add(menu, w.aboutMenuItem, KeyEvent.VK_A, ui.helpActions.about);

		w.menuBar.add(menu);
	}

	private static KeyStroke ctrl(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_DOWN_MASK);
	}

	private void add(JMenu menu, JMenuItem item, int mnemonic) {
		item.setMnemonic(mnemonic);
		menu.add(item);
	}

	private void add(JMenu menu, JMenuItem item, Action action) {
		TMUIBind.bind(item, action);
		menu.add(item);
	}

	private void add(JMenu menu, JMenuItem item, int mnemonic, Action action) {
		item.setMnemonic(mnemonic);
		TMUIBind.bind(item, action);
		menu.add(item);
	}

	private void add(JMenu menu, JMenuItem item, int mnemonic, KeyStroke accelerator, Action action) {
		item.setMnemonic(mnemonic);
		item.setAccelerator(accelerator);
		TMUIBind.bind(item, action);
		menu.add(item);
	}

	private void addChecked(JMenu menu, JCheckBoxMenuItem item, int mnemonic, boolean selected, Action action) {
		item.setMnemonic(mnemonic);
		item.setSelected(selected);
		TMUIBind.bind(item, action);
		menu.add(item);
	}
}
