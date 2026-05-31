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

package tm.utils;

import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Application-wide logging and error reporting.
 * <p>
 * Call {@link #init()} once at startup. Errors shown to the user include the
 * full stack trace in the dialog and are written to {@value #LOG_FILE_NAME}.
 **/
public final class TMLog {

	public static final String LOGGER_NAME = "TileMolester";
	public static final String LOG_FILE_NAME = "tilemolester.log";
	private static final String DIALOG_TITLE = "Tile Molester";

	private static final Logger LOG = Logger.getLogger(LOGGER_NAME);
	private static volatile Component dialogParent;
	private static boolean initialized;

	private TMLog() {
	}

	/** @return the shared application logger */
	public static Logger getLogger() {
		return LOG;
	}

	/**
	 * Configures file and console handlers for the application logger.
	 * Safe to call once; subsequent calls are ignored.
	 **/
	public static synchronized void init() {
		if (initialized) {
			return;
		}
		LOG.setUseParentHandlers(false);
		LOG.setLevel(Level.ALL);

		SimpleFormatter formatter = new SimpleFormatter();
		ConsoleHandler console = new ConsoleHandler();
		console.setLevel(Level.INFO);
		console.setFormatter(formatter);
		LOG.addHandler(console);

		try {
			FileHandler file = new FileHandler(LOG_FILE_NAME, true);
			file.setLevel(Level.ALL);
			file.setFormatter(formatter);
			LOG.addHandler(file);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not open log file " + LOG_FILE_NAME, e);
		}

		initialized = true;
		LOG.info("Tile Molester logging initialized");
	}

	/** Sets the default parent for error dialogs (typically the main {@link tm.ui.TMUI} frame). */
	public static void setDialogParent(Component parent) {
		dialogParent = parent;
	}

	public static void log(Level level, String message, Throwable thrown) {
		if (thrown != null) {
			LOG.log(level, message, thrown);
		} else {
			LOG.log(level, message);
		}
	}

	public static void severe(String message, Throwable thrown) {
		log(Level.SEVERE, message, thrown);
	}

	public static void warning(String message, Throwable thrown) {
		log(Level.WARNING, message, thrown);
	}

	/**
	 * Logs a throwable with full stack trace to the console and log file (no dialog).
	 **/
	public static void logException(Throwable thrown) {
		if (thrown == null) {
			return;
		}
		severe(thrown.getMessage() != null ? thrown.getMessage() : thrown.getClass().getName(), thrown);
	}

	/**
	 * Logs a throwable with full stack trace to the console and log file (no dialog).
	 **/
	public static void logException(String message, Throwable thrown) {
		severe(message, thrown);
	}

	/**
	 * Logs an error and shows a dialog with the message and stack trace.
	 * @param parent dialog owner, or {@code null} to use {@link #setDialogParent(Component)}
	 **/
	public static void showError(String message, Throwable thrown) {
		showError(null, message, thrown);
	}

	/**
	 * Logs an error and shows a dialog with the message and stack trace.
	 **/
	public static void showError(Component parent, String message, Throwable thrown) {
		if (thrown != null) {
			LOG.log(Level.SEVERE, message, thrown);
		} else {
			LOG.severe(message);
		}
		Runnable show = () -> showErrorDialog(resolveParent(parent), message, thrown);
		if (SwingUtilities.isEventDispatchThread()) {
			show.run();
		} else {
			SwingUtilities.invokeLater(show);
		}
	}

	private static Component resolveParent(Component parent) {
		return parent != null ? parent : dialogParent;
	}

	private static void showErrorDialog(Component parent, String message, Throwable thrown) {
		String stack = formatStackTrace(thrown);
		String text = stack.isEmpty() ? message : message + "\n\n" + stack;
		JTextArea area = new JTextArea(text, Math.min(20, 4 + text.split("\n", -1).length), 72);
		area.setEditable(false);
		area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		area.setCaretPosition(0);
		JScrollPane scroll = new JScrollPane(area);
		scroll.setPreferredSize(area.getPreferredSize());
		JOptionPane.showMessageDialog(parent, scroll, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
	}

	private static String formatStackTrace(Throwable thrown) {
		if (thrown == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		thrown.printStackTrace(new PrintWriter(sw));
		return sw.toString().stripTrailing();
	}
}
