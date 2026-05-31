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

package tm.modaldialog;

import tm.utils.DecimalNumberVerifier;

import javax.swing.*;
import java.awt.*;

/**
 * The dialog that's shown when user wants to change the size of current palette.
 **/
public class TMPaletteSizeDialog extends TMModalDialog {

    private JLabel sizeLabel;
    private JTextField sizeField;

    /**
     * Creates the Palette Size dialog.
     **/
    public TMPaletteSizeDialog(Frame owner, tm.utils.Xlator xl) {
        super(owner, "Palette_Size_Dialog_Title", xl);
    }

    /**
     * Gets the palette size given by the user.
     **/
    public int getPaletteSize() {
        return Integer.parseInt(sizeField.getText());
    }

    /**
     * Builds and returns the dialog content panel.
     * @return dialog content panel
     **/
    protected JPanel getDialogPane() {
        JPanel p = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        p.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 8);
        sizeLabel = new JLabel(xlate("Size_Prompt"));
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        buildConstraints(gbc, 0, 0, 1, 1, 0, 0);
        gbl.setConstraints(sizeLabel, gbc);
        p.add(sizeLabel);
        sizeField = new JTextField();
        TMDialogFields.configure(sizeField, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        buildConstraints(gbc, 1, 0, 1, 1, 100, 0);
        gbl.setConstraints(sizeField, gbc);
        p.add(sizeField);
        p.setPreferredSize(new Dimension(320, 56));
        sizeField.addKeyListener(new DecimalNumberVerifier());
        sizeField.getDocument().addDocumentListener(new TMDocumentListener());

        return p;
    }

    /**
     * Shows the dialog and waits for user confirmation.
     * @param initialSize initial palette size shown in the dialog
     * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
     **/
    public int showDialog(int initialSize) {
        sizeField.setText(Integer.toString(initialSize));
        maybeEnableOKButton();
        SwingUtilities.invokeLater( new Runnable() {
            /**
             * Runs the deferred UI task.
             **/
            public void run() {
                sizeField.requestFocus();
            }
        });
        return super.showDialog();
    }

    /**
     * Reports whether the current dialog input is valid for OK.
     * @return true if dialog input is valid for OK
     **/
    public boolean inputOK() {
        return !(sizeField.getText().equals("") || (getPaletteSize() == 0));
    }

}