import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Display extends Application {
    private TileDisplay[][] tiles;
    private GridPane boardDisplay;
    private Board board;
    private int size;
    private HumanPlayer human;
    private ComputerPlayer ai;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        size = 15;
        board = new Board(size);
        tiles = new TileDisplay[size][size];
        boardDisplay = new GridPane();
        Scene scene = new Scene(boardDisplay, 1000, 800);

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                tiles[i][j] = new TileDisplay(board.getTile(i,j));
                tiles[i][j].heightProperty().bind(boardDisplay.heightProperty().divide(15));
                tiles[i][j].widthProperty().bind(boardDisplay.widthProperty().divide(15));
                boardDisplay.add(tiles[i][j],j,i);
                tiles[i][j].repaint();
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(true);
        primaryStage.setTitle("Scrabble");
    }
}
