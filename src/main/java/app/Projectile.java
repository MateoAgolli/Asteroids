package app;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

public class Projectile extends Character {

    private static double size = 1;
    private static double a1 = 5;
    private static double a2 = -3;
    private static double b1 = 5;
    private static double b2 = 3;
    private static double c1 = -5;
    private static double c2 = 3;
    private static double d1 = -5;
    private static double d2 = -3;

    public Projectile(int x, int y) {
        super(new Polygon(a1, a2, b1, b2, c1, c2, d1, d2), x, y);

        Image projectile = new Image(getClass().getResource("/app/projectile.png").toExternalForm());
        ImagePattern imagePattern = new ImagePattern(projectile);
        getCharacter().setFill(imagePattern);

        double speedFactor = AsteroidsApplication.WIDTH / 600.0;
        setSpeedBasedOnWindowSize(speedFactor);
    }

    // Static method to set the size of all projectiles
    public static void setSize(double newSize) {
        size = newSize;
        a1 *= size;
        a2 *= size;
        b1 *= size;
        b2 *= size;
        c1 *= size;
        c2 *= size;
        d1 *= size;
        d2 *= size;
    }
}