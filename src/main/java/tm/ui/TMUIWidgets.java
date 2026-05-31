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
import tm.fileselection.*;
import tm.modaldialog.*;
import tm.filelistener.TMFileListener;
import tm.tilecodecs.TileCodec;
import tm.utils.mxScrollableDesktop;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.HashMap;
import java.util.Map;

/** Menus, toolbars, dialogs, and other Swing widgets for {@link TMUI}. */
public class TMUIWidgets {

	// UI components
	mxScrollableDesktop desktop = new mxScrollableDesktop();
	TMStatusBar statusBar = new TMStatusBar();
	JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
	JToolBar toolBarMDI = new JToolBar(JToolBar.HORIZONTAL);
	JToolBar toolPalette = new JToolBar(JToolBar.VERTICAL);
	JToolBar selectionToolBar = new JToolBar(JToolBar.VERTICAL);
	JToolBar navBar = new JToolBar(JToolBar.HORIZONTAL);
	JMenuBar menuBar = new JMenuBar();
	JPanel toolPane = new JPanel(); // the drawing tools and such
	JPanel toolBarPane = new JPanel(); // the program toolbars
	JPanel bottomPane = new JPanel(); // palette and statusbar
	TMPalettePane palettePane;

	// file choosers
	TMApprovedFileOpenChooser fileOpenChooser = new TMApprovedFileOpenChooser();
	TMApprovedFileSaveChooser fileSaveChooser = new TMApprovedFileSaveChooser();
	TMApprovedFileOpenChooser bitmapOpenChooser = new TMApprovedFileOpenChooser();
	TMApprovedFileSaveChooser bitmapSaveChooser = new TMApprovedFileSaveChooser();
	TMApprovedFileOpenChooser paletteOpenChooser = new TMApprovedFileOpenChooser();

	TMBitmapFilters bmf = new TMBitmapFilters();
	TMFileFilter allFilter;

	// custom dialogs
	TMGoToDialog goToDialog;
	TMNewFileDialog newFileDialog;
	TMCustomCodecDialog customCodecDialog;
	TMStretchDialog stretchDialog;
	TMCanvasSizeDialog canvasSizeDialog;
	TMBlockSizeDialog blockSizeDialog;
	TMAddToTreeDialog addBookmarkDialog;
	TMAddToTreeDialog addPaletteDialog;
	TMOrganizeTreeDialog organizeBookmarksDialog;
	TMOrganizeTreeDialog organizePalettesDialog;
	TMNewPaletteDialog newPaletteDialog;
	TMPaletteSizeDialog paletteSizeDialog;
	TMImportInternalPaletteDialog importInternalPaletteDialog;

	// toolbar buttons
	TMToolButton newButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/document_add_24_regular.svg", 22, 22));
	TMToolButton openButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/folder_open_24_regular.svg", 22, 22));
	TMToolButton saveButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/save_24_regular.svg", 22, 22));
	TMToolButton cutButton = new TMToolButton(new FlatSVGIcon("icons/fluent/cut_24_regular.svg", 22, 22));
	TMToolButton copyButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/copy_24_regular.svg", 22, 22));
	TMToolButton pasteButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/clipboard_paste_24_regular.svg", 22, 22));
	TMToolButton undoButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/arrow_undo_24_regular.svg", 22, 22));
	TMToolButton redoButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/arrow_redo_24_regular.svg", 22, 22));
	TMToolButton gotoButton = new TMToolButton(new FlatSVGIcon("icons/fluent/custom/jump-to.svg", 22, 22));
	TMToolButton addBookmarkButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/bookmark_add_24_regular.svg", 22, 22));
	TMToolButton decWidthButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/panel_left_contract_24_regular.svg", 22, 22));
	TMToolButton incWidthButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/panel_left_expand_24_regular.svg", 22, 22));
	TMToolButton decHeightButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/custom/decrease-height.svg", 22, 22));
	TMToolButton incHeightButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/custom/increase-height.svg", 22, 22));

	// navigation bar buttons
	TMToolButton minusPageButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/rewind_24_regular.svg", 22, 22));
	TMToolButton plusPageButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/fast_forward_24_regular.svg", 22, 22));
	TMToolButton minusRowButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/previous_frame_24_regular.svg", 22, 22));
	TMToolButton plusRowButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/next_frame_24_regular.svg", 22, 22));
	TMToolButton minusTileButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/custom/tile-previous.svg", 22, 22));
	TMToolButton plusTileButton = new TMToolButton(new FlatSVGIcon("icons/fluent/custom/tile-next.svg", 22, 22));
	TMToolButton minusByteButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/subtract_square_24_regular.svg", 22, 22));
	TMToolButton plusByteButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/add_square_24_regular.svg", 22, 22));

	// tool palette buttons
	TMToolToggleButton selectButton = new TMToolToggleButton(
			new FlatSVGIcon("icons/fluent/square_hint_24_regular.svg", 22, 22));
	TMToolToggleButton zoomButton = new TMToolToggleButton(new FlatSVGIcon("icons/fluent/custom/zoom.svg", 22, 22));
	TMToolToggleButton pickupButton = new TMToolToggleButton(
			new FlatSVGIcon("icons/fluent/eyedropper_24_regular.svg", 22, 22));
	TMToolToggleButton brushButton = new TMToolToggleButton(
			new FlatSVGIcon("icons/fluent/edit_24_regular.svg", 22, 22));
	TMToolToggleButton lineButton = new TMToolToggleButton(new FlatSVGIcon("icons/fluent/line_24_regular.svg", 22, 22));
	TMToolToggleButton fillButton = new TMToolToggleButton(new FlatSVGIcon("icons/fluent/paint_bucket_24_regular.svg", 22, 22));
	TMToolToggleButton replaceButton = new TMToolToggleButton(
			new FlatSVGIcon("icons/fluent/custom/color-replace.svg", 22, 22));
	TMToolToggleButton moveButton = new TMToolToggleButton(
			new FlatSVGIcon("icons/fluent/arrow_move_24_regular.svg", 22, 22));

	// selection palette buttons
	TMToolButton mirrorButton = new TMToolButton(new FlatSVGIcon("icons/fluent/flip_horizontal_24_regular.svg", 22, 22));
	TMToolButton flipButton = new TMToolButton(new FlatSVGIcon("icons/fluent/flip_vertical_24_regular.svg", 22, 22));
	TMToolButton rotateRightButton = new TMToolButton(
			new FlatSVGIcon("icons/fluent/rotate_right_24_regular.svg", 22, 22));
	TMToolButton rotateLeftButton = new TMToolButton(new FlatSVGIcon("icons/fluent/rotate_left_24_regular.svg", 22, 22));

	TMToolButton shiftLeftButton = new TMToolButton(new FlatSVGIcon("icons/fluent/table_move_left_24_regular.svg", 22, 22));
	TMToolButton shiftRightButton = new TMToolButton(new FlatSVGIcon("icons/fluent/table_move_right_24_regular.svg", 22, 22));
	TMToolButton shiftUpButton = new TMToolButton(new FlatSVGIcon("icons/fluent/table_move_above_24_regular.svg", 22, 22));
	TMToolButton shiftDownButton = new TMToolButton(new FlatSVGIcon("icons/fluent/table_move_below_24_regular.svg", 22, 22));

	// File menu
	JMenu fileMenu = new JMenu("File");
	JMenuItem newMenuItem = new JMenuItem("New...");
	JMenuItem openMenuItem = new JMenuItem("Open...");
	JMenu reopenMenu = new JMenu("Reopen");
	JMenuItem closeMenuItem = new JMenuItem("Close");
	JMenuItem closeAllMenuItem = new JMenuItem("Close All");
	JMenuItem saveMenuItem = new JMenuItem("Save");
	JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
	JMenuItem saveAllMenuItem = new JMenuItem("Save All");
	JMenuItem exitMenuItem = new JMenuItem("Exit");
	// Edit menu
	JMenu editMenu = new JMenu("Edit");
	JMenuItem undoMenuItem = new JMenuItem("Undo");
	JMenuItem redoMenuItem = new JMenuItem("Redo");
	JMenuItem cutMenuItem = new JMenuItem("Cut");
	JMenuItem copyMenuItem = new JMenuItem("Copy");
	JMenuItem pasteMenuItem = new JMenuItem("Paste");
	JMenuItem clearMenuItem = new JMenuItem("Clear");
	JMenuItem selectAllMenuItem = new JMenuItem("Select All");
	JMenuItem copyToMenuItem = new JMenuItem("Export As...");
	JMenuItem pasteFromMenuItem = new JMenuItem("Paste From...");
	JMenuItem newSelectionMenuItem = new JMenuItem("New Selection");
	JMenuItem applySelectionMenuItem = new JMenuItem("Apply Selection");
	// Image menu
	JMenu imageMenu = new JMenu("Image");
	JMenuItem mirrorMenuItem = new JMenuItem("Mirror");
	JMenuItem flipMenuItem = new JMenuItem("Flip");
	JMenuItem rotateRightMenuItem = new JMenuItem("Rotate Right");
	JMenuItem rotateLeftMenuItem = new JMenuItem("Rotate Left");
	JMenuItem shiftLeftMenuItem = new JMenuItem("Shift Left");
	JMenuItem shiftRightMenuItem = new JMenuItem("Shift Right");
	JMenuItem shiftUpMenuItem = new JMenuItem("Shift Up");
	JMenuItem shiftDownMenuItem = new JMenuItem("Shift Down");
	JMenuItem canvasSizeMenuItem = new JMenuItem("Canvas Size...");
	JMenuItem stretchMenuItem = new JMenuItem("Stretch...");
	// View menu
	JMenu viewMenu = new JMenu("View");
	JCheckBoxMenuItem statusBarMenuItem = new JCheckBoxMenuItem("Statusbar");
	JCheckBoxMenuItem toolBarMenuItem = new JCheckBoxMenuItem("Toolbar");
	JCheckBoxMenuItem darkModeMenuItem = new JCheckBoxMenuItem("Dark mode");
	JMenu tileCodecMenu = new JMenu("Codec");
	JMenu zoomMenu = new JMenu("Zoom");
	JMenuItem zoomInMenuItem = new JMenuItem("In");
	JMenuItem zoomOutMenuItem = new JMenuItem("Out");
	JMenuItem _100MenuItem = new JMenuItem("100%");
	JMenuItem _200MenuItem = new JMenuItem("200%");
	JMenuItem _400MenuItem = new JMenuItem("400%");
	JMenuItem _800MenuItem = new JMenuItem("800%");
	JMenuItem _1600MenuItem = new JMenuItem("1600%");
	JMenuItem _3200MenuItem = new JMenuItem("3200%");
	JMenu blockSizeMenu = new JMenu("Block Size");
	JCheckBoxMenuItem sizeBlockToCanvasMenuItem = new JCheckBoxMenuItem("Full Canvas");
	JMenuItem customBlockSizeMenuItem = new JMenuItem("Custom...");
	JRadioButtonMenuItem rowInterleaveBlocksMenuItem = new JRadioButtonMenuItem("Row-interleave Blocks");
	JMenu modeMenu = new JMenu("Mode");
	JRadioButtonMenuItem _1DimensionalMenuItem = new JRadioButtonMenuItem("1-Dimensional");
	JRadioButtonMenuItem _2DimensionalMenuItem = new JRadioButtonMenuItem("2-Dimensional");
	JCheckBoxMenuItem blockGridMenuItem = new JCheckBoxMenuItem("Block Grid");
	JCheckBoxMenuItem tileGridMenuItem = new JCheckBoxMenuItem("Tile Grid");
	JCheckBoxMenuItem pixelGridMenuItem = new JCheckBoxMenuItem("Pixel Grid");
	// Navigate menu
	JMenu navigateMenu = new JMenu("Navigate");
	JMenuItem goToMenuItem = new JMenuItem("Go To...");
	JMenuItem goToAgainMenuItem = new JMenuItem("Go To Again");
	JMenuItem addToBookmarksMenuItem = new JMenuItem("Add To Bookmarks...");
	JMenuItem organizeBookmarksMenuItem = new JMenuItem("Organize Bookmarks...");
	// private JMenuItem saveBookmarksMenuItem = new JMenuItem("Save Bookmarks");
	// Palette menu
	JMenu paletteMenu = new JMenu("Palette");
	JMenuItem editColorsMenuItem = new JMenuItem("Edit Color");
	JMenu colorCodecMenu = new JMenu("Format");
	JMenu paletteEndiannessMenu = new JMenu("Endianness");
	JRadioButtonMenuItem paletteLittleEndianMenuItem = new JRadioButtonMenuItem("Little");
	JRadioButtonMenuItem paletteBigEndianMenuItem = new JRadioButtonMenuItem("Big");
	JRadioButtonMenuItem dummyPaletteMenuItem = new JRadioButtonMenuItem();
	JMenuItem paletteSizeMenuItem = new JMenuItem("Size...");
	JMenuItem newPaletteMenuItem = new JMenuItem("New...");
	JMenu importPaletteMenu = new JMenu("Import From");
	JMenuItem importInternalPaletteMenuItem = new JMenuItem("This File...");
	JMenuItem importExternalPaletteMenuItem = new JMenuItem("Another File...");
	JMenuItem addToPalettesMenuItem = new JMenuItem("Add To Palettes...");
	JMenuItem organizePalettesMenuItem = new JMenuItem("Organize Palettes...");
	// private JMenuItem savePalettesMenuItem = new JMenuItem("Save Palettes");
	// private JMenuItem exportPaletteMenuItem = new JMenuItem("Export..."); // tpl,
	// c, asm, java?
	// Window menu
	JMenu windowMenu = new JMenu("Window");
	JMenuItem newWindowMenuItem = new JMenuItem("New Window");
	JMenuItem tileMenuItem = new JMenuItem("Tile");
	JMenuItem cascadeMenuItem = new JMenuItem("Cascade");
	JMenuItem arrangeIconsMenuItem = new JMenuItem("Arrange Icons");
	// Help menu
	JMenu helpMenu = new JMenu("Help");
	JMenuItem helpTopicsMenuItem = new JMenuItem("Help Topics");
	// private JMenuItem tipMenuItem = new JMenuItem("Tip of the Millennium..."); //
	// Still say no to drugs, okay?
	JMenuItem aboutMenuItem = new JMenuItem("About Tile Molester...");

	// button groups
	//private ButtonGroup toolButtonGroup = new ButtonGroup();
	ButtonGroup colorCodecButtonGroup = new ButtonGroup();
	ButtonGroup tileCodecButtonGroup = new ButtonGroup();
	ButtonGroup paletteButtonGroup = new ButtonGroup();
	ButtonGroup modeButtonGroup = new ButtonGroup();
	ButtonGroup paletteEndiannessButtonGroup = new ButtonGroup();

	Map<TileCodec, TMTileCodecMenuItem> tileCodecButtonHashtable = new HashMap<>();
	Map<ColorCodec, TMColorCodecMenuItem> colorCodecButtonHashtable = new HashMap<>();
	Map<TMPalette, TMPaletteMenuItem> paletteButtonHashtable = new HashMap<>();
	Map<byte[], TMFileListener> fileListenerHashtable = new HashMap<>();

}
