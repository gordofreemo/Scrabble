import java.util.ArrayList;
import java.util.HashMap;

public class ComputerPlayer {
    private Board board;
    public HashMap<BoardTile, Anchor> anchors;
    private TrieNode root;
    private ArrayList<Character> hand;

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
    }

    public void addToHand(Character c) {
        hand.add(c);
    }

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
            }
        }
    }

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
            }
        }
    }

    private void extendDown(String partial, TrieNode node, BoardTile square) {
        if(square == null) {
            if(node.isTerminalNode()) checkWord(partial, Board.Direction.DOWN);
            return;
        }
        if(square.isEmpty()) {
            if(node.isTerminalNode()) checkWord(partial, Board.Direction.DOWN);
            for(Character c : node.getChildrenData()) {
                boolean condition = !anchors.containsKey(square)
                                    || anchors.get(square).hasHCheck(c);
                if(hand.contains(c) && condition) {
                    hand.remove(c);
                    TrieNode newNode = node.getChild(c);
                    int row = square.getRow() +1;
                    BoardTile nextSquare = board.getTile(row, currCol);
                    String newString = partial + c;
                    extendDown(newString, newNode, nextSquare);
                    hand.add(c);
                }
            }
        }
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

    public void makeMove() {
        getAnchors();
        for(Anchor anchor : anchors.values()) {
            currCol = anchor.getCol();
            currRow = anchor.getRow();

            BoardTile prev = board.getTile(currRow, currCol-1);
            if(prev == null) {
                extendRight("", root, anchor.getTile());
            }
            else if(prev.isEmpty()) {
                int lim = 0;
                while (prev != null && !anchors.containsKey(prev)) {
                    lim += 1;
                    prev = board.getTile(currRow, currCol - lim);
                }
                leftPart("", root, lim-1, anchor.getTile());
            }
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
            prev = board.getTile(currRow-1, currCol);
            if(prev == null) {
                extendDown("", root, anchor.getTile());
            }
            else if(prev.isEmpty()) {
                int lim = 0;
                while(prev != null && !anchors.containsKey(prev)) {
                    lim += 1;
                    prev = board.getTile(currRow-lim, currCol);
                }
                topPart("", root, lim-1, anchor.getTile());
            }
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
        System.out.println("Best word " + bestWord + " at " + bestDirection + "(" + bestRow + "," + bestCol + ")" + " with score " + bestScore);
    }

    private void checkWord(String partial, Board.Direction direction) {
        int row = currRow;
        int col = currCol;
        boolean valid = false;
        for(int i = 0; i < partial.length(); i++) {
            board.placeTile(row, col, partial.charAt(i));
            if(anchors.containsKey(board.getTile(row,col))) valid = true;
            if(direction == Board.Direction.ACROSS) col++;
            else row++;
        }
        if(!board.validWord(currRow,currCol,direction,root)) return;
        int score = board.scoreWord(currRow, currCol, direction);
        row = currRow;
        col = currCol;
        for(int i = 0; i < partial.length(); i++) {
            board.getTile(row,col).clearTile();
            if(direction == Board.Direction.ACROSS) col++;
            else row++;
        }
        if(hand.isEmpty()) score += 50;
        if(score > bestScore && valid) {
            bestRow = currRow;
            bestCol = currCol;
            bestWord = partial;
            bestDirection = direction;
            bestScore = score;
        }


    }
}
