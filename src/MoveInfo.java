/**
 * Andrew Geyko
 * This class represents bundled up information about a move made. Used in
 * the solver program to get information from the ComputerPlayer
 */

import java.util.ArrayList;

public class MoveInfo {
    private int score;
    private String word;
    private int row;
    private int col;
    private Board.Direction direction;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Board.Direction getDirection() {
        return direction;
    }

    public void setDirection(Board.Direction direction) {
        this.direction = direction;
    }


    public void clear() {
        score = 0;
        word = null;
        row = 0;
        col = 0;
        direction = null;
    }
}
