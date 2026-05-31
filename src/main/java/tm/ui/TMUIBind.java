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

/**
 * Binds Swing menu and toolbar controls to command handlers.
 **/
public final class TMUIBind {

	private TMUIBind() {
	}

	public static void bind(JMenuItem item, Action action) {
		String text = item.getText();
		KeyStroke accelerator = item.getAccelerator();
		int mnemonic = item.getMnemonic();
		item.setAction(action);
		item.setText(text);
		if (accelerator != null) {
			item.setAccelerator(accelerator);
		}
		if (mnemonic != 0) {
			item.setMnemonic(mnemonic);
		}
	}

	public static void bind(AbstractButton button, Action action) {
		String toolTip = button.getToolTipText();
		button.setAction(action);
		button.setFocusable(false);
		if (toolTip != null) {
			button.setToolTipText(toolTip);
		}
	}

	public static void addToolBarButton(JToolBar bar, AbstractButton button, String toolTip, Action action) {
		button.setToolTipText(toolTip);
		button.setFocusable(false);
		bind(button, action);
		bar.add(button);
	}

	public static void addToolBarButton(JToolBar bar, AbstractButton button, String toolTip, Runnable handler) {
		addToolBarButton(bar, button, toolTip, command(handler));
	}

	private static Action command(Runnable handler) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				handler.run();
			}
		};
	}
}
