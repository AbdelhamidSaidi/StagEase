package com.gestionstages.controller;

import com.gestionstages.dao.StagiaireDAO;
import com.gestionstages.dao.CandidatureDAO;
import com.gestionstages.model.Candidature;
import com.gestionstages.model.Stagiaire;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PromouvoirStagiaireController implements Initializable {
    
    @FXML private TextField badgeField;
    @FXML private DatePicker dateArriveeField;
    @FXML private Button confirmerButton;
    @FXML private Button annulerButton;
    
    private Candidature candidature;
    private StagiaireDAO stagiaireDAO;
    private CandidatureDAO candidatureDAO;
    private CandidaturesController parentController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser la date à aujourd'hui
        dateArriveeField.setValue(LocalDate.now());
        
        // Configurer les boutons
        confirmerButton.setOnAction(e -> handleConfirmer());
        annulerButton.setOnAction(e -> handleAnnuler());
    }
    
    public void setCandidature(Candidature candidature) {
        this.candidature = candidature;
    }
    
    public void setStagiaireDAO(StagiaireDAO stagiaireDAO) {
        this.stagiaireDAO = stagiaireDAO;
    }
    
    public void setCandidatureDAO(CandidatureDAO candidatureDAO) {
        this.candidatureDAO = candidatureDAO;
    }
    
    public void setParentController(CandidaturesController parentController) {
        this.parentController = parentController;
    }
    
    @FXML
    private void handleConfirmer() {
        if (!validerChamps()) {
            return;
        }
        
        try {
            // Générer un numéro de badge si non fourni
            String numeroBadge = badgeField.getText().trim();
            if (numeroBadge.isEmpty()) {
                numeroBadge = stagiaireDAO.genererNumeroBadge();
                badgeField.setText(numeroBadge);
            }
            
            // Créer le stagiaire
            Stagiaire stagiaire = new Stagiaire(
                candidature.getCandidatId(),
                candidature.getStageId(),
                numeroBadge
            );
            stagiaire.setDateArriveeEffective(dateArriveeField.getValue());
            
            // Ajouter le stagiaire
            stagiaireDAO.ajouterStagiaire(stagiaire);
            
            // Fermer la fenêtre
            Stage stage = (Stage) confirmerButton.getScene().getWindow();
            stage.close();
            
            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Le candidat a été promu en stagiaire avec succès!");
            alert.showAndWait();
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de promouvoir le candidat: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }
    
    private boolean validerChamps() {
        if (dateArriveeField.getValue() == null) {
            showAlert("Erreur", "La date d'arrivée est obligatoire");
            return false;
        }
        
        return true;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
