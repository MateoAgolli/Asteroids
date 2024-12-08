package app;

import java.util.Random;

import javafx.scene.shape.Polygon;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class PolygonFactory {

    public Polygon createPolygon(double size) {
        Random rnd = new Random();
        Polygon polygon = new Polygon();

        // Calculate angles for a decagon
        int sides = 10;
        double angleIncrement = 2 * Math.PI / sides;

        for (int i = 0; i < sides; i++) {
            double angle = i * angleIncrement;
            double x = size * Math.cos(angle);
            double y = size * Math.sin(angle);
            polygon.getPoints().addAll(x, y);
        }

        // Slightly randomize the points
        for (int i = 0; i < polygon.getPoints().size(); i++) {
            int change = rnd.nextInt(5) - 2; // Random change between -2 and 2
            polygon.getPoints().set(i, polygon.getPoints().get(i) + change);
        }

        // Set the fill pattern
        Image asteroidTexture = new Image(getClass().getResource("/app/asteroid_texture.jpeg").toExternalForm());
        ImagePattern imagePattern = new ImagePattern(asteroidTexture);
        polygon.setFill(imagePattern);

        return polygon;
    }
}