package app;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AsteroidsApplication extends Application {

    public static int WIDTH = 600;
    public static int HEIGHT = 400;

    private AnimationTimer timer;
    private Ship ship;
    private Armor movingArmor;
    private PowerUp powerUp;
    private double asteroidChance;
    private double speedMultiplier;
    private static double resizePercentage;
    private boolean isManualResize = true;  // This will be true when resizing is manual
    private int newArmorCount = 0;
    private int powerUpCount = 0;

    public static void main(String[] args) {
        launch(AsteroidsApplication.class);
    }

    @Override
    public void start(Stage window) {
        window.setTitle("Asteroids!");

        asteroidChance = 0.02;
        speedMultiplier = 1.3;
        resizePercentage = 1.0;

        GridPane layout = new GridPane();
        layout.setAlignment(Pos.CENTER);

        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        BackgroundImage bgImage = createBackground();
        pane.setBackground(new Background(bgImage));

        Text text = new Text(10, 25, "Points: 0");
        text.setFont(Font.font("Arial", 20));
        text.setFill(Color.WHITE);

        Text armorText = new Text(150, 25, "Armor: ");
        armorText.setFont(Font.font("Arial", 20));
        armorText.setFill(Color.WHITE);

        AtomicInteger points = new AtomicInteger();

        ship = new Ship(WIDTH / 2, HEIGHT / 2);
        List<Armor> armors = new ArrayList<>();
        createNewArmor(armors, 3, 0);

        movingArmor = null;
        powerUp = null;

        List<Asteroid> asteroids = new ArrayList<>();
        createFirstFiveAsteroids(asteroids);

        List<Projectile> projectiles = new ArrayList<>();

        List<LaserBeam> laserBeams = new ArrayList<>();

        pane.getChildren().add(ship.getCharacter());
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

        armors.forEach(armor -> pane.getChildren().add(armor.getCharacter()));
        pane.getChildren().add(text);
        pane.getChildren().add(armorText);

        layout.add(pane, 0, 0);

        Scene scene = new Scene(layout, Color.BLACK);

        // Listen for window size changes
        window.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (isManualResize) {
                WIDTH = newWidth.intValue();
                pane.setPrefWidth(WIDTH);
                if (!Double.isNaN((double) newWidth) && !Double.isNaN((double) oldWidth)) {
                    resizePercentage = newWidth.doubleValue() / oldWidth.doubleValue();
                    ship.resize(resizePercentage);
                    Projectile.setSize(resizePercentage);
                    for (Asteroid asteroid : asteroids) {
                        asteroid.resize(resizePercentage);
                    }
                    Asteroid.setSize(resizePercentage);
                    ship.setSpeedBasedOnWindowSize(resizePercentage);
                    if (movingArmor != null) {
                        movingArmor.resize(resizePercentage);
                        movingArmor.setSpeedBasedOnWindowSize(resizePercentage);
                    }
                    Armor.setSize(resizePercentage);

                    if (powerUp != null) {
                        powerUp.resize(resizePercentage);
                        powerUp.setSpeedBasedOnWindowSize(resizePercentage);
                    }
                    PowerUp.setSize(resizePercentage);
                }
            }
        });

        window.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            if (isManualResize) {
                HEIGHT = newHeight.intValue();
                pane.setPrefHeight(HEIGHT);
            }
        });

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
        boolean[] canFire = {true};
        boolean[] canFireHugeLaserBeam = {false};
        boolean[] justFiredHugeLaserBeam = {false};

        enableKeyListeners(scene, pressedKeys, canFire, canFireHugeLaserBeam, justFiredHugeLaserBeam, projectiles, laserBeams, pane, points);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pressedKeys.getOrDefault(KeyCode.LEFT, false) || pressedKeys.getOrDefault(KeyCode.A, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false) || pressedKeys.getOrDefault(KeyCode.D, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false) || pressedKeys.getOrDefault(KeyCode.W, false)) {
                    ship.accelerate();
                }

                if (points.get() == 500) {
                    speedMultiplier = 1.8;
                    asteroidChance = 0.025;
                } else if (points.get() == 1000) {
                    speedMultiplier = 2.5;
                    asteroidChance = 0.05;
                } else if (points.get() == 1500) {
                    speedMultiplier = 3.0;
                    asteroidChance = 0.07;
                } else if (points.get() == 2000) {
                    speedMultiplier = 4.0;
                    asteroidChance = 0.09;
                } else if (points.get() == 2500) {
                    speedMultiplier = 5.0;
                    asteroidChance = 0.11;
                } else if (points.get() == 3000) {
                    speedMultiplier = 5.5;
                    asteroidChance = 0.13;
                } else if (points.get() == 3500) {
                    speedMultiplier = 6.0;
                    asteroidChance = 0.15;
                } else if (points.get() == 4000) {
                    speedMultiplier = 6.5;
                    asteroidChance = 0.17;
                } else if (points.get() == 4500) {
                    speedMultiplier = 7.0;
                    asteroidChance = 0.19;
                } else if (points.get() == 5000) {
                    speedMultiplier = 7.5;
                    asteroidChance = 0.21;
                }

                if (armors.size() < 3) {
                    if ((Math.random() <= 0.000385 && newArmorCount == 0 && points.get() < 1000) || (newArmorCount == 0 && points.get() == 910)) { // 50% probability of happening at least once in 30 seconds
                        newArmorCount++;
                        addMovingArmorToTheLayout(pane);
                    } else if ((Math.random() <= 0.000385 && newArmorCount == 1 && points.get() >= 1000 && points.get() < 2000) || (newArmorCount == 1 && points.get() == 1930)) {
                        newArmorCount++;
                        addMovingArmorToTheLayout(pane);
                    } else if ((Math.random() <= 0.000385 && newArmorCount == 2 && points.get() >= 2000 && points.get() < 3000) || (newArmorCount == 2 && points.get() == 2950)) {
                        newArmorCount++;
                        addMovingArmorToTheLayout(pane);
                    } else if ((Math.random() <= 0.000385 && newArmorCount == 3 && points.get() >= 3000 && points.get() < 4000) || (newArmorCount == 3 && points.get() == 3880)) {
                        newArmorCount++;
                        addMovingArmorToTheLayout(pane);
                    } else if ((Math.random() <= 0.000385 && newArmorCount == 4 && points.get() >= 4000 && points.get() < 5000) || (newArmorCount == 4 && points.get() == 4930)) {
                        newArmorCount++;
                        addMovingArmorToTheLayout(pane);
                    } else if (Math.random() <= 0.0002 && points.get() >= 5000) { // 51.33% probability of happening at least once in 1 minute
                        newArmorCount++;
                        addMovingArmorToTheLayout(pane);
                    }
                }

                // if no armor spawned because the player had 3 armors already, we add one to the newArmorCount anyway so we can move to the next range
                if (newArmorCount == 0 && points.get() == 1000) {
                    newArmorCount++;
                } else if (newArmorCount == 1 && points.get() == 2000) {
                    newArmorCount++;
                } else if (newArmorCount == 2 && points.get() == 3000) {
                    newArmorCount++;
                } else if (newArmorCount == 3 && points.get() == 4000) {
                    newArmorCount++;
                }

                if (movingArmor != null) {
                    movingArmor.move();

                    if (ship.collide(movingArmor) && movingArmor.isAlive()) { // We check if movingArmor isAlive so the instruction inside this if condition get executed only once for every collision
                        playSound("armor_pickup.mp3");
                        pane.getChildren().remove(movingArmor.getCharacter());
                        createNewArmor(armors, 1, armors.size());
                        armors.get(armors.size() - 1).resetSize();
                        pane.getChildren().add(armors.get(armors.size() - 1).getCharacter());
                        Armor.setSize(resizePercentage); // so the new movingArmors are not the size of the armors that shows how many armors you have
                        movingArmor.setAlive(false);
                    }
                }

                if ((Math.random() <= 0.000385 && powerUpCount == 0 && points.get() < 1000) || (powerUpCount == 0 && points.get() == 960)) { // 50% probability of happening at least once in 30 seconds
                    powerUpCount++;
                    addPowerUpToTheLayout(pane);
                } else if ((Math.random() <= 0.000385 && powerUpCount == 1 && points.get() >= 1000 && points.get() < 2000) || (powerUpCount == 1 && points.get() == 1970)) {
                    powerUpCount++;
                    addPowerUpToTheLayout(pane);
                } else if ((Math.random() <= 0.000385 && powerUpCount == 2 && points.get() >= 2000 && points.get() < 3000) || (powerUpCount == 2 && points.get() == 2900)) {
                    powerUpCount++;
                    addPowerUpToTheLayout(pane);
                } else if ((Math.random() <= 0.000385 && powerUpCount == 3 && points.get() >= 3000 && points.get() < 4000) || (powerUpCount == 3 && points.get() == 3780)) {
                    powerUpCount++;
                    addPowerUpToTheLayout(pane);
                } else if ((Math.random() <= 0.000385 && powerUpCount == 4 && points.get() >= 4000 && points.get() < 5000) || (powerUpCount == 4 && points.get() == 4800)) {
                    powerUpCount++;
                    addPowerUpToTheLayout(pane);
                } else if (Math.random() <= 0.0002 && points.get() >= 5000) { // 51.33% probability of happening at least once in 1 minute
                    powerUpCount++;
                    addPowerUpToTheLayout(pane);
                }

                if (powerUp != null) {
                    powerUp.move();

                    if (ship.collide(powerUp) && powerUp.isAlive()) {
                        playSound("power_up.mp3");
                        pane.getChildren().remove(powerUp.getCharacter());
                        canFireHugeLaserBeam[0] = true;
                        powerUp.resetSize();
                        PowerUp.setSize(resizePercentage);
                        powerUp.setAlive(false);
                    }
                }

                // Move the ship, asteroids, and projectiles
                ship.moveShip();
                asteroids.forEach(asteroid -> {
                    asteroid.moveFaster(speedMultiplier);
                    asteroid.move();

                });
                projectiles.forEach(projectile -> projectile.move());
                laserBeams.forEach(laserBeam -> laserBeam.move());


                // Handle collisions
                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collide(asteroid)) {
                            playSound("small_explosion.wav");
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                        }
                    });

                    if (!projectile.isAlive()) {
                        text.setText("Points: " + points.addAndGet(10));
                    }
                });

                laserBeams.forEach(laserBeam -> asteroids.forEach(asteroid -> {
                    if (laserBeam.collide(asteroid)) {
                        playSound("small_explosion.wav");
                        laserBeam.setAlive(false);
                        asteroid.setAlive(false);
                        text.setText("Points: " + points.addAndGet(10));
                    }
                }));

                // Remove inactive projectiles and asteroids
                projectiles.removeIf(projectile -> {
                    if (!projectile.isAlive()) {
                        pane.getChildren().remove(projectile.getCharacter());
                        return true; // Remove the projectile from the list
                    }
                    // Remove projectile if it goes off-screen
                    double x = projectile.getCharacter().getTranslateX();
                    double y = projectile.getCharacter().getTranslateY();
                    if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                        pane.getChildren().remove(projectile.getCharacter());
                        return true; // Remove the projectile from the list
                    }
                    return false;
                });

                laserBeams.removeIf(laserBeam -> {
                    // Remove laser beam if it goes off-screen
                    double x = laserBeam.getCharacter().getTranslateX();
                    double y = laserBeam.getCharacter().getTranslateY();
                    if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                        pane.getChildren().remove(laserBeam.getCharacter());
                        return true; // Remove the laser beam from the list
                    }
                    return false;
                });

                asteroids.removeIf(asteroid -> {
                    if (!asteroid.isAlive()) {
                        pane.getChildren().remove(asteroid.getCharacter());
                        return true; // Remove the asteroid from the list
                    }
                    return false;
                });

                // Check for collision between ship and asteroids
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        pane.getChildren().remove(armors.get(armors.size() - 1).getCharacter());
                        armors.remove(armors.size() - 1);
                        if (armors.isEmpty()) {
                            stop();
                            disableKeyListeners(scene);
                            playSound("explosion.wav");
                            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                            pause.setOnFinished(event -> {
                                enableKeyListeners(scene, pressedKeys, canFire, canFireHugeLaserBeam, justFiredHugeLaserBeam, projectiles, laserBeams, pane, points);
                                isManualResize = false;
                                GridPane gameOverView = gameOverView(window, scene, pane, points, text, asteroids, projectiles, laserBeams, pressedKeys, armors);
                                window.setScene(new Scene(gameOverView, WIDTH, HEIGHT, Color.BLACK));
                                isManualResize = true;
                                playSound("game-over.mp3");
                            });
                            pause.play();
                        } else if (armors.size() <= 3) {
                            stop();
                            pressedKeys.clear();
                            disableKeyListeners(scene);
                            playSound("explosion.wav");
                            asteroid.setAlive(false);
                            PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
                            pause.setOnFinished(event -> {
                                enableKeyListeners(scene, pressedKeys, canFire, canFireHugeLaserBeam, justFiredHugeLaserBeam, projectiles, laserBeams, pane, points);
                                pane.getChildren().remove(asteroid.getCharacter());
                                ship.setMovement(new Point2D(0, 0));
                                start();
                            });
                            pause.play();
                        }
                    }
                });

                if (Math.random() < asteroidChance) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                        // Put the texts and the armors on top of the layout so that they cannot be hid by asteroids
                        armors.forEach(armor -> armor.getCharacter().toFront());
                        text.toFront();
                        armorText.toFront();
                    }
                }
            }
        };

        welcomeView(window, scene);
        window.show();

        playSound("game-start.mp3");
    }

    // Method to fire a projectile
    private void fireProjectile(Ship ship, List<Projectile> projectiles, Pane pane) {
        Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
        projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
        projectiles.add(projectile);
        projectile.accelerate();
        projectile.setMovement(projectile.getMovement().normalize().multiply(3));
        pane.getChildren().add(projectile.getCharacter());
        playSound("fire.wav");
    }

    private GridPane gameOverView(Stage window, Scene scene, Pane pane, AtomicInteger pointsText, Text
            text, List<Asteroid> asteroids, List<Projectile> projectiles, List<LaserBeam> laserBeams, Map<KeyCode, Boolean> pressedKeys, List<Armor> armors) {
        GridPane layout = new GridPane();
        layout.setPrefSize(WIDTH, HEIGHT);

        BackgroundImage bgImage = createBackground();
        layout.setBackground(new Background(bgImage));
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(40);
        layout.setHgap(20);

        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        gameOverLabel.setTextFill(Color.WHITE);

        Label points = new Label("Points: " + pointsText.toString());
        points.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        points.setTextFill(Color.WHITE);

        HBox pointsContainer = new HBox();
        pointsContainer.setAlignment(Pos.CENTER);
        pointsContainer.getChildren().add(points);

        Button playAgain = new Button("Play Again");
        styleButton(playAgain);

        Button exit = new Button("Exit");
        styleButton(exit);

        HBox playExit = new HBox();
        playExit.getChildren().addAll(playAgain, exit);
        playExit.setSpacing(20);
        playExit.setAlignment(Pos.CENTER);

        layout.add(gameOverLabel, 0, 0);
        layout.add(pointsContainer, 0, 1);
        layout.add(playExit, 0, 2);

        exit.setOnAction(event -> window.close());

        playAgain.setOnAction(event -> {
            resetGame(pane, pointsText, text, asteroids, projectiles, laserBeams, pressedKeys, armors);

            timer.start();

            // Set the scene back to the game scene
            window.setScene(scene);

            playSound("game-start.mp3");
        });

        return layout;
    }

    private void resetGame(Pane pane, AtomicInteger pointsText, Text
            text, List<Asteroid> asteroids, List<Projectile> projectiles, List<LaserBeam> laserBeams, Map<KeyCode, Boolean> pressedKeys, List<Armor> armors) {
        // Clear existing game elements from the pane
        powerUpCount = 0;
        newArmorCount = 0;
        createNewArmor(armors, 3, 0);
        asteroidChance = 0.02;
        speedMultiplier = 1.3;

        pane.setPrefSize(WIDTH, HEIGHT);
        pane.getChildren().clear();
        pointsText.set(0);
        text.setText("Points: 0");
        pane.getChildren().add(text);

        Text armorText = new Text(150, 25, "Armor: ");
        armorText.setFont(Font.font("Arial", 20));
        armorText.setFill(Color.WHITE);
        pane.getChildren().add(armorText);

        // Reset ship position
        ship.getCharacter().setTranslateX(WIDTH / 2);
        ship.getCharacter().setTranslateY(HEIGHT / 2);
        ship.setMovement(new Point2D(0, 0));
        ship.getCharacter().setRotate(0);
        pressedKeys.clear();
        pane.getChildren().add(ship.getCharacter());

        // Clear lists
        asteroids.clear();
        projectiles.clear();
        laserBeams.clear();

        createFirstFiveAsteroids(asteroids);

        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
        armors.forEach(armor -> {
            armor.resetSize();
            pane.getChildren().add(armor.getCharacter());
            if (WIDTH > 700 && HEIGHT > 500) {
                Armor.setSize(resizePercentage);
            }
        });
    }

    private void playSound(String file) {
        AudioClip sound = new AudioClip(getClass().getResource("/app/" + file).toExternalForm());
        sound.play();
    }

    private void disableKeyListeners(Scene scene) {
        scene.setOnKeyPressed(null);
        scene.setOnKeyReleased(null);
    }

    private void enableKeyListeners(Scene scene, Map<KeyCode, Boolean> pressedKeys, boolean[] canFire, boolean[] canFireHugeLaserBeam, boolean[] justFiredHugeLaserBeam, List<
            Projectile> projectiles, List<LaserBeam> laserBeams, Pane pane, AtomicInteger points) {
        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);

            if (event.getCode() == KeyCode.SPACE && canFire[0] && !canFireHugeLaserBeam[0]) {
                fireProjectile(ship, projectiles, pane);
                canFire[0] = false;
            } else if (event.getCode() == KeyCode.SPACE && canFireHugeLaserBeam[0]) {
                double startX = ship.getCharacter().getTranslateX();
                double startY = ship.getCharacter().getTranslateY();
                double rotation = ship.getCharacter().getRotate();

                fireLaserBeam(startX, startY, rotation, laserBeams, pane, "laser_beam1.png");
                playSound("laser_beam.wav");
                // For subsequent laser beams, pass the same startX, startY, and rotation
                Timeline timeline = new Timeline();
                for (int i = 1; i <= 24; i++) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(80 * i), event2 -> fireLaserBeam(startX, startY, rotation, laserBeams, pane, "laser_beam2.png")));
                }
                timeline.play();

                canFireHugeLaserBeam[0] = false;
                justFiredHugeLaserBeam[0] = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);

            if (event.getCode() == KeyCode.SPACE) {
                if (points.get() >= 1000 && !justFiredHugeLaserBeam[0]) {
                    fireProjectile(ship, projectiles, pane);
                }

                canFire[0] = true;
                justFiredHugeLaserBeam[0] = false;
            }
        });
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black; -fx-background-radius: 10; -fx-border-radius: 10;");
        button.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        button.setOnMouseEntered(event -> button.setStyle(button.getStyle() + "-fx-border-color: cyan; -fx-border-width: 2px;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black; -fx-background-radius: 10; -fx-border-radius: 10;"));
    }

    private void createFirstFiveAsteroids(List<Asteroid> asteroids) {
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }
    }

    private BackgroundImage createBackground() {
        Image background = new Image(getClass().getResource("/app/background.jpg").toExternalForm());
        BackgroundImage bgImage;
        bgImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

        return bgImage;
    }

    private void createNewArmor(List<Armor> armors, int numberOfLives, int armorPositionStart) {
        armorPositionStart *= 30;
        armorPositionStart += 230;
        for (int i = 0; i < numberOfLives; i++) {
            Armor armor = new Armor(armorPositionStart, 20);
            armors.add(armor);
            armorPositionStart += 30;
        }
    }

    private Armor createMovingArmor() {
        Random random = new Random();
        return new Armor(40 + random.nextInt(WIDTH - 80), 0);
    }

    private void addMovingArmorToTheLayout(Pane pane) {
        movingArmor = createMovingArmor();
        pane.getChildren().add(movingArmor.getCharacter());
    }

    private PowerUp createPowerUp() {
        Random random = new Random();
        return new PowerUp(40 + random.nextInt(WIDTH - 80), 0);
    }

    private void addPowerUpToTheLayout(Pane pane) {
        powerUp = createPowerUp();
        pane.getChildren().add(powerUp.getCharacter());
    }

    private void fireLaserBeam(double startX, double startY, double rotation, List<LaserBeam> laserBeams, Pane pane, String imageName) {
        // Create the laser beam at the provided position and rotation
        LaserBeam laserBeam = new LaserBeam((int) startX, (int) startY);
        laserBeam.setCharacterFill(imageName);
        laserBeam.getCharacter().setRotate(rotation); // Set the rotation to match the first laser

        laserBeams.add(laserBeam);
        laserBeam.accelerate();
        laserBeam.setMovement(laserBeam.getMovement().normalize().multiply(3));
        pane.getChildren().add(laserBeam.getCharacter());
    }

    private void welcomeView(Stage window, Scene scene) {
        VBox layout = new VBox();
        layout.setPrefSize(WIDTH, HEIGHT);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setSpacing(40);

        window.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            if (isManualResize) {
                HEIGHT = newHeight.intValue();
                layout.setPrefHeight(HEIGHT);
            }
        });

        window.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (isManualResize) {
                WIDTH = newWidth.intValue();
                layout.setPrefWidth(WIDTH);
            }
        });

        BackgroundImage bgImage = createBackground();
        layout.setBackground(new Background(bgImage));

        Label welcomeLabel = new Label("Welcome to Asteroids!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        welcomeLabel.setTextFill(Color.WHITE);

        Label text = new Label("Prepare for an exciting adventure in space!\nYour mission is to destroy asteroids while avoiding collisions.");
        text.setFont(Font.font("Arial", 20));
        text.setTextFill(Color.WHITE);

        Button howToPlayButton = new Button("How to Play");
        styleButton(howToPlayButton);

        Label finalSentence = new Label("Can you survive the asteroid storm and set a high score?");
        finalSentence.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        finalSentence.setTextFill(Color.WHITE);

        Button startGame = new Button("Start Game");
        styleButton(startGame);

        layout.getChildren().addAll(welcomeLabel, text, howToPlayButton, finalSentence, startGame);

        Scene welcomeScene = new Scene(layout, Color.BLACK);

        startGame.setOnAction(event -> {
            window.setScene(scene);
            isManualResize = true;
            playSound("game-start.mp3");
            timer.start();
        });

        howToPlayButton.setOnAction(event -> {
            isManualResize = false;
            BorderPane howToPlayLayout = new BorderPane();
            howToPlayLayout.setPrefSize(WIDTH, HEIGHT);
            howToPlayLayout.setPadding(new Insets(20, 20, 20, 20));

            BackgroundImage bg = createBackground();
            howToPlayLayout.setBackground(new Background(bg));

            Label rules = new Label("""
                    • Shoot asteroids to earn points. You get 10 points for every asteroid
                       you destroy.
                    
                    • You have 3 armors. If all armors are destroyed, the game is over.
                    
                    • Armors can appear randomly within each 1000-point interval if you have
                       fewer than 3. Collect them to stay in the game!
                    
                    • Every 500 points, the number of asteroids and their speed increases.
                    
                    • When you reach 1000 points, you will start shooting two blasts at the
                       same time instead of one.
                    
                    • A laser blast power-up will appear randomly within each 1000-point
                       interval. Pick it up to unleash a powerful laser attack!
                    
                    • Control your spaceship:
                                          - Move forward with W or Up Arrow
                                          - Rotate left with A or Left Arrow
                                          - Rotate right with D or Right Arrow
                                          - Shoot with Spacebar""");

            rules.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            rules.setTextFill(Color.WHITE);

            howToPlayLayout.setCenter(rules);

            Button x = new Button("x");
            styleButton(x);
            howToPlayLayout.setRight(x);

            Scene howToPlayScene = new Scene(howToPlayLayout, Color.BLACK);
            window.setScene(howToPlayScene);
            x.setOnAction(event2 -> {
                isManualResize = false;
                window.setScene(welcomeScene);
                isManualResize = true;
            });
        });

        window.setScene(welcomeScene);
    }
}