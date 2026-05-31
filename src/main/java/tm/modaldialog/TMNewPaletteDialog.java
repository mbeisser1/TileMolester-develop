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
import tm.utils.PaletteCodecSort;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for creating a new palette with size, format, and endianness.
 **/
public class TMNewPaletteDialog extends TMModalDialog {

    private static final Dimension SIZE_FIELD_DIM = new Dimension(72, 24);
    private static final Dimension COMBO_FIELD_DIM = new Dimension(220, 24);

    private JLabel sizeLabel;
    private JTextField sizeField;
    private JLabel formatLabel;
    private JComboBox<ColorCodec> codecCombo;
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
        sizeField = new JTextField();
        formatLabel = new JLabel(xlate("Format"));
        codecCombo = new JComboBox<>();

        sizeField.setColumns(8);
        sizeField.setMinimumSize(SIZE_FIELD_DIM);
        sizeField.setPreferredSize(SIZE_FIELD_DIM);
        sizeField.getDocument().addDocumentListener(new TMDocumentListener());
        sizeField.addKeyListener(new DecimalNumberVerifier());

        JPanel endiannessPane = new JPanel();
        endiannessPane.setBorder(new TitledBorder(new EtchedBorder(), xlate("Endianness")));
        endiannessPane.setLayout(new BoxLayout(endiannessPane, BoxLayout.Y_AXIS));
        littleRadio = new JRadioButton(xlate("Little_Endian"));
        bigRadio = new JRadioButton(xlate("Big_Endian"));
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
        gbc.insets = new Insets(4, 4, 4, 8);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        buildConstraints(gbc, 0, 0, 1, 1, 0, 0);
        gbl.setConstraints(sizeLabel, gbc);
        p.add(sizeLabel);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.15;
        buildConstraints(gbc, 1, 0, 1, 1, 15, 0);
        gbl.setConstraints(sizeField, gbc);
        p.add(sizeField);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        buildConstraints(gbc, 0, 1, 1, 1, 0, 0);
        gbl.setConstraints(formatLabel, gbc);
        p.add(formatLabel);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        buildConstraints(gbc, 1, 1, 1, 1, 100, 0);
        codecCombo.setMinimumSize(COMBO_FIELD_DIM);
        codecCombo.setPreferredSize(COMBO_FIELD_DIM);
        gbl.setConstraints(codecCombo, gbc);
        p.add(codecCombo);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        buildConstraints(gbc, 2, 0, 1, 2, 0, 0);
        gbl.setConstraints(endiannessPane, gbc);
        p.add(endiannessPane);

        p.setPreferredSize(new Dimension(480, 120));

        return p;
    }

    /**
     * Gets the palette size entered by the user.
     * @return palette size entered by the user
     **/
    public int getPaletteSize() {
        return Integer.parseInt(sizeField.getText().trim());
    }

    /**
     * Gets the selected byte-order endianness.
     * @return {@link ColorCodec#LITTLE_ENDIAN} or {@link ColorCodec#BIG_ENDIAN}
     **/
    public int getEndianness() {
        return littleRadio.isSelected() ? ColorCodec.LITTLE_ENDIAN : ColorCodec.BIG_ENDIAN;
    }

    /**
     * Gets the selected color codec.
     * @return selected color codec
     **/
    public ColorCodec getCodec() {
        return codecCombo.getItemAt(codecCombo.getSelectedIndex());
    }

    /**
     * Populates the color codec combo box (sorted by effective bpp, then name).
     * @param codecs available color codecs for the combo box
     **/
    public void setCodecs(List<ColorCodec> codecs) {
        codecCombo.removeAllItems();
        List<ColorCodec> sorted = PaletteCodecSort.sortedForPaletteUi(codecs);
        for (ColorCodec codec : sorted) {
            codecCombo.addItem(codec);
        }
        if (codecCombo.getItemCount() > 0) {
            codecCombo.setSelectedIndex(0);
        }
    }

    /**
     * Shows the dialog and waits for user confirmation.
     * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
     **/
    public int showDialog() {
        maybeEnableOKButton();
        SwingUtilities.invokeLater(() -> sizeField.requestFocusInWindow());
        return super.showDialog();
    }

    /**
     * Reports whether the current dialog input is valid for OK.
     * @return true if dialog input is valid for OK
     **/
    public boolean inputOK() {
        String text = sizeField.getText().trim();
        if (text.isEmpty()) {
            return false;
        }
        try {
            return Integer.parseInt(text) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
