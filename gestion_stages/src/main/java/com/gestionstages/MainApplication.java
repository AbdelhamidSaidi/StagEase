package com.gestionstages;

import com.gestionstages.util.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        
        Scene scene = new Scene(root);
        StyleManager.applyCommonStyle(scene);
        
        primaryStage.setTitle("Gestion des Stages - Connexion");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
