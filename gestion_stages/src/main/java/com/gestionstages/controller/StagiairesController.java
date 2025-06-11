package com.gestionstages.controller;

import com.gestionstages.dao.StagiaireDAO;
import com.gestionstages.dao.CandidatureDAO;
import com.gestionstages.model.Stagiaire;
import com.gestionstages.model.Candidature;
import com.gestionstages.model.Utilisateur;
import com.gestionstages.util.SessionManager;
import com.gestionstages.util.StyleManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class StagiairesController implements Initializable {
    
    @FXML private VBox rootContainer;
    @FXML private TableView<Stagiaire> stagiairesTable;
    @FXML private TableColumn<Stagiaire, String> candidatColumn;
    @FXML private TableColumn<Stagiaire, String> stageColumn;
    @FXML private TableColumn<Stagiaire, String> badgeColumn;
    @FXML private TableColumn<Stagiaire, LocalDate> arriveeColumn;
    @FXML private TableColumn<Stagiaire, LocalDate> departColumn;
    @FXML private TableColumn<Stagiaire, String> conventionColumn;
    @FXML private HBox navBar;

    @FXML private VBox gestionSection;
    @FXML private VBox evaluationSection;
    @FXML private Label moyenneLabel;
    @FXML private Label moyenneValue;
    @FXML private Label infoLabel;
    
    @FXML private TextField badgeField;
    @FXML private DatePicker arriveeDate;
    @FXML private DatePicker departDate;
    @FXML private TextField congeField;
    
    @FXML private TextField noteTravailField;
    @FXML private TextField noteComportementField;
    @FXML private TextField noteRapportField;
    
    @FXML private Button modifierButton;
    @FXML private Button retourButton;
    
    private StagiaireDAO stagiaireDAO = new StagiaireDAO();
    private CandidatureDAO candidatureDAO = new CandidatureDAO();
    private ObservableList<Stagiaire> stagiairesList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadStagiaires();
        setupButtons();
        
        // Ajouter le bouton de retour
        if (retourButton != null) {
            retourButton.setOnAction(e -> handleRetour());
        }
        
        // Configurer la fenêtre en plein écran après que la scène soit prête
        Platform.runLater(() -> {
            javafx.stage.Stage jfxStage = (javafx.stage.Stage) stagiairesTable.getScene().getWindow();
            if (jfxStage != null) {
                jfxStage.setMaximized(true);
                jfxStage.setMinWidth(1200);
                jfxStage.setMinHeight(800);
            }
        });
        
        // Listener pour remplir les champs lors de la sélection
        stagiairesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFields(newSelection);
                }
            }
        );
        
        // Ajouter un listener pour la date de départ
        departDate.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && arriveeDate.getValue() != null && newValue.isBefore(arriveeDate.getValue())) {
                showAlert("Erreur", "La date de départ ne peut pas être antérieure à la date d'arrivée");
                departDate.setValue(oldValue);
            }
        });
    }
    
    private void setupTableColumns() {
        candidatColumn.setCellValueFactory(new PropertyValueFactory<>("candidatNom"));
        stageColumn.setCellValueFactory(new PropertyValueFactory<>("stageTitre"));
        badgeColumn.setCellValueFactory(new PropertyValueFactory<>("numeroBadge"));
        arriveeColumn.setCellValueFactory(new PropertyValueFactory<>("dateArriveeEffective"));
        departColumn.setCellValueFactory(new PropertyValueFactory<>("dateDepartEffective"));
        
        // Colonne statut basée sur la présence
        conventionColumn.setCellValueFactory(cellData -> {
            Stagiaire stagiaire = cellData.getValue();
            String statut = "Non arrivé";
            if (stagiaire.getDateArriveeEffective() != null) {
                if (stagiaire.getDateDepartEffective() != null) {
                    statut = "Terminé";
                } else {
                    statut = "En cours";
                }
            }
            return new javafx.beans.property.SimpleStringProperty(statut);
        });
        
        stagiairesTable.setItems(stagiairesList);
    }
    
    private void loadStagiaires() {
        try {
            Utilisateur currentUser = SessionManager.getCurrentUser();
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                stagiairesList.setAll(stagiaireDAO.getStagiairesByResponsable(currentUser.getId()));
            } else {
                stagiairesList.setAll(stagiaireDAO.getAllStagiaires());
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les stagiaires: " + e.getMessage());
        }
    }
    
    private void setupButtons() {
        Utilisateur currentUser = SessionManager.getCurrentUser();
        
        if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
            // Responsable de stages : peut seulement évaluer (notes)
            
            // Masquer la section de gestion administrative
            gestionSection.setVisible(false);
            gestionSection.setManaged(false);
            
            modifierButton.setText("Sauvegarder Évaluation");
            modifierButton.setStyle("-fx-background-color: " + StyleManager.COLOR_WARNING + "; -fx-text-fill: white;");
            
            infoLabel.setText("En tant que Responsable de Stages, vous pouvez uniquement évaluer VOS stagiaires (notes de travail, comportement et rapport).");
            
        } else {
            // Responsable personnel : peut tout gérer sauf évaluer
            
            // Masquer la section d'évaluation
            evaluationSection.setVisible(false);
            evaluationSection.setManaged(false);
            
            modifierButton.setText("Modifier Stagiaire");
            modifierButton.setStyle("-fx-background-color: " + StyleManager.COLOR_SUCCESS + "; -fx-text-fill: white;");
            
            infoLabel.setText("En tant que Responsable du Personnel, vous gérez les aspects administratifs des stagiaires (badges, dates, congés).");
        }
    }
    
    @FXML
    private void handleRetour() {
        // Retourner à la scène précédente au lieu de fermer la fenêtre
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = stagiairesTable.getScene();
            scene.setRoot(root);
            
            // Configurer la scène en plein écran
            javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le tableau de bord: " + e.getMessage());
        }
    }
    
    private void fillFields(Stagiaire stagiaire) {
        badgeField.setText(stagiaire.getNumeroBadge());
        arriveeDate.setValue(stagiaire.getDateArriveeEffective());
        departDate.setValue(stagiaire.getDateDepartEffective());
        congeField.setText(String.valueOf(stagiaire.getJoursConge()));
        
        noteTravailField.setText(stagiaire.getNoteTravail() != null ? 
            stagiaire.getNoteTravail().toString() : "");
        noteComportementField.setText(stagiaire.getNoteComportement() != null ? 
            stagiaire.getNoteComportement().toString() : "");
        noteRapportField.setText(stagiaire.getNoteRapport() != null ? 
            stagiaire.getNoteRapport().toString() : "");
    
        // Calculer et afficher la moyenne
        Double moyenne = stagiaire.getMoyenneNotes();
        if (moyenne != null) {
            moyenneValue.setText(String.format("%.2f/20", moyenne));
            if (moyenne >= 16) {
                moyenneValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50;");
            } else if (moyenne >= 12) {
                moyenneValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800;");
            } else {
                moyenneValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #f44336;");
            }
        } else {
            moyenneValue.setText("-");
            moyenneValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #666666;");
        }
    }
    
    @FXML
    private void handleModifier() {
        Stagiaire selected = stagiairesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un stagiaire");
            return;
        }
        
        try {
            Utilisateur currentUser = SessionManager.getCurrentUser();
            
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                // Responsable de stages : uniquement les notes
                
                // Valider les notes (max 20)
                Double noteTravail = null;
                Double noteComportement = null;
                Double noteRapport = null;
                
                if (!noteTravailField.getText().trim().isEmpty()) {
                    try {
                        noteTravail = Double.parseDouble(noteTravailField.getText().trim());
                        if (noteTravail < 0 || noteTravail > 20) {
                            showAlert("Erreur", "La note de travail doit être comprise entre 0 et 20");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Erreur", "Note travail invalide");
                        return;
                    }
                }
                
                if (!noteComportementField.getText().trim().isEmpty()) {
                    try {
                        noteComportement = Double.parseDouble(noteComportementField.getText().trim());
                        if (noteComportement < 0 || noteComportement > 20) {
                            showAlert("Erreur", "La note de comportement doit être comprise entre 0 et 20");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Erreur", "Note comportement invalide");
                        return;
                    }
                }
                
                if (!noteRapportField.getText().trim().isEmpty()) {
                    try {
                        noteRapport = Double.parseDouble(noteRapportField.getText().trim());
                        if (noteRapport < 0 || noteRapport > 20) {
                            showAlert("Erreur", "La note de rapport doit être comprise entre 0 et 20");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Erreur", "Note rapport invalide");
                        return;
                    }
                }
                
                // Mettre à jour les notes
                selected.setNoteTravail(noteTravail);
                selected.setNoteComportement(noteComportement);
                selected.setNoteRapport(noteRapport);
                
            } else {
                // Responsable personnel : données administratives
                
                // Mettre à jour les données du stagiaire
                selected.setNumeroBadge(badgeField.getText().trim());
                selected.setDateArriveeEffective(arriveeDate.getValue());
                selected.setDateDepartEffective(departDate.getValue());
                
                // Vérifier que la date de départ n'est pas antérieure à la date d'arrivée
                if (selected.getDateDepartEffective() != null && 
                    selected.getDateArriveeEffective() != null && 
                    selected.getDateDepartEffective().isBefore(selected.getDateArriveeEffective())) {
                    showAlert("Erreur", "La date de départ ne peut pas être antérieure à la date d'arrivée");
                    return;
                }
                
                try {
                    selected.setJoursConge(Integer.parseInt(congeField.getText().trim()));
                } catch (NumberFormatException e) {
                    selected.setJoursConge(0);
                }
            }
            
            // Sauvegarder les modifications
            stagiaireDAO.updateStagiaire(selected);
            
            // Recharger les données
            loadStagiaires();
            
            // Mettre à jour la moyenne si c'est un responsable de stages
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                // Récupérer le stagiaire mis à jour
                Stagiaire updated = stagiaireDAO.getStagiaireById(selected.getId());
                if (updated != null) {
                    Double moyenne = updated.getMoyenneNotes();
                    if (moyenne != null) {
                        moyenneValue.setText(String.format("%.2f/20", moyenne));
                        if (moyenne >= 16) {
                            moyenneValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50;");
                        } else if (moyenne >= 12) {
                            moyenneValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800;");
                        } else {
                            moyenneValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #f44336;");
                        }
                    }
                }
            }
            
            showAlert("Succès", "Stagiaire modifié avec succès!");
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de modifier le stagiaire: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
