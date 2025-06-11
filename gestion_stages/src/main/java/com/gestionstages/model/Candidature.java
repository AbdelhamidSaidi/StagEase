package com.gestionstages.model;

import java.time.LocalDate;

public class Candidature {
    private int id;
    private int candidatId;
    private int stageId;
    private StatutCandidature statut;
    private String candidatNom;
    private String stageTitre;
    
    // Gestion de la convention
    private LocalDate dateEnvoiConvention;
    private boolean conventionSignee;
    private String fichierConvention;
    private LocalDate dateSignatureConvention;
    
    public enum StatutCandidature {
        EN_ATTENTE, SELECTIONNE, ACCEPTE, REFUSE, ANNULE
    }
    
    // Constructeurs
    public Candidature() {}
    
    public Candidature(int candidatId, int stageId) {
        this.candidatId = candidatId;
        this.stageId = stageId;
        this.statut = StatutCandidature.EN_ATTENTE;
        this.conventionSignee = false;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCandidatId() { return candidatId; }
    public void setCandidatId(int candidatId) { this.candidatId = candidatId; }
    
    public int getStageId() { return stageId; }
    public void setStageId(int stageId) { this.stageId = stageId; }
    
    public StatutCandidature getStatut() { return statut; }
    public void setStatut(StatutCandidature statut) { this.statut = statut; }
    
    public String getCandidatNom() { return candidatNom; }
    public void setCandidatNom(String candidatNom) { this.candidatNom = candidatNom; }
    
    public String getStageTitre() { return stageTitre; }
    public void setStageTitre(String stageTitre) { this.stageTitre = stageTitre; }
    
    // Getters et Setters pour la convention
    public LocalDate getDateEnvoiConvention() { return dateEnvoiConvention; }
    public void setDateEnvoiConvention(LocalDate dateEnvoiConvention) { this.dateEnvoiConvention = dateEnvoiConvention; }
    
    public boolean isConventionSignee() { return conventionSignee; }
    public void setConventionSignee(boolean conventionSignee) { this.conventionSignee = conventionSignee; }
    
    public String getFichierConvention() { return fichierConvention; }
    public void setFichierConvention(String fichierConvention) { this.fichierConvention = fichierConvention; }
    
    public LocalDate getDateSignatureConvention() { return dateSignatureConvention; }
    public void setDateSignatureConvention(LocalDate dateSignatureConvention) { this.dateSignatureConvention = dateSignatureConvention; }
    
    // Méthodes utilitaires
    public boolean peutEtreAcceptee() {
        return statut == StatutCandidature.SELECTIONNE && conventionSignee;
    }
    
    public String getStatutConvention() {
        if (dateEnvoiConvention == null) {
            return "Non envoyée";
        } else if (!conventionSignee) {
            return "En attente de signature";
        } else {
            return "Signée le " + dateSignatureConvention;
        }
    }
}
