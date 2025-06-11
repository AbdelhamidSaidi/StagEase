package com.gestionstages.controller;

import com.gestionstages.dao.UtilisateurDAO;
import com.gestionstages.model.Utilisateur;
import com.gestionstages.util.SessionManager;
import com.gestionstages.util.StyleManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    
    @FXML
    private void initialize() {
        // Appliquer le style au bouton de connexion
        loginButton.setStyle("-fx-background-color: " + StyleManager.COLOR_PRIMARY + "; -fx-text-fill: white;");
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez saisir l'email et le mot de passe");
            return;
        }
        
        try {
            Utilisateur utilisateur = utilisateurDAO.authentifier(email, password);
            
            if (utilisateur != null) {
                SessionManager.setCurrentUser(utilisateur);
                ouvrirTableauDeBord();
            } else {
                showError("Email ou mot de passe incorrect");
            }
        } catch (SQLException e) {
            showError("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }
    
    private void ouvrirTableauDeBord() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = loginButton.getScene();
            scene.setRoot(root);
            
            javafx.stage.Stage jfxStage = (javafx.stage.Stage) scene.getWindow();
            jfxStage.setTitle("Gestion des Stages - Tableau de Bord");
            jfxStage.setMaximized(true);
            jfxStage.centerOnScreen();
        } catch (IOException e) {
            showError("Erreur lors du chargement du tableau de bord: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
