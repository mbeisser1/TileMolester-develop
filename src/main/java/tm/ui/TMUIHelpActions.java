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
import tm.utils.BrowserControl;
import javax.swing.*;
import java.io.File;

/** Help menu command handlers for {@link TMUI}. */
public class TMUIHelpActions extends TMUICommandGroup {
	public final Action helpTopics;
	public final Action about;

	public TMUIHelpActions(TMUI ui) {
		super(ui);
		helpTopics = command(this::doHelpTopicsCommand);
		about = command(this::doAboutCommand);
	}


	public void doHelpTopicsCommand() {
		File localizedHelpFile = new File("docs/help_" + ui.locale.toString() + ".htm");
		if (localizedHelpFile.exists()) {
			BrowserControl.displayURL("file://" + localizedHelpFile.getAbsolutePath());
		} else {
			BrowserControl.displayURL("docs\\help.htm");
		}
	}

	public void doAboutCommand() {
		JOptionPane.showMessageDialog(ui,
				"Tile Molester v0.21\n\nby SnowBro 2003-2005 (v0.16)\nby Dr. MefistO 2013 (v0.17.2)\nby Mewster 2014-2015 (v0.19)\nby toruzz 2020-2024 (v0.21)",
				"Tile Molester",
				1);
	}

}
