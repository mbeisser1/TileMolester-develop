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

package tm.ui.view;

import tm.ui.TMUI;
import tm.FileImage;
import tm.TMPalette;
import tm.treenodes.*;
import tm.reversibleaction.*;
import tm.canvases.TMEditorCanvas;
import tm.canvases.TMTileCanvas;
import tm.tilecodecs.TileCodec;
import tm.ui.widget.TMOffsetNavigator;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A graphical view of a file image.
 * The frame contains a panel that's used for rendering the tile data, and a
 * offset navigator (up/down buttons and slider) for keeping track of the file position. The keyboard can also be used
 * to change the file position (see class TMViewKeyListener). The filename is
 * shown in the titlebar, along with a * when the file has been modified.
 **/
public class TMView extends JInternalFrame {
	private static final int INITIAL_OFFSET_NAV_HEIGHT = 384;
	private static final Dimension SCROLL_PANE_SIZE = new Dimension(550, 530);
	private static final double INITIAL_SCALE = 4.0;
	private static final int INITIAL_GRID = 16;

	private static int frameCount = 0;
	private JPanel contentPane = new JPanel();
	private TMOffsetNavigator offsetNavigator;
	private boolean offsetControlUpdating;
	private boolean fittingRowsToViewport;
	private JScrollPane scrollPane;
	private TMEditorCanvas editorCanvas;
	private TMUI ui;
	private FileImage fileImage;

	private int fgColor = 0x000000;
	private int bgColor = 0xFFFFFF;

	private int minOffset = 0;
	private int maxOffset = 0; // can't scroll past this

	private boolean keysEnabled = true;

	private List<ReversibleAction> undoableActions = new ArrayList<>();
	private List<ReversibleAction> redoableActions = new ArrayList<>();

	private boolean sizeBlockToCanvas = true;

	@Override
	/**
	 * Sets the frame icon.
	 * @param icon icon to assign (may be ignored)
	 **/
	public void setFrameIcon(Icon icon)  {
        Icon oldIcon = frameIcon;
		frameIcon = null;
		firePropertyChange(FRAME_ICON_PROPERTY, oldIcon, null);
    }

	/**
	 * Constructs a TMView for the given FileImage.
	 **/
	public TMView(TMUI ui, FileImage fileImage, TileCodec tileCodec) {
		super(fileImage.getName(), true, true, true, true);
		this.ui = ui;
		this.fileImage = fileImage;
		this.frameIcon = null;
		fileImage.addView(this);

		initFrame();
		buildContentPane(tileCodec);
		positionFrame();
	}

	private void initFrame() {
		setDoubleBuffered(true);
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				doViewSelected();
			}

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				doCloseCommand();
			}
		});
	}

	private void buildContentPane(TileCodec tileCodec) {
		contentPane.setLayout(null);
		contentPane.setFocusable(true);
		contentPane.addKeyListener(new TMViewKeyListener(this));
		contentPane.setFocusTraversalKeysEnabled(false);

		editorCanvas = new TMEditorCanvas(ui, this);
		contentPane.add(editorCanvas);
		editorCanvas.setLocation(TMOffsetNavigator.WIDTH, 0);

		offsetNavigator = new TMOffsetNavigator(
				() -> nudgeOffsetByRows(-1),
				() -> nudgeOffsetByRows(1));
		contentPane.add(offsetNavigator);
		offsetNavigator.setLocation(0, 0);
		offsetNavigator.setSize(TMOffsetNavigator.WIDTH, INITIAL_OFFSET_NAV_HEIGHT);

		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				editorCanvas.maybeApplySelection();
			}
		});

		configureEditorCanvas(tileCodec);
		wireOffsetNavigator();

		scrollPane = new JScrollPane(contentPane);
		scrollPane.setPreferredSize(SCROLL_PANE_SIZE);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setContentPane(scrollPane);

		ComponentAdapter resizeHandler = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onViewAreaResized();
			}
		};
		addComponentListener(resizeHandler);
		scrollPane.getViewport().addComponentListener(resizeHandler);
	}

	private void configureEditorCanvas(TileCodec tileCodec) {
		editorCanvas.setOffset(0);
		editorCanvas.setGridSize(INITIAL_GRID, INITIAL_GRID);
		editorCanvas.setBlockDimensions(INITIAL_GRID, INITIAL_GRID);
		editorCanvas.setCodec(tileCodec);
		editorCanvas.unpackPixels();
		setScale(INITIAL_SCALE);
		updateOffsetNavigator();
		syncOffsetControlValue(minOffset);
	}

	private void wireOffsetNavigator() {
		offsetNavigator.slider.addChangeListener(e -> {
			if (offsetControlUpdating) {
				return;
			}
			int rowSize = editorCanvas.getRowIncrement();
			if (rowSize > 0) {
				int offset = editorCanvas.getOffset();
				int relOfs = offset % rowSize;
				int newOfs = (offsetNavigator.slider.getValue() / rowSize) * rowSize;
				setAbsoluteOffset(relOfs + newOfs);
			}
		});
	}

	private void nudgeOffsetByRows(int direction) {
		int rowSize = editorCanvas.getRowIncrement();
		if (rowSize > 0) {
			setRelativeOffset(direction * rowSize);
		}
	}

	private void onViewAreaResized() {
		if (fittingRowsToViewport) {
			return;
		}
		if (offsetNavigator != null && offsetNavigator.slider.getValueIsAdjusting()) {
			return;
		}
		fitRowsToViewport();
	}

	/**
	 * Adjusts visible row count so the tile canvas fills the viewport height.
	 **/
	private void fitRowsToViewport() {
		if (scrollPane == null || editorCanvas == null || offsetNavigator == null) {
			return;
		}
		int viewportHeight = scrollPane.getViewport().getExtentSize().height;
		if (viewportHeight <= 0) {
			return;
		}
		int scaledRowHeight = Math.max(1, editorCanvas.getScaledTileDim());
		int rows = Math.max(1, Math.min(1024, viewportHeight / scaledRowHeight));
		if (rows != editorCanvas.getRows()) {
			fittingRowsToViewport = true;
			try {
				setGridSize(editorCanvas.getCols(), rows);
			} finally {
				fittingRowsToViewport = false;
			}
		} else {
			layoutOffsetNavigator();
			syncContentPanePreferredSize();
		}
	}

	private void layoutOffsetNavigator() {
		offsetNavigator.setBounds(0, 0, TMOffsetNavigator.WIDTH, editorCanvas.getHeight());
		offsetNavigator.revalidate();
		editorCanvas.setLocation(TMOffsetNavigator.WIDTH, 0);
	}

	private void syncContentPanePreferredSize() {
		contentPane.setPreferredSize(new Dimension(
				offsetNavigator.getWidth() + editorCanvas.getWidth(), editorCanvas.getHeight()));
		contentPane.revalidate();
	}

	private void syncOffsetControlValue(int absOfs) {
		JSlider slider = offsetNavigator.slider;
		if (slider.getValue() == absOfs) {
			return;
		}
		offsetControlUpdating = true;
		try {
			slider.setValue(absOfs);
		} finally {
			offsetControlUpdating = false;
		}
	}

	private void positionFrame() {
		pack();
		fitRowsToViewport();
		setLocation(frameCount * 20, frameCount * 20);
		frameCount += 1;
	}

	/**
	 * Gets the FileImage associated with this TMView.
	 * @return file image displayed by this view
	 **/
	public FileImage getFileImage() {
		return fileImage;
	}

	/**
	 * Sets the tile codec that's used in the editor canvas.
	 * It also maps the current draw colors to the closest
	 * matching colors in the new color range (when going
	 * from, say, 4-bit to 2-bit codec).
	 * @param codec tile codec used for encode/decode
	 **/
	public void setTileCodec(TileCodec codec) {
		// map fg+bg colors according to old and new color range
		mapDrawColors(codec);
		// set the new codec
		editorCanvas.setCodec(codec);
		// update display
		editorCanvas.unpackPixels();
		editorCanvas.repaint();
		updateOffsetNavigator();
	}

	/**
	 * Converts foreground and background color to new palette range.
	 * It's only necessary when going from a codec that has a greater bitdepth
	 * (more colors) than the new codec. Does not apply when going to direct-color
	 * modes.
	 * The two colors that best match the current foreground and background colors
	 * are
	 * found in the new palette and set as the new drawing colors.
	 * The palette index must be converted also!!
	 * @param codec tile codec used for encode/decode
	 **/
	public void mapDrawColors(TileCodec codec) {
		TileCodec oldCodec = editorCanvas.getCodec();
		if ((oldCodec != null)
				&& (codec.getBitsPerPixel() != oldCodec.getBitsPerPixel())) {
			// get the old color settings
			int oldColorIndex = getColorIndex();
			// calculate the new color settings
			int newColorCount = codec.getColorCount();
			int newPalIndex = oldColorIndex / newColorCount;
			int newColorIndex = newPalIndex * newColorCount;
			editorCanvas.setPalIndex(newPalIndex); // NB!

			if (codec.getBitsPerPixel() <= 8) {
				// update the drawing colors
				TMPalette pal = editorCanvas.getPalette();
				setFGColor(pal.closestMatchingEntryRGB(newColorIndex, newColorCount, fgColor));
				setBGColor(pal.closestMatchingEntryRGB(newColorIndex, newColorCount, bgColor));
			}
		}
	}

	/**
	 * Called when the user changes the current palette index, to set the
	 * new foreground and background color. This is done by finding their
	 * indexes in the old palette range and getting the RGB values of the
	 * corresponding indexes in the new palette range.
	 * @param newPalIndex newPalIndex value
	 **/
	public void mapDrawColorsToPalIndex(int newPalIndex) {
		TileCodec codec = getTileCodec();
		if (codec.getBitsPerPixel() <= 8) {
			TMPalette pal = getPalette();
			int palIndex = getPalIndex();
			if (palIndex != newPalIndex) { // only if the indexes are different
				int colorCount = codec.getColorCount();
				int colorIndex = palIndex * colorCount;
				int newColorIndex = newPalIndex * colorCount;
				// update the colors
				int fgIndex = pal.indexOf(colorIndex, fgColor);
				int bgIndex = pal.indexOf(colorIndex, bgColor);
				setFGColor(pal.getEntryRGB(newColorIndex + fgIndex));
				setBGColor(pal.getEntryRGB(newColorIndex + bgIndex));
			}
		}
	}

	/**
	 * Gets the tile codec for this view.
	 * @return tile codec used by this view
	 **/
	public TileCodec getTileCodec() {
		return editorCanvas.getCodec();
	}

	/**
	 * Sets the palette for this view.
	 * @param palette palette whose colors are displayed or edited
	 **/
	public void setPalette(TMPalette palette) {
		editorCanvas.setPalette(palette);
		editorCanvas.setPalIndex(0);
		editorCanvas.unpackPixels();
		editorCanvas.repaint();

		updateSelectionPaletteAndIndex(palette, 0);

		setFGColor(palette.getEntryRGB(1));
		setBGColor(palette.getEntryRGB(0));
	}

	/**
	 * Keep selection canvas palette/index in sync with the parent view.
	 * @param palette palette whose colors are displayed or edited
	 * @param palIndex palette page index
	 **/
	public void updateSelectionPaletteAndIndex(TMPalette palette, int palIndex) {
		if (editorCanvas.hasSelection()) {
			TMTileCanvas selection = editorCanvas.getSelectionCanvas();
			if (selection != editorCanvas) {
				selection.setPalette(palette);
				selection.setPalIndex(palIndex);
				selection.unpackPixels();
				selection.repaint();
			}
		}
	}

	/**
	 * Refreshes the editor and any active selection after palette data changes.
	 * Unpacks tile pixels from the file buffer, repaints the editor, and syncs
	 * the floating selection canvas palette/page if present.
	 **/
	public void refreshPaletteDisplay() {
		editorCanvas.unpackPixels();
		editorCanvas.repaint();
		updateSelectionPaletteAndIndex(getPalette(), getPalIndex());
	}

	/**
	 * Gets the palette for this view.
	 * @return active palette
	 **/
	public TMPalette getPalette() {
		return editorCanvas.getPalette();
	}

	/**
	 * Sets the palette index for this view.
	 * @param palIndex palette page index
	 **/
	public void setPalIndex(int palIndex) {
		editorCanvas.setPalIndex(palIndex);
		editorCanvas.unpackPixels();
		editorCanvas.repaint();

		updateSelectionPaletteAndIndex(getPalette(), palIndex);
	}

	/**
	 * Gets the palette index for this view.
	 * @return current palette page index
	 **/
	public int getPalIndex() {
		return editorCanvas.getPalIndex();
	}

	/**
	 * Gets the maximum palette index, given the palette size and current codec.
	 * @return highest valid palette page index
	 **/
	public int getPalIndexMaximum() {
		TileCodec codec = getTileCodec();
		int cc = 256; // assume >= 8 bpp
		if (codec.getBitsPerPixel() < 8) {
			cc = codec.getColorCount();
		}
		int max = (getPalette().getSize() / cc) - 1;
		return (max >= 0) ? max : 0;
	}

	/**
	 * Sets the mode for this view.
	 * Can be 1-dimensional (MODE_1D) or 2-dimensional (MODE_2D).
	 * @param mode tile layout mode ({@link tm.tilecodecs.TileCodec#MODE_1D} or {@link tm.tilecodecs.TileCodec#MODE_2D})
	 **/
	public void setMode(int mode) {
		editorCanvas.setMode(mode);
		editorCanvas.unpackPixels();
		editorCanvas.repaint();
	}

	/**
	 * Reserved for propagating byte order to the tile codec (not implemented).
	 * @param endianness {@link tm.tilecodecs.DirectColorTileCodec#LITTLE_ENDIAN} or {@link tm.tilecodecs.DirectColorTileCodec#BIG_ENDIAN} (TODO)
	 **/
	public void setEndianness(int endianess) {
		// this.endianness = endianness;
		// getTileCodec().setEndianness(endianness);
	}

	/**
	 * Updates the file-offset slider range and tick spacing for the current tile layout.
	 **/
	private void updateOffsetNavigator() {
		JSlider slider = offsetNavigator.slider;
		int rowIncrement = Math.max(1, editorCanvas.getRowIncrement());
		int pageIncrement = Math.max(1, editorCanvas.getPageIncrement());
		slider.setMinimum(minOffset);
		slider.setMinorTickSpacing(rowIncrement);
		slider.setMajorTickSpacing(pageIncrement);

		maxOffset = getFileImage().getSize();
		if (maxOffset > pageIncrement) {
			maxOffset -= pageIncrement;
		} else {
			maxOffset = 0;
		}
		slider.setMaximum(maxOffset);
		if (slider.getValue() > maxOffset) {
			syncOffsetControlValue(maxOffset);
		}
	}

	/**
	 * Sets the size of the tile grid.
	 * @param cols number of tile columns
	 * @param rows number of tile rows
	 **/
	public void setGridSize(int cols, int rows) {
		if(cols < 1) {
			cols = 1;
		}
		else if(cols > 1024) {
			cols = 1024;
		}
		if(rows < 1) {
			rows = 1;
		}
		else if(rows > 1024) {
			rows = 1024;
		}
		editorCanvas.setGridSize(cols, rows);
		if (sizeBlockToCanvas) {
			editorCanvas.setBlockDimensions(cols, rows);
		}
		updateOffsetNavigator();
		editorCanvas.unpackPixels();
		setScale(getScale());

		// update statusbar
		ui.refreshStatusBar(); // TODO: Move to TMUI
	}

	/**
	 * Gets the current position in the file.
	 * @return current file offset shown in the view
	 **/
	public int getOffset() {
		return editorCanvas.getOffset();
	}

	/**
	 * Gets the minimum file scroll offset.
	 * @return min offset value
	 **/
	public int getMinOffset() {
		return minOffset;
	}

	/**
	 * Gets the maximum file scroll offset.
	 * @return max offset value
	 **/
	public int getMaxOffset() {
		return maxOffset;
	}

	/**
	 * Sets the position in the file, relative to start of file.
	 * @param absOfs absOfs value
	 **/
	public void setAbsoluteOffset(int absOfs) {
		if (absOfs < minOffset) {
			absOfs = minOffset; // lower boundary
		} else if (absOfs > maxOffset) {
			absOfs = maxOffset; // upper boundary
		}
		syncOffsetControlValue(absOfs);
		editorCanvas.setOffset(absOfs);
		editorCanvas.unpackPixels();
		editorCanvas.repaint();

		// Update statusbar
		ui.refreshStatusBar(); // TODO: Move to TMUI
	}

	/**
	 * Sets the position in the file, relative to current (previous) offset.
	 * @param relOfs relOfs value
	 **/
	public void setRelativeOffset(int relOfs) {
		setAbsoluteOffset(relOfs + editorCanvas.getOffset());
	}

	/**
	 * Sets the drawing scale.
	 * @param scale zoom factor applied to the canvas
	 **/
	public void setScale(double scale) {
		editorCanvas.setScale(scale);

		layoutOffsetNavigator();
		syncContentPanePreferredSize();
	}

	/**
	 * Gets the drawing scale.
	 * @return current zoom factor
	 **/
	public double getScale() {
		return editorCanvas.getScale();
	}

	/**
	 * Gets the # of tiles per row.
	 * @return number of tile columns in the view
	 **/
	public int getCols() {
		return editorCanvas.getCols();
	}

	/**
	 * Gets the # of tiles per column.
	 * @return number of tile rows in the view
	 **/
	public int getRows() {
		return editorCanvas.getRows();
	}

	/**
	 * Turns the block grid on or off.
	 * @param showBlockGrid whether the block grid overlay is visible
	 **/
	public void setBlockGridVisible(boolean showBlockGrid) {
		editorCanvas.setBlockGridVisible(showBlockGrid);
	}

	/**
	 * Turns the simple tile grid on or off.
	 * @param showTileGrid whether the atomic tile grid overlay is visible
	 **/
	public void setTileGridVisible(boolean showTileGrid) {
		editorCanvas.setTileGridVisible(showTileGrid);
	}

	/**
	 * Turns the pixel tile grid on or off.
	 * @param showPixelGrid whether the per-pixel grid overlay is visible
	 **/
	public void setPixelGridVisible(boolean showPixelGrid) {
		editorCanvas.setPixelGridVisible(showPixelGrid);
	}

	/**
	 * Gets the visibility status of the block grid.
	 * @return whether the block grid is shown
	 **/
	public boolean isBlockGridVisible() {
		return editorCanvas.isBlockGridVisible();
	}

	/**
	 * Gets the visibility status of the simple tile grid.
	 * @return whether the tile grid is shown
	 **/
	public boolean isTileGridVisible() {
		return editorCanvas.isTileGridVisible();
	}

	/**
	 * Gets the visibility status of the pixel tile grid.
	 * @return whether the pixel grid is shown
	 **/
	public boolean isPixelGridVisible() {
		return editorCanvas.isPixelGridVisible();
	}

	/**
	 * Gets the scroll pane.
	 * @return scroll pane wrapping the editor canvas
	 **/
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * Gets the tile pane.
	 * @return editable tile canvas in this view
	 **/
	public TMEditorCanvas getEditorCanvas() {
		return editorCanvas;
	}

	/**
	 * Gets the TMUI.
	 * @return main application window
	 **/
	public TMUI getTMUI() {
		return ui;
	}

	/**
	 * Sets the foreground color.
	 * @param fgColor foreground draw color as 32-bit ARGB
	 **/
	public void setFGColor(int fgColor) {
		this.fgColor = fgColor;
	}

	/**
	 * Sets the background color.
	 * @param bgColor background color used to clear pixels
	 **/
	public void setBGColor(int bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * Gets the current foreground color.
	 * @return foreground draw color
	 **/
	public int getFGColor() {
		return fgColor;
	}

	/**
	 * Gets the current background color.
	 * @return background draw color
	 **/
	public int getBGColor() {
		return bgColor;
	}

	/**
	 * Returns the palette index of the current foreground draw color on this page.
	 * @return index within the current palette page, or 0 for direct-color modes
	 **/
	public int getFGColorIndex() {
		return colorIndexForDrawColor(fgColor);
	}

	/**
	 * Returns the palette index of the current background draw color on this page.
	 * @return index within the current palette page, or 0 for direct-color modes
	 **/
	public int getBGColorIndex() {
		return colorIndexForDrawColor(bgColor);
	}

	private int colorIndexForDrawColor(int rgb) {
		TileCodec codec = getTileCodec();
		if (codec == null || codec.getBitsPerPixel() > 8) {
			return 0;
		}
		TMPalette pal = getPalette();
		if (pal == null) {
			return 0;
		}
		int idx = pal.indexOf(getColorIndex(), rgb);
		return idx >= 0 ? idx : 0;
	}

	/**
	 * Enables/disables the keyboard event handler.
	 * This is desired in some situations, such as when the user is drawing with the
	 * mouse. We don't want him to be able to change format, scroll up/down etc. in
	 * the middle of an edit operation (the changes would be lost).
	 * This is sort of a hack though; what is desired is the behaviour as exhibited
	 * by
	 * menu items when the mouse is pressed; the menu shortcut keys can't be used to
	 * fire events at that time.
	 * @param keysEnabled true to allow view keyboard shortcuts (navigation, undo, etc.)
	 **/
	public void setKeysEnabled(boolean keysEnabled) {
		this.keysEnabled = keysEnabled;
	}

	/**
	 * Returns whether the view's key listener should handle the keypress or not.
	 * There are two cases when they shouldn't be handled (i.e. be ignored):
	 * 1) When setKeysEnabled(true) has been executed programatically by, say, the
	 * editor canvas mousePressed event; or
	 * 2) When the user is adjusting the file-offset slider with the mouse.
	 * @return whether keyboard navigation is active
	 **/
	public boolean getKeysEnabled() {
		if (offsetNavigator.slider.getValueIsAdjusting()) {
			return false;
		}
		return keysEnabled;
	}

	/**
	 * Gets the color count.
	 * @return number of colors per palette page
	 **/
	public int getColorCount() {
		if (getTileCodec().getBitsPerPixel() < 8) {
			return getTileCodec().getColorCount();
		}
		return 256;
	}

	/**
	 * Gets the color index.
	 * @return base palette index for the current page
	 **/
	public int getColorIndex() {
		return getColorCount() * getPalIndex();
	}

	/**
	 * Gets the mode.
	 * @return current tile layout mode
	 **/
	public int getMode() {
		return editorCanvas.getMode();
	}

	/*
	 * public void mouseWheelMoved(MouseWheelEvent e) {
	 * int units = e.getUnitsToScroll();
	 * if (units < 0) {
	 * // up
	 * units = -units;
	 * for (int i=0; i<units; i++) {
	 * ui.doMinusRowCommand();
	 * }
	 * }
	 * else {
	 * // down
	 * for (int i=0; i<units; i++) {
	 * ui.doPlusRowCommand();
	 * }
	 * }
	 * }
	 */
	/**
	 * Undoes the last action.
	 **/
	public void undo() {
		if (!undoableActions.isEmpty()) {
			ReversibleAction ra = undoableActions.remove(undoableActions.size() - 1);
			ra.undo();
			redoableActions.add(ra);
		}
	}

	/**
	 * Redoes a previously undone action.
	 **/
	public void redo() {
		if (!redoableActions.isEmpty()) {
			ReversibleAction ra = redoableActions.remove(redoableActions.size() - 1);
			ra.redo();
			undoableActions.add(ra);
		}
	}

	/**
	 * Records an edit on the undo stack and clears the redo stack.
	 * @param ra reversible action describing the change (undo/redo callbacks)
	 **/
	public void addReversibleAction(ReversibleAction ra) {
		if (undoableActions.size() > 32) {
			undoableActions.remove(0);
		}
		undoableActions.add(ra);
		redoableActions.clear();
		ui.fileImageModified(getFileImage());
		ui.refreshUndoRedo();
	}

	/**
	 * @return true if {@link #undo()} can run
	 **/
	public boolean canUndo() {
		return !undoableActions.isEmpty();
	}

	/**
	 * @return true if {@link #redo()} can run
	 **/
	public boolean canRedo() {
		return !redoableActions.isEmpty();
	}

	/**
	 * Goes to the specified bookmark.
	 * @param bookmark bookmark value
	 **/
	public void gotoBookmark(BookmarkItemNode bookmark) {
		editorCanvas.setGridSize(bookmark.getCols(), bookmark.getRows());
		editorCanvas.setBlockDimensions(bookmark.getBlockWidth(), bookmark.getBlockHeight());
		sizeBlockToCanvas = bookmark.getSizeBlockToCanvas();
		editorCanvas.setRowInterleaveBlocks(bookmark.getRowInterleaved());
		editorCanvas.setMode(bookmark.getMode());
		setAbsoluteOffset(bookmark.getOffset());
		// editorCanvas.setPalette(
		setTileCodec(bookmark.getCodec());
		mapDrawColorsToPalIndex(bookmark.getPalIndex());
		setPalIndex(bookmark.getPalIndex());
		setScale(getScale());
		ui.viewSelected(this);
		// fitTilesInWindow();
	}

	/**
	 * Captures the current view layout and format as a bookmark node.
	 * @param description user-visible bookmark label
	 * @return new bookmark with offset, grid, codec, palette page, and block settings
	 **/
	public BookmarkItemNode createBookmark(String description) {
		return new BookmarkItemNode(
				getOffset(),
				getCols(),
				getRows(),
				getBlockWidth(),
				getBlockHeight(),
				getRowInterleaveBlocks(),
				getSizeBlockToCanvas(),
				getMode(),
				getPalIndex(),
				getTileCodec(),
				description);
	}

	/**
	 * Closes the view.
	 **/
	private void doCloseCommand() {
		ui.fileActions.doCloseCommand();
	}

	/**
	 * Notifies UI that this view has been selected.
	 **/
	private void doViewSelected() {
		ui.viewSelected(this);
	}

	/**
	 * Resizes the internal frame so the content pane fits the editor canvas and offset navigator.
	 **/
	public void fitTilesInWindow() {
		contentPane.setSize(offsetNavigator.getWidth() + editorCanvas.getWidth(), editorCanvas.getHeight());
		Insets ins = getInsets();
		setSize(contentPane.getWidth() + ins.left + ins.right, 20 + contentPane.getHeight() + ins.top + ins.bottom);
	}

	/**
	 * Returns the action that {@link #undo()} would reverse next (top of the undo stack).
	 * @return most recent undoable action, for menu labeling
	 **/
	public ReversibleAction getFirstUndoableAction() {
		return undoableActions.get(undoableActions.size() - 1);
	}

	/**
	 * Returns the action that {@link #redo()} would reapply next (top of the redo stack).
	 * @return most recent redoable action, for menu labeling
	 **/
	public ReversibleAction getFirstRedoableAction() {
		return redoableActions.get(redoableActions.size() - 1);
	}

	/**
	 * Disposes of the view.
	 **/
	public void dispose() {
		contentPane.removeKeyListener(contentPane.getKeyListeners()[0]);
		removeInternalFrameListener(getInternalFrameListeners()[0]);
		removeComponentListener(getComponentListeners()[0]);
		removeAll();
		fileImage = null;
		editorCanvas.killViewRef();
		editorCanvas = null;
		frameCount--;
		super.dispose();
	}

	/**
	 * Sets the block dimensions.
	 * @param blockWidth block width in tiles
	 * @param blockHeight block height in tiles
	 **/
	public void setBlockDimensions(int blockWidth, int blockHeight) {
		if (blockWidth > getCols()) {
			blockWidth = getCols();
		} else if (blockWidth <= 0) {
			blockWidth = 1;
		}
		if (blockHeight > getRows()) {
			blockHeight = getRows();
		} else if (blockHeight <= 0) {
			blockHeight = 1;
		}
		editorCanvas.setBlockDimensions(blockWidth, blockHeight);
		editorCanvas.unpackPixels();
		editorCanvas.repaint();
	}

	/**
	 * Gets the block width.
	 * @return block width in tiles
	 **/
	public int getBlockWidth() {
		return editorCanvas.getBlockWidth();
	}

	/**
	 * Gets the block height.
	 * @return block height in tiles
	 **/
	public int getBlockHeight() {
		return editorCanvas.getBlockHeight();
	}

	/**
	 * Gets whether blocks should be row-interleaved.
	 * @return whether row-interleaved block layout is enabled
	 **/
	public boolean getRowInterleaveBlocks() {
		return editorCanvas.getRowInterleaveBlocks();
	}

	/**
	 * Sets whether blocks should be row-interleaved.
	 * @param rowInterleaved whether blocks use row-interleaved layout
	 **/
	public void setRowInterleaveBlocks(boolean rowInterleaved) {
		editorCanvas.setRowInterleaveBlocks(rowInterleaved);
		editorCanvas.unpackPixels();
		editorCanvas.repaint();
	}

	/**
	 * Sets whether the block size should follow the canvas size.
	 * @param sizeBlockToCanvas sizeBlockToCanvas value
	 **/
	public void setSizeBlockToCanvas(boolean sizeBlockToCanvas) {
		this.sizeBlockToCanvas = sizeBlockToCanvas;
		if (sizeBlockToCanvas) {
			setBlockDimensions(getCols(), getRows());
		}
	}

	/**
	 * Gets whether the block size should follow the canvas size.
	 * @return size block to canvas flag
	 **/
	public boolean getSizeBlockToCanvas() {
		return sizeBlockToCanvas;
	}

}
