package app;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public abstract class Character {

    private Polygon character;
    private Point2D movement;
    private boolean alive;
    private double speed;

    public Character(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);

        this.movement = new Point2D(0, 0);

        this.alive = true;
        this.speed = 1.3;
    }

    public Polygon getCharacter() {
        return character;
    }

    public Point2D getMovement() {
        return movement;
    }

    public void setMovement(Point2D newMovement) {
        this.movement = newMovement;
    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 5);
    }

    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 5);
    }

    public void moveShip() {
        // Calculate the new position based on current movement vector
        double newX = this.character.getTranslateX() + this.movement.getX() * this.speed;
        double newY = this.character.getTranslateY() + this.movement.getY() * this.speed;

        // Get the bounds of the ship in the parent container (Pane)
        double shipWidth = this.character.getBoundsInParent().getWidth();
        double shipHeight = this.character.getBoundsInParent().getHeight();

        // Check horizontal boundaries (X-axis)
        if (newX < 0) {
            this.character.setTranslateX(0); // Keep inside the left boundary
            this.movement = new Point2D(this.movement.getX() * -0.5, this.movement.getY()); // Slightly reverse movement
        } else if (newX + shipWidth > AsteroidsApplication.WIDTH) {
            this.character.setTranslateX(AsteroidsApplication.WIDTH - shipWidth); // Keep inside the right boundary
            this.movement = new Point2D(this.movement.getX() * -0.5, this.movement.getY()); // Slightly reverse movement
        } else {
            this.character.setTranslateX(newX); // Move ship if not hitting boundary
        }

        // Check vertical boundaries (Y-axis)
        if (newY < 0) {
            this.character.setTranslateY(0); // Keep inside the top boundary
            this.movement = new Point2D(this.movement.getX(), this.movement.getY() * -0.5); // Slightly reverse movement
        } else if (newY + shipHeight > AsteroidsApplication.HEIGHT) {
            this.character.setTranslateY(AsteroidsApplication.HEIGHT - shipHeight); // Keep inside the bottom boundary
            this.movement = new Point2D(this.movement.getX(), this.movement.getY() * -0.5); // Slightly reverse movement
        } else {
            this.character.setTranslateY(newY); // Move ship if not hitting boundary
        }

        // Apply damping to slow down the movement near boundaries
        this.movement = this.movement.multiply(0.99); // Apply friction to slow down over time
    }

    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX() * this.speed);
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY() * this.speed);
    }

    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

        changeX *= 0.05;
        changeY *= 0.05;

        this.movement = this.movement.add(changeX, changeY);
    }

    public boolean collide(Character other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean isAlive) {
        this.alive = isAlive;
    }

    public void moveFaster(double howMuch) {
        this.speed = howMuch;
    }

    public void resize(double scaleFactor) {
        for (int i = 0; i < this.character.getPoints().size(); i++) {
            this.character.getPoints().set(i, this.character.getPoints().get(i) * scaleFactor);
        }

        double newX = this.character.getTranslateX() * scaleFactor;
        double newY = this.character.getTranslateY() * scaleFactor;

        this.character.setTranslateX(newX);
        this.character.setTranslateY(newY);
    }

    public void setSpeedBasedOnWindowSize(double resizePercentage) {
        this.speed *= resizePercentage;
    }

    public double getSpeed() {
        return this.speed;
    }
}