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

package tm.ui.widget;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;

/**
 * File-offset control for {@link tm.ui.view.TMView}: explicit up/down buttons with a
 * vertical slider between them. Used instead of {@link JScrollBar} so arrow buttons
 * are always visible on Linux and other platforms where L&amp;F scrollbars omit them.
 */
public final class TMOffsetNavigator extends JPanel {

	public static final int WIDTH = 24;
	private static final int BUTTON_HEIGHT = 20;
	private static final int ICON_SIZE = 16;

	private final JButton upButton = new JButton();
	private final JButton downButton = new JButton();
	public final JSlider slider = new JSlider(JSlider.VERTICAL);

	public TMOffsetNavigator(Runnable scrollUp, Runnable scrollDown) {
		super(new BorderLayout(0, 0));
		setFocusable(false);

		upButton.setFocusable(false);
		downButton.setFocusable(false);
		upButton.setMargin(new Insets(0, 0, 0, 0));
		downButton.setMargin(new Insets(0, 0, 0, 0));
		upButton.setIcon(new FlatSVGIcon("icons/fluent/table_move_above_24_regular.svg", ICON_SIZE, ICON_SIZE));
		downButton.setIcon(new FlatSVGIcon("icons/fluent/table_move_below_24_regular.svg", ICON_SIZE, ICON_SIZE));
		upButton.setToolTipText("Row back");
		downButton.setToolTipText("Row forward");
		upButton.addActionListener(e -> scrollUp.run());
		downButton.addActionListener(e -> scrollDown.run());

		slider.setFocusable(false);
		slider.setInverted(true);

		add(upButton, BorderLayout.NORTH);
		add(slider, BorderLayout.CENTER);
		add(downButton, BorderLayout.SOUTH);

		upButton.setPreferredSize(new Dimension(WIDTH, BUTTON_HEIGHT));
		downButton.setPreferredSize(new Dimension(WIDTH, BUTTON_HEIGHT));
		setPreferredSize(new Dimension(WIDTH, 384));
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.width = WIDTH;
		return d;
	}
}
