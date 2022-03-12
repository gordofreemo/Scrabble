package scrabble;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class BoardSolver {
    private Scanner sc;
    private TrieNode root;

    /**
     * Used for reading from standard input
     */
    public BoardSolver() {
        sc = new Scanner(System.in);
    }

    /**
     * Used for loading a resource and generating a board
     * @param filename - filename of resource to grab
     */
    public BoardSolver(String filename) {
        sc = new Scanner(ClassLoader.getSystemResourceAsStream(filename));
    }

    /**
     * Parses a board from the scanner input and returns a Board object
     * representing what was read in
     * @return - board object that file represents
     */
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

    /**
     * Output information about the solution that the computer player
     * found for the given board
     * @param board - board to solve
     */
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

    /**
     * Read and "solve" an entire input file specified in the format
     */
    private void parse() {
        while(sc.hasNext()) {
            Board board = makeBoard();
            outputSolution(board);
        }
    }

    /**
     * @param root - root of the trie representing lexicon
     */
    private void setRoot(TrieNode root) {
        this.root = root;
    }

    public static void main(String[] args) throws FileNotFoundException {
        TrieFileParser parser = new TrieFileParser(args[0]);
        TrieNode root = parser.makeTree();
        BoardSolver solver = new BoardSolver();
        solver.setRoot(root);
        solver.parse();
    }
}
