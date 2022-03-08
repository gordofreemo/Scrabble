/**
 * Andrew Geyko
 * This class reads in a dictionary file and makes a Trie
 * from it, constructor throws an exception if the file was not found
 * and the only open method, makeTree, returns the root of the trie.
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class TrieFileParser {
    private TrieNode root;
    private String fileName;
    private FileReader in;

    public TrieFileParser(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        in  = new FileReader(fileName);
        root = new TrieNode('\0');
    }

    /**
     * Makes a complete Trie from the given dictionary
     * @return - root of the trie
     */
    public TrieNode makeTree() {
        Scanner sc = new Scanner(in);
        while(sc.hasNextLine()) {
            TrieNode currNode = root;
            String line = sc.nextLine();
            for(int i = 0; i < line.length(); i++) {
                char toAdd = Character.toLowerCase(line.charAt(i));
                currNode = addNode(currNode, toAdd);
            }
            currNode.setTerminalNode(true);
        }
        return root;
    }

    /**
     * Helper method to put a new node into the trie
     * @param root - node to add character to
     * @param data - character to add to node
     * @return - returns the node that was either added or if it was already
     * a child of the root, that node itself
     */
    private TrieNode addNode(TrieNode root, Character data) {
        TrieNode node = root.getChild(data);
        if(node == null) {
            node = new TrieNode(data);
            root.addChild(node);
        }
        return node;
    }
}
