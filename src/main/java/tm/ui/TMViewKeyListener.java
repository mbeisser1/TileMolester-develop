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

import tm.tilecodecs.TileCodec;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

/**
 * Handles keypresses in the file view window.
 **/
public class TMViewKeyListener extends KeyAdapter {

	private final TMView view;

	public TMViewKeyListener(TMView view) {
		this.view = view;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!view.getKeysEnabled()) {
			return;
		}

		TMUI ui = view.getTMUI();
		TileCodec tc = view.getTileCodec();
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
				if (e.isShiftDown()) {
					ui.viewActions.doDecreaseHeightCommand();
				} else {
					ui.navActions.doMinusRowCommand();
				}
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
				if (e.isShiftDown()) {
					ui.viewActions.doIncreaseHeightCommand();
				} else {
					ui.navActions.doPlusRowCommand();
				}
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
				if (e.isShiftDown()) {
					ui.viewActions.doDecreaseWidthCommand();
				} else {
					ui.navActions.doMinusTileCommand();
				}
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				if (e.isShiftDown()) {
					ui.viewActions.doIncreaseWidthCommand();
				} else {
					ui.navActions.doPlusTileCommand();
				}
				break;
			case KeyEvent.VK_HOME:
				ui.navActions.doHomeCommand();
				break;
			case KeyEvent.VK_END:
				ui.navActions.doEndCommand();
				break;
			case 109:
			case KeyEvent.VK_MINUS:
				ui.navActions.doMinusByteCommand();
				break;
			case 107:
			case KeyEvent.VK_PLUS:
				ui.navActions.doPlusByteCommand();
				break;
			case KeyEvent.VK_PAGE_UP:
				ui.navActions.doMinusPageCommand();
				break;
			case KeyEvent.VK_PAGE_DOWN:
				ui.navActions.doPlusPageCommand();
				break;
			case KeyEvent.VK_TAB:
				if (e.isShiftDown()) {
					ui.viewActions.doTileCodecCommand(ui.getTileCodecPredecessor(tc));
				} else {
					ui.viewActions.doTileCodecCommand(ui.getTileCodecSuccessor(tc));
				}
				break;
			case KeyEvent.VK_ESCAPE:
				view.getEditorCanvas().encodeSelection();
				break;
		}
	}

}
