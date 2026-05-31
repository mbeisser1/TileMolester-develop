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
import tm.utils.TMLog;
import tm.modaldialog.TMNewFolderDialog;
import tm.utils.Xlator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**
 * Dialog where the user can organize treenodes.
 * Items and folders can be moved, renamed and deleted.
 * New folders can be created.
 * Items can be sorted (?)
 **/
public class TMOrganizeTreeDialog extends JDialog implements TreeModelListener {

    private TMTreeNodeTree tree = new TMTreeNodeTree();
    private JScrollPane scrollPane = new JScrollPane(tree);
    private TMNewFolderDialog newFolderDialog;

    private JButton newFolderButton;
    private JButton renameButton;
    private JButton moveButton;
    private JButton deleteButton;
    private JButton closeButton;
    private Xlator xl;

    /**
     * Creates a TMOrganizeTreeDialog instance.
     * @param owner parent frame for the dialog
     * @param title dialog title key or literal string
     * @param xl string translator for dialog labels
     **/
    public TMOrganizeTreeDialog(Frame owner, String title, Xlator xl) {
        super(owner, xl != null ? xl.xlate(title) : title, true);
        this.xl = xl;

        newFolderButton = new JButton(xlate("New_Folder"));
        renameButton = new JButton(xlate("Rename"));
        moveButton = new JButton(xlate("Move"));
        deleteButton = new JButton(xlate("Delete"));
        closeButton = new JButton(xlate("Close"));

        newFolderDialog = new TMNewFolderDialog(owner, xl);
        setSize(500, 400);

        tree.setRootVisible(false);
        tree.setEditable(true);

        DragSource dragSource = DragSource.getDefaultDragSource() ;
        dragSource.createDefaultDragGestureRecognizer(
            tree,                             //DragSource
            DnDConstants.ACTION_MOVE,         //specifies valid actions
            new MyDragGestureListener()       //DragGestureListener
        );
        new DropTarget(tree, DnDConstants.ACTION_MOVE, new MyDropTargetListener(), true);

        JPanel contentPane = new JPanel() {
            /**
             * Returns content pane insets for dialog padding.
             * @return inset margins around the dialog content
             **/
            public Insets getInsets() {
                return new Insets(10,10,10,10);
            }
        };
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // create panel with command buttons
        JPanel topPane = new JPanel();
        topPane.setLayout(new GridLayout(1, 4));
        topPane.add(renameButton);
        topPane.add(moveButton);
        topPane.add(newFolderButton);
        topPane.add(deleteButton);
		topPane.setBorder(new EmptyBorder(0,0,10,0));

        // create panel with Close button
        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPane.add(closeButton);

        contentPane.add(topPane, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(bottomPane, BorderLayout.SOUTH);

        // make button action handlers
        newFolderButton.addActionListener(
            new ActionListener() {
                /**
                 * Handles a UI action event.
                 * @param e event object
                 **/
                public void actionPerformed(ActionEvent e) {
                    doNewFolderCommand();
                }
            }
        );
        renameButton.addActionListener(
            new ActionListener() {
                /**
                 * Handles a UI action event.
                 * @param e event object
                 **/
                public void actionPerformed(ActionEvent e) {
                    doRenameCommand();
                }
            }
        );
        moveButton.addActionListener(
            new ActionListener() {
                /**
                 * Handles a UI action event.
                 * @param e event object
                 **/
                public void actionPerformed(ActionEvent e) {
                    doMoveCommand();
                }
            }
        );
        deleteButton.addActionListener(
            new ActionListener() {
                /**
                 * Handles a UI action event.
                 * @param e event object
                 **/
                public void actionPerformed(ActionEvent e) {
                    doDeleteCommand();
                }
            }
        );
        closeButton.addActionListener(
            new ActionListener() {
                /**
                 * Handles a UI action event.
                 * @param e event object
                 **/
                public void actionPerformed(ActionEvent e) {
                    doCloseCommand();
                }
            }
        );

        tree.addKeyListener(
            new KeyAdapter() {
                /**
                 * Filters or handles key-typed input.
                 * @param e event object
                 **/
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        doDeleteCommand();
                    }
                }
            }
        );
    }

    /**
     * Prompts for a folder name and inserts a new folder.
     **/
    public void doNewFolderCommand() {
        newFolderDialog.setName("");    // clear previous input, if any
        int retVal = newFolderDialog.showDialog();  // prompt user for folder name
        if (retVal == JOptionPane.OK_OPTION) {
            // create & insert the folder
            String name = newFolderDialog.getName();
            TMTreeNode node = tree.getSelectedNode();
            FolderNode folder;
            if (node instanceof FolderNode) {
                folder = (FolderNode)node;
            }
            else {
                folder = (FolderNode)node.getParent();
            }
            FolderNode newFolder = new FolderNode(name);
            folder.add(newFolder);
            ((DefaultTreeModel)tree.getModel()).reload(folder);
            tree.expandNode(newFolder);
            tree.setSelectionPath(new TreePath(newFolder.getPath()));
            scrollPane.revalidate();
        }
    }

    /**
     * Starts in-place renaming of the selected node.
     **/
    public void doRenameCommand() {
        DefaultMutableTreeNode node = tree.getSelectedNode();
        if(node.isRoot()) {
            return;
        }
        tree.startEditingAtPath(tree.getSelectionPath());
    }

    /**
     * TODO: Move command not yet implemented.
     **/
    public void doMoveCommand() {
        JOptionPane.showMessageDialog(
            this,
            "Todo.\nUse drag and drop to move items.",
            "Tile Molester",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Deletes the selected tree node after confirmation.
     **/
    public void doDeleteCommand() {
        TMTreeNode node = tree.getSelectedNode();
        if(node.isRoot()) {
            return;
        }
        int retVal = JOptionPane.showConfirmDialog(this,
                        "You sure about this?", // i18n
                        "Tile Molester",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
        if (retVal == JOptionPane.OK_OPTION) {
            TMTreeNode parent = node.getTMParent();
            parent.remove(node);

            ((DefaultTreeModel)tree.getModel()).reload(parent);
            tree.expandNode(parent);
            tree.setSelectionPath(new TreePath(parent.getPath()));
            scrollPane.revalidate();
        }
    }

    /**
     * Closes the organize-tree dialog.
     **/
    public void doCloseCommand() {
        setVisible(false);
    }

    /**
     * Shows the dialog.
     **/
    public void showDialog(FolderNode root) {
        tree.loadTreeNodes(root, true);
        tree.getModel().addTreeModelListener(this);
        // center the dialog
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int insetx = (screenSize.width - getWidth()) / 2;
        int insety = (screenSize.height - getHeight()) / 2;
        setBounds(insetx, insety,
                  getWidth(), getHeight());

        setVisible(true);
    }

    /**
     * Syncs node text when tree editing completes.
     * @param e event object
     **/
    public void treeNodesChanged(TreeModelEvent e) {
        TMTreeNode node;
        node = (TMTreeNode)e.getTreePath().getLastPathComponent();
        try {
            int index = e.getChildIndices()[0];
            node = (TMTreeNode)node.getChildAt(index);
        } catch (NullPointerException exc) {
            TMLog.logException("Tree node change index missing", exc);
        }
        node.setText((String)node.getUserObject());
    }

    /**
     * Handles tree nodes inserted into the model.
     * @param e event object
     **/
    public void treeNodesInserted(TreeModelEvent e) {
    }

    /**
     * Handles tree nodes removed from the model.
     * @param e event object
     **/
    public void treeNodesRemoved(TreeModelEvent e) {
    }

    /**
     * Handles tree structure changes.
     * @param e event object
     **/
    public void treeStructureChanged(TreeModelEvent e) {
    }

    /**
     * Translates a resource key.
     * @param key resource bundle lookup key
     * @return translated string, or the key if lookup fails
     **/
    public String xlate(String key) {
        try {
            String value = xl.xlate(key);
            return value;
        }
        catch (NullPointerException e) {
            TMLog.handled("Organize tree dialog translation unavailable: " + key, e);
            return key;
        }
    }

    private class MyDragGestureListener implements DragGestureListener {

        /**
         * Starts a drag operation for the selected node.
         * @param dge drag gesture event
         **/
        public void dragGestureRecognized(DragGestureEvent dge) {
            TMTreeNode dragNode = tree.getSelectedNode();
            if ((dragNode != null) && !dragNode.isRoot()) {
                dge.startDrag(DragSource.DefaultMoveDrop, new TMTreeNodeTransferable(dragNode));
            }
        }

    }

    private class MyDropTargetListener extends DropTargetAdapter {

        /**
         * Handles drop of a tree node onto a new parent.
         * @param dtde drop target drop event
         **/
        public void drop(DropTargetDropEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            //flavor not supported, reject drop
            if (!transferable.isDataFlavorSupported(TMTreeNodeTransferable.localTMTreeNodeFlavor)) {
                dtde.rejectDrop();
                return;
            }
            TMTreeNode node = null;
            try {
                node = (TMTreeNode)transferable.getTransferData(TMTreeNodeTransferable.localTMTreeNodeFlavor);
            } catch (UnsupportedFlavorException | java.io.IOException e) {
                TMLog.logException("Tree drag-and-drop transfer failed", e);
            }
            TMTreeNode oldParent = node.getTMParent();
            Point loc = dtde.getLocation();
            TMTreeNode newParent;
            try {
                TreePath destinationPath = tree.getPathForLocation(loc.x, loc.y);
                newParent = (TMTreeNode)destinationPath.getLastPathComponent();
            }
            catch (NullPointerException e) {
                TMLog.handled("Drop not over tree node; using root", e);
                newParent = (TMTreeNode)((DefaultTreeModel)tree.getModel()).getRoot();
            }
            if (!(newParent instanceof FolderNode)) {
                newParent = newParent.getTMParent();
            }
            oldParent.remove(node);
            newParent.add(node);
            ((DefaultTreeModel)tree.getModel()).reload();
            tree.expandNode(oldParent);
            tree.expandNode(newParent);
            tree.setSelectionPath(new TreePath(newParent.getPath()));
            scrollPane.revalidate();
        }

    }

}