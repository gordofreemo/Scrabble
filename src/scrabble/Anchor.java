package scrabble;

import java.util.HashMap;

/**
 * Andrew Geyko
 * This class represents the concept of an scrabble.Anchor Square, which is an empty
 * tile on the board that is connected to some other tile on the board.
 * This should only really be used in the scrabble.ComputerPlayer class, where the
 * AI gets anchor tiles on the board and attempts to make words around the
 * anchor tile.
 */

public class Anchor {
    private boolean[] vCheck; //What letters form valid down words when playing across
    private boolean[] hCheck; //What letters form valid across words when playing down
    private BoardTile tile;
    private Board board;
    private int row;
    private int col;

    public Anchor(BoardTile tile, Board board) {
        this.tile = tile;
        this.board = board;
        row = tile.getRow();;
        col = tile.getColumn();
        vCheck = new boolean[26];
        hCheck = new boolean[26];
    }

    /**
     * Given a board, make a hashmap relating what tiles the anchors are on.
     * Useful to be static since it makes sense that the scrabble.Anchor class should
     * be able to give us all the anchors we need.
     * @param board - the board to find the anchors of
     * @return - hashmap where the keys are tiles on the board and
     * values are anchor objects
     */
    static public HashMap<BoardTile, Anchor> getAnchors(Board board) {
        HashMap<BoardTile, Anchor> anchors = new HashMap<>();
        boolean boardEmpty = true;
        for(int i = 0; i < board.getSize(); i++) {
            for(int j = 0; j < board.getSize(); j++) {
                if(board.getTile(i,j).isEmpty()) continue;
                boardEmpty = false;
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
        if(boardEmpty) {
            int size = board.getSize();
            BoardTile tile = board.getTile(size/2,size/2);
            anchors.put(tile, new Anchor(tile, board));
        }
        return anchors;
    }

    /**
     * @param c - character to check in across check
     * @return - whether c forms legal ACROSS words if it is to be placed
     * onto the board, true if it does, false otherwise
     */
    public boolean hasHCheck(Character c) {
        if((c-97) > 25 || (c-97) < 0) return false;
        else return hCheck[c-97];
    }

    /**
     * @param c - character to check in vertical checl
     * @return - whether c forms legal DOWN words if it is to be place onto
     * the board, true if it does, false otherwise
     */
    public boolean hasVCheck(Character c) {
        if((c-97) > 25 || (c-97) < 0) return false;
        else return vCheck[c-97];
    }

    /**
     * Compute the cross checks for the given anchor square
     * @param root - root of the Trie
     */
    public void updateCrossChecks(TrieNode root) {
        updateHorizontalCheck(root);
        updateVerticalCheck(root);
    }

    /**
     * Updates the vertical crosscheck array with the characters that
     * form legal DOWN words when playing an ACROSS word.
     * @param root - root of the Trie
     */
    private void updateVerticalCheck(TrieNode root) {
        for(int i = 0; i < 26; i++) vCheck[i] = false;

        for(int i = 'a'; i <= 'z'; i++) {
            tile.setData((char)i);
            //If we form a valid word or if both tiles nearby are empty
            boolean condition = board.getTile(row-1,col) == null || board.getTile(row-1,col).isEmpty();
            condition &= board.getTile(row+1,col) == null || board.getTile(row+1,col).isEmpty();
            vCheck[i-'a'] = board.validWord(row, col, Board.Direction.DOWN, root) || condition;
            tile.clearTile();
        }
    }

    /**
     * Updates the horizontal crosscheck array with the characters that
     * form legal ACROSS words when playing DOWN words
     * @param root - root of the Trie
     */
    private void updateHorizontalCheck(TrieNode root) {
        for(int i = 0; i < 26; i++) hCheck[i] = false;

        for(int i = 'a'; i <= 'z'; i++) {
            tile.setData((char)i);
            //If we form a valid word or if both tiles nearby are empty
            boolean condition = board.getTile(row,col-1) == null || board.getTile(row,col-1).isEmpty();
            condition &= board.getTile(row,col+1) == null || board.getTile(row,col+1).isEmpty();
            hCheck[i-'a'] = board.validWord(row, col, Board.Direction.ACROSS, root) || condition;
            tile.clearTile();
        }
    }

    /**
     * @return - Row on the board where the anchor square is
     */
    public int getRow() {
        return row;
    }

    /**
     * @return - Column on the board where the anchor square is
     */
    public int getCol() {
        return col;
    }

    /**
     * @return - Tile on the board where the anchor square is
     */
    public BoardTile getTile() {
        return tile;
    }

    /**
     * Used in HashMap shenanigans
     */
    @Override
    public int hashCode() {
        return tile.hashCode();
    }

    /**
     * @return - visual representation of AnchorSquare
     */
    @Override
    public String toString() {
        String string1 = "";
        String string2 = "";
        string1 += "(" + row + "," + col + ")\n";

        string1 += "Across Cross Checks   [";
        string2 += "Vertical Cross Checks [";
        for(int i = 0; i < 26; i++) {
            string1 += (char)(i+97) + " " + hCheck[i] + ",";
            string2 += (char)(i+97) + " " + vCheck[i] + ',';
        }
        string1 += "]\n";
        string2 += "]\n";

        return string1 + string2;
    }

    /**
     * Also used in HashMap shenanigans, the cast should be fine, we only
     * ever compare Anchors with Anchors
     */
    @Override
    public boolean equals(Object obj) {
        Anchor anchor = (Anchor)obj;
        return anchor.tile == tile;
    }
}
