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

import tm.treenodes.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog for choosing a folder and description when adding a tree item.
 **/
public class TMAddToTreeDialog extends TMModalDialog {

    private TMTreeNodeTree tree;
    private JLabel descLabel;
    private JLabel createInLabel;
    private JTextField descField;
    private JButton newFolderButton;
    private TMNewFolderDialog newFolderDialog;
    private JScrollPane scrollPane;

    /**
     * Creates the dialog.
     * @param owner parent frame for the dialog
     * @param title dialog title key
     * @param xl translator for dialog strings
     **/
    public TMAddToTreeDialog(Frame owner, String title, tm.utils.Xlator xl) {
        super(owner, title, xl);
        newFolderDialog = new TMNewFolderDialog(owner, xl);
    }

    /**
     * Builds the folder tree and description input panel.
     * @return dialog content panel
     **/
    protected JPanel getDialogPane() {
        descLabel = new JLabel(xlate("Description_Prompt"));
        descField = new JTextField();
        descField.getDocument().addDocumentListener(new TMDocumentListener());
        createInLabel = new JLabel(xlate("Create_In"));

        newFolderButton = new JButton(xlate("New_Folder"));
        newFolderButton.addActionListener(
            new ActionListener() {
                /**
                 * Handles New Folder button click.
                 * @param e action event
                 **/
                public void actionPerformed(ActionEvent e) {
                    doNewFolderCommand();
                }
            }
        );

        tree = new TMTreeNodeTree();
        tree.setCellRenderer(new FolderCellRenderer());
        scrollPane = new JScrollPane(tree);

        JPanel p = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        p.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        buildConstraints(gbc, 0, 0, 1, 1, 25, 10);
        gbl.setConstraints(descLabel, gbc);
        p.add(descLabel);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buildConstraints(gbc, 1, 0, 1, 1, 75, 10);
        gbl.setConstraints(descField, gbc);
        p.add(descField);
        gbc.fill = GridBagConstraints.NONE;
        buildConstraints(gbc, 0, 1, 1, 1, 25, 10);
        gbl.setConstraints(createInLabel, gbc);
        p.add(createInLabel);
        gbc.anchor = GridBagConstraints.EAST;
        buildConstraints(gbc, 1, 1, 1, 1, 75, 10);
        gbl.setConstraints(newFolderButton, gbc);
        p.add(newFolderButton);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        buildConstraints(gbc, 0, 2, 2, 1, 80, 80);
        gbl.setConstraints(scrollPane, gbc);
        p.add(scrollPane);

        p.setPreferredSize(new Dimension(300, 200));
        return p;
    }

    /**
     * Prompts the user for a folder name, then inserts a folder node with that name
     * as a subfolder of the currently selected node in the tree.
     **/
    public void doNewFolderCommand() {
        newFolderDialog.setName("");    // clear previous input, if any
        int retVal = newFolderDialog.showDialog();  // prompt user for folder name
        if (retVal == JOptionPane.OK_OPTION) {
            // create & insert the folder
            String name = newFolderDialog.getName();
            FolderNode folder = (FolderNode)tree.getSelectedNode();
            FolderNode newFolder = new FolderNode(name);
            folder.add(newFolder);
            ((DefaultTreeModel)tree.getModel()).reload(folder);
            tree.expandNode(newFolder);
            tree.setSelectionPath(new TreePath(newFolder.getPath()));
            scrollPane.revalidate();
        }
    }

    /**
     * Gets the folder where the new item is to be inserted.
     * Gets the folder where the new item is to be inserted.
     * @return selected folder node
     **/
    public FolderNode getFolder() {
        return (FolderNode)tree.getSelectedNode();
    }

    /**
     * Gets the description that the user entered for the item.
     * Gets the description that the user entered for the item.
     * @return description text from the input field
     **/
    public String getDescription() {
        return descField.getText();
    }

    private class FolderCellRenderer extends DefaultTreeCellRenderer {

        /**
         * Creates a tree cell renderer that shows folders as expandable nodes.
         **/
        public FolderCellRenderer() {
            super();
            setLeafIcon(openIcon);
        }
    }

    /**
     * Loads the tree and shows the add-to-tree dialog.
     * @param root root node of the tree to display
     * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
     **/
    public int showDialog(TMTreeNode root) {
        tree.loadTreeNodes(root, false);
        descField.setText("");
        maybeEnableOKButton();
        SwingUtilities.invokeLater( new Runnable() {
            /**
             * Requests focus on the description field when the dialog opens.
             **/
            public void run() {
                descField.requestFocus();
            }
        });
        return super.showDialog();
    }

    /**
     * Reports whether a non-empty description has been entered.
     * @return true if description field is not blank
     **/
    public boolean inputOK() {
        return !(descField.getText().trim().equals(""));
    }

}