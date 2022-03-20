package scrabble;
/**
 * Andrew Geyko
 * This class is for the Human Player in the scrabble game. It has functionality
 * to place things onto the board and to validate a move made.
 */

import java.util.*;

public class HumanPlayer {
    /**
     * Represents whether a move is valid, if it is not then describes how
     * the move is incorrect.
     */
    public enum MoveStatus {
        NOT_ATTACHED,
        NOT_WORD,
        DIR_MISMATCH,
        MOVE_SUCCESS
    }

    private ArrayList<Character> hand;
    private HashSet<BoardTile> placed;
    private HashMap<BoardTile, Anchor> anchors;
    private Board board;
    private MoveInfo moveInfo;

    public HumanPlayer(Board board) {
        hand = new ArrayList<>();
        placed = new HashSet<>();
        this.board = board;
        moveInfo = new MoveInfo();
    }

    /**
     * @return - MoveInfo object represent information about move made
     */
    public MoveInfo getMoveInfo() {
        return moveInfo;
    }

    /**
     * Add a character c to the player's hand
     * @param c - character to add to hand
     */
    public void addToHand(Character c) {
        hand.add(c);
    }

    /**
     * @return - how many "tiles" are in the hand of the player
     */
    public int getHandSize() {
        return hand.size();
    }

    /**
     * @return - returns the hand of the player
     */
    public List<Character> getHand() {
        return hand;
    }

    /**
     * @return - set of tiles that the player has put onto the board
     */
    public Set<BoardTile> getPlaced() {
        return placed;
    }
    
    /**
     * Places the given character onto a tile onto the board and
     * updates bookkeeping structures
     * @param tile - what tile to place on
     * @param move - what character to place on tile
     */
    public void placeTile(BoardTile tile, Character move) {
        if(placed.isEmpty()) {
            anchors = Anchor.getAnchors(board);
            moveInfo.clear();
        }
        if(!tile.isEmpty()) return;
        tile.setData(move);
        hand.remove(move);
        placed.add(tile);
    }

    /**
     * Take everything off the board that has been placed this turn
     * and update bookkeeping structure
     */
    public void resetMove() {
        for(BoardTile tile : placed) {
            Character toAdd = tile.getData();
            if(Character.isUpperCase(toAdd)) hand.add('*');
            else hand.add(toAdd);
            tile.clearTile();
        }
        placed.clear();
        moveInfo.clear();
    }

    /**
     * Looks at what has been placed this turn and determines if the move made
     * is valid
     * @return - enum showing the various kinds of misplays that may have
     * occurred
     */
    public MoveStatus validateMove(TrieNode root) {
        Board.Direction direction;
        if(placed.isEmpty()) return MoveStatus.NOT_WORD;
        //Get left and topmost placed tile
        int row = placed.stream()
                .map(BoardTile::getRow)
                .reduce(Integer.MAX_VALUE,Integer::min);
        int col = placed.stream()
                .map(BoardTile::getColumn)
                .reduce(Integer.MAX_VALUE,Integer::min);
        //Are all rows/columns consistent?
        boolean rowMatch = placed.stream()
                        .allMatch(x -> x.getRow() == row);
        boolean colMatch = placed.stream()
                        .allMatch(x -> x.getColumn() == col);

        boolean both = rowMatch && colMatch;
        if(rowMatch) direction = Board.Direction.ACROSS;
        else if(colMatch) direction = Board.Direction.DOWN;
        else return MoveStatus.DIR_MISMATCH;

        if(!both) {
            if (!board.validWord(row, col, direction, root)) return MoveStatus.NOT_WORD;
        }
        else {
            if(board.validWord(row,col, Board.Direction.ACROSS, root)) direction = Board.Direction.ACROSS;
            else if(board.validWord(row,col, Board.Direction.DOWN, root)) direction = Board.Direction.DOWN;
            else return MoveStatus.NOT_WORD;
        }

        //is at least one tile placed at some anchor?
        boolean anchored = anchors.keySet().stream()
                .anyMatch(x -> placed.contains(x));
        if(!anchored) return MoveStatus.NOT_ATTACHED;

        moveInfo.setScore(board.scoreWord(row, col, direction));
        moveInfo.setWord(board.getWord(row,col,direction));
        moveInfo.setDirection(direction);
        moveInfo.setRow(row);
        moveInfo.setCol(col);
        moveInfo.setMoveSuccess(true);

        return MoveStatus.MOVE_SUCCESS;
    }

    /**
     * Lock the move the player made onto the board
     */
    public void placeMove() {
        for(BoardTile tile : placed) tile.lockTile();
        placed.clear();
        anchors.clear();
    }
}
