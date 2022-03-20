package scrabble;

/**
 * Andrew Geyko
 * This class represents bundled up information about a move made. Used in
 * the solver program to get information from the scrabble.ComputerPlayer
 */

public class MoveInfo {
    private int score;
    private String word;
    private int row;
    private int col;
    private Board.Direction direction;
    private boolean moveSuccess;

    /**
     * @return - score of last move
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score - score of last move
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return - word that was played on last move
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word - word that was played on last move
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * @return - row where root of word is
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row - row where root of word is
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return - column where root of word is
     */
    public int getCol() {
        return col;
    }

    /**
     * @param col - column where root of word is
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * @return - direction in which word was played
     */
    public Board.Direction getDirection() {
        return direction;
    }

    /**
     * @param direction - direction in which word was played
     */
    public void setDirection(Board.Direction direction) {
        this.direction = direction;
    }

    /**
     * @return - whether the move was successful
     */
    public boolean getMoveSuccess() {
        return moveSuccess;
    }

    /**
     * @param bool - whether the move was successful
     */
    public void setMoveSuccess(boolean bool) {
        moveSuccess = bool;
    }

    /**
     * Resets the information in the object for next move
     */
    public void clear() {
        score = 0;
        word = null;
        row = 0;
        col = 0;
        direction = null;
        moveSuccess = false;
    }
}
