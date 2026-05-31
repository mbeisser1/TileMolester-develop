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

import java.util.List;
import java.util.Objects;

import tm.colorcodecs.ColorCodec;
import tm.fileselection.TMPaletteFileFilter;
import tm.fileselection.TMTileCodecFileFilter;
import tm.utils.Xlator;

/**
 * Dependencies required to build a fully initialized {@link TMUIWidgets}.
 * Passed to {@link TMUIWidgets#create} so dialogs and file choosers are always set up together.
 **/
public final class TMUIWidgetsBootstrap {

	public final TMUI ui;
	public final Xlator xlator;
	public final List<ColorCodec> colorCodecs;
	public final List<TMTileCodecFileFilter> tileFileFilters;
	public final List<TMPaletteFileFilter> paletteFileFilters;

	public TMUIWidgetsBootstrap(
			TMUI ui,
			Xlator xlator,
			List<ColorCodec> colorCodecs,
			List<TMTileCodecFileFilter> tileFileFilters,
			List<TMPaletteFileFilter> paletteFileFilters) {
		this.ui = Objects.requireNonNull(ui, "ui");
		this.xlator = Objects.requireNonNull(xlator, "xlator");
		this.colorCodecs = List.copyOf(Objects.requireNonNull(colorCodecs, "colorCodecs"));
		this.tileFileFilters = List.copyOf(Objects.requireNonNull(tileFileFilters, "tileFileFilters"));
		this.paletteFileFilters = List.copyOf(Objects.requireNonNull(paletteFileFilters, "paletteFileFilters"));
	}
}
