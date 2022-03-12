package scrabble; /**
 * Andrew Geyko
 * This class is for the Human Player in the scrabble game. It has functionality
 * to place things onto the board and to validate a move made.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
    private int totalScore;
    private MoveInfo moveInfo;
    private TrieNode root;

    public HumanPlayer(Board board) {
        hand = new ArrayList<>();
        placed = new HashSet<>();
        this.board = board;
        moveInfo = new MoveInfo();
        totalScore = 0;
    }

    /**
     * Places the given character onto a tile onto the board and
     * updates bookkeeping structures
     * @param tile - what tile to place on
     * @param move - what character to place on tile
     */
    public void placeTile(BoardTile tile, Character move) {
        if(placed.isEmpty()) anchors = Anchor.getAnchors(board);
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
    }

    /**
     * Looks at what has been placed this turn and determines if the move made
     * is valid
     * @return - enum showing the various kinds of misplays that may have
     * occurred
     */
    public MoveStatus validateMove() {
        Board.Direction direction;
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

        if(rowMatch) direction = Board.Direction.DOWN;
        else if(colMatch) direction = Board.Direction.ACROSS;
        else return MoveStatus.DIR_MISMATCH;

        if(!board.validWord(row, col, direction, root)) return MoveStatus.NOT_WORD;

        //is at least one tile placed at some anchor?
        boolean anchored = anchors.keySet().stream()
                .anyMatch(x -> placed.contains(x));
        if(!anchored) return MoveStatus.NOT_ATTACHED;

        moveInfo.setScore(board.scoreWord(row, col, direction));
        moveInfo.setWord(board.getWord(row,col,direction));
        moveInfo.setDirection(direction);
        moveInfo.setRow(row);
        moveInfo.setCol(col);

        return MoveStatus.MOVE_SUCCESS;
    }

}
