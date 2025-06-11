package com.gestionstages.controller;

import com.gestionstages.dao.*;
import com.gestionstages.model.*;
import com.gestionstages.util.EmailService;
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
import javafx.stage.FileChooser;
import javafx.print.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CandidaturesController implements Initializable {
    
    @FXML private VBox rootContainer;
    @FXML private VBox ajoutSection;
    @FXML private Label infoLabel;
    @FXML private HBox navBar;
    @FXML private Label sectionTitle;

    @FXML private TableView<Candidature> candidaturesTable;
    @FXML private TableColumn<Candidature, String> candidatColumn;
    @FXML private TableColumn<Candidature, String> stageColumn;
    @FXML private TableColumn<Candidature, String> statutColumn;
    @FXML private TableColumn<Candidature, String> typeColumn;
    @FXML private TableColumn<Candidature, String> conventionColumn;
    
    // Champs pour le formulaire d'ajout de candidat
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private ComboBox<Candidat.TypeEnvoi> typeEnvoiCombo;
    @FXML private ComboBox<Ecole> ecoleCombo;
    @FXML private DatePicker dateDebutPicker;
    @FXML private TextField cvField;
    
    // Sélection des stages
    @FXML private ComboBox<com.gestionstages.model.Stage> stage1Combo;
    @FXML private ComboBox<com.gestionstages.model.Stage> stage2Combo;
    @FXML private ComboBox<com.gestionstages.model.Stage> stage3Combo;
    
    @FXML private Button ajouterButton;
    @FXML private Button effacerButton;
    @FXML private Button selectionnerButton;
    @FXML private Button refuserButton;
    @FXML private Button accepterButton;
    @FXML private Button annulerButton;
    @FXML private Button retourButton;
    @FXML private Button parcourirButton;
    @FXML private Button envoyerConventionButton;
    @FXML private Button signerConventionButton;
    @FXML private Button imprimerConventionButton;
    @FXML private Button promouvoirButton;
    @FXML private Button supprimerButton;
    
    private CandidatureDAO candidatureDAO = new CandidatureDAO();
    private CandidatDAO candidatDAO = new CandidatDAO();
    private StageDAO stageDAO = new StageDAO();
    private StagiaireDAO stagiaireDAO = new StagiaireDAO();
    private EcoleDAO ecoleDAO = new EcoleDAO();
    private ObservableList<Candidature> candidaturesList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadCandidatures();
        loadCombos();
        setupButtons();
        
        // Initialiser la date à aujourd'hui
        dateDebutPicker.setValue(LocalDate.now().plusMonths(1));
        
        // Ajouter le bouton de retour
        if (retourButton != null) {
            retourButton.setOnAction(e -> handleRetour());
        }
        
        // Configurer la fenêtre en plein écran après que la scène soit prête
        Platform.runLater(() -> {
            javafx.stage.Stage jfxStage = (javafx.stage.Stage) candidaturesTable.getScene().getWindow();
            if (jfxStage != null) {
                jfxStage.setMaximized(true);
                jfxStage.setMinWidth(1200);
                jfxStage.setMinHeight(800);
            }
        });
    }
    
    private void setupTableColumns() {
        candidatColumn.setCellValueFactory(new PropertyValueFactory<>("candidatNom"));
        stageColumn.setCellValueFactory(new PropertyValueFactory<>("stageTitre"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        // Pour la colonne type, on affiche maintenant le type d'envoi du candidat
        typeColumn.setCellValueFactory(cellData -> {
            try {
                Candidat candidat = candidatDAO.getCandidatById(cellData.getValue().getCandidatId());
                if (candidat != null && candidat.getTypeEnvoi() != null) {
                    return new javafx.beans.property.SimpleStringProperty(candidat.getTypeEnvoi().toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        // Nouvelle colonne pour le statut de la convention
        conventionColumn.setCellValueFactory(cellData -> {
            Candidature candidature = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(candidature.getStatutConvention());
        });
        
        candidaturesTable.setItems(candidaturesList);
        
        // Listener pour activer/désactiver les boutons
        candidaturesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateButtonStates()
        );
    }
    
    private void loadCandidatures() {
        try {
            Utilisateur currentUser = SessionManager.getCurrentUser();
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                candidaturesList.setAll(candidatureDAO.getCandidaturesByResponsable(currentUser.getId()));
            } else {
                candidaturesList.setAll(candidatureDAO.getAllCandidatures());
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les candidatures: " + e.getMessage());
        }
    }
    
    private void loadCombos() {
        try {
            // Charger les écoles
            ObservableList<Ecole> ecoles = FXCollections.observableArrayList(
                ecoleDAO.getAllEcoles()
            );
            ecoleCombo.setItems(ecoles);
            
            // Charger les types d'envoi
            ObservableList<Candidat.TypeEnvoi> typesEnvoi = 
                FXCollections.observableArrayList(Candidat.TypeEnvoi.values());
            typeEnvoiCombo.setItems(typesEnvoi);
            typeEnvoiCombo.setValue(Candidat.TypeEnvoi.EMAIL); // Valeur par défaut
            
            // Charger les stages selon le type d'utilisateur
            Utilisateur currentUser = SessionManager.getCurrentUser();
            ObservableList<com.gestionstages.model.Stage> stages;
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                stages = FXCollections.observableArrayList(
                    stageDAO.getStagesByResponsable(currentUser.getId())
                );
            } else {
                stages = FXCollections.observableArrayList(stageDAO.getAllStages());
            }
            
            stage1Combo.setItems(stages);
            stage2Combo.setItems(stages);
            stage3Combo.setItems(stages);
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }
    
    private void setupButtons() {
        Utilisateur currentUser = SessionManager.getCurrentUser();
        
        if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
            // Responsable de stages : peut seulement sélectionner/annuler ses candidatures
            ajoutSection.setVisible(false);
            ajoutSection.setManaged(false);
            accepterButton.setVisible(false);
            envoyerConventionButton.setVisible(false);
            signerConventionButton.setVisible(false);
            imprimerConventionButton.setVisible(false);
            promouvoirButton.setVisible(false);
            refuserButton.setVisible(false); // Retirer le bouton refuser pour le responsable de stages
            annulerButton.setText("Annuler Sélection");
            supprimerButton.setVisible(false);
            
            // Afficher les informations pour le responsable de stages
            infoLabel.setText("En tant que Responsable de Stages, vous pouvez sélectionner ou annuler la sélection des candidats pour VOS stages uniquement.");
            
            // Changer le titre de la section
            if (sectionTitle != null) {
                sectionTitle.setText("Gestion des Candidatures pour vos Stages");
            }
        
        } else {
            // Responsable personnel : peut tout faire sauf sélectionner
            selectionnerButton.setVisible(false);
            refuserButton.setText("Refuser");
            annulerButton.setVisible(false); // Ne pas permettre la suppression directe
            envoyerConventionButton.setVisible(false); // Retirer le bouton envoyer convention
            
            // Ajouter le bouton pour promouvoir en stagiaire
            if (promouvoirButton != null) {
                promouvoirButton.setVisible(true);
                promouvoirButton.setStyle("-fx-background-color: " + StyleManager.COLOR_PRIMARY + "; -fx-text-fill: white;");
            }
            
            // Ajouter le bouton pour supprimer les candidatures
            if (supprimerButton != null) {
                supprimerButton.setVisible(true);
                supprimerButton.setStyle("-fx-background-color: " + StyleManager.COLOR_DANGER + "; -fx-text-fill: white;");
            }
            
            // Afficher les informations pour le responsable personnel
            infoLabel.setText("En tant que Responsable du Personnel, vous gérez tous les candidats et pouvez ajouter de nouveaux candidats (max 3 candidatures par candidat).");
        }
        
        // Appliquer les styles
        if (selectionnerButton != null) selectionnerButton.setStyle("-fx-background-color: " + StyleManager.COLOR_SUCCESS + "; -fx-text-fill: white;");
        if (refuserButton != null) refuserButton.setStyle("-fx-background-color: " + StyleManager.COLOR_DANGER + "; -fx-text-fill: white;");
        if (signerConventionButton != null) signerConventionButton.setStyle("-fx-background-color: " + StyleManager.COLOR_WARNING + "; -fx-text-fill: white;");
        if (accepterButton != null) accepterButton.setStyle("-fx-background-color: " + StyleManager.COLOR_SUCCESS + "; -fx-text-fill: white;");
        if (annulerButton != null) annulerButton.setStyle("-fx-background-color: " + StyleManager.COLOR_DANGER + "; -fx-text-fill: white;");
        if (ajouterButton != null) ajouterButton.setStyle("-fx-background-color: " + StyleManager.COLOR_SUCCESS + "; -fx-text-fill: white;");
        if (effacerButton != null) effacerButton.setStyle("-fx-background-color: " + StyleManager.COLOR_PRIMARY + "; -fx-text-fill: white;");
        if (imprimerConventionButton != null) imprimerConventionButton.setStyle("-fx-background-color: " + StyleManager.COLOR_PRIMARY + "; -fx-text-fill: white;");
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selected != null;
        
        Utilisateur currentUser = SessionManager.getCurrentUser();
        boolean isResponsableStages = currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES;
        
        if (hasSelection) {
            Candidature.StatutCandidature statut = selected.getStatut();
            
            if (isResponsableStages) {
                // Responsable de stages
                if (selectionnerButton != null) {
                    try {
                        // Vérifier si un autre candidat est déjà sélectionné pour ce stage
                        boolean stageDejaSelectionne = candidatureDAO.stageDejaSelectionne(selected.getStageId());
                        selectionnerButton.setDisable(statut != Candidature.StatutCandidature.EN_ATTENTE || stageDejaSelectionne);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        selectionnerButton.setDisable(true);
                    }
                }
                if (annulerButton != null) annulerButton.setDisable(statut != Candidature.StatutCandidature.SELECTIONNE);
            } else {
                // Responsable personnel
                if (refuserButton != null) refuserButton.setDisable(statut != Candidature.StatutCandidature.SELECTIONNE);
                if (signerConventionButton != null) signerConventionButton.setDisable(statut != Candidature.StatutCandidature.SELECTIONNE || 
                                   selected.isConventionSignee());
                if (accepterButton != null) accepterButton.setDisable(statut != Candidature.StatutCandidature.SELECTIONNE || 
                                   !selected.isConventionSignee());
                if (imprimerConventionButton != null) imprimerConventionButton.setDisable(
                    statut != Candidature.StatutCandidature.ACCEPTE && 
                    (statut != Candidature.StatutCandidature.SELECTIONNE || !selected.isConventionSignee())
                );
                if (promouvoirButton != null) promouvoirButton.setDisable(
                    statut != Candidature.StatutCandidature.ACCEPTE || 
                    !selected.isConventionSignee()
                );
                if (supprimerButton != null) {
                    try {
                        // Vérifier si toutes les candidatures du candidat sont en attente
                        boolean peutSupprimer = candidatureDAO.toutesLesCandidaturesEnAttente(selected.getCandidatId());
                        supprimerButton.setDisable(!peutSupprimer);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        supprimerButton.setDisable(true);
                    }
                }
            }
        } else {
            // Aucune sélection
            if (selectionnerButton != null) selectionnerButton.setDisable(true);
            if (refuserButton != null) refuserButton.setDisable(true);
            if (signerConventionButton != null) signerConventionButton.setDisable(true);
            if (accepterButton != null) accepterButton.setDisable(true);
            if (annulerButton != null) annulerButton.setDisable(true);
            if (imprimerConventionButton != null) imprimerConventionButton.setDisable(true);
            if (promouvoirButton != null) promouvoirButton.setDisable(true);
            if (supprimerButton != null) supprimerButton.setDisable(true);
        }
    }
    
    @FXML
    private void handleSignerConvention() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        if (selected.getStatut() != Candidature.StatutCandidature.SELECTIONNE) {
            showAlert("Erreur", "Seuls les candidats sélectionnés peuvent avoir leur convention signée");
            return;
        }
        
        try {
            candidatureDAO.signerConvention(selected.getId());
            loadCandidatures();
            showAlert("Succès", "Convention marquée comme signée!");
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de marquer la convention comme signée: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleImprimerConvention() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        if (!selected.isConventionSignee() && selected.getStatut() != Candidature.StatutCandidature.ACCEPTE) {
            showAlert("Erreur", "La convention doit être signée ou le candidat accepté pour l'impression");
            return;
        }
        
        try {
            // Créer le contenu de la convention
            TextFlow conventionContent = createConventionContent(selected);
            
            // Configurer l'impression
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean showDialog = job.showPrintDialog(candidaturesTable.getScene().getWindow());
                if (showDialog) {
                    // Créer une scène temporaire pour l'impression
                    javafx.scene.Scene scene = new javafx.scene.Scene(conventionContent, 595, 842); // A4 size
                    
                    if (job.printPage(conventionContent)) {
                        job.endJob();
                        showAlert("Succès", "Convention imprimée avec succès!");
                    } else {
                        showAlert("Erreur", "Échec de l'impression");
                    }
                }
            } else {
                showAlert("Erreur", "Aucune imprimante disponible");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'imprimer la convention: " + e.getMessage());
        }
    }
 

@FXML
private void handleEnvoyerConvention() {
    // code pour envoyer la convention
}

    @FXML
    private void handlePromouvoir() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        if (selected.getStatut() != Candidature.StatutCandidature.ACCEPTE || !selected.isConventionSignee()) {
            showAlert("Erreur", "Seuls les candidats acceptés avec convention signée peuvent être promus en stagiaire");
            return;
        }
        
        try {
            // Ouvrir une fenêtre modale pour saisir les informations du stagiaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/promouvoir-stagiaire.fxml"));
            Parent root = loader.load();
            
            PromouvoirStagiaireController controller = loader.getController();
            controller.setCandidature(selected);
            controller.setStagiaireDAO(stagiaireDAO);
            controller.setCandidatureDAO(candidatureDAO);
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Promouvoir en stagiaire");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Recharger les candidatures après la promotion
            loadCandidatures();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de promotion: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimer() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            // Vérifier si toutes les candidatures du candidat sont en attente
            boolean peutSupprimer = candidatureDAO.toutesLesCandidaturesEnAttente(selected.getCandidatId());
            if (!peutSupprimer) {
                showAlert("Erreur", "Impossible de supprimer cette candidature car le candidat a d'autres candidatures non en attente");
                return;
            }
            
            // Confirmation de suppression
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer la candidature");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature ?");
            
            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                candidatureDAO.supprimerCandidature(selected.getId());
                loadCandidatures();
                showAlert("Succès", "Candidature supprimée avec succès");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de supprimer la candidature: " + e.getMessage());
        }
    }
    
    private TextFlow createConventionContent(Candidature candidature) throws SQLException {
        TextFlow content = new TextFlow();
        content.setPrefWidth(595); // A4 width
        content.setLineSpacing(1.5);
        
        // Récupérer les informations nécessaires
        Candidat candidat = candidatDAO.getCandidatById(candidature.getCandidatId());
        com.gestionstages.model.Stage stage = stageDAO.getStageById(candidature.getStageId());
        Ecole ecole = ecoleDAO.getEcoleById(candidat.getEcoleId());
        
        // Titre
        Text title = new Text("CONVENTION DE STAGE\n\n");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        // En-tête
        Text header = new Text(
            "Entre les soussignés :\n\n" +
            "L'entreprise, représentée par " + stage.getResponsableNom() + "\n" +
            "ET\n" +
            "L'établissement d'enseignement : " + ecole.getNom() + "\n" +
            "Adresse : " + ecole.getAdresse() + "\n" +
            "ET\n" +
            "Le stagiaire : " + candidat.getNomComplet() + "\n" +
            "Email : " + candidat.getEmail() + "\n" +
            "Téléphone : " + candidat.getTelephone() + "\n\n"
        );
        
        // Détails du stage
        Text stageDetails = new Text(
            "Il a été convenu ce qui suit :\n\n" +
            "Article 1 : Objet de la convention\n" +
            "La présente convention a pour objet de définir les conditions dans lesquelles le stagiaire effectuera son stage au sein de l'entreprise.\n\n" +
            "Article 2 : Nature du stage\n" +
            "Référence : " + stage.getReference() + "\n" +
            "Titre : " + stage.getTitre() + "\n" +
            "Sujet : " + stage.getSujet() + "\n\n" +
            "Article 3 : Durée du stage\n" +
            "Le stage aura une durée de " + stage.getDuree() + " jours.\n" +
            "Date de début souhaitée : " + candidat.getDateDebutSouhaitee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n" +
            "Article 4 : Statut du stagiaire\n" +
            "Le stagiaire, pendant la durée de son stage, demeure étudiant de son établissement d'enseignement.\n\n"
        );
        
        // Signatures
        Text signatures = new Text(
            "Fait en trois exemplaires à _________________, le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n" +
            "Pour l'entreprise                Pour l'établissement                Pour le stagiaire\n" +
            "(signature et cachet)            (signature et cachet)               (signature)\n\n" +
            "____________________           ____________________              ____________________\n"
        );
        
        // Ajouter tous les éléments au TextFlow
        content.getChildren().addAll(title, header, stageDetails, signatures);
        
        return content;
    }
    
    @FXML
    private void handleRetour() {
        // Retourner à la scène précédente au lieu de fermer la fenêtre
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = candidaturesTable.getScene();
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
    private void handleParcourir() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un CV");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documents PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Documents Word", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        
        File selectedFile = fileChooser.showOpenDialog(cvField.getScene().getWindow());
        if (selectedFile != null) {
            cvField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    @FXML
    private void handleEffacer() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        typeEnvoiCombo.setValue(Candidat.TypeEnvoi.EMAIL);
        ecoleCombo.setValue(null);
        dateDebutPicker.setValue(LocalDate.now().plusMonths(1));
        cvField.clear();
        stage1Combo.setValue(null);
        stage2Combo.setValue(null);
        stage3Combo.setValue(null);
    }
    
    @FXML
    private void handleAjouter() {
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            // Vérifier si l'email existe déjà
            if (candidatDAO.emailExists(emailField.getText().trim())) {
                // Proposer d'utiliser le candidat existant
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Email existant");
                confirmation.setHeaderText("Un candidat avec cet email existe déjà");
                confirmation.setContentText("Voulez-vous ajouter des candidatures pour ce candidat existant?");
                
                if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    // Utiliser le candidat existant
                    Candidat candidatExistant = candidatDAO.getCandidatByEmail(emailField.getText().trim());
                    ajouterCandidatures(candidatExistant.getId());
                }
                return;
            }
            
            // Créer le nouveau candidat
            Candidat candidat = new Candidat(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                telephoneField.getText().trim(),
                cvField.getText().trim(),
                typeEnvoiCombo.getValue(),
                dateDebutPicker.getValue(),
                ecoleCombo.getValue().getId()
            );
            
            // Ajouter le candidat à la base de données
            candidatDAO.ajouterCandidat(candidat);
            
            // Ajouter les candidatures
            ajouterCandidatures(candidat.getId());
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'ajouter le candidat: " + e.getMessage());
        }
    }
    
    private void ajouterCandidatures(int candidatId) throws SQLException {
        // Vérifier la limite de 3 candidatures par candidat
        int candidaturesExistantes = candidatureDAO.countCandidaturesByCandidat(candidatId);
        
        // Compter les nouvelles candidatures à ajouter
        int nouvellesCandidatures = 0;
        if (stage1Combo.getValue() != null) nouvellesCandidatures++;
        if (stage2Combo.getValue() != null) nouvellesCandidatures++;
        if (stage3Combo.getValue() != null) nouvellesCandidatures++;
        
        if (candidaturesExistantes + nouvellesCandidatures > 3) {
            showAlert("Erreur", "Ce candidat dépasserait la limite de 3 candidatures. " +
                     "Candidatures existantes: " + candidaturesExistantes + 
                     ", Nouvelles: " + nouvellesCandidatures);
            return;
        }
        
        // Ajouter les candidatures pour chaque stage sélectionné
        int candidaturesAjoutees = 0;
        
        if (stage1Combo.getValue() != null) {
            Candidature candidature = new Candidature(candidatId, stage1Combo.getValue().getId());
            candidatureDAO.ajouterCandidature(candidature);
            candidaturesAjoutees++;
        }
        
        if (stage2Combo.getValue() != null) {
            Candidature candidature = new Candidature(candidatId, stage2Combo.getValue().getId());
            candidatureDAO.ajouterCandidature(candidature);
            candidaturesAjoutees++;
        }
        
        if (stage3Combo.getValue() != null) {
            Candidature candidature = new Candidature(candidatId, stage3Combo.getValue().getId());
            candidatureDAO.ajouterCandidature(candidature);
            candidaturesAjoutees++;
        }
        
        // Recharger les candidatures
        loadCandidatures();
        
        // Effacer le formulaire
        handleEffacer();
        
        showAlert("Succès", "Candidat traité avec " + candidaturesAjoutees + " candidature(s) ajoutée(s)!");
    }
    
    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();
        
        if (nomField.getText().trim().isEmpty()) {
            erreurs.append("- Le nom est obligatoire\n");
        }
        
        if (prenomField.getText().trim().isEmpty()) {
            erreurs.append("- Le prénom est obligatoire\n");
        }
        
        if (emailField.getText().trim().isEmpty()) {
            erreurs.append("- L'email est obligatoire\n");
        } else if (!emailField.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            erreurs.append("- Format d'email invalide\n");
        }
        
        if (typeEnvoiCombo.getValue() == null) {
            erreurs.append("- Veuillez sélectionner un type d'envoi\n");
        }
        
        if (ecoleCombo.getValue() == null) {
            erreurs.append("- Veuillez sélectionner une école\n");
        }
        
        if (dateDebutPicker.getValue() == null) {
            erreurs.append("- La date de début souhaitée est obligatoire\n");
        }
        
        if (stage1Combo.getValue() == null && stage2Combo.getValue() == null && stage3Combo.getValue() == null) {
            erreurs.append("- Veuillez sélectionner au moins un stage\n");
        }
        
        // Vérifier que les stages sélectionnés sont différents
        if (stage1Combo.getValue() != null && stage2Combo.getValue() != null && 
            stage1Combo.getValue().getId() == stage2Combo.getValue().getId()) {
            erreurs.append("- Vous avez sélectionné le même stage deux fois (1 et 2)\n");
        }
        
        if (stage1Combo.getValue() != null && stage3Combo.getValue() != null && 
            stage1Combo.getValue().getId() == stage3Combo.getValue().getId()) {
            erreurs.append("- Vous avez sélectionné le même stage deux fois (1 et 3)\n");
        }
        
        if (stage2Combo.getValue() != null && stage3Combo.getValue() != null && 
            stage2Combo.getValue().getId() == stage3Combo.getValue().getId()) {
            erreurs.append("- Vous avez sélectionné le même stage deux fois (2 et 3)\n");
        }
        
        if (erreurs.length() > 0) {
            showAlert("Erreur de validation", "Veuillez corriger les erreurs suivantes:\n" + erreurs.toString());
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleSelectionner() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            // Vérifier que le candidat n'est pas déjà sélectionné ailleurs
            if (candidatureDAO.candidatDejaSelectionne(selected.getCandidatId())) {
                showAlert("Erreur", "Ce candidat est déjà sélectionné pour un autre stage");
                return;
            }
            
            // Vérifier qu'aucun autre candidat n'est déjà sélectionné pour ce stage
            if (candidatureDAO.stageDejaSelectionne(selected.getStageId())) {
                showAlert("Erreur", "Un autre candidat est déjà sélectionné pour ce stage");
                return;
            }
            
            candidatureDAO.updateStatutCandidature(selected.getId(), Candidature.StatutCandidature.SELECTIONNE);
            
            // Envoyer email au responsable du personnel (simulé)
            EmailService.envoyerNotificationSelection(selected);
            
            loadCandidatures();
            showAlert("Succès", "Candidat sélectionné! Email envoyé au responsable du personnel.");
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de sélectionner le candidat: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRefuser() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            candidatureDAO.updateStatutCandidature(selected.getId(), Candidature.StatutCandidature.REFUSE);
            loadCandidatures();
            
            Utilisateur currentUser = SessionManager.getCurrentUser();
            if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
                showAlert("Succès", "Candidature refusée");
            } else {
                showAlert("Succès", "Candidature marquée comme refusée par le candidat");
            }
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de refuser la candidature: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAccepter() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            candidatureDAO.updateStatutCandidature(selected.getId(), Candidature.StatutCandidature.ACCEPTE);
            loadCandidatures();
            showAlert("Succès", "Candidat accepté! Vous pouvez maintenant imprimer la convention.");
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'accepter le candidat: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAnnuler() {
        Candidature selected = candidaturesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Utilisateur currentUser = SessionManager.getCurrentUser();
        
        if (currentUser.getTypeUtilisateur() == Utilisateur.TypeUtilisateur.RESPONSABLE_STAGES) {
            // Pour le responsable de stages, c'est juste annuler la sélection
            try {
                candidatureDAO.updateStatutCandidature(selected.getId(), Candidature.StatutCandidature.EN_ATTENTE);
                loadCandidatures();
                showAlert("Succès", "Sélection annulée");
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible d'annuler la sélection: " + e.getMessage());
            }
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
