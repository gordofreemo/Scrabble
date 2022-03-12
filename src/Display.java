import javafx.application.Application;
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
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                tiles[i][j] = new TileDisplay(board.getTile(i,j));
                boardDisplay.add(tiles[i][j],j,i);
            }
        }


    }
}
