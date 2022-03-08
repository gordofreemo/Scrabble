
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BoardSolver {
    public static void main(String[] args) throws FileNotFoundException {
        File readFile = new File("testing.txt");
        TrieFileParser parser = new TrieFileParser(args[0]);
        TrieNode root = parser.makeTree();

        Scanner sc = new Scanner(readFile);

        while(sc.hasNext()) {
            int size = sc.nextInt();
            Board board = new Board(size);
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    String nextTile = sc.next();
                    if (Character.isAlphabetic(nextTile.charAt(0))) {
                        board.placeTile(i, j, nextTile.charAt(0));
                        board.getTile(i, j).lockTile();
                        continue;
                    }

                    if (nextTile.charAt(0) != '.') {
                        int mult = Character.getNumericValue(nextTile.charAt(0));
                        board.getTile(i, j).setWordMultiplier(mult);
                    }
                    if (nextTile.charAt(1) != '.') {
                        int mult = Character.getNumericValue(nextTile.charAt(1));
                        board.getTile(i, j).setCharMultiplier(mult);
                    }
                }
            }

            ComputerPlayer player = new ComputerPlayer(board, root);

            sc.nextLine();
            String hand = sc.nextLine();
            for (int i = 0; i < hand.length(); i++) {
                player.addToHand(hand.charAt(i));
            }


            player.makeMove();
            MoveInfo info = player.getMoveInfo();
            System.out.print("Tray: " + hand);
            System.out.println();
            System.out.println("Solution " + info.getWord() + " has " + info.getScore() + " points");
        }
    }
}
