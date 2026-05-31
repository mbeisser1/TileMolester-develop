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

import tm.fileselection.TMFileFilter;
import java.io.File;

/**
 * File filter that accepts all files.
 **/
public class TMAllFilter extends TMFileFilter {

	private final String description;

	/**
	 * Creates a filter that accepts every file.
	 * @param description human-readable filter label
	 **/
	public TMAllFilter(String description) {
		this.description = description;
	}

	/**
	 * Accepts all files.
	 * @return always {@code true}
	 * @param f file to test
	 **/
	@Override
	public boolean accept(File f) {
		return true;
	}

	/**
	 * Gets the description.
	 * @return description string
	 **/
	@Override
	public String getDescription() {
		return description;
	}
}
