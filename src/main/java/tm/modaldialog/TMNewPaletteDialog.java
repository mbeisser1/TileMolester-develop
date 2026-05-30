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

import tm.colorcodecs.ColorCodec;
import tm.utils.DecimalNumberVerifier;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Vector;

/**
 * Dialog for creating a new palette with size, format, and endianness.
 **/
public class TMNewPaletteDialog extends TMModalDialog {

    private JLabel sizeLabel;
    private JFormattedTextField sizeField;
    private JLabel formatLabel;
    private JComboBox codecCombo;
    private JRadioButton littleRadio;
    private JRadioButton bigRadio;

    /**
     * Creates the dialog.
     **/
    public TMNewPaletteDialog(Frame owner, tm.utils.Xlator xl) {
        super(owner, "New_Palette_Dialog_Title", xl);
    }

    /**
     * Builds and returns the dialog content panel.
     * @return dialog content panel
     **/
    protected JPanel getDialogPane() {
        sizeLabel = new JLabel(xlate("Size_Prompt"));
        sizeField = new JFormattedTextField();
        formatLabel = new JLabel(xlate("Format"));
        codecCombo = new JComboBox();

        sizeField.setColumns(5);
        sizeField.getDocument().addDocumentListener(new TMDocumentListener());
        sizeField.addKeyListener(new DecimalNumberVerifier());

        JPanel endiannessPane = new JPanel();
        endiannessPane.setBorder(new TitledBorder(new EtchedBorder(), xlate("Endianness")));
        endiannessPane.setLayout(new BoxLayout(endiannessPane, BoxLayout.Y_AXIS));
        littleRadio = new JRadioButton(xlate("Little_Endian")+"      ");
        bigRadio = new JRadioButton(xlate("Big_Endian")+"      ");
        endiannessPane.add(littleRadio);
        endiannessPane.add(bigRadio);

        ButtonGroup endiannessButtonGroup = new ButtonGroup();
        endiannessButtonGroup.add(littleRadio);
        endiannessButtonGroup.add(bigRadio);
        littleRadio.setSelected(true);

        JPanel p = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        p.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        buildConstraints(gbc, 0, 0, 1, 1, 25, 10);
        gbl.setConstraints(sizeLabel, gbc);
        p.add(sizeLabel);
        buildConstraints(gbc, 1, 0, 1, 1, 25, 10);
        gbl.setConstraints(sizeField, gbc);
        p.add(sizeField);
        buildConstraints(gbc, 0, 1, 1, 1, 25, 10);
        gbl.setConstraints(formatLabel, gbc);
        p.add(formatLabel);
        buildConstraints(gbc, 1, 1, 2, 1, 50, 10);
        gbl.setConstraints(codecCombo, gbc);
        p.add(codecCombo);
        buildConstraints(gbc, 3, 1, 1, 1, 25, 10);
        gbl.setConstraints(endiannessPane, gbc);
        p.add(endiannessPane);

        p.setPreferredSize(new Dimension(400, 100));

        return p;
    }

    /**
     * Gets the palette size entered by the user.
     * @return palette size entered by the user
     **/
    public int getPaletteSize() {
        return Integer.parseInt(sizeField.getText());
    }

    /**
     * Gets the selected byte-order endianness.
     * @return TODO: not yet implemented; always returns 0
     **/
    public int getEndianness() {
        return littleRadio.isSelected() ? ColorCodec.LITTLE_ENDIAN : ColorCodec.BIG_ENDIAN;
    }

    /**
     * Gets the selected color codec.
     * @return selected color codec
     **/
    public ColorCodec getCodec() {
        return (ColorCodec)codecCombo.getSelectedItem();
    }

    /**
     * Populates the color codec combo box.
     * @param codecs available color codecs for the combo box
     **/
    public void setCodecs(Vector<ColorCodec> codecs) {
        codecCombo.removeAllItems();
        for (int i=0; i<codecs.size(); i++) {
            codecCombo.addItem(codecs.get(i));
        }
        codecCombo.setSelectedIndex(0);
    }

    /**
     * Shows the dialog and waits for user confirmation.
     * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
     **/
    public int showDialog() {
        //sizeField.setText("");
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
        return (!sizeField.getText().equals("") && (getPaletteSize() > 0));
    }

}