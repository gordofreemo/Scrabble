package scrabble;

/**
 * Andrew Geyko
 * Class representing the game board, main functionality is placing/removing
 * things from the board, getting a tile from the board.
 * You can also count the score of a given word based on the rules of scrabble
 * and see if a word on the board is in a given dictionary.
 */

public class Board {
    /**
     * Representing a direction in which a word can be placed onto the board,
     * pretty self-explanatory. Used in loops to decide which direction to go.
     */
    public enum Direction {
        ACROSS,
        DOWN
    }

    private BoardTile[][] board; //All operations w/ these are zero-indexed
    private int size;

    public Board(int size) {
        board = new BoardTile[size][size];
        this.size = size;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                board[i][j] = new BoardTile(i,j);
            }
        }
    }

    /**
     * Put the given data onto the given row/column onto the board, if
     * out of bounds just do nothing
     * @param row - what row to place on
     * @param col - what column to place on
     * @param data - what character to put on the tile
     */
    public void placeTile(int row, int col, char data) {
        if(row >= size || col >= size || row < 0 || col < 0) return;
        board[row][col].setData(data);
    }

    /**
     * Get a tile from the board at a specified index
     * @param row - what row to get the tile from
     * @param col - what column to get the tile from
     * @return - Tile at given index, or null if out of bounds
     */
    public BoardTile getTile(int row, int col) {
        if (row >= size || col >= size || row < 0 || col < 0) return null;
        return board[row][col];
    }

    /**
     * @return - size of the board
     */
    public int getSize() {
        return size;
    }

    /**
     * Checks to see if a word on the board is part of the dictionary. It
     * doesn't matter if you give it the start of a word or the middle of
     * a word, the computation works either way.
     * @param row - row where some part of the word is
     * @param col - column where some part of the word is
     * @param direction - direction in which the word is going
     * @param root - root of the Trie representing the lexicon
     * @return - true if the word on the board is in the dictionary, false
     * otherwise
     */
    public boolean validWord(int row, int col, Direction direction, TrieNode root) {
        //go to start of word
        if(direction == Direction.ACROSS) {
            while(col > 0 && !getTile(row, col-1).isEmpty()) col--;
        }
        else {
            while(row > 0 && !getTile(row-1, col).isEmpty()) row--;
        }

        TrieNode currNode = root;
        while(row < size && col < size && !getTile(row,col).isEmpty()) {
            BoardTile tile = getTile(row,col);
            currNode = currNode.getChild(Character.toLowerCase(tile.getData()));
            if(currNode == null) break;
            if(direction == Direction.ACROSS) col++;
            else row++;
        }

        return currNode != null && currNode.isTerminalNode();
    }

    /**
     * Given part of a word and the direction the word is in, scores
     * the word. You don't need to do any additional bookkeeping because
     * BoardTiles are classified as either "new" or "filled", which
     * allows us to apply multipliers to only tiles that are considered "new",
     * and only recur into connected words when we have a "new" tile.
     * @param row - row where part of word is
     * @param col - column where part of word is
     * @param direction - direction in which the word goes
     * @return - returns the total score of the word, with multipliers and
     * connected words counted (doesn't count the bonus when hand empty)
     */
    public int scoreWord(int row, int col, Direction direction) {
        int total = 0;
        int connectedTotal = 0;
        int wordMultiplier = 1;

        //move to start of word
        if(direction == Direction.ACROSS) {
            while(col > 0 && !getTile(row, col-1).isEmpty()) col--;
        }
        else {
            while(row > 0 && !getTile(row-1,col).isEmpty()) row--;
        }

        while(row < size && col < size && !getTile(row,col).isEmpty()) {
            total += getTile(row,col).getScore();
            wordMultiplier *= getTile(row, col).getWordMultiplier();

            if(direction == Direction.ACROSS) {
                BoardTile tile1 = getTile(row+1, col);
                BoardTile tile2 = getTile(row-1, col);
                //If one of the tiles above/below the current one is not
                //empty AND the current tile is new, count connected word
                boolean condition = (tile1 != null && !tile1.isEmpty());
                condition |= (tile2 != null && !tile2.isEmpty());
                condition &= getTile(row,col).getState() == BoardTile.State.NEW;
                if(condition) connectedTotal += nonRecurScore(row,col,Direction.DOWN);
                col++;
            }
            else if(direction == Direction.DOWN) {
                BoardTile tile1 = getTile(row, col+1);
                BoardTile tile2 = getTile(row, col-1);
                //If one of the tiles to the right/left is not empty
                //AND the current tile is new, count connected word score
                boolean condition = (tile1 != null && !tile1.isEmpty());
                condition |= tile2 != null && !tile2.isEmpty();
                condition &= getTile(row,col).getState() == BoardTile.State.NEW;
                if(condition) connectedTotal += nonRecurScore(row,col,Direction.ACROSS);
                row++;
            }
        }

        return total*wordMultiplier + connectedTotal;
    }

    /**
     * Used for counting addition words formed when placing something
     * down onto the board. Basically the same as the original method, just
     * doesn't recur into connected words.
     * @param row - row where part of the connected word is
     * @param col - column where part of the connected word is
     * @param direction - direction in which to go
     * @return - returns total score of the connected word, multipliers
     * and all
     */
    private int nonRecurScore(int row, int col, Direction direction) {
        int total = 0;
        int wordMultiplier = 1;
        //move to start of word
        if(direction == Direction.ACROSS) {
            while(col > 0 && !getTile(row, col-1).isEmpty()) col--;
        }
        else {
            while(row > 0 && !getTile(row-1,col).isEmpty()) row--;
        }

        //Move across word and count score
        while(row < size && col < size && !getTile(row,col).isEmpty()) {
            total += getTile(row,col).getScore();
            wordMultiplier *= getTile(row,col).getWordMultiplier();
            if(direction == Direction.ACROSS) col++;
            else row++;
        }

        return total * wordMultiplier;
    }

    /**
     * Given any part of a word on the board and a direction, returns a
     * string representation of the word on the board
     * @param row - what row a part of the word is on
     * @param col - what column a part of the word is on
     * @param direction - direction in which word is placed
     * @return - string representing the word on the board
     */
    public String getWord(int row, int col, Direction direction) {
        String word = "";
        //move to start of word
        if(direction == Direction.ACROSS) {
            while(col > 0 && !getTile(row, col-1).isEmpty()) col--;
        }
        else {
            while(row > 0 && !getTile(row-1,col).isEmpty()) row--;
        }

        while(row < size && col < size && !getTile(row,col).isEmpty()) {
            word += getTile(row,col).getData();
            if(direction == Direction.ACROSS) col++;
            else row++;
        }

        return word;
    }

    /**
     * @return - string representation of the board
     */
    public String toString() {
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                string.append(board[i][j]);
                if(j != size-1) string.append(" ");
            }
            string.append('\n');
        }
        return string.toString();
    }
}
