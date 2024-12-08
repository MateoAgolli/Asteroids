module app.asteroids {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;


    opens app to javafx.fxml;
    exports app;
}