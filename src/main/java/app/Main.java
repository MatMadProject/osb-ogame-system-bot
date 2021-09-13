package app;

import app.data.Configuration;
import app.data.Session;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        if(Configuration.firstConfiguration) {
            Configuration.save();
        }
        Configuration.saveCounter();
        loadStartWindow(primaryStage);
    }


    private void loadStartWindow(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/gui/login-window.fxml"));
        primaryStage.setTitle("OGame System Bot - Login");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/resources/css/login-style.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setOnCloseRequest(e ->
        {
            if(Session.user != null)
                Session.logout();
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
