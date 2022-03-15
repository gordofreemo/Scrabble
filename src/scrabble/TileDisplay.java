package scrabble;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TileDisplay extends Canvas {
    private BoardTile tile;

    public TileDisplay(BoardTile tile) {
        super();
        this.tile = tile;
    }

    public void placeMove(Character move) {
        tile.setData(move);
        repaint();
    }

    public void repaint() {
        double height = this.getHeight();
        double width = this.getWidth();
        GraphicsContext gc = this.getGraphicsContext2D();
        Color back;
        gc.clearRect(0,0,width,height);

        if(tile.getWordMultiplier() == 2) back = Color.PINK;
        else if(tile.getWordMultiplier() == 3) back = Color.RED;
        else if(tile.getCharMultiplier() == 2) back = Color.LIGHTBLUE;
        else if(tile.getCharMultiplier() == 3) back = Color.DARKBLUE;
        else back = Color.WHITE;

        gc.setFill(back);
        gc.fillRect(0,0,width,height);
        gc.strokeRect(0,0,width,height);

        if(tile.isEmpty()) return;

        gc.setFont(new Font((height+width)/5));
        gc.strokeText(tile.getData().toString(), width/2-width/10, height/2);
        Integer score = tile.getScore()/tile.getCharMultiplier();
        gc.setFont(new Font((height+width)/10));
        gc.strokeText(score.toString(), width-width/5,height-height/10);
    }

}
