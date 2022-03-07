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


    public boolean hasHCheck(Character c) {
        if((c-97) > 25 || (c-97) < 0) return false;
        else return hCheck[c-97];
    }

    public boolean hasVCheck(Character c) {
        if((c-97) > 25 || (c-97) < 0) return false;
        else return vCheck[c-97];
    }

    public void updateCrossChecks(TrieNode root) {
        updateHorizontalCheck(root);
        updateVerticalCheck(root);
    }

    public void updateVerticalCheck(TrieNode root) {
        for(int i = 0; i < 26; i++) vCheck[i] = false;

        for(int i = 'a'; i <= 'z'; i++) {
            tile.setData((char)i);
            vCheck[i-'a'] = board.validWord(row, col, Board.Direction.DOWN, root);
            tile.clearTile();
        }
    }

    public void updateHorizontalCheck(TrieNode root) {
        for(int i = 0; i < 26; i++) hCheck[i] = false;

        for(int i = 'a'; i <= 'z'; i++) {
            tile.setData((char)i);
            hCheck[i-'a'] = board.validWord(row, col, Board.Direction.ACROSS, root);
            tile.clearTile();
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public BoardTile getTile() {
        return tile;
    }
    @Override
    public int hashCode() {
        return tile.hashCode();
    }

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

    @Override
    public boolean equals(Object obj) {
        Anchor anchor = (Anchor)obj;
        return anchor.tile == tile;
    }
}
