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

import tm.ui.TMUI;
import tm.ui.view.TMView;
import tm.ui.widget.TMPaletteVizualiser;
import tm.*;
import tm.colorcodecs.ColorCodec;
import tm.fileselection.TMPaletteFileFilter;
import tm.reversibleaction.ReversiblePaletteEditAction;
import tm.treenodes.*;
import tm.utils.TMLog;
import tm.utils.PaletteCsvParseException;
import tm.utils.PaletteCsvReader;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.text.MessageFormat;

/** Palette command handlers for {@link TMUI}. */
public class TMUIPaletteActions extends TMUICommandGroup {
	public final Action addToPalettes;
	public final Action organizePalettes;
	public final Action editColors;
	public final Action paletteSize;
	public final Action newPalette;
	public final Action importInternalPalette;
	public final Action importExternalPalette;
public final Action paletteLittleEndian;
	public final Action paletteBigEndian;

	public TMUIPaletteActions(TMUI ui) {
		super(ui);
		addToPalettes = command(this::doAddToPalettesCommand);
		organizePalettes = command(this::doOrganizePalettesCommand);
		editColors = command(this::doEditColorsCommand);
		paletteSize = command(this::doPaletteSizeCommand);
		newPalette = command(this::doNewPaletteCommand);
		importInternalPalette = command(this::doImportInternalPaletteCommand);
		importExternalPalette = command(this::doImportExternalPaletteCommand);
paletteLittleEndian = command(() -> doPaletteEndiannessCommand(ColorCodec.LITTLE_ENDIAN));
		paletteBigEndian = command(() -> doPaletteEndiannessCommand(ColorCodec.BIG_ENDIAN));
	}


	public void doAddToPalettesCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.widgets.addPaletteDialog.showDialog(view.getFileImage().getResources().getPalettesRoot());
			if (retVal == JOptionPane.OK_OPTION) {
				FolderNode folder = ui.widgets.addPaletteDialog.getFolder();
				PaletteItemNode palNode = new PaletteItemNode(view.getPalette(), ui.widgets.addPaletteDialog.getDescription());
				folder.add(palNode);
				ui.refreshPalettesMenu();
			}
		});
	}

	public void doOrganizePalettesCommand() {
		ui.withSelectedView(view -> {
			ui.widgets.organizePalettesDialog.showDialog(view.getFileImage().getResources().getPalettesRoot());
			ui.refreshPalettesMenu();
		});
	}

	public void doEditColorsCommand() {
		ui.withSelectedView(view -> {
			Color newColor = JColorChooser.showDialog(ui, "Edit Color", new Color(view.getFGColor()));
			if (newColor != null) {
				int rgb = newColor.getRGB();
				TMPaletteVizualiser vizualiser = ui.widgets.palettePane.getVizualiser();
				int colorIndex = vizualiser.getLastIndex();

				view.addReversibleAction(new ReversiblePaletteEditAction(view, view.getPalette(), colorIndex,
						view.getPalette().getEntryRGB(colorIndex), rgb));
				view.getPalette().setEntryRGB(colorIndex, rgb);

				ui.setFGColor(rgb);
				view.refreshPaletteDisplay();
				ui.repaint();
			}
		});
	}

	public void doColorCodecCommand(ColorCodec codec) {
		ui.withSelectedView(view -> {
			view.getPalette().setCodec(codec);
			view.refreshPaletteDisplay();
			ui.refreshPalettePane();
		});
	}

	public void doPaletteSizeCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.widgets.paletteSizeDialog.showDialog(view.getPalette().getSize());
			if (retVal == JOptionPane.OK_OPTION) {
				view.getPalette().setSize(ui.widgets.paletteSizeDialog.getPaletteSize());
				ui.refreshPalettePane();
			}
		});
	}

	public void doNewPaletteCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.widgets.newPaletteDialog.showDialog();
			if (retVal == JOptionPane.OK_OPTION) {
				int size = ui.widgets.newPaletteDialog.getPaletteSize();
				ColorCodec codec = ui.widgets.newPaletteDialog.getCodec();
				int endianness = ui.widgets.newPaletteDialog.getEndianness();

				TMPalette palette = new TMPalette("ID", size, codec, endianness);
				view.setPalette(palette);
				ui.refreshPalettePane();
				ui.refreshPalettesMenu();
			}
		});
	}

	public void doImportInternalPaletteCommand() {
		ui.withSelectedView(view -> {
			int retVal = ui.widgets.importInternalPaletteDialog.showDialog();
			if (retVal == JOptionPane.OK_OPTION) {
				int offset = ui.widgets.importInternalPaletteDialog.getOffset();
				int size = ui.widgets.importInternalPaletteDialog.getPaletteSize();
				ColorCodec codec = ui.widgets.importInternalPaletteDialog.getCodec();
				int endianness = ui.widgets.importInternalPaletteDialog.getEndianness();
				boolean copy = ui.widgets.importInternalPaletteDialog.getCopy();

				byte[] data = view.getFileImage().getContents();
				TMPalette palette = new TMPalette("ID", data, offset, size, codec, endianness, copy, false);
				view.setPalette(palette);
				ui.refreshPalettePane();
				ui.refreshPalettesMenu();
			}
		});
	}

	public void doImportExternalPaletteCommand() {
		ui.withSelectedView(view -> {
			if (new File(ui.lastPath).exists()) {
				ui.widgets.paletteOpenChooser.setCurrentDirectory(new File(ui.lastPath));
			} else {
				ui.widgets.paletteOpenChooser.setCurrentDirectory(new File("."));
			}
			int retVal = ui.widgets.paletteOpenChooser.showOpenDialog(ui);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = ui.widgets.paletteOpenChooser.getSelectedFile();

				FileFilter ff = ui.widgets.paletteOpenChooser.getFileFilter();
				if (!(ff instanceof TMPaletteFileFilter)) {
					ff = ui.getPaletteFilterForFile(file);
				}
				TMPaletteFileFilter pf = (TMPaletteFileFilter) ff;

				if (isCsvPaletteImport(pf, file)) {
					importPaletteFromCsvFile(view, file);
					return;
				}

				int size = pf.getSize();
				ColorCodec codec = ui.getColorCodecByID(pf.getCodecID());
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
					ui.showError("Palette_Read_Error", e);
					return;
				}

				TMPalette palette = new TMPalette("ID", data, 0, size, codec, endianness, true, false);
				view.setPalette(palette);
				ui.refreshPalettePane();
				ui.refreshPalettesMenu();
			}
		});
	}

	private boolean isCsvPaletteImport(TMPaletteFileFilter pf, File file) {
		if (pf.getSize() == 0 && "CF01".equals(pf.getCodecID())) {
			return true;
		}
		String name = file.getName().toLowerCase();
		return name.endsWith(".csv");
	}

	private void importPaletteFromCsvFile(TMView view, File file) {
		int[] rgb;
		try {
			rgb = PaletteCsvReader.read(file);
		} catch (PaletteCsvParseException e) {
			TMLog.logException("Invalid palette CSV", e);
			String msg = MessageFormat.format(
					ui.xlate("Palette_Csv_Invalid_Entry"),
					Integer.valueOf(e.getEntryNumber()),
					e.getValue());
			JOptionPane.showMessageDialog(ui, msg, "Tile Molester", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException e) {
			ui.showError("Palette_Read_Error", e);
			return;
		}

		ColorCodec codec = ui.getColorCodecByID("CF01");
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
		ui.refreshPalettePane();
		ui.refreshPalettesMenu();
	}

	public void doPaletteEndiannessCommand(int endianness) {
		ui.withSelectedView(view -> view.getPalette().setEndianness(endianness));
	}

	public void doSelectPaletteCommand(TMPalette palette) {
		ui.withSelectedView(view -> {
			view.setPalette(palette);
			ui.refreshPalettePane();
			ui.refreshPaletteEndiannessSelection(view);
			ui.refreshColorCodecSelection(view);
		});
	}

}
