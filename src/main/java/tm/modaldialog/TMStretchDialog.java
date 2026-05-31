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
import javax.swing.border.EmptyBorder;

import java.awt.*;

/**
 * The dialog where user can enter new dimensions for the current selection.
 **/
public class TMStretchDialog extends TMModalDialog {

    private JLabel colsLabel;
    private JLabel rowsLabel;
    private JTextField colsField;
    private JTextField rowsField;

    /**
     * Creates the stretch dialog.
     **/
    public TMStretchDialog(Frame owner, tm.utils.Xlator xl) {
        super(owner, "Stretch_Image_Dialog_Title", xl);
    }

    /**
     * Gets the number of columns entered by the user.
     * @return column count entered by the user
     **/
    public int getCols() {
        return Integer.parseInt(colsField.getText());
    }

    /**
     * Gets the number of rows entered by the user.
     * @return row count entered by the user
     **/
    public int getRows() {
        return Integer.parseInt(rowsField.getText());
    }

    /**
     * Builds and returns the dialog content panel.
     * @return dialog content panel
     **/
    protected JPanel getDialogPane() {
        colsLabel = new JLabel(xlate("Columns_Prompt"));
        rowsLabel = new JLabel(xlate("Rows_Prompt"));
        colsField = new JTextField();
        rowsField = new JTextField();
        TMDialogFields.configure(colsField, 5);
        TMDialogFields.configure(rowsField, 5);

		colsLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		rowsLabel.setBorder(new EmptyBorder(0, 4, 0, 4));

        JPanel colsPane = new JPanel();
        colsPane.setLayout(new BoxLayout(colsPane, BoxLayout.X_AXIS));
        colsPane.add(colsLabel);
        colsPane.add(colsField);

        JPanel rowsPane = new JPanel();
        rowsPane.setLayout(new BoxLayout(rowsPane, BoxLayout.X_AXIS));
        rowsPane.add(rowsLabel);
        rowsPane.add(rowsField);

        JPanel p = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        p.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        buildConstraints(gbc, 0, 0, 1, 1, 100, 0);
        gbl.setConstraints(colsPane, gbc);
        p.add(colsPane);

        buildConstraints(gbc, 0, 1, 1, 1, 100, 0);
        gbl.setConstraints(rowsPane, gbc);
        p.add(rowsPane);

        p.setPreferredSize(new Dimension(280, 72));

        colsField.addKeyListener(new DecimalNumberVerifier());
        colsField.getDocument().addDocumentListener(new TMDocumentListener());
        rowsField.addKeyListener(new DecimalNumberVerifier());
        rowsField.getDocument().addDocumentListener(new TMDocumentListener());

        return p;
    }

    /**
     * Shows the dialog and waits for user confirmation.
     * @param initialCols initial column count shown in the dialog
     * @param initialRows initial row count shown in the dialog
     * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
     **/
    public int showDialog(int initialCols, int initialRows) {
        colsField.setText(Integer.toString(initialCols));
        rowsField.setText(Integer.toString(initialRows));
        maybeEnableOKButton();
        SwingUtilities.invokeLater( new Runnable() {
            /**
             * Runs the deferred UI task.
             **/
            public void run() {
                colsField.requestFocus();
            }
        });
        return super.showDialog();
    }

    /**
     * Reports whether the current dialog input is valid for OK.
     * @return true if dialog input is valid for OK
     **/
    public boolean inputOK() {
        return (!colsField.getText().equals("") && !rowsField.getText().equals("")
            && (getCols() > 0) && (getRows() > 0));
    }

}