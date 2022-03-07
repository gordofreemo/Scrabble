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

    private TrieNode addNode(TrieNode root, Character data) {
        TrieNode node = root.getChild(data);
        if(node == null) {
            node = new TrieNode(data);
            root.addChild(node);
        }
        return node;
    }
}
