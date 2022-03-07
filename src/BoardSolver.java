
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BoardSolver {
    public static void main(String[] args) throws FileNotFoundException {
        File readFile = new File("testing.txt");
        TrieFileParser parser = new TrieFileParser("sowpods.txt");
        TrieNode root = parser.makeTree();

        Scanner sc = new Scanner(readFile);
        Board board = new Board(sc.nextInt());

        for(int i = 0; i < board.getSize(); i++) {
            for(int j = 0; j < board.getSize(); j++) {
                String nextTile = sc.next();
                if(Character.isAlphabetic(nextTile.charAt(0))) {
                    board.placeTile(i,j,nextTile.charAt(0));
                    board.getTile(i,j).lockTile();
                    continue;
                }

                if(nextTile.charAt(0) != '.') board.getTile(i,j).setWordMultiplier(Character.getNumericValue(nextTile.charAt(0)));
                if(nextTile.charAt(1) != '.') board.getTile(i,j).setCharMultiplier(Character.getNumericValue(nextTile.charAt(1)));
            }
        }

        System.out.println(board);
        ComputerPlayer player = new ComputerPlayer(board, root);

        sc.nextLine();
        String hand = sc.nextLine();
        for(int i = 0; i < hand.length(); i++) {
            player.addToHand(hand.charAt(i));
        }

        player.makeMove();
    }
}
