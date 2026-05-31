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
import tm.colorcodecs.*;
import tm.tilecodecs.*;
import tm.fileselection.*;
import tm.modaldialog.*;
import tm.reversibleaction.ReversiblePaletteEditAction;
import tm.treenodes.*;
import tm.utils.*;
import tm.threads.*;
import tm.filelistener.*;
import tm.canvases.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.SystemInfo;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The main UI window.
 * Has a desktop for child frames, a menu, toolbars, a palette panel, and a
 * statusbar.
 * The code is mainly dominated by
 * 1) setting up the various menus and toolbars; and
 * 2) providing action handlers for menu items and tool buttons.
 **/
public class TMUI extends JFrame {
	public static boolean isWindows = SystemInfo.isWindows;

	// tool types
	public TMTools.ToolType toolType = TMTools.ToolType.SELECT_TOOL;

	private int previousTool;

	private java.util.List<ColorCodec> colorcodecs;
	private java.util.List<TileCodec> tilecodecs;
	private java.util.List<TMTileCodecFileFilter> filefilters;
	private java.util.List<TMPaletteFileFilter> palettefilters;
	private java.util.List<TMFileListener> filelisteners;

	private TMSelectionCanvas copiedSelection = null;

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
	ClassLoader cl = getClass().getClassLoader();
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

	private Xlator xl;

	Locale locale;
	boolean viewStatusBar = true;
	boolean viewToolBar = true;
	boolean darkMode = TMTheme.darkMode;

	private String lastPath;

	private Border emptyBorder = BorderFactory.createEmptyBorder();
	private JSeparator separator = new JSeparator();

	private Logger uiLogger = Logger.getLogger("D_TMUI");

	TMUIMenuBuilder menuBuilder;
	TMUIToolbarBuilder toolbarBuilder;
	TMUITreeMenuBuilder treeMenuBuilder;

	/**
	 * Creates a Tile Molester UI.
	 **/
	public TMUI() {
		super("Tile Molester");

		ImageIcon imgIcon = new ImageIcon(cl.getResource("icons/TMIcon32.png"));
		setIconImage(imgIcon.getImage());
		
		locale = TileMolester.settings.getLocale();
		lastPath = TileMolester.settings.getLastPath();
		// create a translator
		try {
			xl = new Xlator("languages/language", locale);
		} catch (MissingResourceException e) {
			showError("Error reading language file:", e);
			System.exit(0);
		}

		allFilter = new TMAllFilter(xlate("All_Files"));

		
		setLocale(locale);
		Locale.setDefault(locale);
		JComponent.setDefaultLocale(this.locale);
		// separator.setForeground(Color.decode("#292929"));

		// File menu
		fileMenu.setText(xlate("File"));
		newMenuItem.setText(xlate("New"));
		openMenuItem.setText(xlate("Open"));
		reopenMenu.setText(xlate("Reopen"));
		closeMenuItem.setText(xlate("Close"));
		closeAllMenuItem.setText(xlate("Close_All"));
		saveMenuItem.setText(xlate("Save"));
		saveAsMenuItem.setText(xlate("Save_As"));
		saveAllMenuItem.setText(xlate("Save_All"));
		exitMenuItem.setText(xlate("Exit"));
		// Edit menu
		editMenu.setText(xlate("Edit"));
		undoMenuItem.setText(xlate("Undo"));
		redoMenuItem.setText(xlate("Redo"));
		cutMenuItem.setText(xlate("Cut"));
		copyMenuItem.setText(xlate("Copy"));
		pasteMenuItem.setText(xlate("Paste"));
		clearMenuItem.setText(xlate("Clear"));
		selectAllMenuItem.setText(xlate("Select_All"));
		copyToMenuItem.setText(xlate("Export_As"));
		pasteFromMenuItem.setText(xlate("Paste_From"));
		newSelectionMenuItem.setText(xlate("New_Selection"));
		applySelectionMenuItem.setText(xlate("Apply_Selection"));
		// Image menu
		imageMenu.setText(xlate("Image"));
		mirrorMenuItem.setText(xlate("Mirror"));
		flipMenuItem.setText(xlate("Flip"));
		rotateRightMenuItem.setText(xlate("Rotate_Right"));
		rotateLeftMenuItem.setText(xlate("Rotate_Left"));
		shiftLeftMenuItem.setText(xlate("Shift_Left"));
		shiftRightMenuItem.setText(xlate("Shift_Right"));
		shiftUpMenuItem.setText(xlate("Shift_Up"));
		shiftDownMenuItem.setText(xlate("Shift_Down"));
		canvasSizeMenuItem.setText(xlate("Canvas_Size"));
		stretchMenuItem.setText(xlate("Stretch"));
		// View menu
		viewMenu.setText(xlate("View"));
		statusBarMenuItem.setText(xlate("Statusbar"));
		toolBarMenuItem.setText(xlate("Toolbar"));
		darkModeMenuItem.setText(xlate("Dark_Mode"));
		tileCodecMenu.setText(xlate("Codec"));
		zoomMenu.setText(xlate("Zoom"));
		zoomInMenuItem.setText(xlate("In"));
		zoomOutMenuItem.setText(xlate("Out"));
		_100MenuItem.setText(xlate("100%"));
		_200MenuItem.setText(xlate("200%"));
		_400MenuItem.setText(xlate("400%"));
		_800MenuItem.setText(xlate("800%"));
		_1600MenuItem.setText(xlate("1600%"));
		_3200MenuItem.setText(xlate("3200%"));
		modeMenu.setText(xlate("Mode"));
		_1DimensionalMenuItem.setText(xlate("1_Dimensional"));
		_2DimensionalMenuItem.setText(xlate("2_Dimensional"));
		blockSizeMenu.setText(xlate("Block_Size"));
		sizeBlockToCanvasMenuItem.setText(xlate("Full_Canvas"));
		customBlockSizeMenuItem.setText(xlate("Custom_Block_Size"));
		rowInterleaveBlocksMenuItem.setText(xlate("Row_Interleave_Blocks"));
		blockGridMenuItem.setText(xlate("Block_Grid"));
		tileGridMenuItem.setText(xlate("Tile_Grid"));
		pixelGridMenuItem.setText(xlate("Pixel_Grid"));
		// Navigate menu
		navigateMenu.setText(xlate("Navigate"));
		goToMenuItem.setText(xlate("Go_To"));
		goToAgainMenuItem.setText(xlate("Go_To_Again"));
		addToBookmarksMenuItem.setText(xlate("Add_To_Bookmarks"));
		organizeBookmarksMenuItem.setText(xlate("Organize_Bookmarks"));
		// Palette menu
		paletteMenu.setText(xlate("Palette"));
		editColorsMenuItem.setText(xlate("Edit_Color"));
		colorCodecMenu.setText(xlate("Format"));
		paletteEndiannessMenu.setText(xlate("Endianness"));
		paletteLittleEndianMenuItem.setText(xlate("Little_Endian"));
		paletteBigEndianMenuItem.setText(xlate("Big_Endian"));
		paletteSizeMenuItem.setText(xlate("Size"));
		newPaletteMenuItem.setText(xlate("New"));
		importPaletteMenu.setText(xlate("Import_From"));
		importInternalPaletteMenuItem.setText(xlate("This_File"));
		importExternalPaletteMenuItem.setText(xlate("Another_File"));
		addToPalettesMenuItem.setText(xlate("Add_To_Palettes"));
		organizePalettesMenuItem.setText(xlate("Organize_Palettes"));
		// Window menu
		windowMenu.setText(xlate("Window"));
		newWindowMenuItem.setText(xlate("New_Window"));
		tileMenuItem.setText(xlate("Tile"));
		cascadeMenuItem.setText(xlate("Cascade"));
		arrangeIconsMenuItem.setText(xlate("Arrange_Icons"));
		// Help menu
		helpMenu.setText(xlate("Help"));
		helpTopicsMenuItem.setText(xlate("Help_Topics"));
		aboutMenuItem.setText(xlate("About_Tile_Molester"));

		UIManager.put("OptionPane.yesButtonText", xlate("Yes"));
		UIManager.put("OptionPane.noButtonText", xlate("No"));
		UIManager.put("OptionPane.cancelButtonText", xlate("Cancel"));
		UIManager.put("OptionPane.okButtonText", xlate("OK"));

		fileOpenChooser.setDialogTitle(xlate("Open_File_Dialog_Title"));
		fileSaveChooser.setDialogTitle(xlate("Save_As_Dialog_Title"));
		bitmapOpenChooser.setDialogTitle(xlate("Paste_From_Dialog_Title"));
		bitmapSaveChooser.setDialogTitle(xlate("Export_As_Dialog_Title"));
		paletteOpenChooser.setDialogTitle(xlate("Open_Palette_Dialog_Title"));

		///////// Read specs
		try {
			TMSpecReader.readSpecsFromFile(resolveTmspecFile());
		} catch (SAXParseException e) {
			showError("Parser_Parse_Error",
					e.getMessage() + "\n(" + e.getSystemId() + ",\nline " + e.getLineNumber() + ")\n");
			System.exit(0);
		} catch (SAXException e) {
			showError("Parser_Parse_Error", e);
			System.exit(0);
		} catch (ParserConfigurationException e) {
			showError("Parser_Config_Error", e);
			System.exit(0);
		} catch (IOException e) {
			showError("Parser_IO_Error", e);
			System.exit(0);
		}

		colorcodecs = TMSpecReader.getColorCodecs();
		tilecodecs = TMSpecReader.getTileCodecs();
		filefilters = TMSpecReader.getFileFilters();
		palettefilters = TMSpecReader.getPaletteFilters();
		filelisteners = TMSpecReader.getFileListeners();

		tilecodecs.add(new _3BPPLinearTileCodec());
		tilecodecs.add(new _6BPPLinearTileCodec());
		//////////

		// create dialogs.
		goToDialog = new TMGoToDialog(this, xl);
		newFileDialog = new TMNewFileDialog(this, xl);
		// customCodecDialog = new TMCustomCodecDialog(this, "Custom Codec", true, xl);
		stretchDialog = new TMStretchDialog(this, xl);
		canvasSizeDialog = new TMCanvasSizeDialog(this, xl);
		blockSizeDialog = new TMBlockSizeDialog(this, xl);
		addBookmarkDialog = new TMAddToTreeDialog(this, "Add_To_Bookmarks_Dialog_Title", xl);
		addPaletteDialog = new TMAddToTreeDialog(this, "Add_To_Palettes_Dialog_Title", xl);
		organizeBookmarksDialog = new TMOrganizeTreeDialog(this, "Organize_Bookmarks_Dialog_Title", xl);
		organizePalettesDialog = new TMOrganizeTreeDialog(this, "Organize_Palettes_Dialog_Title", xl);
		newPaletteDialog = new TMNewPaletteDialog(this, xl);
		paletteSizeDialog = new TMPaletteSizeDialog(this, xl);
		importInternalPaletteDialog = new TMImportInternalPaletteDialog(this, xl);

		newPaletteDialog.setCodecs(colorcodecs);
		importInternalPaletteDialog.setCodecs(colorcodecs);

		menuBuilder = new TMUIMenuBuilder(this);
		toolbarBuilder = new TMUIToolbarBuilder(this);
		treeMenuBuilder = new TMUITreeMenuBuilder(this);

		// Set up the GUI.
		// main contentpane
		JPanel pane = new JPanel();
		setContentPane(pane);
		pane.setDoubleBuffered(true);
		pane.setLayout(new BorderLayout());

		// main toolbar
		toolbarBuilder.buildToolBar();
		toolbarBuilder.buildNavBar();
		toolBarPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBarPane.add(toolBar);
		toolBarPane.add(toolBarMDI);
		toolBarPane.add(navBar);
		pane.add(toolBarPane, BorderLayout.NORTH);

		// desktop
		pane.add(new JScrollPane(desktop), BorderLayout.CENTER);

		// palette pane & statusbar
		palettePane = new TMPalettePane(this);
		// statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		bottomPane.setLayout(new BorderLayout());
		bottomPane.add(palettePane, BorderLayout.CENTER);
		bottomPane.add(statusBar, BorderLayout.SOUTH);
		pane.add(bottomPane, BorderLayout.SOUTH);

		JPanel barPane = new JPanel();
		// tool palettes
		toolbarBuilder.buildToolPalette();
		toolbarBuilder.buildSelectionToolBar();
		//barPane.setLayout(new GridLayout(1, 2));

		barPane.add(selectionToolBar);
		barPane.add(toolPalette);
		toolPane.setLayout(new BorderLayout());
		toolPane.add(barPane, BorderLayout.NORTH);
		pane.add(toolPane, BorderLayout.WEST);

		// menus
		menuBuilder.buildMenuBar();
		setJMenuBar(menuBar);
		buildReopenMenu();

		initTileCodecUIStuff();
		buildColorCodecsMenu();
		initPaletteOpenChooser();

		// Set up file save chooser.
		fileSaveChooser.setAcceptAllFileFilterUsed(false);
		fileSaveChooser.addChoosableFileFilter(allFilter);
		fileSaveChooser.setFileFilter(allFilter);

		// Set up bitmap open chooser.
		bitmapOpenChooser.setAcceptAllFileFilterUsed(false);
		bitmapOpenChooser.addChoosableFileFilter(bmf.supported);
		bitmapOpenChooser.addChoosableFileFilter(bmf.gif);
		bitmapOpenChooser.addChoosableFileFilter(bmf.jpeg);
		bitmapOpenChooser.addChoosableFileFilter(bmf.png);
		bitmapOpenChooser.addChoosableFileFilter(bmf.bmp);
		bitmapOpenChooser.addChoosableFileFilter(bmf.pcx);
		bitmapOpenChooser.setFileFilter(bmf.supported);

		// Set up bitmap save chooser.
		bitmapSaveChooser.setAcceptAllFileFilterUsed(false);
		bitmapSaveChooser.addChoosableFileFilter(bmf.gif);
		bitmapSaveChooser.addChoosableFileFilter(bmf.jpeg);
		bitmapSaveChooser.addChoosableFileFilter(bmf.png);
		bitmapSaveChooser.addChoosableFileFilter(bmf.bmp);
		bitmapSaveChooser.addChoosableFileFilter(bmf.pcx);
		bitmapSaveChooser.setFileFilter(bmf.bmp);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			/**
			 * Handles the user request to close the main window.
			 * @param e event from the AWT/Swing listener
			 **/
			public void windowClosing(WindowEvent e) {
				doExitCommand();
			}

			/**
			 * Restores normal window state when the frame is activated.
			 * @param e event from the AWT/Swing listener
			 **/
			public void windowActivated(WindowEvent e) {
				setExtendedState(JFrame.NORMAL); // Hacky way to make it not run in full screen by default
				// HACK to fix the GUI after running FCEU in fullscreen mode
				// int state = getExtendedState();
				// setExtendedState(JFrame.ICONIFIED);
				// setExtendedState(state);
			}
		});

		// Center the frame
		int inset = 128;
		int maxWidth = 1600;
		int maxHeight = 1080;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int finalWidth = (screenSize.width > maxWidth ? maxWidth : screenSize.width) - inset * 2;
		int finalHeight = (screenSize.height > maxHeight ? maxHeight : screenSize.height) - inset * 2;
		setBounds((screenSize.width - finalWidth) / 2,
				(screenSize.height - finalHeight) / 2,
				finalWidth,
				finalHeight);


		// MDI menus and such shouldn't be shown until file loaded.
		disableMDIStuff();

		toolBarPane.setVisible(viewToolBar);

		com.formdev.flatlaf.FlatLaf.updateUI();
		// Show and maximize.
		setVisible(true);
	}

	/**
	 * Deselects all tools in the tool palette.
	 **/
	public void deselectToolPalette() {
		selectButton.setSelected(false);
		zoomButton.setSelected(false);
		pickupButton.setSelected(false);
		brushButton.setSelected(false);
		lineButton.setSelected(false);
		fillButton.setSelected(false);
		replaceButton.setSelected(false);
		moveButton.setSelected(false);
	}

	//////////////////////////////////////////////////////////////////////////////
	// Begin code for handling menu commands

	/**
	 * Handles menu command "New".
	 * Prompts the user to enter the desired file size, then creates a new
	 * FileImage and a default view + palette.
	 **/
	public void doNewCommand() {
		// Show dialog for creating new file
		// TMNewFileDialog newFileDialog = new TMNewFileDialog(this, xl);
		int retVal = newFileDialog.showDialog();
		if (retVal == JOptionPane.OK_OPTION) {
			// create fileimage
			FileImage img = new FileImage(newFileDialog.getFileSize());
			new TMFileResources(img, this);
			// create view for it
			TileCodec tc = tilecodecs.get(0); // default
			TMPalette pal = new TMPalette("PAL000", TMPalette.defaultPalette, getColorCodecByID("CF01"),
					ColorCodec.LITTLE_ENDIAN, true);
			addViewToDesktop(createView(img, tc, pal, TileCodec.MODE_1D));
		}
	}

	/**
	 * Handles menu command "Open...".
	 * User selects file from standard file dialog, the file is
	 * opened and a default view + palette is assigned.
	 **/
	public void doOpenCommand() {
		// set to directory of selected file, if there is one
		TMView view = getSelectedView();
		if (view != null) {
			this.fileOpenChooser.setCurrentDirectory(view.getFileImage().getFile().getParentFile());
		} else if (new File(this.lastPath).exists()) {
			this.fileOpenChooser.setCurrentDirectory(new File(this.lastPath));
		} else {
			this.fileOpenChooser.setCurrentDirectory(new File("."));
		}

		// have the user select a file
		int retVal = fileOpenChooser.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			// get the selected file and open it
			File file = fileOpenChooser.getSelectedFile();
			// updates the last path opened
			lastPath = file.getPath().substring(0, file.getPath().lastIndexOf(File.separator));
			TileMolester.settings.setLastPath(lastPath);
			openFile(file);
		}
	}

	/**
	 * Handles menu command "Close".
	 * Closes a view. If it is the last (only) view of a FileImage,
	 * and the file is modified, the user is prompted to save the file.
	 **/
	public void doCloseCommand() {
		TMView view = getSelectedView();
		if (view != null) {
			FileImage img = view.getFileImage();

			// check if it's the last view
			if (img.getViews().length == 1) {
				saveResources(img); // TODO
				// check if saving required/desired
				if (img.isModified()) {
					int retVal = JOptionPane.showConfirmDialog(this,
							xlate("Save_Changes_To") + " " + img.getName() + "?", "Tile Molester",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (retVal == JOptionPane.YES_OPTION) {
						doSaveCommand();
					} else if (retVal == JOptionPane.NO_OPTION) {
					} else if (retVal == JOptionPane.CANCEL_OPTION) {
						return; // return to program without saving and/or closing
					}
				}
				// remove potential file listener
				fileListenerHashtable.remove(img.getContents());
			}

			// update recent files
			File f = new File(img.getFile().getAbsolutePath());
			addToRecentFiles(f);
			buildReopenMenu();

			// remove view from the FileImage and desktop
			img.removeView(view);
			desktop.remove(view);
			view.dispose();
			desktop.revalidate();
			desktop.repaint();

			img = null;
			view = null;
			System.gc();
		}

		desktop.setSelectedFrame(null);
		JInternalFrame[] frames = desktop.getAllFrames();
		if (frames.length == 0) {
			// no more frames left on the desktop, hide MDI menus and toolbars
			disableMDIStuff();
			setTitle("Tile Molester");
		} else {
			// select a random frame (Swing doesn't do it for you...)
			try {
				frames[0].setSelected(true);
			} catch (java.beans.PropertyVetoException e) {
			}
		}
	}

	/**
	 * Saves the resources for the given fileimage to a file in XML format.
	 * @param img img value
	 **/
	public void saveResources(FileImage img) {
		// TODO: should only save if # bookmarks | # of palettes > 0?
		File resourceFile = TMFileResources.getResourceFileFor(img.getFile());
		try {
			File res = new File("./resources");
			if (!res.exists()) {
				res.mkdir();
			}
			FileWriter fw = new FileWriter(resourceFile);
			if(img.getResources() != null) {
				fw.write(img.getResources().toXML());
				fw.close();
			}
		} catch (IOException e) {
			showError("Save_Resources_Error", e);
		}
	}

	/**
	 *
	 **/
	public void saveBookmarks() {
		// TODO
	}

	/**
	 *
	 **/
	public void savePalettes() {
		// TODO
	}

	/**
	 * Handles menu command "Close All".
	 * Does the same as "Close", only for all the current frames.
	 **/
	public void doCloseAllCommand() {
		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			TMView view = (TMView) frames[i];
			FileImage img = view.getFileImage();
			if (img.getViews().length == 1) {
				// check if saving required/desired
				if (img.isModified()) {
					int retVal = JOptionPane.showConfirmDialog(this,
							xlate("Save_Changes_To") + " " + img.getName() + "?", "Tile Molester",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (retVal == JOptionPane.YES_OPTION) {
						try {
							view.setSelected(true);
						} catch (java.beans.PropertyVetoException x) {
							x.printStackTrace();
						}
						doSaveCommand();
					} else if (retVal == JOptionPane.NO_OPTION) {
					} else if (retVal == JOptionPane.CANCEL_OPTION) {
						return;
					}
				}
				// remove potential file listener
				fileListenerHashtable.remove(img.getContents());
			}
		}

		// remove them all
		for (int i = 0; i < frames.length; i++) {
			TMView view = (TMView) frames[i];
			FileImage img = view.getFileImage();

			saveResources(img); // TODO

			addToRecentFiles(new File(img.getFile().getAbsolutePath()));

			// remove the view
			img.removeView(view);
			desktop.remove(view);
			view.dispose();
		}

		buildReopenMenu();
		desktop.setSelectedFrame(null);
		desktop.revalidate();
		desktop.repaint();
		disableMDIStuff();
		setTitle("Tile Molester");

		System.gc();
	}

	/**
	 * Handles menu command "Save".
	 **/
	public void doSaveCommand() {
		TMView view = getSelectedView();
		if (view != null) {
			FileImage img = view.getFileImage();
			File file = img.getFile();
			String ext = TMFileFilter.getExtension(file);

			saveResources(img); // TODO

			// if (img.isModified()) {
			if (file.exists()) {
				if (!file.canWrite()) {
					showError("File_Write_Error", file.getName());
				} else {
					FileSaverThread thread = null;
					byte[] contents = img.getContents();
					try {
						thread = new FileSaverThread(contents, file);
					} catch (IOException e) {
						showError("File_Save_Error", e);
						return;
					}

					// see if a filelistener should be notified
					TMFileListener fl = fileListenerHashtable.get(contents);
					if (fl != null) {
						fl.fileSaving(contents, ext);
					}

					// save it!
					new ProgressDialog(this, thread);
					img.setModified(false);
					setSaveButtonsEnabled(false);

					if (fl != null) {
						fl.fileLoaded(contents, ext);
					}
				}
			} else {
				doSaveAsCommand();
			}
			// }
		}
	}

	/**
	 * Handles menu command "Save As...".
	 **/
	public void doSaveAsCommand() {
		TMView view = getSelectedView();
		if (view != null) {
			fileSaveChooser.setCurrentDirectory(view.getFileImage().getFile().getParentFile());
			fileSaveChooser.setSelectedFile(view.getFileImage().getFile());
			int retVal = fileSaveChooser.showSaveDialog(this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = fileSaveChooser.getSelectedFile();
				view.getFileImage().setFile(file);
				doSaveCommand();
				setTitle("Tile Molester - " + view.getTitle());
			}
		}
		setSaveButtonsEnabled(false);
	}

	/**
	 * Handles menu command "Save All".
	 **/
	public void doSaveAllCommand() {
		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			TMView view = (TMView) frames[i];
			if (view.getFileImage().isModified()) {
				try {
					view.setSelected(true);
				} catch (java.beans.PropertyVetoException x) {
					x.printStackTrace();
				}
				doSaveCommand();
			}
		}
		setSaveButtonsEnabled(false);
	}

	/**
	 * Handles menu command "Exit".
	 **/
	public void doExitCommand() {
		doCloseAllCommand();
		// if all frames were closed, the operation was successful and we can exit.
		if (desktop.getAllFrames().length == 0) {
			TileMolester.settings.saveSettings();
			System.exit(0);
		}
	}

	/**
	 * Handles menu command "Undo".
	 * Extracts the top item in the Undo stack and undoes it.
	 * Moves the item to the Redo stack.
	 **/
	public void doUndoCommand() {
		withSelectedView(view -> {
			view.undo();
			refreshUndoRedo();
			fileImageModified(view.getFileImage());
		});
	}

	/**
	 * Handles menu command "Redo".
	 * Extracts the top item in the Redo stack and redoes it.
	 * Moves the item to the Undo stack.
	 **/
	public void doRedoCommand() {
		withSelectedView(view -> {
			view.redo();
			refreshUndoRedo();
			fileImageModified(view.getFileImage());
		});
	}

	/**
	 * Handles menu command "Cut".
	 * The current selection of the selected frame is cut to the
	 * central selection.
	 **/
	public void doCutCommand() {
		withSelectedView(view -> {
			copiedSelection = view.getEditorCanvas().cutSelection();
			pasteButton.setEnabled(true);
			pasteMenuItem.setEnabled(true);
		});
	}

	/**
	 * Handles menu command "Copy".
	 **/
	public void doCopyCommand() {
		withSelectedView(view -> {
			copiedSelection = view.getEditorCanvas().copySelection();
			pasteButton.setEnabled(true);
			pasteMenuItem.setEnabled(true);
		});
	}

	/**
	 * Handles menu command "Paste".
	 **/
	public void doPasteCommand() {
		withSelectedView(view -> {
			if (copiedSelection != null) {
				view.getEditorCanvas().paste(copiedSelection);
			}
		});
	}

	/**
	 * Handles menu command "Clear".
	 **/
	public void doClearCommand() {
		withSelectedView(view -> view.getEditorCanvas().clearSelection());
	}

	/**
	 * Handles menu command "Go To...".
	 * Shows a dialog where the user can enter an absolute or relative
	 * file offset to jump to. Then jumps to that offset.
	 **/
	public void doGoToCommand() {
		withSelectedView(view -> {
			int retVal = goToDialog.showDialog();
			if (retVal == JOptionPane.OK_OPTION) {
				if (goToDialog.getMode() == TMGoToDialog.ABSOLUTE_MODE) {
					view.setAbsoluteOffset(goToDialog.getOffset());
				} else {
					view.setRelativeOffset(goToDialog.getOffset());
				}
				view.repaint();
			}
		});
	}

	/**
	 * Handles menu command "Go To Again".
	 * Only applicable when the preceding "Go To..." was of relative type.
	 **/
	public void doGoToAgainCommand() {
		withSelectedView(view -> {
			if (goToDialog.getMode() == TMGoToDialog.ABSOLUTE_MODE) {
				view.setAbsoluteOffset(goToDialog.getOffset());
			} else {
				view.setRelativeOffset(goToDialog.getOffset());
			}
			view.repaint();
		});
	}

	/**
	 * Handles menu command "Select All".
	 **/
	public void doSelectAllCommand() {
		withSelectedView(view -> view.getEditorCanvas().selectAll());
	}

	/**
	 * Handles menu command "Save Selection As...".
	 * @return export selection as flag
	 **/
	public boolean exportSelectionAs() {
		TMView view = getSelectedView();
		if (view == null) {
			return false;
		}
		bitmapSaveChooser.setFileFilter(bmf.bmp);
		int retVal = bitmapSaveChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = bitmapSaveChooser.getSelectedFile();
			try {
				TMBitmapExporter.saveTileCanvasToFile(view.getEditorCanvas().getSelectionCanvas(), file);
				// Keep selection and palette in sync after export.
				view.refreshPaletteDisplay();
				return true;
			} catch (IOException e) {
				showError("Save_Bitmap_Error", e);
				return false;
			}
		}
		return false;
	}

	/**
	 * Handles the "CopyTo" menu or toolbar command.
	 **/
	public void doCopyToCommand() {
		exportSelectionAs();
	}

	/**
	 * Handles the "CutAs" menu or toolbar command.
	 **/
	public void doCutAsCommand() {
		if (exportSelectionAs()) {
			doCutCommand();
		}
	}

	/**
	 * Handles menu command "Paste From...".
	 **/
	public void doPasteFromCommand() {
		withSelectedView(view -> {
			if (new File(this.lastPath).exists()) {
				this.bitmapOpenChooser.setCurrentDirectory(new File(this.lastPath));
			} else {
				this.bitmapOpenChooser.setCurrentDirectory(new File("."));
			}
			int retVal = bitmapOpenChooser.showOpenDialog(this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = bitmapOpenChooser.getSelectedFile();
				TMTileCanvas bitmapCanvas;
				try {
					bitmapCanvas = TMBitmapImporter.loadTileCanvasFromFile(file);
				} catch (InterruptedException | IOException e) {
					showError("Load_Bitmap_Error", e);
					return;
				}
				TMSelectionCanvas selCanvas = new TMSelectionCanvas(
						this, bitmapCanvas, 0, 0,
						bitmapCanvas.getCols(),
						bitmapCanvas.getRows());
				view.getEditorCanvas().paste(selCanvas);
			}
		});
	}

	/**
	 * Handles menu command "Tile".
	 * Code ruthlessly stolen from some guy on the Java forums. Thanks and sorry. :)
	 **/
	public void doTileCommand() {
		JInternalFrame[] frames = desktop.getAllFrames();
		// count frames that aren't iconized
		int frameCount = 0;
		for (int i = 0; i < frames.length; i++) {
			if (!frames[i].isIcon())
				frameCount++;
		}
		int rows = (int) Math.sqrt(frameCount);
		int cols = frameCount / rows;
		int extra = frameCount % rows;
		// number of columns with an extra row
		int width = desktop.getWidth() / cols;
		int height = desktop.getHeight() / rows;
		int r = 0;
		int c = 0;
		for (int i = 0; i < frames.length; i++) {
			if (!frames[i].isIcon()) {
				frames[i].reshape(c * width, r * height, width, height);
				r++;
				if (r == rows) {
					r = 0;
					c++;
					if (c == cols - extra) {
						// start adding an extra row
						rows++;
						height = desktop.getHeight() / rows;
					}
				}
			}
		}
		desktop.revalidate();
	}

	/**
	 * Handles menu command "Cascade".
	 * Code ruthlessly stolen from some guy on the Java forums. Thanks and sorry. :)
	 **/
	public void doCascadeCommand() {
		int FRAME_OFFSET = 30;
		int xpos = 0, ypos = 0;
		JInternalFrame frames[] = desktop.getAllFrames();
		int cascadeWidth = desktop.getBounds().width - 5;
		int cascadeHeight = desktop.getBounds().height - 5;
		int frameHeight = cascadeHeight - frames.length * FRAME_OFFSET;
		int frameWidth = cascadeWidth - frames.length * FRAME_OFFSET;
		for (int i = frames.length - 1; i >= 0; i--) {
			if (!frames[i].isIcon()) {
				frames[i].setLocation(xpos, ypos);
				xpos += FRAME_OFFSET;
				ypos += FRAME_OFFSET;
			}
		}
		desktop.revalidate();
	}

	/**
	 * Handles menu command "Arrange Icons".
	 **/
	public void doArrangeIconsCommand() {
		JInternalFrame[] frames = desktop.getAllFrames();
		int xpos = 0;
		int ypos = 0;
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].isIcon()) {
				JInternalFrame.JDesktopIcon icon = frames[i].getDesktopIcon();
				icon.setLocation(xpos, desktop.getHeight() - icon.getHeight());
				xpos += icon.getWidth();
			}
		}
		desktop.revalidate();
	}

	/**
	 * Handles menu command "Help Topics".
	 **/
	public void doHelpTopicsCommand() {
		File localizedHelpFile = new File("docs/help_" + locale.toString() + ".htm");
		if (localizedHelpFile.exists()) {
			BrowserControl.displayURL("file://" + localizedHelpFile.getAbsolutePath());
		} else {
			BrowserControl.displayURL("docs\\help.htm");
		}
	}

	/**
	 * Handles menu command "About".
	 * Displays a small dialog with info about the program.
	 **/
	public void doAboutCommand() {
		JOptionPane.showMessageDialog(this,
				"Tile Molester v0.21\n\nby SnowBro 2003-2005 (v0.16)\nby Dr. MefistO 2013 (v0.17.2)\nby Mewster 2014-2015 (v0.19)\nby toruzz 2020-2024 (v0.21)",
				"Tile Molester",
				1);
	}

	/**
	 * Handles menu command "Tile Codec".
	 * Changes the tile codec for the current view to the specified one.
	 * @param codec tile codec used for encode/decode
	 **/
	public void doTileCodecCommand(TileCodec codec) {
		withSelectedView(view -> {
			view.setTileCodec(codec);
			refreshPalettePane();
			refreshStatusBar();
			refreshTileCodecSelection(view);
		});
	}

	/**
	 * Handles menu command "Zoom".
	 * Zooms the current frame to the given scale (1.0 = 100%, 2.0 = 200% and so on)
	 * @param scale zoom factor applied to the canvas
	 **/
	public void doZoomCommand(double scale) {
		withSelectedView(view -> view.setScale(scale));
	}

	/**
	 * Handles menu command "Zoom In".
	 * Scale += 1.0
	 **/
	public void doZoomInCommand() {
		withSelectedView(view -> view.setScale(view.getScale() + 1.0));
	}

	/**
	 * Handles menu command "Zoom Out".
	 * Scale -= 1.0
	 **/
	public void doZoomOutCommand() {
		withSelectedView(view -> view.setScale(view.getScale() - 1.0));
	}

	/**
	 * Handles menu command "Block Grid".
	 **/
	public void doBlockGridCommand() {
		withSelectedView(view -> {
			view.setBlockGridVisible(!view.isBlockGridVisible());
			blockGridMenuItem.setSelected(view.isBlockGridVisible());
			view.repaint();
		});
	}

	/**
	 * Handles menu command "Tile Grid".
	 **/
	public void doTileGridCommand() {
		withSelectedView(view -> {
			view.setTileGridVisible(!view.isTileGridVisible());
			tileGridMenuItem.setSelected(view.isTileGridVisible());
			view.repaint();
		});
	}

	/**
	 * Handles menu command "Pixel Grid".
	 **/
	public void doPixelGridCommand() {
		withSelectedView(view -> {
			view.setPixelGridVisible(!view.isPixelGridVisible());
			pixelGridMenuItem.setSelected(view.isPixelGridVisible());
			view.repaint();
		});
	}

	/**
	 * Handles menu command "Statusbar".
	 * Toggles the statusbar visibility.
	 **/
	public void doStatusBarCommand() {
		viewStatusBar = !viewStatusBar;
		TileMolester.settings.setViewStatusBar(viewStatusBar);
		statusBar.setVisible(viewStatusBar);
		statusBarMenuItem.setSelected(viewStatusBar);
	}

	/**
	 * Handles menu command "Toolbar".
	 * Toggles the toolbar visibility.
	 **/
	public void doToolBarCommand() {
		viewToolBar = !viewToolBar;
		TileMolester.settings.setViewToolBar(viewToolBar);
		toolBarPane.setVisible(viewToolBar);
		toolBarMenuItem.setSelected(viewToolBar);
	}

	/**
	 * Handles menu command "Dark mode".
	 * Toggles the dark mode theme.
	 **/
	public void doDarkModeCommand() {
		darkMode = !TMTheme.darkMode;
		darkModeMenuItem.setSelected(darkMode);
		TMTheme.setDarkMode(darkMode);
	}

	/**
	 * Handles menu command "New Window".
	 * Creates a new view for the current one.
	 * Duplicates view settings (offset, codec, width/height etc.)
	 **/
	public void doNewWindowCommand() {
		withSelectedView(view -> {
			FileImage img = view.getFileImage();
			TMView newView = createView(img, view.getTileCodec(), view.getPalette(), view.getMode());
			newView.setPalIndex(view.getPalIndex());
			newView.setFGColor(view.getFGColor());
			newView.setBGColor(view.getBGColor());
			newView.setAbsoluteOffset(view.getOffset());
			newView.setGridSize(view.getCols(), view.getRows());
			addViewToDesktop(newView);
		});
	}

	/**
	 * Handles menu command "Mirror".
	 **/
	public void doMirrorCommand() {
		withSelectedView(view -> view.getEditorCanvas().flipSelectionHorizontally());
	}

	/**
	 * Handles menu command "Flip".
	 **/
	public void doFlipCommand() {
		withSelectedView(view -> view.getEditorCanvas().flipSelectionVertically());
	}

	/**
	 * Handles menu command "Rotate +90".
	 **/
	public void doRotateRightCommand() {
		withSelectedView(view -> view.getEditorCanvas().rotateSelectionClockwise());
	}

	/**
	 * Handles menu command "Rotate Left".
	 **/
	public void doRotateLeftCommand() {
		withSelectedView(view -> view.getEditorCanvas().rotateSelectionCounterClockwise());
	}

	/**
	 * Handles menu command "Shift Left".
	 **/
	public void doShiftLeftCommand() {
		withSelectedView(view -> view.getEditorCanvas().shiftSelectionLeft());
	}

	/**
	 * Handles menu command "Shift Right".
	 **/
	public void doShiftRightCommand() {
		withSelectedView(view -> view.getEditorCanvas().shiftSelectionRight());
	}

	/**
	 * Handles menu command "Shift Up".
	 **/
	public void doShiftUpCommand() {
		withSelectedView(view -> view.getEditorCanvas().shiftSelectionUp());
	}

	/**
	 * Handles menu command "Shift Down".
	 **/
	public void doShiftDownCommand() {
		withSelectedView(view -> view.getEditorCanvas().shiftSelectionDown());
	}

	/**
	 * Handles menu command "Stretch".
	 **/
	public void doStretchCommand() {
		withSelectedView(view -> {
			int retVal = stretchDialog.showDialog(view.getEditorCanvas().getSelectionCanvas().getCols(),
					view.getEditorCanvas().getSelectionCanvas().getRows());
			if (retVal == JOptionPane.OK_OPTION) {
				view.getEditorCanvas().stretchSelection(stretchDialog.getCols(), stretchDialog.getRows());
			}
		});
	}

	/**
	 * Handles menu command "Canvas Size".
	 **/
	public void doCanvasSizeCommand() {
		withSelectedView(view -> {
			int retVal = canvasSizeDialog.showDialog(view.getCols(), view.getRows());
			if (retVal == JOptionPane.OK_OPTION) {
				view.setGridSize(canvasSizeDialog.getCols(), canvasSizeDialog.getRows());
				view.setScale(view.getScale());
			}
		});
	}

	/**
	 * Handles menu command "Mode".
	 * Switches to the specified tile mode for the current frame.
	 * The valid modes are MODE_1D and MODE_2D.
	 * @param mode tile layout mode ({@link tm.tilecodecs.TileCodec#MODE_1D} or {@link tm.tilecodecs.TileCodec#MODE_2D})
	 **/
	public void doModeCommand(int mode) {
		withSelectedView(view -> {
			view.setMode(mode);
			refreshStatusBar();
		});
	}

	/**
	 * Handles the "SizeBlockToCanvas" menu or toolbar command.
	 **/
	public void doSizeBlockToCanvasCommand() {
		withSelectedView(view -> {
			view.setSizeBlockToCanvas(!view.getSizeBlockToCanvas());
			sizeBlockToCanvasMenuItem.setSelected(view.getSizeBlockToCanvas());
		});
	}

	/**
	 * Handles the "CustomBlockSize" menu or toolbar command.
	 **/
	public void doCustomBlockSizeCommand() {
		withSelectedView(view -> {
			int retVal = blockSizeDialog.showDialog(view.getBlockWidth(), view.getBlockHeight());
			if (retVal == JOptionPane.OK_OPTION) {
				view.setSizeBlockToCanvas(false);
				sizeBlockToCanvasMenuItem.setSelected(false);
				view.setBlockDimensions(blockSizeDialog.getCols(), blockSizeDialog.getRows());
			}
		});
	}

	/**
	 * Handles the "RowInterleaveBlocks" menu or toolbar command.
	 **/
	public void doRowInterleaveBlocksCommand() {
		withSelectedView(view -> {
			view.setRowInterleaveBlocks(!view.getRowInterleaveBlocks());
			rowInterleaveBlocksMenuItem.setSelected(view.getRowInterleaveBlocks());
		});
	}

	/**
	 * Handles the "Reopen" menu or toolbar command.
	 * @param recentFile recentFile value
	 **/
	public void doReopenCommand(File recentFile) {
		if (recentFile.exists() && recentFile.canRead()) {
			java.util.List<File> recentFiles = TileMolester.settings.getRecentFiles();
			fileOpenChooser.setFileFilter(getTileCodecFilterForFile(recentFile));
			openFile(recentFile);
			recentFiles.remove(recentFile);
			buildReopenMenu();
		}
	}

	/**
	 * Handles the menu command "Custom Codec".
	 **/
	public void doCustomCodecCommand() {
		withSelectedView(view -> {
			customCodecDialog.setVisible(true);
			int retVal = 0; // TODO
			if (retVal == JOptionPane.OK_OPTION) {
				int bpp = customCodecDialog.getBitsPerPixel();
				int rmask = customCodecDialog.getRedMask();
				int gmask = customCodecDialog.getBlueMask();
				int bmask = customCodecDialog.getGreenMask();
				int amask = customCodecDialog.getAlphaMask();
				String desc = customCodecDialog.getDescription();
				DirectColorTileCodec codec = new DirectColorTileCodec("", bpp, rmask, gmask, bmask, amask, desc);
				addTileCodec(codec);
				view.setTileCodec(codec);
			}
		});
	}

	/**
	 * Navigation button press handlers.
	 **/
	public void doHomeCommand() {
		withSelectedView(view -> view.setAbsoluteOffset(view.getMinOffset()));
	}

	/**
	 * Handles the "MinusPage" menu or toolbar command.
	 **/
	public void doMinusPageCommand() {
		withSelectedView(view -> view.setRelativeOffset(-view.getEditorCanvas().getPageIncrement()));
	}

	/**
	 * Handles the "MinusRow" menu or toolbar command.
	 **/
	public void doMinusRowCommand() {
		withSelectedView(view -> view.setRelativeOffset(-view.getEditorCanvas().getRowIncrement()));
	}

	/**
	 * Handles the "MinusTile" menu or toolbar command.
	 **/
	public void doMinusTileCommand() {
		withSelectedView(view -> view.setRelativeOffset(-view.getEditorCanvas().getTileIncrement()));
	}

	/**
	 * Handles the "MinusByte" menu or toolbar command.
	 **/
	public void doMinusByteCommand() {
		adjustOffset(-1);
	}

	/**
	 * Handles the "PlusByte" menu or toolbar command.
	 **/
	public void doPlusByteCommand() {
		adjustOffset(1);
	}

	/**
	 * Handles the "PlusTile" menu or toolbar command.
	 **/
	public void doPlusTileCommand() {
		withSelectedView(view -> view.setRelativeOffset(view.getEditorCanvas().getTileIncrement()));
	}

	/**
	 * Handles the "PlusRow" menu or toolbar command.
	 **/
	public void doPlusRowCommand() {
		withSelectedView(view -> view.setRelativeOffset(view.getEditorCanvas().getRowIncrement()));
	}

	/**
	 * Handles the "PlusPage" menu or toolbar command.
	 **/
	public void doPlusPageCommand() {
		withSelectedView(view -> view.setRelativeOffset(view.getEditorCanvas().getPageIncrement()));
	}

	/**
	 * Handles the "End" menu or toolbar command.
	 **/
	public void doEndCommand() {
		withSelectedView(view -> view.setRelativeOffset(view.getMaxOffset()));
	}

	/**
	 * Handles the menu command "Add To Bookmarks".
	 **/
	public void doAddToBookmarksCommand() {
		withSelectedView(view -> {
			int retVal = addBookmarkDialog.showDialog(view.getFileImage().getResources().getBookmarksRoot());
			if (retVal == JOptionPane.OK_OPTION) {
				FolderNode folder = addBookmarkDialog.getFolder();
				BookmarkItemNode bookmark = view.createBookmark(addBookmarkDialog.getDescription());
				folder.add(bookmark);
				refreshBookmarksMenu();
			}
		});
	}

	/**
	 * Handles the menu command "Organize Bookmarks".
	 **/
	public void doOrganizeBookmarksCommand() {
		withSelectedView(view -> {
			organizeBookmarksDialog.showDialog(view.getFileImage().getResources().getBookmarksRoot());
			refreshBookmarksMenu();
		});
	}

	/**
	 * Handles the menu command "Add To Palettes".
	 **/
	public void doAddToPalettesCommand() {
		withSelectedView(view -> {
			int retVal = addPaletteDialog.showDialog(view.getFileImage().getResources().getPalettesRoot());
			if (retVal == JOptionPane.OK_OPTION) {
				FolderNode folder = addPaletteDialog.getFolder();
				PaletteItemNode palNode = new PaletteItemNode(view.getPalette(), addPaletteDialog.getDescription());
				folder.add(palNode);
				refreshPalettesMenu();
			}
		});
	}

	/**
	 * Handles the menu command "Organize Palettes".
	 **/
	public void doOrganizePalettesCommand() {
		withSelectedView(view -> {
			organizePalettesDialog.showDialog(view.getFileImage().getResources().getPalettesRoot());
			refreshPalettesMenu();
		});
	}

	/**
	 * Handles the menu command "Edit Colors".
	 **/
	public void doEditColorsCommand() {
		withSelectedView(view -> {
			Color newColor = JColorChooser.showDialog(this, "Edit Color", new Color(view.getFGColor()));
			if (newColor != null) {
				int rgb = newColor.getRGB();
				TMPaletteVizualiser vizualiser = palettePane.getVizualiser();
				int colorIndex = vizualiser.getLastIndex();

				view.addReversibleAction(new ReversiblePaletteEditAction(view, view.getPalette(), colorIndex,
						view.getPalette().getEntryRGB(colorIndex), rgb));
				view.getPalette().setEntryRGB(colorIndex, rgb);

				this.setFGColor(rgb);
				view.refreshPaletteDisplay();
				repaint();
			}
		});
	}

	/**
	 * Handles the menu command "Format Palette".
	 * @param codec tile codec used for encode/decode
	 **/
	public void doColorCodecCommand(ColorCodec codec) {
		withSelectedView(view -> {
			view.getPalette().setCodec(codec);
			view.refreshPaletteDisplay();
			refreshPalettePane();
		});
	}

	/**
	 * Handles the menu command "Set Palette Size".
	 **/
	public void doPaletteSizeCommand() {
		withSelectedView(view -> {
			int retVal = paletteSizeDialog.showDialog(view.getPalette().getSize());
			if (retVal == JOptionPane.OK_OPTION) {
				view.getPalette().setSize(paletteSizeDialog.getPaletteSize());
				refreshPalettePane();
			}
		});
	}

	/**
	 * Handles the menu command "New Palette".
	 **/
	public void doNewPaletteCommand() {
		withSelectedView(view -> {
			int retVal = newPaletteDialog.showDialog();
			if (retVal == JOptionPane.OK_OPTION) {
				int size = newPaletteDialog.getPaletteSize();
				ColorCodec codec = newPaletteDialog.getCodec();
				int endianness = newPaletteDialog.getEndianness();

				TMPalette palette = new TMPalette("ID", size, codec, endianness);
				view.setPalette(palette);
				refreshPalettePane();
				refreshPalettesMenu();
			}
		});
	}

	/**
	 * Handles the menu command "Import Palette From This File".
	 **/
	public void doImportInternalPaletteCommand() {
		withSelectedView(view -> {
			int retVal = importInternalPaletteDialog.showDialog();
			if (retVal == JOptionPane.OK_OPTION) {
				int offset = importInternalPaletteDialog.getOffset();
				int size = importInternalPaletteDialog.getPaletteSize();
				ColorCodec codec = importInternalPaletteDialog.getCodec();
				int endianness = importInternalPaletteDialog.getEndianness();
				boolean copy = importInternalPaletteDialog.getCopy();

				byte[] data = view.getFileImage().getContents();
				TMPalette palette = new TMPalette("ID", data, offset, size, codec, endianness, copy, false);
				view.setPalette(palette);
				refreshPalettePane();
				refreshPalettesMenu();
			}
		});
	}

	/**
	 * Handles the menu command "Import Palette From Another File".
	 **/
	public void doImportExternalPaletteCommand() {
		withSelectedView(view -> {
			if (new File(this.lastPath).exists()) {
				this.paletteOpenChooser.setCurrentDirectory(new File(this.lastPath));
			} else {
				this.paletteOpenChooser.setCurrentDirectory(new File("."));
			}
			int retVal = paletteOpenChooser.showOpenDialog(this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = paletteOpenChooser.getSelectedFile();

				FileFilter ff = paletteOpenChooser.getFileFilter();
				if (!(ff instanceof TMPaletteFileFilter)) {
					ff = getPaletteFilterForFile(file);
				}
				TMPaletteFileFilter pf = (TMPaletteFileFilter) ff;

				if (isCsvPaletteImport(pf, file)) {
					importPaletteFromCsvFile(view, file);
					return;
				}

				int size = pf.getSize();
				ColorCodec codec = getColorCodecByID(pf.getCodecID());
				int offset = pf.getOffset();
				int endianness = pf.getEndianness();

				byte[] data = new byte[size * codec.getBytesPerPixel()];

				RandomAccessFile raf = null;
				try {
					raf = new RandomAccessFile(file, "r");
					raf.seek(offset);
					raf.read(data);
					raf.close();
				} catch (IOException e) {
					showError("Palette_Read_Error", e);
					return;
				}

				TMPalette palette = new TMPalette("ID", data, 0, size, codec, endianness, true, false);
				view.setPalette(palette);
				refreshPalettePane();
				refreshPalettesMenu();
			}
		});
	}

	/**
	 * Reports whether csv palette import.
	 * @return whether csv palette import
	 * @param pf pf value
	 * @param file file value
	 **/
	private boolean isCsvPaletteImport(TMPaletteFileFilter pf, File file) {
		if (pf.getSize() == 0 && "CF01".equals(pf.getCodecID())) {
			return true;
		}
		String name = file.getName().toLowerCase();
		return name.endsWith(".csv");
	}

	/**
	 * @param view file view associated with this component
	 * @param file file value
	 **/
	private void importPaletteFromCsvFile(TMView view, File file) {
		int[] rgb;
		try {
			rgb = PaletteCsvReader.read(file);
		} catch (PaletteCsvParseException e) {
			String msg = MessageFormat.format(
					xlate("Palette_Csv_Invalid_Entry"),
					Integer.valueOf(e.getEntryNumber()),
					e.getValue());
			JOptionPane.showMessageDialog(this, msg, "Tile Molester", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException e) {
			showError("Palette_Read_Error", e);
			return;
		}

		ColorCodec codec = getColorCodecByID("CF01");
		int colorCount = view.getTileCodec().getColorCount();
		int n = rgb.length;
		int pages = Math.max(1, (n + colorCount - 1) / colorCount);
		int size = pages * colorCount;

		TMPalette palette = new TMPalette("ID", size, codec, ColorCodec.BIG_ENDIAN);
		for (int i = 0; i < n; i++) {
			palette.setEntryRGB(i, rgb[i]);
		}
		for (int i = n; i < size; i++) {
			palette.setEntryRGB(i, 0x000000);
		}

		view.setPalette(palette);
		refreshPalettePane();
		refreshPalettesMenu();
	}

	/**
	 * Handles menu command "Palette Endianness".
	 * @param endianness endianness value
	 **/
	public void doPaletteEndiannessCommand(int endianness) {
		withSelectedView(view -> view.getPalette().setEndianness(endianness));
	}

	/**
	 * Called when user has selected a bookmark to jump to from the Navigate menu.
	 * @param bookmark bookmark value
	 **/
	public void doGotoBookmarkCommand(BookmarkItemNode bookmark) {
		withSelectedView(view -> view.gotoBookmark(bookmark));
	}

	/**
	 * Called when user has selected a palette to use from the Palette menu.
	 * @param palette palette whose colors are displayed or edited
	 **/
	public void doSelectPaletteCommand(TMPalette palette) {
		withSelectedView(view -> {
			view.setPalette(palette);
			refreshPalettePane();
			refreshPaletteEndiannessSelection(view);
			refreshColorCodecSelection(view);
		});
	}

	/**
	 * Handles the "DecreaseWidth" menu or toolbar command.
	 **/
	public void doDecreaseWidthCommand() {
		withSelectedView(view -> {
			view.setGridSize(view.getCols() - 1, view.getRows());
			view.setScale(view.getScale());
		});
	}

	/**
	 * Handles the "IncreaseWidth" menu or toolbar command.
	 **/
	public void doIncreaseWidthCommand() {
		withSelectedView(view -> {
			view.setGridSize(view.getCols() + 1, view.getRows());
			view.setScale(view.getScale());
		});
	}

	/**
	 * Handles the "DecreaseHeight" menu or toolbar command.
	 **/
	public void doDecreaseHeightCommand() {
		withSelectedView(view -> {
			view.setGridSize(view.getCols(), view.getRows() - 1);
			view.setScale(view.getScale());
		});
	}

	/**
	 * Handles the "IncreaseHeight" menu or toolbar command.
	 **/
	public void doIncreaseHeightCommand() {
		withSelectedView(view -> {
			view.setGridSize(view.getCols(), view.getRows() + 1);
			view.setScale(view.getScale());
		});
	}

	//////////////////////////////////////////////////////////////////////////////

	/**
	 * Call this when a fileimage has been modified.
	 * @param img img value
	 **/
	public void fileImageModified(FileImage img) {
		img.setModified(true);
		setSaveButtonsEnabled(true);
		saveAllMenuItem.setEnabled(true);
	}

	/**
	 * Sets enabled state of save buttons.
	 * @param b b value
	 **/
	public void setSaveButtonsEnabled(boolean b) {
		saveButton.setEnabled(b);
		saveMenuItem.setEnabled(b);
	}

	/**
	 * Sets enabled state of undo buttons.
	 * @param b b value
	 **/
	public void setUndoButtonsEnabled(boolean b) {
		undoButton.setEnabled(b);
		undoMenuItem.setEnabled(b);
	}

	/**
	 * Sets enabled state of redo buttons.
	 * @param b b value
	 **/
	public void setRedoButtonsEnabled(boolean b) {
		redoButton.setEnabled(b);
		redoMenuItem.setEnabled(b);
	}

	/**
	 * Hides/disables MDI-specific menus and buttons.
	 **/
	public void disableMDIStuff() {
		// Hide MDI menus
		menuBar.remove(editMenu);
		menuBar.remove(viewMenu);
		menuBar.remove(imageMenu);
		menuBar.remove(navigateMenu);
		menuBar.remove(paletteMenu);
		menuBar.remove(windowMenu);
		// Hide some File menu items
		closeMenuItem.setVisible(false);
		closeAllMenuItem.setVisible(false);
		saveMenuItem.setVisible(false);
		saveAsMenuItem.setVisible(false);
		saveAllMenuItem.setVisible(false);


		Component[] menuComponents = fileMenu.getMenuComponents();
		if (menuComponents.length >= 6) {
            menuComponents[5].setVisible(false);
        }

		// Hide some Toolbar buttons
		saveButton.setVisible(false);
		cutButton.setVisible(false);
		copyButton.setVisible(false);
		pasteButton.setVisible(false);
		undoButton.setVisible(false);
		redoButton.setVisible(false);
		gotoButton.setVisible(false);
		addBookmarkButton.setVisible(false);
		decWidthButton.setVisible(false);
		incWidthButton.setVisible(false);
		decHeightButton.setVisible(false);
		incHeightButton.setVisible(false);
		// Hide navigation bar
		navBar.setVisible(false);
		// Hide tool pane
		toolPane.setVisible(false);
		// hide bottom pane
		bottomPane.setVisible(false);
	}

	/**
	 * Shows/enables MDI-specific menus and buttons.
	 **/
	public void enableMDIStuff() {
		// Show MDI menus
		menuBar.remove(helpMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(imageMenu);
		menuBar.add(navigateMenu);
		menuBar.add(paletteMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		// Show File menu items
		closeMenuItem.setVisible(true);
		closeAllMenuItem.setVisible(true);
		saveMenuItem.setVisible(true);
		saveAsMenuItem.setVisible(true);
		saveAllMenuItem.setVisible(true);
		saveAllMenuItem.setEnabled(false);

		Component[] menuComponents = fileMenu.getMenuComponents();
		if (menuComponents.length >= 6) {
            menuComponents[5].setVisible(true);
        }

		// TODO: Enable previously hidden menu items w/ key accelerators
		// Show Toolbar buttons
		saveButton.setVisible(true);
		cutButton.setVisible(true);
		copyButton.setVisible(true);
		pasteButton.setVisible(true);
		undoButton.setVisible(true);
		redoButton.setVisible(true);
		gotoButton.setVisible(true);
		addBookmarkButton.setVisible(true);
		decWidthButton.setVisible(true);
		incWidthButton.setVisible(true);
		decHeightButton.setVisible(true);
		incHeightButton.setVisible(true);
		// disable some buttons
		saveButton.setEnabled(false);
		pasteButton.setEnabled(false);
		pasteMenuItem.setEnabled(false);
		undoButton.setEnabled(false);
		redoButton.setEnabled(false);
		// Show navigation bar
		navBar.setVisible(true);
		// Show tool pane
		toolPane.setVisible(true);
		// Maybe show statusbar
		statusBar.setVisible(viewStatusBar);
		// show bottom pane
		bottomPane.setVisible(true);
	}

	/**
	 * Adds a codec to the list of available codecs and creates a menu item for it.
	 * @param codec tile codec used for encode/decode
	 **/
	public void addTileCodec(TileCodec codec) {
		TMTileCodecMenuItem codecMenuItem = new TMTileCodecMenuItem(codec, this::doTileCodecCommand);
		tileCodecMenu.add(codecMenuItem);
		tileCodecButtonGroup.add(codecMenuItem);
		tileCodecButtonHashtable.put(codec, codecMenuItem);
	}

	/**
	 * Gets the "successor" of the given codec, which is the next codec in
	 * the global list of codecs (with wraparound).
	 * @return tile codec successor
	 * @param codec tile codec used for encode/decode
	 **/
	public TileCodec getTileCodecSuccessor(TileCodec codec) {
		int i = tilecodecs.indexOf(codec);
		if (i == tilecodecs.size() - 1) {
			return tilecodecs.get(0);
		} else {
			return tilecodecs.get(i + 1);
		}
	}

	/**
	 * Gets the "predecessor" of the given codec, which is the previous codec in
	 * the global list of codecs (with wraparound).
	 * @return tile codec predecessor
	 * @param codec tile codec used for encode/decode
	 **/
	public TileCodec getTileCodecPredecessor(TileCodec codec) {
		int i = tilecodecs.indexOf(codec);
		if (i == 0) {
			return tilecodecs.get(tilecodecs.size() - 1);
		} else {
			return tilecodecs.get(i - 1);
		}
	}

	/**
	 * Gets the foreground color for the current view.
	 * @return foreground draw color
	 **/
	public int getFGColor() {
		TMView view = getSelectedView();
		if (view != null) {
			return view.getFGColor();
		}
		return 0;
	}

	/**
	 * Gets the background color for the current view.
	 * @return background draw color
	 **/
	public int getBGColor() {
		TMView view = getSelectedView();
		if (view != null) {
			return view.getBGColor();
		}
		return 0;
	}

	/**
	 * Sets the foreground color for the current view.
	 * @param fgColor foreground draw color as 32-bit ARGB
	 **/
	public void setFGColor(int fgColor) {
		withSelectedView(view -> {
			view.setFGColor(fgColor);
			palettePane.setFGColor(fgColor);
		});
	}

	/**
	 * Sets the background color for the current view.
	 * @param bgColor background color used to clear pixels
	 **/
	public void setBGColor(int bgColor) {
		withSelectedView(view -> {
			view.setBGColor(bgColor);
			palettePane.setBGColor(bgColor);
		});
	}

	/**
	 * Sets the palette index for the current view.
	 * @param palIndex palette page index
	 **/
	public void setPalIndex(int palIndex) {
		withSelectedView(view -> view.setPalIndex(palIndex));
	}

	/**
	 * Gets the current tool.
	 * @return active drawing tool
	 **/
	public TMTools.ToolType getToolType() {
		return toolType;
	}

	/**
	 * Gets the desktop.
	 * @return MDI desktop pane
	 **/
	public JDesktopPane getDesktop() {
		return desktop;
	}

	/**
	 * Gets the color index.
	 * @return base palette index for the current page
	 * @param palIndex palette page index
	 * @param bpp bpp value
	 **/
	public static int getColorIndex(int palIndex, int bpp) {
		if (bpp > 8)
			bpp = 8;
		int cols = 1 << bpp;
		return palIndex * cols;
	}

	/**
	 * Creates a view with the given resources/attributes.
	 * @return create view
	 * @param img img value
	 * @param tc tc value
	 * @param pal pal value
	 * @param mode tile layout mode ({@link tm.tilecodecs.TileCodec#MODE_1D} or {@link tm.tilecodecs.TileCodec#MODE_2D})
	 **/
	public TMView createView(FileImage img, TileCodec tc, TMPalette pal, int mode) {
		TMView view = new TMView(this, img, tc);
		view.setMode(mode);
		view.setPalette(pal);
		return view;
	}

	/**
	 * Adds a view to the desktop.
	 * @param view file view associated with this component
	 **/
	public void addViewToDesktop(TMView view) {
		desktop.add(view);
		try {
			view.setSelected(true);
		} catch (java.beans.PropertyVetoException x) {
			x.printStackTrace();
		}
		desktop.revalidate();
		desktop.repaint();

		if (desktop.getAllFrames().length == 1) {
			// this is the first frame, show the MDI toolbars and menus
			enableMDIStuff();
		}
	}

	/**
	 * Initializes the View->Codec menu based on the tilecodecs present, and sets up
	 * the fileOpenChooser accordingly.
	 **/
	private void initTileCodecUIStuff() {
		buildTileCodecsMenu();
		initFileOpenChooser();
	}

	/**
	 * Builds the View->Codec menu.
	 **/
	private void buildTileCodecsMenu() {
		tileCodecMenu.setMnemonic(KeyEvent.VK_C);
		tileCodecMenu.removeAll();
		for (int i = 0; i < tilecodecs.size(); i++) {
			addTileCodec(tilecodecs.get(i));
		}

	}

	/**
	 * Builds the Palette->Format menu.
	 **/
	private void buildColorCodecsMenu() {
		colorCodecMenu.setMnemonic(KeyEvent.VK_F);
		colorCodecMenu.removeAll();
		for (int i = 0; i < colorcodecs.size(); i++) {
			addColorCodec(colorcodecs.get(i));
		}
	}

	/**
	 * Adds a codec to the list of available codecs and creates a menu item for it.
	 * @param codec tile codec used for encode/decode
	 **/
	public void addColorCodec(ColorCodec codec) {
		TMColorCodecMenuItem codecMenuItem = new TMColorCodecMenuItem(codec, this::doColorCodecCommand);
		colorCodecMenu.add(codecMenuItem);
		colorCodecButtonGroup.add(codecMenuItem);
		colorCodecButtonHashtable.put(codec, codecMenuItem);
	}

	/**
	 * Sets up the file open chooser.
	 **/
	private void initFileOpenChooser() {
		fileOpenChooser.setAcceptAllFileFilterUsed(false);
		fileOpenChooser.resetChoosableFileFilters();
		ArrayList<TMTileCodecFileFilter> sortedFileFilters = new ArrayList<>();
		for (int i = 0; i < filefilters.size(); i++) {
			sortedFileFilters.add(filefilters.get(i));
		}
		Collections.sort(sortedFileFilters,
				(a, b) -> a.getDescription().compareToIgnoreCase(b.getDescription()));
		String extlist = "";
		for (int i = 0; i < sortedFileFilters.size(); i++) {
			TMTileCodecFileFilter cff = sortedFileFilters.get(i);
			fileOpenChooser.addChoosableFileFilter(cff);
			if (i > 0)
				extlist += ",";
			extlist += cff.getExtlist();
		}
		TMFileFilter supportedFilter = new TMFileFilter(extlist, xlate("All_Supported_Formats"));
		fileOpenChooser.addChoosableFileFilter(supportedFilter);
		fileOpenChooser.addChoosableFileFilter(allFilter);
		fileOpenChooser.setFileFilter(supportedFilter);
	}

	/**
	 * Loads tmspec.xml from the working directory, or from the classpath if missing.
	 * @return resolve tmspec file
	 * @throws IOException if the operation fails
	 **/
	private static File resolveTmspecFile() throws IOException {
		File cwdSpec = new File("tmspec.xml");
		if (cwdSpec.isFile()) {
			return cwdSpec;
		}
		java.net.URL url = TMUI.class.getResource("/tmspec.xml");
		if (url == null) {
			return cwdSpec;
		}
		if ("file".equals(url.getProtocol())) {
			return new File(url.getPath());
		}
		File temp = File.createTempFile("tmspec", ".xml");
		temp.deleteOnExit();
		try (InputStream in = url.openStream();
				FileOutputStream out = new FileOutputStream(temp)) {
			byte[] buf = new byte[4096];
			int n;
			while ((n = in.read(buf)) > 0) {
				out.write(buf, 0, n);
			}
		}
		return temp;
	}

	/**
	 * Sets up the palette open chooser.
	 * @return palette filter sort rank value
	 * @param pff pff value
	 **/
	private static int paletteFilterSortRank(TMPaletteFileFilter pff) {
		String ext = pff.getExtlist();
		if ("csv".equals(ext)) {
			return 0;
		}
		if (ext.indexOf("col") >= 0) {
			return 1;
		}
		if ("tpl".equals(ext)) {
			return 2;
		}
		if ("pal".equals(ext) && "RIFF".equals(pff.getCodecID())) {
			return 3;
		}
		return 4;
	}

	/**
	 *
	 **/
	private void initPaletteOpenChooser() {
		paletteOpenChooser.setAcceptAllFileFilterUsed(false);
		paletteOpenChooser.resetChoosableFileFilters();
		ArrayList<TMPaletteFileFilter> sortedPaletteFilters = new ArrayList<>();
		for (int i = 0; i < palettefilters.size(); i++) {
			sortedPaletteFilters.add(palettefilters.get(i));
		}
		Collections.sort(sortedPaletteFilters, (a, b) -> {
			int ra = paletteFilterSortRank(a);
			int rb = paletteFilterSortRank(b);
			if (ra != rb) {
				return ra - rb;
			}
			return a.getDescription().compareToIgnoreCase(b.getDescription());
		});
		String extlist = "";
		for (int i = 0; i < sortedPaletteFilters.size(); i++) {
			TMPaletteFileFilter pff = sortedPaletteFilters.get(i);
			paletteOpenChooser.addChoosableFileFilter(pff);
			if (i > 0) {
				extlist += ",";
			}
			extlist += pff.getExtlist();
		}
		TMFileFilter supportedFilter = new TMFileFilter(extlist, xlate("All_Supported_Formats"));
		paletteOpenChooser.addChoosableFileFilter(supportedFilter);
		paletteOpenChooser.setFileFilter(sortedPaletteFilters.get(0));
	}

	/**
	 * Gets the color codec that has the specified ID, or null if no such codec
	 * exists.
	 * @return color codec by id
	 * @param codecID codecID value
	 **/
	public ColorCodec getColorCodecByID(String codecID) {
		for (int i = 0; i < colorcodecs.size(); i++) {
			ColorCodec cc = colorcodecs.get(i);
			if (cc.getID().equals(codecID)) {
				return cc;
			}
		}
		return null;
	}

	/**
	 * Gets the tile codec that has the specified ID, or null if no such codec
	 * exists.
	 * @return tile codec by id
	 * @param codecID codecID value
	 **/
	public TileCodec getTileCodecByID(String codecID) {
		for (int i = 0; i < tilecodecs.size(); i++) {
			TileCodec tc = tilecodecs.get(i);
			if (tc.getID().equals(codecID)) {
				return tc;
			}
		}
		return null;
	}

	/**
	 * Gets the default tile codec file filter for the specified file
	 * based on its extension.
	 * @return tile codec filter for file
	 * @param file file value
	 **/
	private TMTileCodecFileFilter getTileCodecFilterForFile(File file) {
		for (int i = 0; i < filefilters.size(); i++) {
			TMTileCodecFileFilter cff = filefilters.get(i);
			if (cff.accept(file)) {
				return cff;
			}
		}
		return filefilters.get(0);
	}

	/**
	 * Gets the default palette file filter for the specified file
	 * based on its extension.
	 * @return palette filter for file
	 * @param file file value
	 **/
	private TMPaletteFileFilter getPaletteFilterForFile(File file) {
		for (int i = 0; i < palettefilters.size(); i++) {
			TMPaletteFileFilter pff = palettefilters.get(i);
			if (pff.accept(file)) {
				return pff;
			}
		}
		return palettefilters.get(0);
	}

	/**
	 * Builds the menu containing all the bookmarks.
	 * @param root root value
	 **/

	/**
	 * Recursive routine that adds the given node to the given menu.
	 * If the node is internal it is expanded into a menu of its own.
	 * @param node node value
	 * @param menu menu value
	 **/

	/**
	 * Builds the menu containing all the palettes.
	 * @param root root value
	 **/

	/**
	 * Recursive routine that adds the given node to the given menu.
	 * If the node is internal it is expanded into a menu of its own.
	 * @param node node value
	 * @param menu menu value
	 **/

	/**
	 * Updates various UI components (menus, statusbar, palette) to reflect the
	 * settings of the current frame.
	 * @param view file view associated with this component
	 **/
	public void viewSelected(TMView view) {
		setSaveButtonsEnabled(view.getFileImage().isModified());

		// (un)check some menu items
		TMEditorCanvas ec = view.getEditorCanvas();
		blockGridMenuItem.setSelected(ec.isBlockGridVisible());
		tileGridMenuItem.setSelected(ec.isTileGridVisible());
		pixelGridMenuItem.setSelected(ec.isPixelGridVisible());
		rowInterleaveBlocksMenuItem.setSelected(ec.getRowInterleaveBlocks());

		refreshModeSelection(view);
		refreshTileCodecSelection(view);
		refreshBlockSizeSelection(view);
		refreshPalettePane();
		refreshStatusBar();
		refreshBookmarksMenu();
		refreshPalettesMenu();
		refreshUndoRedo();

		setTitle("Tile Molester - " + view.getTitle());
	}

	/**
	 * Selects the correct menu item, according to the view's block size.
	 * @param view file view associated with this component
	 **/
	public void refreshBlockSizeSelection(TMView view) {
		sizeBlockToCanvasMenuItem.setSelected(view.getSizeBlockToCanvas());
	}

	/**
	 * Selects the correct menu item, according to the view's mode.
	 * @param view file view associated with this component
	 **/
	public void refreshModeSelection(TMView view) {
		// select the correct mode menu item
		if (view.getMode() == TileCodec.MODE_1D) {
			_1DimensionalMenuItem.setSelected(true);
		} else {
			_2DimensionalMenuItem.setSelected(true);
		}
	}

	/**
	 * Selects the correct menu item, according to the view's tile codec.
	 * @param view file view associated with this component
	 **/
	public void refreshTileCodecSelection(TMView view) {
		tileCodecButtonHashtable.get(view.getTileCodec()).setSelected(true);
	}

	/**
	 * Reloads the palette.
	 **/
	public void refreshPalettePane() {
		TMView view = getSelectedView();
		if (view != null) {
			palettePane.viewSelected(view);
		}
	}

	/**
	 * Updates the Undo/Redo buttons text+status.
	 **/
	public void refreshUndoRedo() {
		TMView view = getSelectedView();
		if (view != null) {
			setUndoButtonsEnabled(view.canUndo());
			if (view.canUndo()) {
				undoMenuItem.setText(xlate("Undo") + " " + xlate(view.getFirstUndoableAction().getPresentationName()));
			} else {
				undoMenuItem.setText(xlate("Cant_Undo"));
			}
			undoButton.setToolTipText(undoMenuItem.getText());

			setRedoButtonsEnabled(view.canRedo());
			if (view.canRedo()) {
				redoMenuItem.setText(xlate("Redo") + " " + xlate(view.getFirstRedoableAction().getPresentationName()));
			} else {
				redoMenuItem.setText(xlate("Cant_Redo"));
			}
			redoButton.setToolTipText(redoMenuItem.getText());
		}
	}

	/**
	 * Sets the statusbar fields according to current view settings.
	 **/
	public void refreshStatusBar() {
		TMView view = getSelectedView();
		if (view != null) {
			statusBar.viewSelected(view);
		}
	}

	/**
	 * Hide the statusbar coordenates.
	 **/
	public void hideStatusBarCoords() {
		statusBar.setCoords("");
	}

	/**
	 * Builds the bookmarks menu according to current file image.
	 **/
	public void refreshBookmarksMenu() {
		TMView view = getSelectedView();
		if (view != null && view.getFileImage().getResources() != null) {
			treeMenuBuilder.buildBookmarksMenu(view.getFileImage().getResources().getBookmarksRoot());
		}
	}

	/**
	 * Builds the palettes menu according to current file image.
	 **/
	public void refreshPalettesMenu() {
		TMView view = getSelectedView();
		if (view != null && view.getFileImage().getResources() != null) {
			treeMenuBuilder.buildPalettesMenu(view.getFileImage().getResources().getPalettesRoot());
			refreshPaletteSelection(view);
			refreshPaletteEndiannessSelection(view);
			refreshColorCodecSelection(view);
		}
	}

	/**
	 * Refreshes the palette selection.
	 * @param view file view associated with this component
	 **/
	public void refreshPaletteSelection(TMView view) {
		TMPaletteMenuItem item = paletteButtonHashtable.get(view.getPalette());
		if (item != null) {
			item.setSelected(true);
		} else {
			dummyPaletteMenuItem.setSelected(true);
		}
	}

	/**
	 * Refreshes the palette endianness.
	 * @param view file view associated with this component
	 **/
	public void refreshPaletteEndiannessSelection(TMView view) {
		if (view.getPalette().getEndianness() == ColorCodec.LITTLE_ENDIAN) {
			paletteLittleEndianMenuItem.setSelected(true);
		} else {
			paletteBigEndianMenuItem.setSelected(true);
		}
	}

	/**
	 * Selects the correct menu item, according to the view's color codec.
	 * @param view file view associated with this component
	 **/
	public void refreshColorCodecSelection(TMView view) {
		colorCodecButtonHashtable.get(view.getPalette().getCodec()).setSelected(true);
	}

	/**
	 * Opens the specified file.
	 * @param file file value
	 **/
	public void openFile(File file) {
		System.gc();
		// read file
		FileLoaderThread thread = null;
		try {
			thread = new FileLoaderThread(file);
		} catch (OutOfMemoryError e) {
			showError("Out_Of_Memory", file.length() + " bytes needed to load file.");
			return;
		} catch (FileNotFoundException e) {
			showError("Load_File_Error", e);
			return;
		}
		ProgressDialog dialog = new ProgressDialog(this, thread);
		byte[] contents = thread.getContents();

		// see if a filelistener should receive notification
		String ext = TMFileFilter.getExtension(file);
		for (int i = 0; i < filelisteners.size(); i++) {
			TMFileListener fl = filelisteners.get(i);
			if (fl.doFormatDetect(contents, ext)) {
				fileListenerHashtable.put(contents, fl);
				fl.fileLoaded(contents, ext);
				break;
			}
		}

		// create fileimage
		FileImage img = new FileImage(file, contents);
		// create resources for it
		File resourceFile = TMFileResources.getResourceFileFor(file);
		if (resourceFile.exists() && resourceFile.length() > 0) {
			// load the resources from XML document
			try {
				new TMFileResources(resourceFile, img, this);
			} catch (SAXException e) {
				showError("Parser_Parse_Error", e);
			} catch (ParserConfigurationException e) {
				showError("Parser_Config_Error", e);
			} catch (IOException e) {
				showError("Parser_IO_Error", e);
			}
		} else {
			// create default resources
			new TMFileResources(img, this);
		}
		// figure out mode and codec based on file filter
		FileFilter ff = fileOpenChooser.getFileFilter();
		if (!(ff instanceof TMTileCodecFileFilter)) {
			ff = getTileCodecFilterForFile(file);
		}
		int mode = ((TMTileCodecFileFilter) ff).getDefaultMode();
		TileCodec tc = getTileCodecByID(((TMTileCodecFileFilter) ff).getCodecID());
		// hardcode 4bpp planar for opened files
		TileCodec forcedTc = getTileCodecByID("PL03");
		if (forcedTc != null) {
			tc = forcedTc;
		}
		TMPalette pal = new TMPalette("PAL000", TMPalette.defaultPalette, getColorCodecByID("CF01"),
				ColorCodec.LITTLE_ENDIAN, true);

		TMView view = createView(img, tc, pal, mode);
		view.setGridSize(3, 36);
		addViewToDesktop(view);

		java.util.List<File> recentFiles = TileMolester.settings.getRecentFiles();
		// Remove file from recentFiles, if it's there
		for (int i = 0; i < recentFiles.size(); i++) {
			File f = recentFiles.get(i);
			if (f.compareTo(file) == 0) {
				recentFiles.remove(f);
				buildReopenMenu();
				break;
			}
		}

		thread.killContentsRef();
		thread = null;
		System.gc();
	}

	/**
	 * Builds the menu containing most recently opened (closed) files.
	 **/
	public void buildReopenMenu() {
		reopenMenu.removeAll();
		java.util.List<File> recentFiles = TileMolester.settings.getRecentFiles();
		if (recentFiles.size() == 0) {
			JMenuItem emptyItem = new JMenuItem("(" + xlate("Empty") + ")");
			emptyItem.setEnabled(false);
			reopenMenu.add(emptyItem);
		} else {
			for (int i = 0; i < recentFiles.size(); i++) {
				File recentFile = recentFiles.get(i);
				reopenMenu.add(new TMRecentFileMenuItem(recentFile, this::doReopenCommand));
			}
		}
	}

	/**
	 * Gets the color codecs.
	 * @return color codecs
	 **/
	public ColorCodec[] getColorCodecs() {
		ColorCodec[] ccs = new ColorCodec[colorcodecs.size()];
		for (int i = 0; i < ccs.length; i++) {
			ccs[i] = colorcodecs.get(i);
		}
		return ccs;
	}


	/**
	 * Attempts to translate the given key string by consulting a ResourceBundle.
	 * If no corresponding value is found, the key itself is returned.
	 * @return localized string for the given key, or the key itself if missing
	 * @param key property key or translation key
	 **/
	public String xlate(String key) {
		try {
			String value = xl.xlate(key);
			return value;
		} catch (NullPointerException e) {
			return key;
		}
	}

	/**
	 * Runs an action on the currently selected view, if one exists.
	 * @param action callback receiving the active {@link TMView}
	 **/
	private void withSelectedView(Consumer<TMView> action) {
		TMView view = getSelectedView();
		if (view != null) {
			action.accept(view);
		}
	}

	/**
	 * Adjusts the file offset of the selected view by the given delta.
	 * @param delta bytes to add to the current offset (negative to move back)
	 **/
	private void adjustOffset(int delta) {
		withSelectedView(view -> view.setRelativeOffset(delta));
	}

	/**
	 * Shows an error dialog with a translated message and optional detail text.
	 * @param messageKey resource key for the primary message
	 * @param detail additional detail appended on a new line, or null
	 **/
	private void showError(String messageKey, String detail) {
		String message = xlate(messageKey);
		if (detail != null && !detail.isEmpty()) {
			message = message + "\n" + detail;
		}
		JOptionPane.showMessageDialog(this, message, "Tile Molester", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Shows an error dialog with a translated message and exception detail.
	 * @param messageKey resource key for the primary message
	 * @param e exception whose message is appended on a new line
	 **/
	private void showError(String messageKey, Exception e) {
		showError(messageKey, e.getMessage());
	}

	/**
	 * Gets the selected view frame.
	 * @return currently selected file view
	 **/
	public TMView getSelectedView() {
		return (TMView) desktop.getSelectedFrame();
	}

	/**
	 * Adds the given file to the list of recently opened (closed) files.
	 * @param f f value
	 **/
	public void addToRecentFiles(File f) {
		java.util.List<File> recentFiles = TileMolester.settings.getRecentFiles();
		// make sure it's not already in the list
		for (int i = 0; i < recentFiles.size(); i++) {
			File rf = recentFiles.get(i);
			if (rf.compareTo(f) == 0) {
				recentFiles.remove(i);
				break;
			}
		}
		// add it
		recentFiles.add(0, f);
		// check for "overflow"
		int maxRecentFiles = TileMolester.settings.getMaxRecentFiles();
		if (recentFiles.size() > maxRecentFiles) {
			recentFiles.remove(maxRecentFiles - 1);
		}
		TileMolester.settings.setRecentFiles(recentFiles);
	}
}