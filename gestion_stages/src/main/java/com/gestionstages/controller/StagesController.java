package com.gestionstages.controller;

import com.gestionstages.dao.StageDAO;
import com.gestionstages.dao.UtilisateurDAO;
import com.gestionstages.dao.CandidatureDAO;
import com.gestionstages.model.Utilisateur;
import com.gestionstages.util.SessionManager;
import com.gestionstages.util.StyleManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StagesController implements Initializable {
    
    @FXML private VBox rootContainer;
    @FXML private TableView<com.gestionstages.model.Stage> stagesTable;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> referenceColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> titreColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> sujetColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, Integer> dureeColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> responsableColumn;
    @FXML private HBox navBar;
    @FXML private GridPane formGrid;
    @FXML private Label formTitle;
    
    @FXML private TextField titreField;
    @FXML private TextArea sujetArea;
    @FXML private TextField dureeField;
    @FXML private ComboBox<Utilisateur> responsableCombo;
    
    @FXML private Button ajouterButton;
    @FXML private Button retourButton;
    @FXML private Button supprimerButton;
    
    private StageDAO stageDAO = new StageDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private CandidatureDAO candidatureDAO = new CandidatureDAO();
    private ObservableList<com.gestionstages.model.Stage> stagesList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadStages();
        
        // Changer le titre du formulaire
        if (formTitle != null) {
            formTitle.setText("Définir un nouveau stage");
        }
        
        // Supprimer le champ responsable pour le responsable de stages
        Utilisateur currentUser = SessionManager.getCurrentUser();
        if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
            // Supprimer la ligne du responsable dans le GridPane
            if (formGrid != null && formGrid.getChildren() != null) {
                formGrid.getChildren().removeIf(node -> 
                    GridPane.getRowIndex(node) != null && 
                    GridPane.getRowIndex(node) == 2
                );
            }
            
            // Cacher le ComboBox du responsable
            if (responsableCombo != null) {
                responsableCombo.setVisible(false);
                responsableCombo.setManaged(false);
            }
            
            // Ajouter le bouton de suppression pour le responsable de stages
            if (supprimerButton != null) {
                supprimerButton.setVisible(true);
                supprimerButton.setStyle("-fx-background-color: " + StyleManager.COLOR_DANGER + "; -fx-text-fill: white;");
            }
        } else {
            // Cacher le bouton de suppression pour le responsable personnel
            if (supprimerButton != null) {
                supprimerButton.setVisible(false);
                supprimerButton.setManaged(false);
            }
            loadResponsables();
        }
        
        // Ajouter le bouton de retour
        if (retourButton != null) {
            retourButton.setOnAction(e -> handleRetour());
        }
        
        // Appliquer les styles aux boutons
        if (ajouterButton != null) {
            ajouterButton.setStyle("-fx-background-color: " + StyleManager.COLOR_SUCCESS + "; -fx-text-fill: white;");
            ajouterButton.setText("Définir Stage");
        }
        
        // Vérifier si l'utilisateur peut ajouter des stages
        if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
            // Limiter à 5 stages par responsable
            try {
                int count = stageDAO.countStagesByResponsable(currentUser.getId());
                if (count >= 5) {
                    ajouterButton.setDisable(true);
                    ajouterButton.setText("Limite atteinte (5 stages max)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Configurer la fenêtre en plein écran
        Platform.runLater(() -> {
            javafx.stage.Stage jfxStage = (javafx.stage.Stage) stagesTable.getScene().getWindow();
            if (jfxStage != null) {
                jfxStage.setMaximized(true);
                jfxStage.setMinWidth(1200);
                jfxStage.setMinHeight(800);
            }
        });
        
        // Ajouter un listener pour la sélection de table
        stagesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (supprimerButton != null) {
                supprimerButton.setDisable(newSelection == null);
            }
        });
    }
    
    private void setupTableColumns() {
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        sujetColumn.setCellValueFactory(new PropertyValueFactory<>("sujet"));
        dureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
        responsableColumn.setCellValueFactory(new PropertyValueFactory<>("responsableNom"));
        
        stagesTable.setItems(stagesList);
    }
    
    private void loadStages() {
        try {
            Utilisateur currentUser = SessionManager.getCurrentUser();
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                stagesList.setAll(stageDAO.getStagesByResponsable(currentUser.getId()));
            } else {
                stagesList.setAll(stageDAO.getAllStages());
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les stages: " + e.getMessage());
        }
    }
    
    private void loadResponsables() {
        try {
            ObservableList<Utilisateur> responsables = FXCollections.observableArrayList(
                utilisateurDAO.getResponsablesStages()
            );
            responsableCombo.setItems(responsables);
            
            // Sélectionner l'utilisateur actuel s'il est responsable de stages
            Utilisateur currentUser = SessionManager.getCurrentUser();
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                responsableCombo.setValue(currentUser);
                responsableCombo.setDisable(true);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les responsables: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRetour() {
        // Retourner à la scène précédente au lieu de fermer la fenêtre
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = stagesTable.getScene();
            scene.setRoot(root);
            
            // Configurer la scène en plein écran
            javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le tableau de bord: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimer() {
        com.gestionstages.model.Stage selectedStage = stagesTable.getSelectionModel().getSelectedItem();
        if (selectedStage == null) {
            showAlert("Erreur", "Veuillez sélectionner un stage à supprimer");
            return;
        }
        
        try {
            // Vérifier si le stage a des candidatures
            int candidaturesCount = candidatureDAO.countCandidaturesByStage(selectedStage.getId());
            if (candidaturesCount > 0) {
                showAlert("Erreur", "Impossible de supprimer ce stage car il a " + candidaturesCount + " candidature(s) associée(s)");
                return;
            }
            
            // Confirmation de suppression
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le stage");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le stage " + selectedStage.getReference() + " - " + selectedStage.getTitre() + " ?");
            
            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                stageDAO.supprimerStage(selectedStage.getId());
                loadStages();
                
                // Mettre à jour le compteur et l'état du bouton d'ajout
                Utilisateur currentUser = SessionManager.getCurrentUser();
                if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                    int count = stageDAO.countStagesByResponsable(currentUser.getId());
                    if (count < 5 && ajouterButton.isDisabled()) {
                        ajouterButton.setDisable(false);
                        ajouterButton.setText("Définir Stage");
                    }
                }
                
                showAlert("Succès", "Stage supprimé avec succès");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de supprimer le stage: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAjouter() {
        if (!validerChamps()) {
            return;
        }
        
        try {
            String reference = stageDAO.genererReferenceStage();
            
            // Déterminer le responsable
            int responsableId;
            Utilisateur currentUser = SessionManager.getCurrentUser();
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                responsableId = currentUser.getId();
            } else {
                if (responsableCombo.getValue() == null) {
                    showAlert("Erreur", "Veuillez sélectionner un responsable");
                    return;
                }
                responsableId = responsableCombo.getValue().getId();
            }
            
            com.gestionstages.model.Stage stage = new com.gestionstages.model.Stage(
                reference,
                titreField.getText().trim(),
                sujetArea.getText().trim(),
                Integer.parseInt(dureeField.getText().trim()),
                responsableId
            );
            
            stageDAO.ajouterStage(stage);
            loadStages();
            clearFields();
            
            showAlert("Succès", "Stage défini avec succès!");
            
            // Vérifier la limite après ajout
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                int count = stageDAO.countStagesByResponsable(currentUser.getId());
                if (count >= 5) {
                    ajouterButton.setDisable(true);
                    ajouterButton.setText("Limite atteinte (5 stages max)");
                }
            }
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'ajouter le stage: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La durée doit être un nombre entier");
        }
    }
    
    private boolean validerChamps() {
        if (titreField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le titre est obligatoire");
            return false;
        }
        
        if (sujetArea.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le sujet est obligatoire");
            return false;
        }
        
        if (dureeField.getText().trim().isEmpty()) {
            showAlert("Erreur", "La durée est obligatoire");
            return false;
        }
        
        try {
            int duree = Integer.parseInt(dureeField.getText().trim());
            if (duree <= 0) {
                showAlert("Erreur", "La durée doit être positive");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La durée doit être un nombre entier");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        titreField.clear();
        sujetArea.clear();
        dureeField.clear();
        
        Utilisateur currentUser = SessionManager.getCurrentUser();
        if (currentUser.getTypeUtilisateur() != Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES && responsableCombo != null) {
            responsableCombo.setValue(null);
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
