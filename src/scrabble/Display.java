package scrabble;

/**
 * Andrew Geyko
 * This class is responsible for the graphical interface of the scrabble game,
 * tying together all the components into one place.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
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
    private Label playerScoreLabel;
    private Label aiScoreLabel;
    private TrieNode root;
    private Board board;
    private HumanPlayer human;
    private TileDisplay selected;
    private ComputerPlayer ai;
    private int playerScore;
    private int aiScore;
    private TilePile pile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        initObjects();
        int size = board.getSize();
        Scene scene = new Scene(mainDisplay, 1000, 1000);

        //Make buttons
        Button reset = new Button();
        reset.setOnMouseClicked(e -> {human.resetMove(); updateHand();});
        reset.setText("RESET");
        reset.setPrefSize(70,30);
        Button submit = new Button();
        submit.setPrefSize(70,30);
        submit.setText("SUBMIT");
        submit.setOnMouseClicked(e -> handleMove());
        Button skip = new Button();
        submit.setPrefSize(70,30);
        skip.setText("SKIP MOVE");
        skip.setOnMouseClicked(e -> skipMove());

        //Make labels
        aiScoreLabel.setText("AI SCORE: " + aiScore);
        playerScoreLabel.setText("PLAYER SCORE: " + playerScore);
        aiScoreLabel.setPadding(new Insets(0,0,0,30));
        playerScoreLabel.setPadding(new Insets(0,0,0,30));

        //Add stuff to the GridPane
        boardDisplay.setPrefSize(900,900);
        handDisplay.setPrefSize(600,100);
        mainDisplay.add(boardDisplay,0,0,3,1);
        mainDisplay.add(handDisplay,0,1, 1, 3);
        mainDisplay.add(submit,3,1);
        mainDisplay.add(reset,3,2);
        mainDisplay.add(skip, 3,3);
        mainDisplay.add(aiScoreLabel, 2, 1);
        mainDisplay.add(playerScoreLabel, 2, 2);

        //Make board tiles
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                tiles[i][j] = new TileDisplay(board.getTile(i,j));
                tiles[i][j].heightProperty().bind(boardDisplay.heightProperty().divide(16));
                tiles[i][j].widthProperty().bind(boardDisplay.widthProperty().divide(16));
                boardDisplay.add(tiles[i][j],j,i);
                tiles[i][j].repaint();
                int finalI = i;
                int finalJ = j;
                tiles[i][j].setOnMouseClicked(e -> {
                    handlePlace(finalI, finalJ);
                    repaint();
                });
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(true);
        primaryStage.setTitle("Scrabble");
        updateHand();
    }

    /**
     * Handles the logic for when the human player submits his move.
     * If the move is valid, AI player makes its move, if not then
     * an alert is popped up.
     */
    private void handleMove() {
        HumanPlayer.MoveStatus status = human.validateMove(root);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Incorrect Move");
        switch(status) {
            case MOVE_SUCCESS -> {
                human.placeMove();
                MoveInfo moveInfo = human.getMoveInfo();
                playerScore += moveInfo.getScore();
                makeMove();
                updateHand();
            }
            case DIR_MISMATCH -> {
                alert.setContentText("Move must be either horizontal or vertical");
                alert.showAndWait();
            }
            case NOT_WORD -> {
                alert.setContentText("Word not in dictionary!");
                alert.showAndWait();
            }
            case NOT_ATTACHED -> {
                alert.setContentText("Word must be connected to word on board" +
                        "(or center square)");
                alert.showAndWait();
            }
        }
        repaint();
    }

    /**
     * Handles the logic for placing a character from the hand onto the board
     * @param row - row on the board where character is being placed
     * @param col - column on the board where character is being placed
     */
    private void handlePlace(int row, int col) {
        if(selected == null) return;
        Character data = selected.getTile().getData();
        BoardTile tile = board.getTile(row,col);
        if(!tile.isEmpty() || data == '\0') return;
        if(data == '*') {
            TextInputDialog popup = new TextInputDialog();
            popup.setTitle("Input");
            popup.setContentText("Choose a letter for the blank");
            popup.showAndWait();
            String input = popup.getResult();
            if(input.length() != 1 || !Character.isAlphabetic(input.charAt(0))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Mismatch");
                alert.setContentText("Blank must be a single character");
                alert.showAndWait();
                return;
            }
            data = Character.toUpperCase(input.charAt(0));
        }

        human.placeTile(tile, data);
        selected.getTile().clearTile();
        selected = null;
    }

    /**
     * Updates graphical display
     */
    private void repaint() {
        for(int i = 0; i < board.getSize(); i++) {
            for(int j = 0; j < board.getSize(); j++) {
                tiles[i][j].repaint();
            }
        }
        for(TileDisplay tile : hand) {
            if(tile == selected) tile.setSelected(true);
            else tile.setSelected(false);
            tile.repaint();
        }
        playerScoreLabel.setText("PLAYER SCORE: " + playerScore);
        aiScoreLabel.setText("AI SCORE: " + aiScore);
    }

    /**
     * Handles logic for when the human player wants to "skip" their move,
     * ending their turn and drawing new tiles from the pile.
     */
    private void skipMove() {
        human.resetMove();
        for(Character c : human.getHand()) {
            pile.add(c);
        }
        pile.shuffle();
        human.getHand().clear();
        makeMove();
        updateHand();
    }

    /**
     * Updates the player's hand to have 7 tiles and repaints screen
     */
    private void updateHand() {
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

    /**
     * Initialize all the objects that are needed to run the program
     */
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
            display.widthProperty().bind(handDisplay.widthProperty().divide(7));
            display.setOnMouseClicked(e -> {
                selected = display;
                repaint();
            });
            handDisplay.add(display, i, 0);
            hand.add(display);
        }
        playerScoreLabel = new Label();
        aiScoreLabel = new Label();
        playerScore = 0;
        aiScore = 0;
    }

    /**
     * Logic for when the AI needs to make a move
     */
    private void makeMove() {
        while(ai.getHandSize() < 7) ai.addToHand(pile.draw());
        ai.makeMove();
        MoveInfo moveInfo = ai.getMoveInfo();
        aiScore += moveInfo.getScore();
        repaint();
    }
}
