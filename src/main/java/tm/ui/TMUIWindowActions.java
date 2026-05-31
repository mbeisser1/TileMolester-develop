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

import tm.*;

/** Window menu command handlers for {@link TMUI}. */
public class TMUIWindowActions extends TMUICommandGroup {
	public final Action tile;
	public final Action cascade;
	public final Action arrangeIcons;
	public final Action newWindow;

	public TMUIWindowActions(TMUI ui) {
		super(ui);
		tile = command(this::doTileCommand);
		cascade = command(this::doCascadeCommand);
		arrangeIcons = command(this::doArrangeIconsCommand);
		newWindow = command(this::doNewWindowCommand);
	}


	public void doTileCommand() {
		JInternalFrame[] frames = ui.widgets.desktop.getAllFrames();
		// count frames that aren't iconized
		int frameCount = 0;
		for (int i = 0; i < frames.length; i++) {
			if(!frames[i].isIcon()) {
				frameCount++;
			}
		}
		int rows = (int) Math.sqrt(frameCount);
		int cols = frameCount / rows;
		int extra = frameCount % rows;
		// number of columns with an extra row
		int width = ui.widgets.desktop.getWidth() / cols;
		int height = ui.widgets.desktop.getHeight() / rows;
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
						height = ui.widgets.desktop.getHeight() / rows;
					}
				}
			}
		}
		ui.widgets.desktop.revalidate();
	}

	public void doCascadeCommand() {
		int xpos = 0, ypos = 0;
		JInternalFrame frames[] = ui.widgets.desktop.getAllFrames();
		int cascadeWidth = ui.widgets.desktop.getBounds().width - TMUIConstants.MDI_CASCADE_MARGIN;
		int cascadeHeight = ui.widgets.desktop.getBounds().height - TMUIConstants.MDI_CASCADE_MARGIN;
		for (int i = frames.length - 1; i >= 0; i--) {
			if (!frames[i].isIcon()) {
				frames[i].setLocation(xpos, ypos);
				xpos += TMUIConstants.MDI_CASCADE_OFFSET;
				ypos += TMUIConstants.MDI_CASCADE_OFFSET;
			}
		}
		ui.widgets.desktop.revalidate();
	}

	public void doArrangeIconsCommand() {
		JInternalFrame[] frames = ui.widgets.desktop.getAllFrames();
		int xpos = 0;
		int ypos = 0;
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].isIcon()) {
				JInternalFrame.JDesktopIcon icon = frames[i].getDesktopIcon();
				icon.setLocation(xpos, ui.widgets.desktop.getHeight() - icon.getHeight());
				xpos += icon.getWidth();
			}
		}
		ui.widgets.desktop.revalidate();
	}

	public void doNewWindowCommand() {
		ui.withSelectedView(view -> {
			FileImage img = view.getFileImage();
			TMView newView = ui.createView(img, view.getTileCodec(), view.getPalette(), view.getMode());
			newView.setPalIndex(view.getPalIndex());
			newView.setFGColor(view.getFGColor());
			newView.setBGColor(view.getBGColor());
			newView.setAbsoluteOffset(view.getOffset());
			newView.setGridSize(view.getCols(), view.getRows());
			ui.addViewToDesktop(newView);
		});
	}

}
