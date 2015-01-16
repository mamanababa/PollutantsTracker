package ie.gmit.computing.pollutantstracker;

import java.io.Serializable;
import java.util.*;

/**
 * implementation of Node interface
 * use List to operate every node and set relationship
 * Created by Zeyuan Li on 15/1/6.
 *
 * @author Zeyuan Li
 */

public class CompositeNode implements Node, Serializable {

    private static final long serialVersionUID = 777L;

    private Node parent = null;
    private List<Node> childrenList = new ArrayList<Node>();
    private byte[] bytes = null;
    private String name = null;

    /**
     * constructor get the node name and parent node
     *
     * @param name
     * @param parent
     */
    public CompositeNode(String name, Node parent) {
        super();
        this.name = name;
        this.parent = parent;
    }

    /**
     * delegate methods of List to operate nodes
     */


    /**
     * add child node by delegate add() method of List
     *
     * @param child
     */
    @Override
    public void addChild(Node child) {
        childrenList.add(child);
    }

    /**
     * remove child node by delegate remove() method of List
     *
     * @param child
     */
    @Override
    public void removeChild(Node child) {
        childrenList.remove(child);
    }

    /**
     * @return return parent node
     */
    @Override
    public Node getParent() {
        return this.parent;
    }

    /**
     * set parent node
     *
     * @param parent
     */
    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * insert node between parent node and one of its children node
     *
     * @param n     node ready to insert to tree
     * @param child child of node n
     */
    @Override
    public void insertChild(Node n, Node child) {
        this.removeChild(child);
        child.setParent(n);
        n.addChild(child);
        n.setParent(this);
        this.addChild(n);
    }

    /**
     * @return return a copy of children list
     */
    @Override
    public Node[] getChildren() {
        Node[] temp = new Node[childrenList.size()];
        for (int i = 0; i < childrenList.size(); i++) {
            temp[i] = childrenList.get(i);
        }
        return temp;
    }

    /**
     * @return return true for this node has children,false for has no children
     */
    @Override
    public boolean hasChildren() {
        if (!isLeaf())
            return true;
        else
            return false;
    }

    /**
     * @return return true for this node is leaf node, false for is not leaf node
     */
    @Override
    public boolean isLeaf() {
        return childrenList.size() == 0;
    }

    /**
     * @return return true for this node is root node, false for is not root node
     */
    @Override
    public boolean isRoot() {
        return this.parent == null;
    }

    /**
     * @return return true for this node has img, false for has no img
     */
    @Override
    public boolean hasImg() {
        return this.bytes != null;
    }

    /**
     * @return photo data of this node
     */
    @Override
    public byte[] getImg() {
        return this.bytes;
    }

    /**
     * @param b set photo data of this node
     */
    @Override
    public void setImg(byte[] b) {
        this.bytes = b;
    }

    /**
     * @return return the name of this node
     */
    @Override
    public String getName() {
        return name;
    }
}