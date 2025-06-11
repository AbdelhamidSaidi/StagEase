package com.gestionstages.controller;

import com.gestionstages.model.Utilisateur;
import com.gestionstages.util.SessionManager;
import com.gestionstages.util.StyleManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    
    @FXML private VBox rootContainer;
    @FXML private Label welcomeLabel;
    @FXML private Button gererStagesButton;
    @FXML private Button gererCandidaturesButton;
    @FXML private Button gererStagiairesButton;
    @FXML private Button catalogueButton;
    @FXML private Button deconnexionButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utilisateur currentUser = SessionManager.getCurrentUser();
        welcomeLabel.setText("Bienvenue, " + currentUser.getNomComplet());
        
        // Configurer les boutons selon le type d'utilisateur
        if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
            // Responsable de stages : peut seulement gérer ses stages et voir ses candidatures/stagiaires
            gererCandidaturesButton.setText("Voir mes Candidats");
            gererStagiairesButton.setText("Évaluer mes Stagiaires");
            catalogueButton.setVisible(false);
        } else {
            // Responsable personnel : peut tout gérer sauf créer des stages
            gererStagesButton.setVisible(false);
            gererCandidaturesButton.setText("Gérer les Candidatures");
            gererStagiairesButton.setText("Gérer les Stagiaires");
        }
        
        // Appliquer le style aux boutons
        gererStagesButton.setStyle("-fx-background-color: " + StyleManager.COLOR_PRIMARY + "; -fx-text-fill: white;");
        gererCandidaturesButton.setStyle("-fx-background-color: " + StyleManager.COLOR_SUCCESS + "; -fx-text-fill: white;");
        gererStagiairesButton.setStyle("-fx-background-color: " + StyleManager.COLOR_WARNING + "; -fx-text-fill: white;");
        catalogueButton.setStyle("-fx-background-color: " + StyleManager.COLOR_PRIMARY + "; -fx-text-fill: white;");
        deconnexionButton.setStyle("-fx-background-color: " + StyleManager.COLOR_DANGER + "; -fx-text-fill: white;");
    }
    
    @FXML
    private void handleGererStages() {
        changerScene("/fxml/stages.fxml", "Gestion des Stages");
    }
    
    @FXML
    private void handleGererCandidatures() {
        changerScene("/fxml/candidatures.fxml", "Gestion des Candidatures");
    }
    
    @FXML
    private void handleGererStagiaires() {
        changerScene("/fxml/stagiaires.fxml", "Gestion des Stagiaires");
    }
    
    @FXML
    private void handleCatalogue() {
        changerScene("/fxml/catalogue.fxml", "Catalogue des Stages");
    }
    
    @FXML
    private void handleDeconnexion() {
        try {
            SessionManager.setCurrentUser(null);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = rootContainer.getScene();
            scene.setRoot(root);
            
            javafx.stage.Stage jfxStage = (javafx.stage.Stage) scene.getWindow();
            jfxStage.setTitle("Connexion - Gestion des Stages");
            jfxStage.setMaximized(true);
            jfxStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page de connexion: " + e.getMessage());
        }
    }
    
    private void changerScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Changer la scène actuelle au lieu d'ouvrir une nouvelle fenêtre
            Scene scene = rootContainer.getScene();
            scene.setRoot(root);
            
            // Configurer la scène en plein écran
            javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
            stage.setTitle(title);
            stage.setMaximized(true);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de " + fxmlPath + ": " + e.getMessage());
            
            // Afficher une alerte à l'utilisateur
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible de charger la page demandée: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
