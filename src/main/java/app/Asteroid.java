package app;

import java.util.Random;

public class Asteroid extends Character {
    static Random rnd = new Random();
    private static double size = 20 + rnd.nextInt(10);
    private static double sizeIncrease = 1;
    private double rotationalMovement;

    public Asteroid(int x, int y) {
        super(new PolygonFactory().createPolygon(size), x, y);

        getCharacter().setRotate(rnd.nextInt(360));

        int accelerationAmount = 1 + rnd.nextInt(10);
        for (int i = 0; i < accelerationAmount; i++) {
            accelerate();
        }

        this.rotationalMovement = 0.5 - rnd.nextDouble();
    }

    @Override
    public void move() {
        super.move();
        if (getCharacter().getTranslateX() < 0) {
            getCharacter().setTranslateX(getCharacter().getTranslateX() + AsteroidsApplication.WIDTH);
        }

        if (getCharacter().getTranslateX() > AsteroidsApplication.WIDTH) {
            getCharacter().setTranslateX(getCharacter().getTranslateX() % AsteroidsApplication.WIDTH);
        }

        if (getCharacter().getTranslateY() < 0) {
            getCharacter().setTranslateY(getCharacter().getTranslateY() + AsteroidsApplication.HEIGHT);
        }

        if (getCharacter().getTranslateY() > AsteroidsApplication.HEIGHT) {
            getCharacter().setTranslateY(getCharacter().getTranslateY() % AsteroidsApplication.HEIGHT);
        }
        getCharacter().setRotate(getCharacter().getRotate() + rotationalMovement);
    }

    public static void setSize(double newSize) {
        sizeIncrease = newSize;
        size *= sizeIncrease;
    }
}