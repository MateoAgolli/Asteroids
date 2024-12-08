package app;

public class Armor extends Ship {
    private static double size = 1;

    private static double[] points = {
            -15.6, -1.3,   // a
            -8.58, -3.38,  // b
            -9.62, -15.6,  // c
            -5.2, -15.6,   // d
            0, -5.2,       // e
            3.38, -3.38,   // f
            2.6, -7.8,     // g
            5.2, -7.8,     // h
            8.84, -2.6,    // i
            18.2, 0,       // j
            8.84, 2.6,     // k
            5.2, 7.8,      // l
            2.6, 7.8,      // m
            3.38, 3.38,    // n
            0, 5.2,        // o
            -5.2, 15.6,    // p
            -9.62, 15.6,   // q
            -8.58, 3.38,   // r
            -15.6, 1.3     // s
    };

    public Armor(int x, int y) {
        super(x, y);

        this.setPoints();

        getCharacter().setScaleX(0.6);
        getCharacter().setScaleY(0.6);
        super.getCharacter().setRotate(270);

        for (int i = 0; i < 15; i++) {
            accelerate();
        }

        double speedFactor = AsteroidsApplication.WIDTH / 600.0;
        setSpeedBasedOnWindowSize(speedFactor);
    }

    @Override
    public void move() {
        getCharacter().setTranslateY(getCharacter().getTranslateY() - getMovement().getY() * getSpeed());
    }

    public static void setSize(double newSize) {
        size = newSize;

        for (int i = 0; i < points.length; i++) {
            points[i] *= size;
        }
    }

    public void resetSize() {
        size = 1;
        double[] originalPoints = {
                -15.6, -1.3, -8.58, -3.38, -9.62, -15.6, -5.2, -15.6, 0, -5.2,
                3.38, -3.38, 2.6, -7.8, 5.2, -7.8, 8.84, -2.6, 18.2, 0,
                8.84, 2.6, 5.2, 7.8, 2.6, 7.8, 3.38, 3.38, 0, 5.2,
                -5.2, 15.6, -9.62, 15.6, -8.58, 3.38, -15.6, 1.3
        };
        System.arraycopy(originalPoints, 0, points, 0, points.length);

        this.setPoints();
    }

    public void setPoints() {
        for (int i = 0; i < getCharacter().getPoints().size(); i++) {
            getCharacter().getPoints().set(i, points[i]);
        }
    }
}