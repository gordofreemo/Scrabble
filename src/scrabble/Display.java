package scrabble;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Display extends Application {
    private TileDisplay[][] tiles;
    private ArrayList<TileDisplay> hand;
    private GridPane mainDisplay;
    private GridPane boardDisplay;
    private GridPane handDisplay;
    private TrieNode root;
    private Board board;
    private HumanPlayer human;
    private TileDisplay selected;
    private ComputerPlayer ai;
    private TilePile pile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        initObjects();
        int size = board.getSize();
        Scene scene = new Scene(mainDisplay, 1000, 1000);

        Button reset = new Button();
        reset.setOnMouseClicked(e -> {human.resetMove(); updateHand();});
        reset.setText("RESET");
        reset.setPrefSize(100,50);
        Button submit = new Button();
        submit.setPrefSize(100,50);
        submit.setText("SUBMIT");
        submit.setOnMouseClicked(e -> handleMove());

        boardDisplay.setPrefSize(900,900);
        handDisplay.setPrefSize(900,100);
        mainDisplay.add(boardDisplay,0,0,2,1);
        mainDisplay.add(handDisplay,0,1);
        mainDisplay.add(submit,1,1);
        mainDisplay.add(reset,1,2);

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                tiles[i][j] = new TileDisplay(board.getTile(i,j));
                tiles[i][j].heightProperty().bind(boardDisplay.heightProperty().divide(16));
                tiles[i][j].widthProperty().bind(boardDisplay.widthProperty().divide(16));
                boardDisplay.add(tiles[i][j],j,i);
                tiles[i][j].repaint();
                int finalI = i;
                int finalJ = j;
                tiles[i][j].setOnMouseClicked(e -> {handlePlace(finalI, finalJ); repaint();});
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(true);
        primaryStage.setTitle("Scrabble");
        updateHand();
    }

    private void handleMove() {
        HumanPlayer.MoveStatus status = human.validateMove(root);
        switch(status) {
            case MOVE_SUCCESS -> {
                human.placeMove();
                makeMove();
                updateHand();
            }
        }
        repaint();
    }

    private void handlePlace(int row, int col) {
        if(selected == null) return;
        BoardTile tile = board.getTile(row,col);
        human.placeTile(tile, selected.getTile().getData());
        selected.getTile().clearTile();
        selected = null;
    }


    private void repaint() {
        for(int i = 0; i < board.getSize(); i++) {
            for(int j = 0; j < board.getSize(); j++) {
                tiles[i][j].repaint();
            }
        }
        for(TileDisplay tile : hand) {
            tile.repaint();
        }
    }

    private void updateHand() {
        System.out.println(human.getHandSize());
        while(human.getHandSize() < 7) {
            Character newHand = pile.draw();
            human.addToHand(newHand);
        }
        int count = 0;
        for(Character c : human.getHand()) {
            hand.get(count).getTile().setData(c);
            count++;
        }
        repaint();
    }

    private void initObjects() {
        BoardSolver scrabbleGetter = new BoardSolver("scrabble_board.txt");
        TrieFileParser parser = new TrieFileParser(ClassLoader.getSystemResourceAsStream("twl06.txt"));
        root = parser.makeTree();
        board = scrabbleGetter.makeBoard();
        pile = new TilePile();
        pile.scrabblePile();
        tiles = new TileDisplay[board.getSize()][board.getSize()];
        boardDisplay = new GridPane();
        ai = new ComputerPlayer(board, root);
        human = new HumanPlayer(board);
        hand = new ArrayList<>();
        mainDisplay = new GridPane();
        handDisplay = new GridPane();
        for(int i = 0; i < 7; i++) {
            TileDisplay display = new TileDisplay(new BoardTile(-1,-1));
            display.heightProperty().bind(handDisplay.heightProperty());
            display.widthProperty().bind(handDisplay.widthProperty().divide(10));
            display.setOnMouseClicked(e -> selected = display);
            handDisplay.add(display, i, 0);
            hand.add(display);
        }
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
