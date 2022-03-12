package scrabble;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class BoardSolver {
    private Scanner sc;
    private TrieNode root;

    public BoardSolver() {
        sc = new Scanner(System.in);
    }

    public BoardSolver(String filename) {
        sc = new Scanner(ClassLoader.getSystemResourceAsStream(filename));
    }

    public Board makeBoard() {
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
        return board;
    }

    private void outputSolution(Board board) {
        ComputerPlayer player = new ComputerPlayer(board, root);
        sc.nextLine();
        String hand = sc.nextLine();
        for (int i = 0; i < hand.length(); i++) {
            player.addToHand(hand.charAt(i));
        }
        System.out.println("Input Board:");
        System.out.print(board);
        player.makeMove();
        MoveInfo info = player.getMoveInfo();
        System.out.print("Tray: " + hand);
        System.out.println();
        System.out.println("Solution " + info.getWord() + " has " + info.getScore() + " points");
        System.out.println("Solution Board:");
        System.out.println(board);
    }

    private void parse() {
        while(sc.hasNext()) {
            Board board = makeBoard();
            outputSolution(board);
        }
    }

    private void setRoot(TrieNode root) {
        this.root = root;
    }

    public static void main(String[] args) throws FileNotFoundException {
        TrieFileParser parser = new TrieFileParser(args[0]);
        TrieNode root = parser.makeTree();
        BoardSolver solver = new BoardSolver("testing.txt");
        solver.setRoot(root);
        solver.parse();
    }
}
