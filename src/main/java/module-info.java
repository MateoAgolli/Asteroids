module app.asteroids {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;
    requires java.sql;


    opens app to javafx.fxml;
    exports app;
}