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

package tm.ui.widget;

import tm.ui.view.TMView;
import tm.tilecodecs.TileCodec;
import tm.canvases.TMEditorCanvas;
import tm.canvases.TMSelectionCanvas;
import javax.swing.*;
import java.awt.*;

/**
 * Tile Molester status bar.
 **/
public class TMStatusBar extends JPanel {

    private JLabel offsetLabel = new JLabel(" ");
    private JLabel cursorLabel = new JLabel(" ");
    private JLabel selectionLabel = new JLabel(" ");
    private JLabel codecLabel = new JLabel(" ");
    private JLabel palOffsetLabel = new JLabel(" ");
    private JLabel modeLabel = new JLabel(" ");
    private JLabel tilesLabel = new JLabel(" ");
    private JLabel messageLabel = new JLabel(" ");

    private String rowLabel = "Row";
    private String colLabel = "Col";
    private String selLabel = "Sel";
    private String paletteLabel = "Palette:";

    /**
     * Creates the status bar.
     **/
    public TMStatusBar() {
        super();
        setLayout(new BorderLayout());

        configureStatusLabel(messageLabel);
        configureStatusLabel(offsetLabel);
        configureStatusLabel(cursorLabel);
        configureStatusLabel(selectionLabel);
        configureStatusLabel(palOffsetLabel);
        configureStatusLabel(codecLabel);
        configureStatusLabel(modeLabel);
        configureStatusLabel(tilesLabel);

        JPanel coordsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        coordsPanel.add(messageLabel);
        coordsPanel.add(offsetLabel);
        coordsPanel.add(cursorLabel);
        coordsPanel.add(selectionLabel);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        infoPanel.add(palOffsetLabel);
        infoPanel.add(codecLabel);
        infoPanel.add(modeLabel);
        infoPanel.add(tilesLabel);

        add(coordsPanel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.EAST);
    }

    private static void configureStatusLabel(JLabel label) {
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private static void setLabelText(JLabel label, String text) {
        if (!text.equals(label.getText())) {
            label.setText(text);
        }
    }

    /**
     * Sets translated labels for tile coordinate fields.
     **/
    public void setCoordLabels(String row, String col, String sel, String palette) {
        rowLabel = row;
        colLabel = col;
        selLabel = sel;
        paletteLabel = palette;
    }

    /**
     * Sets the text for general message.
     * @param s text displayed in the status bar
     **/
    public void setMessage(String s) {
        setLabelText(messageLabel, " " + s + " ");
    }

    /**
     * Sets the hex text that indicates the file position.
     * @param offset byte offset into the file buffer
     **/
    public void setOffset(int offset) {
        setLabelText(offsetLabel, " 0x" + formatHexOffset(offset) + " ");
    }

    /**
     * Sets the cursor tile under the mouse.
     * @param col tile column
     * @param row tile row
     **/
    public void setCursorTile(int col, int row) {
        setLabelText(cursorLabel, formatRowCol(row, col));
    }

    /**
     * Clears the cursor tile field.
     **/
    public void clearCursor() {
        setLabelText(cursorLabel, " ");
    }

    /**
     * Sets the selection from two tile corners (start → end, with size).
     **/
    public void setSelectionRange(int col1, int row1, int col2, int row2) {
        int colMin = Math.min(col1, col2);
        int colMax = Math.max(col1, col2);
        int rowMin = Math.min(row1, row2);
        int rowMax = Math.max(row1, row2);
        int w = colMax - colMin + 1;
        int h = rowMax - rowMin + 1;
        setLabelText(selectionLabel, " " + selLabel + "  "
                + rowLabel + " " + rowMin + " " + colLabel + " " + colMin
                + " → "
                + rowLabel + " " + rowMax + " " + colLabel + " " + colMax
                + "  (" + w + "×" + h + ") ");
    }

    /**
     * Clears the selection tile field.
     **/
    public void clearSelection() {
        setLabelText(selectionLabel, " ");
    }

    /**
     * Sets the text that indicates the graphics codec in use.
     * @param s text displayed in the status bar
     **/
    public void setCodec(String s) {
        setLabelText(codecLabel, " " + s + " ");
    }

    /**
     * Sets the palette file offset.
     * @param offset byte offset into the palette buffer
     **/
    public void setPalOffset(int offset) {
        setLabelText(palOffsetLabel, " " + paletteLabel + " " + offset + " ");
    }

    /**
     * Sets the text that indicates the current mode.
     * @param mode tile layout mode ({@link tm.tilecodecs.TileCodec#MODE_1D} or {@link tm.tilecodecs.TileCodec#MODE_2D})
     **/
    public void setMode(int mode) {
        if (mode == TileCodec.MODE_1D) {
            setLabelText(modeLabel, " 1-Dimensional ");
        }
        else {
            setLabelText(modeLabel, " 2-Dimensional ");
        }
    }

    /**
     * Sets the text that indicates how many tiles are shown.
     * @param w number of tile columns in the view
     * @param h number of tile rows in the view
     **/
    public void setTiles(int w, int h) {
        setLabelText(tilesLabel, " " + w + "x" + h + " tiles ");
    }

    /**
     * Called when a view has been selected.
     * @param view file view associated with this component
     **/
    public void viewSelected(TMView view) {
        TMEditorCanvas ec = view.getEditorCanvas();
        setMessage("");
        setOffset(view.getOffset());
        clearCursor();
        clearSelection();

        if (ec.hasSelection()) {
            TMSelectionCanvas sel = (TMSelectionCanvas) ec.getSelectionCanvas();
            if (ec.isSelectionOnGrid(sel)) {
                int dim = sel.getScaledTileDim();
                int col = sel.getX() / dim;
                int row = sel.getY() / dim;
                setSelectionRange(col, row, col + sel.getCols() - 1, row + sel.getRows() - 1);
            }
        }
        else if (ec.isSelecting()) {
            if (ec.isTileRegionOnGrid(ec.getSelX1(), ec.getSelY1(), ec.getSelX2(), ec.getSelY2())) {
                setSelectionRange(ec.getSelX1(), ec.getSelY1(), ec.getSelX2(), ec.getSelY2());
            }
        }
        else if (ec.isDrawingLine()) {
            int col1 = ec.getLineX1() / 8;
            int row1 = ec.getLineY1() / 8;
            int col2 = ec.getLineX2() / 8;
            int row2 = ec.getLineY2() / 8;
            if (ec.isTileRegionOnGrid(col1, row1, col2, row2)) {
                setSelectionRange(col1, row1, col2, row2);
            }
        }
        else {
            setCursorTile(ec.getCurrentCol(), ec.getCurrentRow());
        }

        if (view.getTileCodec() != null) {
            setCodec(view.getTileCodec().getDescription());
        }
        else {
            setCodec("");
        }
        setPalOffset(view.getPalette().getOffset());
        setMode(view.getMode());
        setTiles(view.getCols(), view.getRows());
    }

    private static String formatHexOffset(int offset) {
        String hexOffset = Integer.toHexString(offset).toUpperCase();
        while (hexOffset.length() < 8) {
            hexOffset = "0" + hexOffset;
        }
        return hexOffset;
    }

    private String formatRowCol(int row, int col) {
        return " " + rowLabel + " " + row + "  " + colLabel + " " + col + " ";
    }

    /**
     * Clears cursor and selection coordinate fields.
     **/
    public void clearCoords() {
        clearCursor();
        clearSelection();
    }

    /**
     * @param string ignored; clears coordinate fields
     **/
    public void setCoords(String string) {
        clearCoords();
    }
}
