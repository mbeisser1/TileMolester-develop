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
import tm.filelistener.TMFileListener;
import tm.fileselection.TMFileFilter;
import tm.fileselection.TMTileCodecFileFilter;
import tm.tilecodecs.TileCodec;
import tm.threads.FileLoaderThread;
import tm.threads.FileSaverThread;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * File-related menu command handlers for {@link TMUI}.
 **/
public class TMUIFileActions {

	private final TMUI ui;

	public TMUIFileActions(TMUI ui) {
		this.ui = ui;
	}

	public void doNewCommand() {
		// Show dialog for creating new file
		// TMNewFileDialog ui.newFileDialog = new TMNewFileDialog(ui, xl);
		int retVal = ui.newFileDialog.showDialog();
		if (retVal == JOptionPane.OK_OPTION) {
			// create fileimage
			FileImage img = new FileImage(ui.newFileDialog.getFileSize());
			new TMFileResources(img, ui);
			// create view for it
			TileCodec tc = ui.tilecodecs.get(0); // default
			TMPalette pal = new TMPalette("PAL000", TMPalette.defaultPalette, ui.getColorCodecByID("CF01"),
					ColorCodec.LITTLE_ENDIAN, true);
			ui.addViewToDesktop(ui.createView(img, tc, pal, TileCodec.MODE_1D));
		}
	}

	public void doOpenCommand() {
		// set to directory of selected file, if there is one
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.fileOpenChooser.setCurrentDirectory(view.getFileImage().getFile().getParentFile());
		} else if (new File(ui.lastPath).exists()) {
			ui.fileOpenChooser.setCurrentDirectory(new File(ui.lastPath));
		} else {
			ui.fileOpenChooser.setCurrentDirectory(new File("."));
		}

		// have the user select a file
		int retVal = ui.fileOpenChooser.showOpenDialog(ui);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			// get the selected file and open it
			File file = ui.fileOpenChooser.getSelectedFile();
			// updates the last path opened
			ui.lastPath = file.getPath().substring(0, file.getPath().lastIndexOf(File.separator));
			TileMolester.settings.setLastPath(ui.lastPath);
			openFile(file);
		}
	}

	public void doCloseCommand() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			FileImage img = view.getFileImage();

			// check if it's the last view
			if (img.getViews().length == 1) {
				saveResources(img); // TODO
				// check if saving required/desired
				if (img.isModified()) {
					int retVal = JOptionPane.showConfirmDialog(ui,
							ui.xlate("Save_Changes_To") + " " + img.getName() + "?", "Tile Molester",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (retVal == JOptionPane.YES_OPTION) {
						doSaveCommand();
					} else if (retVal == JOptionPane.NO_OPTION) {
					} else if (retVal == JOptionPane.CANCEL_OPTION) {
						return; // return to program without saving and/or closing
					}
				}
				// remove potential file listener
				ui.fileListenerHashtable.remove(img.getContents());
			}

			// update recent files
			File f = new File(img.getFile().getAbsolutePath());
			addToRecentFiles(f);
			buildReopenMenu();

			// remove view from the FileImage and ui.desktop
			img.removeView(view);
			ui.desktop.remove(view);
			view.dispose();
			ui.desktop.revalidate();
			ui.desktop.repaint();

			img = null;
			view = null;
			System.gc();
		}

		ui.desktop.setSelectedFrame(null);
		JInternalFrame[] frames = ui.desktop.getAllFrames();
		if (frames.length == 0) {
			// no more frames left on the ui.desktop, hide MDI menus and toolbars
			ui.disableMDIStuff();
			ui.setTitle("Tile Molester");
		} else {
			// select a random frame (Swing doesn't do it for you...)
			try {
				frames[0].setSelected(true);
			} catch (java.beans.PropertyVetoException e) {
			}
		}
	}

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
			ui.showError("Save_Resources_Error", e);
		}
	}

	public void doCloseAllCommand() {
		JInternalFrame[] frames = ui.desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			TMView view = (TMView) frames[i];
			FileImage img = view.getFileImage();
			if (img.getViews().length == 1) {
				// check if saving required/desired
				if (img.isModified()) {
					int retVal = JOptionPane.showConfirmDialog(ui,
							ui.xlate("Save_Changes_To") + " " + img.getName() + "?", "Tile Molester",
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
				ui.fileListenerHashtable.remove(img.getContents());
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
			ui.desktop.remove(view);
			view.dispose();
		}

		buildReopenMenu();
		ui.desktop.setSelectedFrame(null);
		ui.desktop.revalidate();
		ui.desktop.repaint();
		ui.disableMDIStuff();
		ui.setTitle("Tile Molester");

		System.gc();
	}

	public void doSaveCommand() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			FileImage img = view.getFileImage();
			File file = img.getFile();
			String ext = TMFileFilter.getExtension(file);

			saveResources(img); // TODO

			// if (img.isModified()) {
			if (file.exists()) {
				if (!file.canWrite()) {
					ui.showError("File_Write_Error", file.getName());
				} else {
					FileSaverThread thread = null;
					byte[] contents = img.getContents();
					try {
						thread = new FileSaverThread(contents, file);
					} catch (IOException e) {
						ui.showError("File_Save_Error", e);
						return;
					}

					// see if a filelistener should be notified
					TMFileListener fl = ui.fileListenerHashtable.get(contents);
					if (fl != null) {
						fl.fileSaving(contents, ext);
					}

					// save it!
					new ProgressDialog(ui, thread);
					img.setModified(false);
					ui.setSaveButtonsEnabled(false);

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

	public void doSaveAsCommand() {
		TMView view = ui.getSelectedView();
		if (view != null) {
			ui.fileSaveChooser.setCurrentDirectory(view.getFileImage().getFile().getParentFile());
			ui.fileSaveChooser.setSelectedFile(view.getFileImage().getFile());
			int retVal = ui.fileSaveChooser.showSaveDialog(ui);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = ui.fileSaveChooser.getSelectedFile();
				view.getFileImage().setFile(file);
				doSaveCommand();
				ui.setTitle("Tile Molester - " + view.getTitle());
			}
		}
		ui.setSaveButtonsEnabled(false);
	}

	public void doSaveAllCommand() {
		JInternalFrame[] frames = ui.desktop.getAllFrames();
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
		ui.setSaveButtonsEnabled(false);
	}

	public void doExitCommand() {
		doCloseAllCommand();
		// if all frames were closed, the operation was successful and we can exit.
		if (ui.desktop.getAllFrames().length == 0) {
			TileMolester.settings.saveSettings();
			System.exit(0);
		}
	}

	public void doReopenCommand(File recentFile) {
		if (recentFile.exists() && recentFile.canRead()) {
			java.util.List<File> recentFiles = TileMolester.settings.getRecentFiles();
			ui.fileOpenChooser.setFileFilter(ui.getTileCodecFilterForFile(recentFile));
			openFile(recentFile);
			recentFiles.remove(recentFile);
			buildReopenMenu();
		}
	}

	public void openFile(File file) {
		System.gc();
		// read file
		FileLoaderThread thread = null;
		try {
			thread = new FileLoaderThread(file);
		} catch (OutOfMemoryError e) {
			ui.showError("Out_Of_Memory", file.length() + " bytes needed to load file.");
			return;
		} catch (FileNotFoundException e) {
			ui.showError("Load_File_Error", e);
			return;
		}
		ProgressDialog dialog = new ProgressDialog(ui, thread);
		byte[] contents = thread.getContents();

		// see if a filelistener should receive notification
		String ext = TMFileFilter.getExtension(file);
		for (int i = 0; i < ui.filelisteners.size(); i++) {
			TMFileListener fl = ui.filelisteners.get(i);
			if (fl.doFormatDetect(contents, ext)) {
				ui.fileListenerHashtable.put(contents, fl);
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
				new TMFileResources(resourceFile, img, ui);
			} catch (SAXException e) {
				ui.showError("Parser_Parse_Error", e);
			} catch (ParserConfigurationException e) {
				ui.showError("Parser_Config_Error", e);
			} catch (IOException e) {
				ui.showError("Parser_IO_Error", e);
			}
		} else {
			// create default resources
			new TMFileResources(img, ui);
		}
		// figure out mode and codec based on file filter
		FileFilter ff = ui.fileOpenChooser.getFileFilter();
		if (!(ff instanceof TMTileCodecFileFilter)) {
			ff = ui.getTileCodecFilterForFile(file);
		}
		int mode = ((TMTileCodecFileFilter) ff).getDefaultMode();
		TileCodec tc = ui.getTileCodecByID(((TMTileCodecFileFilter) ff).getCodecID());
		// hardcode 4bpp planar for opened files
		TileCodec forcedTc = ui.getTileCodecByID("PL03");
		if (forcedTc != null) {
			tc = forcedTc;
		}
		TMPalette pal = new TMPalette("PAL000", TMPalette.defaultPalette, ui.getColorCodecByID("CF01"),
				ColorCodec.LITTLE_ENDIAN, true);

		TMView view = ui.createView(img, tc, pal, mode);
		view.setGridSize(3, 36);
		ui.addViewToDesktop(view);

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

	public void buildReopenMenu() {
		ui.reopenMenu.removeAll();
		java.util.List<File> recentFiles = TileMolester.settings.getRecentFiles();
		if (recentFiles.size() == 0) {
			JMenuItem emptyItem = new JMenuItem("(" + ui.xlate("Empty") + ")");
			emptyItem.setEnabled(false);
			ui.reopenMenu.add(emptyItem);
		} else {
			for (int i = 0; i < recentFiles.size(); i++) {
				File recentFile = recentFiles.get(i);
				ui.reopenMenu.add(new TMRecentFileMenuItem(recentFile, ui::doReopenCommand));
			}
		}
	}

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
