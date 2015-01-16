package ie.gmit.computing.pollutantstracker;

import java.io.Serializable;

/**
 * Node interface, implemented by CompositeNode class
 * Created by Zeyuan Li on 15/1/6.
 *
 * @author Zeyuan Li
 */
public interface Node extends Serializable {

    public void addChild(Node child);

    public void removeChild(Node child);

    public Node getParent();

    public void setParent(Node parent);

    public void insertChild(Node n, Node child);

    public Node[] getChildren();

    public boolean hasChildren();

    public boolean isLeaf();

    public boolean isRoot();

    public String getName();

    public void setImg(byte[] b);

    public byte[] getImg();

    public boolean hasImg();
}
