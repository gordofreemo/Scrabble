package scrabble;

/**
 * Andrew Geyko
 * This class is responsible for painting a GUI representation of a
 * scrabble tile. You can place either a character or a tile onto it
 * and it will draw the character and the score.
 * If the tile is empty, also does the drawing for character/word
 * multipliers.
 */

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TileDisplay extends Canvas {
    private BoardTile tile;
    private boolean selected;

    public TileDisplay(BoardTile tile) {
        super();
        this.tile = tile;
        selected = false;
    }

    public TileDisplay(Character character) {
        super();
        tile = new BoardTile(-1,-1);
        placeMove(character);
        selected = false;
    }

    /**
     * @return - logical tile that the graphical tile is holding
     */
    public BoardTile getTile() {
        return tile;
    }

    /**
     * Sets whether the tile is "selected", which just gives the tile
     * an orange outline
     * @param bool - true if orange outline wanted, false if not
     */
    public void setSelected(Boolean bool) {
        selected = bool;
    }

    /**
     * Put the given character onto the logical and graphical tile
     * @param move - character to place
     */
    public void placeMove(Character move) {
        tile.setData(move);
        repaint();
    }

    /**
     * Redraws the tile
     */
    public void repaint() {
        double height = this.getHeight();
        double width = this.getWidth();
        GraphicsContext gc = this.getGraphicsContext2D();
        Color back;
        gc.clearRect(0,0,2*width,2*height);

        //Get background color
        if(tile.getWordMultiplier() == 2) back = Color.PINK;
        else if(tile.getWordMultiplier() == 3) back = Color.RED;
        else if(tile.getCharMultiplier() == 2) back = Color.LIGHTBLUE;
        else if(tile.getCharMultiplier() == 3) back = Color.DEEPSKYBLUE;
        else if(tile.isEmpty()) back = Color.WHITE;
        else back = Color.BEIGE;

        gc.setFill(back);
        gc.fillRect(0,0,width,height);
        //Whether to outline or not
        if(selected) {
            gc.setLineWidth(10);
            gc.setStroke(Color.ORANGE);
            gc.strokeRect(0,0,width,height);
        }
        else {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, width, height);
        }

        //Draw multipliers
        gc.setFont(new Font("Arial Regular", (height+width)/10));
        gc.setFill(Color.BLACK);
        boolean empty = tile.isEmpty();
        if(tile.getWordMultiplier() > 1) {
            if(empty) gc.fillText("WORD",width/2-width/5,height/2);
            gc.setFont(new Font("Arial Regular", (height+width)/10));
            gc.fillText(tile.getWordMultiplier()+"x",width/2-width/10,height-height/10);
        }
        if(tile.getCharMultiplier() > 1) {
            if(empty) gc.fillText("CHAR",width/2-width/5,height/2);
            gc.setFont(new Font("Arial Regular", (height+width)/10));
            gc.fillText(tile.getCharMultiplier()+"x",width/2-width/10,height-height/10);
        }

        if(tile.isEmpty()) return;
        gc.setFont(new Font("Arial Bold",(height+width)/5));
        gc.fillText(tile.getData().toString(), width/2-width/10, height/2);
        Integer score = tile.getScore()/tile.getCharMultiplier();
        gc.setFont(new Font("Arial Regular", (height+width)/10));
        gc.fillText(score.toString(), width-width/5,height-height/10);
    }

}
