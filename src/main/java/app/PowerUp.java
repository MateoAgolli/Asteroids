package app;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

public class PowerUp extends Character {
    private static double size = 1;

    private static double[] points = {
            -9.8, 0,         // a
            1.82, 4.9,       // b
            1.82, 0.56,      // c
            10.5, 5.6,       // d
            -1.54, -7,       // e
            -1.54, -1.54,    // f
            -9.8, -6.3,      // g
    };

    public PowerUp(int x, int y) {
        super(new Polygon(-9.8, 0, 1.82, 4.9, 1.82, 0.56, 10.5, 5.6, -1.54, -7, -1.54, -1.54, -9.8, -6.3), x, y);
        this.setPoints();
        // Apply the image pattern to the PowerUp
        Image powerUp = new Image(getClass().getResource("/app/power_up.jpg").toExternalForm());
        ImagePattern imagePattern = new ImagePattern(powerUp);
        getCharacter().setFill(imagePattern);

        super.getCharacter().setRotate(90);

        for (int i = 0; i < 15; i++) {
            accelerate();
        }

        double speedFactor = AsteroidsApplication.WIDTH / 600.0;
        setSpeedBasedOnWindowSize(speedFactor);
    }

    @Override
    public void move() {
        getCharacter().setTranslateY(getCharacter().getTranslateY() + getMovement().getY() * getSpeed());
    }

    public static void setSize(double newSize) {
        size = newSize;

        for (int i = 0; i < points.length; i++) {
            points[i] *= size;
        }
    }

    public void resetSize() {
        size = 1;
        double[] originalPoints = {-9.8, 0, 1.82, 4.9, 1.82, 0.56, 10.5, 5.6, -1.54, -7, -1.54, -1.54, -9.8, -6.3};
        System.arraycopy(originalPoints, 0, points, 0, points.length);

        this.setPoints();
    }

    public void setPoints() {
        for (int i = 0; i < getCharacter().getPoints().size(); i++) {
            getCharacter().getPoints().set(i, points[i]);
        }
    }
}