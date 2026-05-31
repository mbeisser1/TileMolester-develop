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

import com.formdev.flatlaf.extras.FlatSVGIcon;

/** Shared layout and UI sizing constants for {@link TMUI}. */
public final class TMUIConstants {

	public static final int TOOLBAR_ICON_SIZE = 22;

	public static final int WINDOW_INSET = 128;
	public static final int MAX_WINDOW_WIDTH = 1600;
	public static final int MAX_WINDOW_HEIGHT = 1080;

	public static final int MDI_CASCADE_OFFSET = 30;
	public static final int MDI_CASCADE_MARGIN = 5;

	/** Fixed Palette menu items before dynamic palette entries. */
	public static final int PALETTE_MENU_FIXED_ITEM_COUNT = 10;

	public static final int IO_BUFFER_SIZE = 4096;

	public static final int DEFAULT_OPEN_GRID_COLS = 3;
	public static final int DEFAULT_OPEN_GRID_ROWS = 36;

	private TMUIConstants() {
	}

	public static FlatSVGIcon toolbarIcon(String resourcePath) {
		return new FlatSVGIcon(resourcePath, TOOLBAR_ICON_SIZE, TOOLBAR_ICON_SIZE);
	}
}
