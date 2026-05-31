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
	public static final boolean isWindows = SystemInfo.isWindows;

	/** Active drawing tool (toolbar + {@link tm.canvases.TMEditorCanvas}). */
	TMTools.ToolType toolType = TMTools.ToolType.SELECT_TOOL;

	/** Cut/copy buffer for paste between views. */
	TMSelectionCanvas copiedSelection;

	/** Loaded from tmspec.xml; used for menus, choosers, and file open. */
	private final java.util.List<ColorCodec> colorcodecs = new ArrayList<>();
	private final java.util.List<TileCodec> tilecodecs = new ArrayList<>();
	private final java.util.List<TMTileCodecFileFilter> filefilters = new ArrayList<>();
	private final java.util.List<TMPaletteFileFilter> palettefilters = new ArrayList<>();
	private final java.util.List<TMFileListener> filelisteners = new ArrayList<>();

	final TMUIWidgets widgets;
	TMUITreeMenuBuilder treeMenuBuilder;

	private Xlator xl;
	Locale locale;
	String lastPath;
	boolean viewStatusBar = true;
	boolean viewToolBar = true;
	boolean darkMode = TMTheme.darkMode;

	/** Public for {@link tm.canvases} and action classes outside this file. */
	public final TMUIFileActions fileActions;
	public final TMUIEditActions editActions;
	public final TMUINavActions navActions;
	public final TMUIPaletteActions paletteActions;
	public final TMUIRefresh refresh;
	public final TMUIViewActions viewActions;
	public final TMUIImageActions imageActions;
	public final TMUIWindowActions windowActions;
	public final TMUIHelpActions helpActions;

	/**
	 * Creates a Tile Molester UI.
	 **/
	public TMUI() {
		super("Tile Molester");
		TMLog.setDialogParent(this);

		ImageIcon imgIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/TMIcon32.png"));
		setIconImage(imgIcon.getImage());
		
		locale = TileMolester.settings.getLocale();
		lastPath = TileMolester.settings.getLastPath();
		viewStatusBar = TileMolester.settings.viewStatusBar;
		viewToolBar = TileMolester.settings.viewToolBar;
		// create a translator
		try {
			xl = new Xlator("languages/language", locale);
		} catch (MissingResourceException e) {
			showError("Error reading language file:", e);
			System.exit(0);
		}

		setLocale(locale);
		Locale.setDefault(locale);
		JComponent.setDefaultLocale(this.locale);

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

		colorcodecs.addAll(TMSpecReader.getColorCodecs());
		tilecodecs.addAll(TMSpecReader.getTileCodecs());
		filefilters.addAll(TMSpecReader.getFileFilters());
		palettefilters.addAll(TMSpecReader.getPaletteFilters());
		filelisteners.addAll(TMSpecReader.getFileListeners());

		tilecodecs.add(new _3BPPLinearTileCodec());
		tilecodecs.add(new _6BPPLinearTileCodec());

		widgets = TMUIWidgets.create(new TMUIWidgetsBootstrap(
				this, xl, getColorCodecs(), getFileFilters(), getPaletteFilters()));
		applyMenuLabels();

		TMUIMenuBuilder menuBuilder = new TMUIMenuBuilder(this);
		TMUIToolbarBuilder toolbarBuilder = new TMUIToolbarBuilder(this);
		treeMenuBuilder = new TMUITreeMenuBuilder(this);
		fileActions = new TMUIFileActions(this);
		editActions = new TMUIEditActions(this);
		navActions = new TMUINavActions(this);
		paletteActions = new TMUIPaletteActions(this);
		refresh = new TMUIRefresh(this);
		viewActions = new TMUIViewActions(this);
		imageActions = new TMUIImageActions(this);
		windowActions = new TMUIWindowActions(this);
		helpActions = new TMUIHelpActions(this);

		// Set up the GUI.
		// main contentpane
		JPanel pane = new JPanel();
		setContentPane(pane);
		pane.setDoubleBuffered(true);
		pane.setLayout(new BorderLayout());

		// main toolbar
		toolbarBuilder.buildToolBar();
		toolbarBuilder.buildNavBar();
		widgets.toolBarPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		widgets.toolBarPane.add(widgets.toolBar);
		widgets.toolBarPane.add(widgets.toolBarMDI);
		widgets.toolBarPane.add(widgets.navBar);
		pane.add(widgets.toolBarPane, BorderLayout.NORTH);

		// widgets.desktop
		pane.add(new JScrollPane(widgets.desktop), BorderLayout.CENTER);

		// palette pane & statusbar
		widgets.bottomPane.setLayout(new BorderLayout());
		widgets.bottomPane.add(widgets.palettePane, BorderLayout.CENTER);
		widgets.bottomPane.add(widgets.statusBar, BorderLayout.SOUTH);
		pane.add(widgets.bottomPane, BorderLayout.SOUTH);

		JPanel barPane = new JPanel();
		// tool palettes
		toolbarBuilder.buildToolPalette();
		toolbarBuilder.buildSelectionToolBar();
		//barPane.setLayout(new GridLayout(1, 2));

		barPane.add(widgets.selectionToolBar);
		barPane.add(widgets.toolPalette);
		widgets.toolPane.setLayout(new BorderLayout());
		widgets.toolPane.add(barPane, BorderLayout.NORTH);
		pane.add(widgets.toolPane, BorderLayout.WEST);

		// menus
		menuBuilder.buildMenuBar();
		setJMenuBar(widgets.menuBar);
		fileActions.buildReopenMenu();

		initTileCodecUIStuff();
		buildColorCodecsMenu();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			/**
			 * Handles the user request to close the main window.
			 * @param e event from the AWT/Swing listener
			 **/
			public void windowClosing(WindowEvent e) {
				fileActions.doExitCommand();
			}

			/**
			 * Restores normal window state when the frame is activated.
			 * @param e event from the AWT/Swing listener
			 **/
			public void windowActivated(WindowEvent e) {
				setExtendedState(JFrame.NORMAL); // Hacky way to make it not run in full screen by default
			}
		});

		// Center the frame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int finalWidth = (screenSize.width > TMUIConstants.MAX_WINDOW_WIDTH ? TMUIConstants.MAX_WINDOW_WIDTH
				: screenSize.width) - TMUIConstants.WINDOW_INSET * 2;
		int finalHeight = (screenSize.height > TMUIConstants.MAX_WINDOW_HEIGHT ? TMUIConstants.MAX_WINDOW_HEIGHT
				: screenSize.height) - TMUIConstants.WINDOW_INSET * 2;
		setBounds((screenSize.width - finalWidth) / 2,
				(screenSize.height - finalHeight) / 2,
				finalWidth,
				finalHeight);

		// MDI menus and such shouldn't be shown until file loaded.
		refresh.setMdiMode(false);

		widgets.toolBarPane.setVisible(viewToolBar);
		widgets.statusBar.setVisible(viewStatusBar);
		widgets.statusBarMenuItem.setSelected(viewStatusBar);
		widgets.toolBarMenuItem.setSelected(viewToolBar);

		com.formdev.flatlaf.FlatLaf.updateUI();
		// Show and maximize.
		setVisible(true);
	}

	/** Applies localized labels to menu items and JOptionPane buttons. */
	private void applyMenuLabels() {
		// File menu
		widgets.fileMenu.setText(xlate("File"));
		widgets.newMenuItem.setText(xlate("New"));
		widgets.openMenuItem.setText(xlate("Open"));
		widgets.reopenMenu.setText(xlate("Reopen"));
		widgets.closeMenuItem.setText(xlate("Close"));
		widgets.closeAllMenuItem.setText(xlate("Close_All"));
		widgets.saveMenuItem.setText(xlate("Save"));
		widgets.saveAsMenuItem.setText(xlate("Save_As"));
		widgets.saveAllMenuItem.setText(xlate("Save_All"));
		widgets.exitMenuItem.setText(xlate("Exit"));
		// Edit menu
		widgets.editMenu.setText(xlate("Edit"));
		widgets.undoMenuItem.setText(xlate("Undo"));
		widgets.redoMenuItem.setText(xlate("Redo"));
		widgets.cutMenuItem.setText(xlate("Cut"));
		widgets.copyMenuItem.setText(xlate("Copy"));
		widgets.pasteMenuItem.setText(xlate("Paste"));
		widgets.clearMenuItem.setText(xlate("Clear"));
		widgets.selectAllMenuItem.setText(xlate("Select_All"));
		widgets.copyToMenuItem.setText(xlate("Export_As"));
		widgets.pasteFromMenuItem.setText(xlate("Paste_From"));
		widgets.newSelectionMenuItem.setText(xlate("New_Selection"));
		widgets.applySelectionMenuItem.setText(xlate("Apply_Selection"));
		// Image menu
		widgets.imageMenu.setText(xlate("Image"));
		widgets.mirrorMenuItem.setText(xlate("Mirror"));
		widgets.flipMenuItem.setText(xlate("Flip"));
		widgets.rotateRightMenuItem.setText(xlate("Rotate_Right"));
		widgets.rotateLeftMenuItem.setText(xlate("Rotate_Left"));
		widgets.shiftLeftMenuItem.setText(xlate("Shift_Left"));
		widgets.shiftRightMenuItem.setText(xlate("Shift_Right"));
		widgets.shiftUpMenuItem.setText(xlate("Shift_Up"));
		widgets.shiftDownMenuItem.setText(xlate("Shift_Down"));
		widgets.canvasSizeMenuItem.setText(xlate("Canvas_Size"));
		widgets.stretchMenuItem.setText(xlate("Stretch"));
		// View menu
		widgets.viewMenu.setText(xlate("View"));
		widgets.statusBarMenuItem.setText(xlate("Statusbar"));
		widgets.toolBarMenuItem.setText(xlate("Toolbar"));
		widgets.darkModeMenuItem.setText(xlate("Dark_Mode"));
		widgets.tileCodecMenu.setText(xlate("Codec"));
		widgets.zoomMenu.setText(xlate("Zoom"));
		widgets.zoomInMenuItem.setText(xlate("In"));
		widgets.zoomOutMenuItem.setText(xlate("Out"));
		widgets._100MenuItem.setText(xlate("100%"));
		widgets._200MenuItem.setText(xlate("200%"));
		widgets._400MenuItem.setText(xlate("400%"));
		widgets._800MenuItem.setText(xlate("800%"));
		widgets._1600MenuItem.setText(xlate("1600%"));
		widgets._3200MenuItem.setText(xlate("3200%"));
		widgets.modeMenu.setText(xlate("Mode"));
		widgets._1DimensionalMenuItem.setText(xlate("1_Dimensional"));
		widgets._2DimensionalMenuItem.setText(xlate("2_Dimensional"));
		widgets.blockSizeMenu.setText(xlate("Block_Size"));
		widgets.sizeBlockToCanvasMenuItem.setText(xlate("Full_Canvas"));
		widgets.customBlockSizeMenuItem.setText(xlate("Custom_Block_Size"));
		widgets.rowInterleaveBlocksMenuItem.setText(xlate("Row_Interleave_Blocks"));
		widgets.blockGridMenuItem.setText(xlate("Block_Grid"));
		widgets.tileGridMenuItem.setText(xlate("Tile_Grid"));
		widgets.pixelGridMenuItem.setText(xlate("Pixel_Grid"));
		// Navigate menu
		widgets.navigateMenu.setText(xlate("Navigate"));
		widgets.goToMenuItem.setText(xlate("Go_To"));
		widgets.goToAgainMenuItem.setText(xlate("Go_To_Again"));
		widgets.addToBookmarksMenuItem.setText(xlate("Add_To_Bookmarks"));
		widgets.organizeBookmarksMenuItem.setText(xlate("Organize_Bookmarks"));
		// Palette menu
		widgets.paletteMenu.setText(xlate("Palette"));
		widgets.editColorsMenuItem.setText(xlate("Edit_Color"));
		widgets.colorCodecMenu.setText(xlate("Format"));
		widgets.paletteEndiannessMenu.setText(xlate("Endianness"));
		widgets.paletteLittleEndianMenuItem.setText(xlate("Little_Endian"));
		widgets.paletteBigEndianMenuItem.setText(xlate("Big_Endian"));
		widgets.paletteSizeMenuItem.setText(xlate("Size"));
		widgets.newPaletteMenuItem.setText(xlate("New"));
		widgets.importPaletteMenu.setText(xlate("Import_From"));
		widgets.importInternalPaletteMenuItem.setText(xlate("This_File"));
		widgets.importExternalPaletteMenuItem.setText(xlate("Another_File"));
		widgets.addToPalettesMenuItem.setText(xlate("Add_To_Palettes"));
		widgets.organizePalettesMenuItem.setText(xlate("Organize_Palettes"));
		// Window menu
		widgets.windowMenu.setText(xlate("Window"));
		widgets.newWindowMenuItem.setText(xlate("New_Window"));
		widgets.tileMenuItem.setText(xlate("Tile"));
		widgets.cascadeMenuItem.setText(xlate("Cascade"));
		widgets.arrangeIconsMenuItem.setText(xlate("Arrange_Icons"));
		// Help menu
		widgets.helpMenu.setText(xlate("Help"));
		widgets.helpTopicsMenuItem.setText(xlate("Help_Topics"));
		widgets.aboutMenuItem.setText(xlate("About_Tile_Molester"));

		UIManager.put("OptionPane.yesButtonText", xlate("Yes"));
		UIManager.put("OptionPane.noButtonText", xlate("No"));
		UIManager.put("OptionPane.cancelButtonText", xlate("Cancel"));
		UIManager.put("OptionPane.okButtonText", xlate("OK"));
	}

	/**
	 * Deselects all tools in the tool palette.
	 **/
	public void deselectToolPalette() {
		widgets.selectButton.setSelected(false);
		widgets.zoomButton.setSelected(false);
		widgets.pickupButton.setSelected(false);
		widgets.brushButton.setSelected(false);
		widgets.lineButton.setSelected(false);
		widgets.fillButton.setSelected(false);
		widgets.replaceButton.setSelected(false);
		widgets.moveButton.setSelected(false);
	}

	public void saveBookmarks() {
		// TODO
	}

	public void savePalettes() {
		// TODO
	}

	/**
	 * Call this when a fileimage has been modified.
	 * @param img img value
	 **/
	public void fileImageModified(FileImage img) {
		img.setModified(true);
		setSaveButtonsEnabled(true);
		fileActions.saveAll.setEnabled(true);
	}

	/**
	 * Sets enabled state of save buttons.
	 * @param b b value
	 **/
	public void setSaveButtonsEnabled(boolean b) {
		fileActions.save.setEnabled(b);
	}

	/**
	 * Sets enabled state of undo buttons.
	 * @param b b value
	 **/
	public void setUndoButtonsEnabled(boolean b) {
		editActions.undo.setEnabled(b);
	}

	/**
	 * Sets enabled state of redo buttons.
	 * @param b b value
	 **/
	public void setRedoButtonsEnabled(boolean b) {
		editActions.redo.setEnabled(b);
	}

	public void disableMDIStuff() { refresh.setMdiMode(false); }
	public void enableMDIStuff() { refresh.setMdiMode(true); }

	public void refreshBlockSizeSelection(TMView view) { refresh.refreshBlockSizeSelection(view); }
	public void refreshModeSelection(TMView view) { refresh.refreshModeSelection(view); }
	public void refreshTileCodecSelection(TMView view) { refresh.refreshTileCodecSelection(view); }
	public void refreshPalettePane() { refresh.refreshPalettePane(); }
	public void refreshUndoRedo() { refresh.refreshUndoRedo(); }
	public void refreshStatusBar() { refresh.refreshStatusBar(); }
	public void refreshBookmarksMenu() { refresh.refreshBookmarksMenu(); }
	public void refreshPalettesMenu() { refresh.refreshPalettesMenu(); }
	public void refreshPaletteSelection(TMView view) { refresh.refreshPaletteSelection(view); }
	public void refreshPaletteEndiannessSelection(TMView view) { refresh.refreshPaletteEndiannessSelection(view); }
	public void refreshColorCodecSelection(TMView view) { refresh.refreshColorCodecSelection(view); }

	/**
	 * Adds a codec to the list of available codecs and creates a menu item for it.
	 * @param codec tile codec used for encode/decode
	 **/
	public void addTileCodec(TileCodec codec) {
		if (!tilecodecs.contains(codec)) {
			tilecodecs.add(codec);
		}
		TMTileCodecMenuItem codecMenuItem = new TMTileCodecMenuItem(codec, viewActions::doTileCodecCommand);
		widgets.tileCodecMenu.add(codecMenuItem);
		widgets.tileCodecButtonGroup.add(codecMenuItem);
		widgets.tileCodecButtonHashtable.put(codec, codecMenuItem);
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
			widgets.palettePane.setFGColor(fgColor);
		});
	}

	/**
	 * Sets the background color for the current view.
	 * @param bgColor background color used to clear pixels
	 **/
	public void setBGColor(int bgColor) {
		withSelectedView(view -> {
			view.setBGColor(bgColor);
			widgets.palettePane.setBGColor(bgColor);
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
	 * Gets the widgets.desktop.
	 * @return MDI widgets.desktop pane
	 **/
	public JDesktopPane getDesktop() {
		return widgets.desktop;
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
	 * Adds a view to the widgets.desktop.
	 * @param view file view associated with this component
	 **/
	public void addViewToDesktop(TMView view) {
		widgets.desktop.add(view);
		try {
			view.setSelected(true);
		} catch (java.beans.PropertyVetoException x) {
			TMLog.severe("Failed to select view on desktop", x);
		}
		widgets.desktop.revalidate();
		widgets.desktop.repaint();

		if (widgets.desktop.getAllFrames().length == 1) {
			// this is the first frame, show the MDI toolbars and menus
			refresh.setMdiMode(true);
		}
	}

	/** Initializes the View->Codec menu based on the tilecodecs present. */
	private void initTileCodecUIStuff() {
		buildTileCodecsMenu();
	}

	/**
	 * Builds the View->Codec menu.
	 **/
	private void buildTileCodecsMenu() {
		widgets.tileCodecMenu.setMnemonic(KeyEvent.VK_C);
		widgets.tileCodecMenu.removeAll();
		for (int i = 0; i < tilecodecs.size(); i++) {
			addTileCodec(tilecodecs.get(i));
		}

	}

	/**
	 * Builds the Palette->Format menu.
	 **/
	private void buildColorCodecsMenu() {
		widgets.colorCodecMenu.setMnemonic(KeyEvent.VK_F);
		widgets.colorCodecMenu.removeAll();
		for (int i = 0; i < colorcodecs.size(); i++) {
			addColorCodec(colorcodecs.get(i));
		}
	}

	/**
	 * Adds a codec to the list of available codecs and creates a menu item for it.
	 * @param codec tile codec used for encode/decode
	 **/
	public void addColorCodec(ColorCodec codec) {
		TMColorCodecMenuItem codecMenuItem = new TMColorCodecMenuItem(codec, paletteActions::doColorCodecCommand);
		widgets.colorCodecMenu.add(codecMenuItem);
		widgets.colorCodecButtonGroup.add(codecMenuItem);
		widgets.colorCodecButtonHashtable.put(codec, codecMenuItem);
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
			byte[] buf = new byte[TMUIConstants.IO_BUFFER_SIZE];
			int n;
			while ((n = in.read(buf)) > 0) {
				out.write(buf, 0, n);
			}
		}
		return temp;
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

	TMTileCodecFileFilter getTileCodecFilterForFile(File file) {
		for (int i = 0; i < filefilters.size(); i++) {
			TMTileCodecFileFilter cff = filefilters.get(i);
			if (cff.accept(file)) {
				return cff;
			}
		}
		return filefilters.get(0);
	}

	TMPaletteFileFilter getPaletteFilterForFile(File file) {
		for (int i = 0; i < palettefilters.size(); i++) {
			TMPaletteFileFilter pff = palettefilters.get(i);
			if (pff.accept(file)) {
				return pff;
			}
		}
		return palettefilters.get(0);
	}

	/**
	 * Updates various UI components (menus, statusbar, palette) to reflect the
	 * settings of the current frame.
	 * @param view file view associated with this component
	 **/
	public void viewSelected(TMView view) {
		setSaveButtonsEnabled(view.getFileImage().isModified());

		// (un)check some menu items
		TMEditorCanvas ec = view.getEditorCanvas();
		widgets.blockGridMenuItem.setSelected(ec.isBlockGridVisible());
		widgets.tileGridMenuItem.setSelected(ec.isTileGridVisible());
		widgets.pixelGridMenuItem.setSelected(ec.isPixelGridVisible());
		widgets.rowInterleaveBlocksMenuItem.setSelected(ec.getRowInterleaveBlocks());

		refresh.refreshModeSelection(view);
		refresh.refreshTileCodecSelection(view);
		refresh.refreshBlockSizeSelection(view);
		refresh.refreshPalettePane();
		refresh.refreshStatusBar();
		refresh.refreshBookmarksMenu();
		refresh.refreshPalettesMenu();
		refresh.refreshUndoRedo();

		setTitle("Tile Molester - " + view.getTitle());
	}

	/**
	 * Hide the statusbar coordenates.
	 **/
	public void hideStatusBarCoords() {
		widgets.statusBar.setCoords("");
	}

	/** @return unmodifiable color codecs from tmspec.xml */
	public java.util.List<ColorCodec> getColorCodecs() {
		return Collections.unmodifiableList(colorcodecs);
	}

	/** @return unmodifiable tile codecs (spec + built-in extras) */
	public java.util.List<TileCodec> getTileCodecs() {
		return Collections.unmodifiableList(tilecodecs);
	}

	/** @return unmodifiable tile file filters from tmspec.xml */
	public java.util.List<TMTileCodecFileFilter> getFileFilters() {
		return Collections.unmodifiableList(filefilters);
	}

	/** @return unmodifiable palette file filters from tmspec.xml */
	public java.util.List<TMPaletteFileFilter> getPaletteFilters() {
		return Collections.unmodifiableList(palettefilters);
	}

	/** @return unmodifiable file listeners from tmspec.xml */
	public java.util.List<TMFileListener> getFileListeners() {
		return Collections.unmodifiableList(filelisteners);
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
			TMLog.handled("Translation unavailable: " + key, e);
			return key;
		}
	}

	void withSelectedView(Consumer<TMView> action) {
		TMView view = getSelectedView();
		if (view != null) {
			action.accept(view);
		}
	}

	void adjustOffset(int delta) {
		withSelectedView(view -> view.setRelativeOffset(delta));
	}

	void showError(String messageKey, String detail) {
		String message = xlate(messageKey);
		if (detail != null && !detail.isEmpty()) {
			message = message + "\n" + detail;
		}
		TMLog.showError(this, message, null);
	}

	void showError(String messageKey, Exception e) {
		TMLog.showError(this, xlate(messageKey), e);
	}

	/**
	 * Gets the selected view frame.
	 * @return currently selected file view
	 **/
	public TMView getSelectedView() {
		return (TMView) widgets.desktop.getSelectedFrame();
	}

}