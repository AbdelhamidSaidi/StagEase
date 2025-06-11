package com.gestionstages.util;

import com.gestionstages.model.Utilisateur;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class StyleManager {
    
    // Couleurs principales
    public static final String COLOR_DARK_HEADER = "#1a2035";
    public static final String COLOR_LIGHT_BG = "#f5f5f5";
    public static final String COLOR_PRIMARY = "#2196F3";
    public static final String COLOR_SUCCESS = "#4CAF50";
    public static final String COLOR_WARNING = "#FF9800";
    public static final String COLOR_DANGER = "#f44336";
    
    // Appliquer le style à une scène
    public static void applyCommonStyle(Scene scene) {
        scene.getRoot().setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");
    }
    
    // Créer une barre de navigation
    public static VBox createHeader(String title, Utilisateur currentUser) {
        // Barre de navigation principale (sombre)
        VBox header = new VBox();
        header.setStyle("-fx-background-color: " + COLOR_DARK_HEADER + ";");
        header.setPadding(new Insets(10, 20, 10, 20));
        
        // Ligne du haut avec titre et utilisateur connecté
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Connecté: " + currentUser.getNomComplet());
        userLabel.setTextFill(Color.LIGHTGRAY);
        
        topRow.getChildren().addAll(titleLabel, spacer, userLabel);
        
        header.getChildren().add(topRow);
        
        return header;
    }
    
    // Créer un bouton de navigation
    public static Button createNavButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: #cccccc; -fx-border-width: 1;");
        button.setPrefHeight(35);
        button.setMinWidth(120);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: #cccccc; -fx-border-width: 1;"));
        return button;
    }
    
    // Créer un bouton d'action
    public static Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setPrefHeight(35);
        button.setMinWidth(100);
        return button;
    }
    
    // Créer un bouton de retour
    public static Button createBackButton() {
        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        backButton.setPrefHeight(30);
        backButton.setMinWidth(80);
        return backButton;
    }
    
    // Créer une barre de navigation secondaire
    public static HBox createNavBar() {
        HBox navBar = new HBox(10);
        navBar.setPadding(new Insets(10, 20, 10, 20));
        navBar.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return navBar;
    }
    
    // Créer un titre de section
    public static Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setPadding(new Insets(10, 0, 10, 0));
        return title;
    }
}
