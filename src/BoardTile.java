/**
 * Class representing a tile on the board
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

    public void setCharMultiplier(int multiplier) {
        charMultiplier = multiplier;
    }

    public void setWordMultiplier(int multiplier) {
        wordMultiplier = multiplier;
    }

    public boolean isEmpty() {
        return state == State.EMPTY;
    }

    public void setData(Character data) {
        if(state == State.FILLED) return;
        if(Character.isUpperCase(data)) score = 0;
        else score = scores[data-97];
        state = State.NEW;
        this.data = data;
    }

    public void lockTile() {
        state = State.FILLED;
    }

    //can only clear a tile that has not been filled
    public void clearTile() {
        if(state == State.FILLED) return;
        data = '\0';
        state = State.EMPTY;
        score = 0;
    }

    public int getScore() {
        if(state == State.NEW) return score * charMultiplier;
        return score;
    }

    public Character getData() {
        return data;
    }

    public State getState() {
        return state;
    }

    public int getWordMultiplier() {
        if(state == State.FILLED) return 1;
        return wordMultiplier;
    }

    public int getCharMultiplier() {
        if(state == State.FILLED) return 1;
        return charMultiplier;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return "(" + row + "," + column + ")" +
                "[" + getData()
                +"," + getScore()
                +"," + getCharMultiplier()
                +"," + getWordMultiplier() + "]";

    }

}
