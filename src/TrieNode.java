/**
 * Class responsible for the data structure
 */

import java.util.Collection;
import java.util.HashMap;

public class TrieNode {
    private boolean terminalNode;
    private HashMap<Character, TrieNode> children;
    private Character data;

    public TrieNode(Character data) {
        this.data = data;
        children = new HashMap<>();
        terminalNode = false;
    }

    /**
     * Add a new node to the trie
     * @param node - node to add
     */
    public void addChild(TrieNode node) {
        children.put(node.getData(), node);
    }

    /**
     * Get the child containing the specified data (if there is one)
     * @param data - the data child should have
     * @return - the node if the child was found, null otherwise
     */
    public TrieNode getChild(Character data) {
        return children.get(data);
    }

    /**
     * @return A collection of characters representing what letters follow
     * the current one
     */
    public Collection<Character> getChildrenData() {
        return children.keySet();
    }

    /**
     * @return - character that the current node has
     */
    public Character getData() {
        return data;
    }

    /**
     * @param data - does the current node have a child with this given
     *             data in it?
     * @return - true if child with given data, false otherwise
     */
    public boolean contains(Character data) {
        return children.containsKey(data);
    }

    /**
     * @return - true if current nodei s terminal node, false otherwise
     */
    public boolean isTerminalNode() {
        return terminalNode;
    }

    /**
     * @param bool - whether the current node is or is not a terminal node
     */
    public void setTerminalNode(boolean bool) {
        terminalNode = bool;
    }

    /**
     * Print out a visualization of trees, mostly helpful for relatively small
     * lexicons, otherwise is just too overwhelming
     */
    public void printTree() {
        printHelper(0);
    }

    /**
     * Helper function for printing
     * @param depth - how much spacing should be on the left
     */
    private void printHelper(int depth) {
        for(int i = 0; i < depth; i++) {
            System.out.print("  ");
        }

        if(terminalNode) System.out.println(">>" + data);
        else System.out.println("->" + data);
        for(TrieNode node : children.values()) {
            node.printHelper(depth+1);
        }
    }

}
