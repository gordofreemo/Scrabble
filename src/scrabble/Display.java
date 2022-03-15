package scrabble;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class Display extends Application {
    private TileDisplay[][] tiles;
    private GridPane boardDisplay;
    private Board board;
    private HumanPlayer human;
    private ComputerPlayer ai;
    private TilePile pile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        BoardSolver scrabbleGetter = new BoardSolver("scrabble_board.txt");
        TrieFileParser parser = new TrieFileParser(ClassLoader.getSystemResourceAsStream("twl06.txt"));
        TrieNode root = parser.makeTree();
        board = scrabbleGetter.makeBoard();
        pile = new TilePile();
        pile.scrabblePile();

        int size = board.getSize();
        tiles = new TileDisplay[size][size];
        boardDisplay = new GridPane();
        Scene scene = new Scene(boardDisplay, 1000, 800);
        ai = new ComputerPlayer(board, root);


        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                tiles[i][j] = new TileDisplay(board.getTile(i,j));
                tiles[i][j].heightProperty().bind(boardDisplay.heightProperty().divide(16));
                tiles[i][j].widthProperty().bind(boardDisplay.widthProperty().divide(16));
                boardDisplay.add(tiles[i][j],j,i);
                tiles[i][j].repaint();
            }
        }

        boardDisplay.setOnMouseClicked(e -> makeMove());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(true);
        primaryStage.setTitle("Scrabble");
    }

    private void makeMove() {
        while(ai.getHandSize() < 7) ai.addToHand(pile.draw());
        ai.makeMove();
        System.out.println(ai.getMoveInfo().getScore());
        for(TileDisplay[] array : tiles) {
            for(TileDisplay tile : array) tile.repaint();
        }
    }
}
