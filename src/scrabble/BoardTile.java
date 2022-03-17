package scrabble;

/**
 * Andrew Geyko
 * Class representing a tile on the board, mostly just sort of holds
 * various kinds of data, not many "logical" operations to be done on it.
 * The interesting functionality is that a tile can be one of three things:
 * empty, filled, or new. Empty is fairly self-explanatory. A filled tile
 * is one that is already on the board and cannot be removed. On the other hand,
 * a new tile is one that is placed in the course of a turn. It can be removed
 * from the board and all the tile bonuses apply only to new tiles when the score
 * is being counted. After a move is finished, "lock" all of the tiles that
 * you have placed in order to transform them from "new" to "filled".
 * The character \0 (null character) represents an empty tile
 */

public class BoardTile {
    public enum State {
        FILLED, EMPTY, NEW
    }

    private int charMultiplier;
    private int wordMultiplier;
    private Character data;
    private int score;
    private int row;
    private int column;
    //points for certain characters in the scrabble game
    private static int[] scores = {1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10};
    private State state;

    public BoardTile(int row, int column) {
        charMultiplier = 1;
        wordMultiplier = 1;
        score = 0;
        data = '\0';
        this.row = row;
        this.column = column;
        state = State.EMPTY;
    }

    /**
     * Set the character multiplier that the tile has
     * @param multiplier - what the multiplier is
     */
    public void setCharMultiplier(int multiplier) {
        charMultiplier = multiplier;
    }

    /**
     * Set the word multiplier that the tile has
     * @param multiplier - what the multiplier is
     */
    public void setWordMultiplier(int multiplier) {
        wordMultiplier = multiplier;
    }

    /**
     * @return - true if tile is empty, false otherwise
     */
    public boolean isEmpty() {
        return state == State.EMPTY;
    }

    /**
     * Put a given character onto the tile, updates the score of the tile
     * according to the scrabble rule. Placing a capital letter means
     * placing a blank was placed. Do not use this method for putting
     * a '\0' - the empty tile
     * @param data - what character to put onto the tile
     */
    public void setData(Character data) {
        if(state == State.FILLED) return;
        if(Character.isUpperCase(data) || data == '*') score = 0;
        else score = scores[data-97];
        state = State.NEW;
        this.data = data;
    }

    /**
     * Make this tile be uneditable, i.e. a move has been made and this
     * word is now on the board. Only works for "new" tiles.
     */
    public void lockTile() {
        if(state != State.EMPTY) state = State.FILLED;
    }

    /**
     * Remove the data from the tile if the tile has not been
     * locked onto the board yet
     */
    public void clearTile() {
        if(state == State.FILLED) return;
        data = '\0';
        state = State.EMPTY;
        score = 0;
    }

    /**
     * @return - score of the current tile, if the tile is new then
     * the character multiplier is applied, otherwise the multiplier is
     * implicitly 1.
     */
    public int getScore() {
        if(state == State.NEW) return score * charMultiplier;
        return score;
    }

    /**
     * @return - the character on the tile
     */
    public Character getData() {
        return data;
    }

    /**
     * @return - whether a tile is empty, new, or filled
     */
    public State getState() {
        return state;
    }

    /**
     * @return - word multiplier on the tile, the multiplier is 1 if
     * the tile is not new
     */
    public int getWordMultiplier() {
        if(state == State.FILLED) return 1;
        return wordMultiplier;
    }

    /**
     * @return - character multiplier on the tile, implicitly 1 if the tile
     * is not new
     */
    public int getCharMultiplier() {
        if(state == State.FILLED) return 1;
        return charMultiplier;
    }

    /**
     * @return - row on the board where the tile is
     */
    public int getRow() {
        return row;
    }

    /**
     * @return - column on the board where the tile is
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return - String representation of the tile
     */
    public String toString() {
        if(!isEmpty()) return " " + getData();
        String string = "";

        if(getWordMultiplier() == 1) string += ".";
        else string += getWordMultiplier();

        if(getCharMultiplier() == 1) string += ".";
        else string += getCharMultiplier();

        return string;
    }

}
