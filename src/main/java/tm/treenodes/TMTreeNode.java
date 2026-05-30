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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Generic treenode class.
 **/
public abstract class TMTreeNode extends DefaultMutableTreeNode {

    private boolean modified;
    private TMTreeNode parent;

    /**
     * Creates a treenode.
     **/
    public TMTreeNode() {
        super();
        modified = false;
    }

    /**
     * Gets this node's child nodes as an array.
     * @return array of child TMTreeNode references
     **/
    public TMTreeNode[] getChildren() {
        TMTreeNode[] ch = new TMTreeNode[getChildCount()];
        for (int i=0; i<ch.length; i++) {
            ch[i]= (TMTreeNode)getChildAt(i);
        }
        return ch;
    }

    /**
     * Returns whitespace indentation matching tree depth.
     * @return leading spaces matching node depth
     **/
    public String getIndent() {
        StringBuffer sb = new StringBuffer();
        int depth = getDepth();
        for (int i=0; i<depth; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    /**
     * Gets the parent TMTreeNode.
     * @return parent TMTreeNode, or null at root
     **/
    public TMTreeNode getTMParent() {
        return (TMTreeNode)getParent();
    }

    /**
     * Returns the XML representation of this node.
     * @return XML fragment representing this node
     **/
    public abstract String toXML();

    /**
     * Sets the display text of this node.
     * @param text new display text for the node
     **/
    public abstract void setText(String text);

    /**
     * Reports whether this node has no children.
     * @return true if the node has no children
     **/
    public boolean isLeaf() {
        return (getChildCount() == 0);
    }

    /**
     * Sets the modified flag on this node.
     * @param modified new modified flag value
     **/
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * Reports whether this node has unsaved changes.
     * @return true if the node has unsaved changes
     **/
    public boolean isModified() {
        return modified;
    }

}