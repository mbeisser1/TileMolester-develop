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

import tm.colorcodecs.ColorCodec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Sort order for palette color-format combo boxes. */
public final class PaletteCodecSort {

	private PaletteCodecSort() {
	}

	/**
	 * Returns a copy of {@code codecs} sorted for palette UI (low effective bpp first).
	 **/
	public static List<ColorCodec> sortedForPaletteUi(List<ColorCodec> codecs) {
		List<ColorCodec> copy = new ArrayList<>(codecs);
		copy.sort(Comparator
				.comparingInt(PaletteCodecSort::effectiveBitsPerPixel)
				.thenComparing(c -> c.getDescription(), String.CASE_INSENSITIVE_ORDER));
		return copy;
	}

	/**
	 * Effective bpp for sorting; some codecs store more bits per entry than the label implies.
	 **/
	private static int effectiveBitsPerPixel(ColorCodec codec) {
		String id = codec.getID();
		if ("CF05".equals(id)) {
			return 9;
		}
		if ("CF03".equals(id)) {
			return 4;
		}
		if ("CF02".equals(id)) {
			return 6;
		}
		return codec.getBitsPerPixel();
	}
}
