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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * The dialog where user can enter desired file offset.
 **/
public class TMGoToDialog extends TMModalDialog {

    // available modes
    public static int ABSOLUTE_MODE = 1;
    public static int RELATIVE_MODE = 2;

    private JTextField ofsField;
    private JRadioButton hexButton;
    private JRadioButton decButton;
    private JRadioButton absButton;
    private JRadioButton relButton;

    /**
     * Creates the goto dialog.
     **/
    public TMGoToDialog(Frame owner, tm.utils.Xlator xl) {
        super(owner, "Go_To_Dialog_Title", xl);
    }

    /**
     * Gets the selected mode.
     **/
    public int getMode() {
        return (absButton.isSelected()) ? ABSOLUTE_MODE : RELATIVE_MODE;
    }

    /**
     * Gets the offset that was entered.
     **/
    public int getOffset() {
        if (inputOK()) {
            return Integer.parseInt(ofsField.getText().trim(), getRadix());
        }
        return 0;
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
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(4, 4, 4, 8);

        JPanel ofsPane = new JPanel(new GridBagLayout());
        ofsPane.setBorder(new TitledBorder(new EtchedBorder(), xlate("Offset")));
        ofsField = new JTextField();
        TMDialogFields.configure(ofsField, 12);
        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 0;
        fieldGbc.gridy = 0;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets = new Insets(6, 8, 6, 8);
        ofsPane.add(ofsField, fieldGbc);

        JPanel radixPane = new JPanel();
        radixPane.setBorder(new TitledBorder(new EtchedBorder(), xlate("Radix")));
        radixPane.setLayout(new BoxLayout(radixPane, BoxLayout.Y_AXIS));
        hexButton = new JRadioButton(xlate("Hex"));
        decButton = new JRadioButton(xlate("Dec"));
        radixPane.add(hexButton);
        radixPane.add(decButton);

        JPanel modePane = new JPanel();
        modePane.setBorder(new TitledBorder(new EtchedBorder(), xlate("Mode")));
        modePane.setLayout(new BoxLayout(modePane, BoxLayout.Y_AXIS));
        absButton = new JRadioButton(xlate("Absolute"));
        relButton = new JRadioButton(xlate("Relative"));
        modePane.add(absButton);
        modePane.add(relButton);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        buildConstraints(gbc, 0, 0, 1, 1, 100, 0);
        gbl.setConstraints(ofsPane, gbc);
        p.add(ofsPane);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        buildConstraints(gbc, 1, 0, 1, 1, 0, 0);
        gbl.setConstraints(radixPane, gbc);
        p.add(radixPane);

        buildConstraints(gbc, 2, 0, 1, 1, 0, 0);
        gbl.setConstraints(modePane, gbc);
        p.add(modePane);

        ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(absButton);
        modeButtonGroup.add(relButton);
        absButton.setSelected(true);

        ButtonGroup radixButtonGroup = new ButtonGroup();
        radixButtonGroup.add(hexButton);
        radixButtonGroup.add(decButton);
        hexButton.setSelected(true);

        p.setPreferredSize(new Dimension(440, 110));

        ofsField.setText("");
        ofsField.addKeyListener(new KeyAdapter() {
            /**
             * Filters or handles key-typed input.
             * @param e event object
             **/
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((Character.digit(c, getRadix()) != -1) ||
                    (c == KeyEvent.VK_BACK_SPACE) ||
                    (c == KeyEvent.VK_DELETE))) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        ofsField.getDocument().addDocumentListener(new TMDocumentListener());

        hexButton.addActionListener(new ActionListener() {
            /**
             * Handles a UI action event.
             * @param e event object
             **/
            public void actionPerformed(ActionEvent e) {
                if (inputOK()) {
                    int ofs = Integer.parseInt(ofsField.getText().trim(), 10);
                    ofsField.setText(Integer.toString(ofs, 16));
                }
            }
        });
        decButton.addActionListener(new ActionListener() {
            /**
             * Handles a UI action event.
             * @param e event object
             **/
            public void actionPerformed(ActionEvent e) {
                if (inputOK()) {
                    int ofs = Integer.parseInt(ofsField.getText().trim(), 16);
                    ofsField.setText(Integer.toString(ofs, 10));
                }
            }
        });

        return p;
    }

    /**
     * Shows the dialog and waits for user confirmation.
     * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
     **/
    public int showDialog() {
        ofsField.setText("");
        maybeEnableOKButton();
        SwingUtilities.invokeLater(() -> ofsField.requestFocusInWindow());
        return super.showDialog();
    }

    /**
     * Gets the numeric radix selected for offset entry.
     * @return 16 for hex or 10 for decimal entry
     **/
    private int getRadix() {
        return (hexButton.isSelected()) ? 16 : 10;
    }

    /**
     * Reports whether the current dialog input is valid for OK.
     * @return true if dialog input is valid for OK
     **/
    public boolean inputOK() {
        return !ofsField.getText().trim().isEmpty();
    }

}
