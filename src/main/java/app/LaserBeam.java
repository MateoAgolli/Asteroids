package app;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class LaserBeam extends Projectile {

    public LaserBeam(int x, int y) {
        super(x, y);
        super.getCharacter().setScaleX(5);
        super.getCharacter().setScaleY(10);

        Image laserBeam = new Image(getClass().getResource("/app/laser_beam1.png").toExternalForm());
        ImagePattern imagePattern = new ImagePattern(laserBeam);
        getCharacter().setFill(imagePattern);
    }

    public void setCharacterFill(String imageName) {
        Image laserBeam = new Image(getClass().getResource("/app/" + imageName).toExternalForm());
        ImagePattern imagePattern = new ImagePattern(laserBeam);
        getCharacter().setFill(imagePattern);
    }
}