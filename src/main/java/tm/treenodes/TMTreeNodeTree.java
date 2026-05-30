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

package tm.treenodes;

import java.util.Enumeration;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

/**
 * A tree for showing treenodes.
 **/
public class TMTreeNodeTree extends JTree {

    /**
     * Creates the tree component.
     **/
    public TMTreeNodeTree() {
        super();
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setShowsRootHandles(true);
    }

    /**
     * Loads the tree with the given root node.
     * @param rootNode root of the tree to display
     * @param loadLeaves if true, all nodes are loaded; if false, only folder nodes
     **/
    public void loadTreeNodes(TMTreeNode rootNode, boolean loadLeaves) {
        if (loadLeaves) {
            setModel(new DefaultTreeModel(rootNode));
        }
        else {
            setModel(new FolderTreeModel(rootNode));
        }
//        model.addTreeModelListener(this);
    }

    /**
     * Gets the selected node.
     * If there is no selected node, the root is returned as default.
     **/
    public TMTreeNode getSelectedNode() {
        TMTreeNode n = (TMTreeNode)getLastSelectedPathComponent();
        if (n != null) return n;
        return (TMTreeNode)getModel().getRoot();
    }

    /**
     * Expands the specified node.
     **/
    public void expandNode(DefaultMutableTreeNode node) {
        setExpandedState(new TreePath(node.getPath()), true);
    }

    private class FolderTreeModel extends DefaultTreeModel {

        private DefaultMutableTreeNode mRoot;

        FolderTreeModel(DefaultMutableTreeNode root) {
            super(root);
            mRoot= root;
        }

        /**
         * Gets a folder child at the given index.
         * @param parent parent tree node object
         * @param index child index under the parent
         * @return child node at the folder-only index
         **/
        public Object getChild(Object parent, int index) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)parent;
            int pos = 0;
            for (int i=0, cnt=0; i<node.getChildCount(); i++) {
                if (node.getChildAt(i) instanceof FolderNode) {
                    if (cnt++ == index) {
                        pos = i;
                        break;
                    }
                }
            }
            return node.getChildAt(pos);
        }

        /**
         * Counts folder children of the given parent node.
         * @param parent parent tree node object
         * @return number of FolderNode children under the parent
         **/
        public int getChildCount(Object parent) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)parent;
            int childCount = 0;
            Enumeration<?> children = node.children();
            while (children.hasMoreElements()) {
                if (children.nextElement() instanceof FolderNode) {
                    childCount++;
                }
            }
            return childCount;
        }
    }

}