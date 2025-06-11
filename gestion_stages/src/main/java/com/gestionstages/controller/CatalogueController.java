package com.gestionstages.controller;

import com.gestionstages.dao.CandidatureDAO;
import com.gestionstages.dao.StageDAO;
import com.gestionstages.dao.StagiaireDAO;
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
import java.util.List;
import java.util.ResourceBundle;

public class CatalogueController implements Initializable {
    
    @FXML private VBox rootContainer;
    @FXML private TableView<com.gestionstages.model.Stage> catalogueTable;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> referenceColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> titreColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> sujetColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, Integer> dureeColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, String> responsableColumn;
    @FXML private TableColumn<com.gestionstages.model.Stage, Integer> candidaturesColumn;
    @FXML private HBox navBar;
    
    @FXML private Button imprimerButton;
    @FXML private Button exporterButton;
    @FXML private Button actualiserButton;
    @FXML private Button retourButton;
    
    
    @FXML private Label infoLabel;
    
    private StageDAO stageDAO = new StageDAO();
    private CandidatureDAO candidatureDAO = new CandidatureDAO();
    private StagiaireDAO stagiaireDAO = new StagiaireDAO();
    private ObservableList<com.gestionstages.model.Stage> catalogueList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadCatalogue();
        
        
        // Ajouter le bouton de retour
        if (retourButton != null) {
            retourButton.setOnAction(e -> handleRetour());
        }
        
        // Appliquer les styles aux boutons
        if (imprimerButton != null) imprimerButton.setStyle("-fx-background-color: " + StyleManager.COLOR_PRIMARY + "; -fx-text-fill: white;");
        if (exporterButton != null) exporterButton.setStyle("-fx-background-color: " + StyleManager.COLOR_SUCCESS + "; -fx-text-fill: white;");
        if (actualiserButton != null) actualiserButton.setStyle("-fx-background-color: " + StyleManager.COLOR_WARNING + "; -fx-text-fill: white;");
        
        // Configurer la fenêtre en plein écran après que la scène soit prête
        Platform.runLater(() -> {
            javafx.stage.Stage jfxStage = (javafx.stage.Stage) catalogueTable.getScene().getWindow();
            if (jfxStage != null) {
                jfxStage.setMaximized(true);
                jfxStage.setMinWidth(1200);
                jfxStage.setMinHeight(800);
            }
        });
    }
    
    private void setupTableColumns() {
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        sujetColumn.setCellValueFactory(new PropertyValueFactory<>("sujet"));
        dureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
        responsableColumn.setCellValueFactory(new PropertyValueFactory<>("responsableNom"));
        
        // Pour la colonne candidatures, on affichera un nombre fictif pour l'instant
        candidaturesColumn.setCellValueFactory(cellData -> {
            // Ici on pourrait calculer le nombre réel de candidatures par stage
            return new javafx.beans.property.SimpleIntegerProperty(
                (int)(Math.random() * 10) + 1
            ).asObject();
        });
        
        catalogueTable.setItems(catalogueList);
    }
    
    private void loadCatalogue() {
        try {
            List<com.gestionstages.model.Stage> stages = stageDAO.getAllStages();
            catalogueList.setAll(stages);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger le catalogue: " + e.getMessage());
        }
    }
    
    
    
    @FXML
    private void handleRetour() {
        // Retourner à la scène précédente au lieu de fermer la fenêtre
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = catalogueTable.getScene();
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
    private void handleImprimer() {
        // Simulation d'impression
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Impression");
        alert.setHeaderText("Impression du catalogue");
        alert.setContentText("Le catalogue des stages a été envoyé à l'imprimante.\n\n" +
                            "Nombre de stages: " + catalogueList.size() + "\n" +
                            "Date d'impression: " + java.time.LocalDateTime.now().format(
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        alert.showAndWait();
    }
    
    @FXML
    private void handleExporter() {
        // Simulation d'export PDF
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export PDF");
        alert.setHeaderText("Export réussi");
        alert.setContentText("Le catalogue a été exporté en PDF.\n\n" +
                            "Fichier: catalogue_stages_" + 
                            java.time.LocalDate.now().format(
                                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf\n" +
                            "Emplacement: Documents/Stages/");
        alert.showAndWait();
    }
    
    @FXML
    private void handleActualiser() {
        loadCatalogue();
        
        showAlert("Actualisation", "Le catalogue a été actualisé avec succès!");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
