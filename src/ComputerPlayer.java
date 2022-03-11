/**
 * Andrew Geyko
 * Class representing the AI computer in the scrabble game. Each move, it
 * computes and makes the highest scoring move that is possible onto the board.
 * The algorithm it uses is the same as described in the "world's fastest
 * scrabble program" paper.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class ComputerPlayer {
    private Board board;
    private HashMap<BoardTile, Anchor> anchors;
    private TrieNode root;
    private ArrayList<Character> hand;
    private MoveInfo moveInfo;

    private String bestWord;
    private int bestScore;
    private Board.Direction bestDirection;
    private int bestRow;
    private int bestCol;
    private int currRow;
    private int currCol;

    ComputerPlayer(Board board, TrieNode root) {
        this.board = board;
        this.root = root;
        anchors = new HashMap<>();
        hand = new ArrayList<>();
        bestScore = 0;
        moveInfo = new MoveInfo();
    }

    public MoveInfo getMoveInfo() {
        return moveInfo;
    }

    public void addToHand(Character c) {
        hand.add(c);
    }

    /**
     * Gets all the anchors for a given board, and computes all the
     * crosschecks for them.
     */
    public void getAnchors() {
        anchors.clear();

        for(int i = 0; i < board.getSize(); i++) {
            for(int j = 0; j < board.getSize(); j++) {
                if(board.getTile(i,j).isEmpty()) continue;;
                BoardTile[] tiles = new BoardTile[4];
                tiles[0] = board.getTile(i+1, j);
                tiles[1] = board.getTile(i-1,j);
                tiles[2] = board.getTile(i,j+1);
                tiles[3] = board.getTile(i,j-1);
                for(BoardTile tile : tiles ){
                    if(tile == null || !tile.isEmpty()) continue;
                    anchors.put(tile, new Anchor(tile, board));
                }
            }
        }
        for(Anchor anchor : anchors.values()) anchor.updateCrossChecks(root);

    }

    /**
     * Computes all possible "left parts" before a given anchor. For each of these
     * left parts that we generate, attempt to extendRight to find complete
     * words.
     * @param partial - partial word that has been generated so far
     * @param node - node in he trie where we currently are
     * @param lim - how much space we have to form the left part
     * @param anchor - anchor square that we are forming the left part of
     */
    private void leftPart(String partial, TrieNode node, int lim, BoardTile anchor) {
        extendRight(partial, node, anchor);
        if(lim > 0) {
            for(Character c : node.getChildrenData()) {
                if(hand.contains(c)) {
                    hand.remove(c);
                    currCol--;
                    TrieNode newNode = node.getChild(c);
                    leftPart(partial + c, newNode, lim - 1, anchor);
                    currCol++;
                    hand.add(c);
                }
                if(hand.contains('*')) {
                    hand.remove((Character)'*');
                    currCol--;
                    TrieNode newNode = node.getChild(c);
                    leftPart(partial + Character.toUpperCase(c), newNode,lim - 1, anchor);
                    currCol++;
                    hand.add('*');
                }
            }
        }
    }

    /**
     * Find all valid permutations of a given left part in the across direction,
     * calls checkWord for every valid permutation found. This is the same
     * backtracking algorithm that was described in the "worlds fastest scrabble
     * program" paper.
     * @param partial - word that has been built so far
     * @param node - node in the trie where we currently are
     * @param square - square on the board where we currently are
     */
    private void extendRight(String partial, TrieNode node, BoardTile square) {
        if(square == null) {
            if(node.isTerminalNode()) checkWord(partial, Board.Direction.ACROSS);
            return;
        }
        if(square.isEmpty()) {
            if(node.isTerminalNode()) checkWord(partial, Board.Direction.ACROSS);
            for(Character c : node.getChildrenData()) {
                boolean condition = !anchors.containsKey(square)
                                    || anchors.get(square).hasVCheck(c);
                if(hand.contains(c) && condition) {
                    hand.remove(c);
                    TrieNode newNode = node.getChild(c);
                    int col = square.getColumn() + 1;
                    BoardTile nextSquare = board.getTile(currRow, col);
                    String newString = partial + c;
                    extendRight(newString, newNode, nextSquare);
                    hand.add(c);
                }
                if(hand.contains('*') && condition) {
                    hand.remove((Character)'*');
                    TrieNode newNode = node.getChild(c);
                    int col = square.getColumn() + 1;
                    BoardTile nextSquare = board.getTile(currRow, col);
                    String newString = partial + Character.toUpperCase(c);
                    extendRight(newString, newNode, nextSquare);
                    hand.add('*');
                }
            }
        }
        else {
            Character c = square.getData();
            if(node.contains(c)) {
                BoardTile nextSquare = board.getTile(currRow, square.getColumn()+1);
                TrieNode newNode = node.getChild(c);
                String newString = partial + c;
                extendRight(newString, newNode, nextSquare);
            }
        }
    }

    /**
     * Computes all possible "top parts" before a given anchor. For each
     * of these top parts that we generate, attempt to extendDown to find
     * complete words.
     * @param partial - partial word that has been generated so far
     * @param node - node in the trie where we currently are
     * @param lim - how much space we have to form the top part
     * @param anchor - anchor square that we are forming the top part of
     */
    private void topPart(String partial, TrieNode node, int lim, BoardTile anchor) {
        extendDown(partial, node, anchor);
        if(lim > 0) {
            for(Character c : node.getChildrenData()) {
                if(hand.contains(c)) {
                    hand.remove(c);
                    currRow--;
                    TrieNode newNode = node.getChild(c);
                    topPart(partial + c, newNode, lim-1, anchor);
                    currRow++;
                    hand.add(c);
                }
                if(hand.contains('*')) {
                    hand.remove((Character)'*');
                    currRow--;
                    TrieNode newNode = node.getChild(c);
                    topPart(partial + Character.toUpperCase(c), newNode, lim-1, anchor);
                    currRow++;
                    hand.add('*');
                }
            }
        }
    }

    /**
     * Find all valid permutations of a given top part in the downwards direction,
     * calls checkWord for every valid permutation found. This is the same
     * backtracking algorithm that was described in the "worlds fastest scrabble
     * program" paper, just the extendDown version instead of extendRight.
     * @param partial - word that has been built so far
     * @param node - node in the trie where we currently are
     * @param square - square on the board where we currently are
     */
    private void extendDown(String partial, TrieNode node, BoardTile square) {
        //if off the board, stop extending
        if(square == null) {
            if(node.isTerminalNode()) checkWord(partial, Board.Direction.DOWN);
            return;
        }
        //if square is empty, start placing valid characters and doing
        //recursive backtrack
        if(square.isEmpty()) {
            if(node.isTerminalNode()) checkWord(partial, Board.Direction.DOWN);
            for(Character c : node.getChildrenData()) {
                boolean condition = !anchors.containsKey(square)
                                    || anchors.get(square).hasHCheck(c);
                //If we have in our hand a valid character, backtrack
                if(hand.contains(c) && condition) {
                    hand.remove(c);
                    TrieNode newNode = node.getChild(c);
                    int row = square.getRow() +1;
                    BoardTile nextSquare = board.getTile(row, currCol);
                    String newString = partial + c;
                    extendDown(newString, newNode, nextSquare);
                    hand.add(c);
                }
                //If we have a blank in our hand, backtrack for each valid character
                if(hand.contains('*') && condition) {
                    hand.remove((Character) '*');
                    TrieNode newNode = node.getChild(c);
                    int row = square.getRow() + 1;
                    BoardTile nextSquare = board.getTile(row,currCol);
                    String newString = partial + Character.toUpperCase(c);
                    extendDown(newString, newNode, nextSquare);
                    hand.add('*');
                }
            }
        }
        //If the current square already has something, keep going
        else {
            Character c = square.getData();
            if(node.contains(c)) {
                BoardTile nextSquare = board.getTile(square.getRow()+1, currCol);
                TrieNode newNode = node.getChild(c);
                String newString = partial + c;
                extendDown(newString, newNode, nextSquare);
            }
        }
    }

    /**
     * Play the best scoring move onto the board.
     * Basically goes through every anchor, computing the "before" part somehow
     * (either through seeing what came before or calling beforePart) and then
     * extends right.
     */
    public void makeMove() {
        moveInfo.setHand(hand);
        //Recompute anchor squares and reset score
        getAnchors();
        bestWord = "";
        bestScore = 0;
        bestCol = 0;
        bestRow = 0;
        bestDirection = null;

        //For every anchor we computed, try to form a word
        for(Anchor anchor : anchors.values()) {
            currCol = anchor.getCol();
            currRow = anchor.getRow();

            //making across moves
            BoardTile prev = board.getTile(currRow, currCol-1);
            //If the node is at the edge of board, just extendRight
            if(prev == null) {
                extendRight("", root, anchor.getTile());
            }
            //if the node before is empty, see how much empty space we have
            //to form the left part
            else if(prev.isEmpty()) {
                int lim = 0;
                while (prev != null && !anchors.containsKey(prev)) {
                    lim += 1;
                    prev = board.getTile(currRow, currCol - lim);
                }
                leftPart("", root, lim-1, anchor.getTile());
            }
            //If there is something before the anchor, compute that "left part"
            //and then extendRight
            else {
                String partial = "";
                while(prev != null && !prev.isEmpty()) {
                    partial = prev.getData() + partial;
                    currCol--;
                    prev = board.getTile(currRow,currCol-1);
                }
                TrieNode node = root;
                for(int i = 0; i < partial.length(); i++) {
                    node = node.getChild(Character.toLowerCase(partial.charAt(i)));
                }
                extendRight(partial, node, anchor.getTile());
            }

            currCol = anchor.getCol();
            currRow = anchor.getRow();
            //making down moves
            prev = board.getTile(currRow-1, currCol);
            //If the node is at the edge of board, just extendDown
            if(prev == null) {
                extendDown("", root, anchor.getTile());
            }
            //if the node before is empty, see how much empty space we have
            //to form the top part
            else if(prev.isEmpty()) {
                int lim = 0;
                while(prev != null && !anchors.containsKey(prev)) {
                    lim += 1;
                    prev = board.getTile(currRow-lim, currCol);
                }
                topPart("", root, lim-1, anchor.getTile());
            }
            //If there is something before the anchor, compute the "top part"
            //and then extendDown
            else {
                String partial = "";
                while(prev != null && !prev.isEmpty()) {
                    partial = prev.getData() + partial;
                    currRow--;
                    prev = board.getTile(currRow-1, currCol);
                }
                TrieNode node = root;
                for(int i = 0; i < partial.length(); i++) {
                    node = node.getChild(Character.toLowerCase(partial.charAt(i)));
                }
                extendDown(partial, node, anchor.getTile());
            }
        }

        moveInfo.setCol(bestCol);
        moveInfo.setRow(bestRow);
        moveInfo.setWord(bestWord);
        moveInfo.setDirection(bestDirection);
        moveInfo.setScore(bestScore);
        placeMove();
    }

    /**
     * Checks a move that the extendRight algorithm computed to make sure
     * the move is valid, then updates the information about where the best
     * move was made if this move is better than all the others.
     * @param partial - string representing word that has been made
     * @param direction - direction in which word was placed
     */
    private void checkWord(String partial, Board.Direction direction) {
        int row = currRow;
        int col = currCol;
        boolean valid = false;
        //Place word onto the board
        for(int i = 0; i < partial.length(); i++) {
            board.placeTile(row, col, partial.charAt(i));
            if(anchors.containsKey(board.getTile(row,col))) valid = true;
            if(direction == Board.Direction.ACROSS) col++;
            else row++;
        }
        //Double check that word is valid, score word
        if(!board.validWord(currRow,currCol,direction,root)) return;
        int score = board.scoreWord(currRow, currCol, direction);
        row = currRow;
        col = currCol;
        //take word off the board
        for(int i = 0; i < partial.length(); i++) {
            board.getTile(row,col).clearTile();
            if(direction == Board.Direction.ACROSS) col++;
            else row++;
        }
        //Update best move stats if necessary
        if(hand.isEmpty()) score += 50;
        if(score > bestScore && valid) {
            bestRow = currRow;
            bestCol = currCol;
            bestWord = partial;
            bestDirection = direction;
            bestScore = score;
        }
    }

    /**
     * Put the best found move onto the board and lock the tiles
     */
    private void placeMove() {
        int row = bestRow;
        int col = bestCol;
        for(int i = 0; i < bestWord.length(); i++) {
            board.placeTile(row, col, bestWord.charAt(i));
            board.getTile(row,col).lockTile();
            hand.remove((Character)bestWord.charAt(i));
            if(bestDirection == Board.Direction.ACROSS) col++;
            else row++;
        }
    }
}
