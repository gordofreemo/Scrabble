/**
 * Class representing the game board
 */

public class Board {
    public enum Direction {
        ACROSS,
        DOWN
    }

    BoardTile[][] board;
    private int size;
    private TrieNode trieRoot;

    public Board(int size) {
        board = new BoardTile[size][size];
        this.size = size;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                board[i][j] = new BoardTile(i,j);
            }
        }
    }

    public void placeTile(int row, int col, char data) {
        if(row >= size || col >= size || row < 0 || col < 0) return;
        board[row][col].setData(data);
    }

    public BoardTile getTile(int row, int col) {
        if (row >= size || col >= size || row < 0 || col < 0) return null;
        return board[row][col];
    }

    public int getSize() {
        return size;
    }

    public boolean validWord(int row, int col, Direction direction, TrieNode root) {
        //go to start of word
        if(direction == Direction.ACROSS) while(col > 0 && !getTile(row, col-1).isEmpty()) col--;
        else while(row > 0 && !getTile(row-1, col).isEmpty()) row--;

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

    public int scoreWord(int row, int col, Direction direction) {
        int total = 0;
        int connectedTotal = 0;
        int wordMultiplier = 1;

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

    private int nonRecurScore(int row, int col, Direction direction) {
        int total = 0;
        int wordMultiplier = 1;
        //move to start of word
        if(direction == Direction.ACROSS) {
            while(col > 0 && !getTile(row, col-1).isEmpty()) col--;
        }
        else while(row > 0 && !getTile(row-1,col).isEmpty()) row--;

        //Move across word and count score
        while(row < size && col < size && !getTile(row,col).isEmpty()) {
            total += getTile(row,col).getScore();
            wordMultiplier *= getTile(row,col).getWordMultiplier();
            if(direction == Direction.ACROSS) col++;
            else row++;
        }

        return total * wordMultiplier;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                string.append(board[i][j] + " ");
            }
            string.append('\n');
        }
        return string.toString();
    }
}
