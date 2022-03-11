import java.util.ArrayList;

public class HumanPlayer {

    public enum MoveStatus {
        NOT_ATTACHED,
        NOT_WORD,
        DIR_MISMATCH,
        MOVE_SUCCESS
    }

    private ArrayList<Character> hand;
    private ArrayList<BoardTile> placed;
    private Board board;
    private int totalScore;
    private MoveInfo moveInfo;
    private TrieNode root;

    public HumanPlayer(Board board) {
        hand = new ArrayList<>();
        placed = new ArrayList<>();
        this.board = board;
        totalScore = 0;
    }

    public void placeTile(BoardTile tile, Character move) {
        if(!tile.isEmpty()) return;
        tile.setData(move);
        hand.remove(move);
        placed.add(tile);
    }

    public void resetMove() {
        for(BoardTile tile : placed) {
            Character toAdd = tile.getData();
            if(Character.isUpperCase(toAdd)) hand.add('*');
            else hand.add(toAdd);
            tile.clearTile();
        }
        placed.clear();
    }

    public MoveStatus validateMove() {
        Board.Direction direction;

        //Get left and topmost placed tile
        int row = placed.stream()
                .map(x -> x.getRow())
                .reduce(Integer.MAX_VALUE,(acc,n)->Integer.min(acc,n));
        int col = placed.stream()
                .map(x -> x.getColumn())
                .reduce(Integer.MAX_VALUE,(acc,n)->Integer.min(acc,n));

        //Are all rows/columns consistent?
        boolean rowMatch = placed.stream()
                        .allMatch(x -> x.getRow() == row);
        boolean colMatch = placed.stream()
                        .allMatch(x -> x.getColumn() == col);

        if(rowMatch) direction = Board.Direction.DOWN;
        else if(colMatch) direction = Board.Direction.ACROSS;
        else return MoveStatus.DIR_MISMATCH;

        if(!board.validWord(row, col, direction, root)) return MoveStatus.NOT_WORD;

        return MoveStatus.MOVE_SUCCESS;
    }

}
