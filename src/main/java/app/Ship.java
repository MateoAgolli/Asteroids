package app;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

public class Ship extends Character {

    public Ship(int x, int y) {
        super(new Polygon(-15.6, -1.3, -8.58, -3.38, -9.62, -15.6, -5.2, -15.6, 0, -5.2, 3.38, -3.38, 2.6, -7.8, 5.2, -7.8, 8.84, -2.6, 18.2, 0,
                8.84, 2.6, 5.2, 7.8, 2.6, 7.8, 3.38, 3.38, 0, 5.2, -5.2, 15.6, -9.62, 15.6, -8.58, 3.38, -15.6, 1.3), x, y);

        Image shipTexture = new Image(getClass().getResource("/app/ship_texture.jpg").toExternalForm());
        ImagePattern imagePattern = new ImagePattern(shipTexture);
        getCharacter().setFill(imagePattern);
        getCharacter().setStroke(Color.WHITE);
        getCharacter().setStrokeWidth(0.5);
    }
}