package com.gestionstages.model;

import java.time.LocalDate;

public class Stagiaire {
    private int id;
    private int candidatId;
    private int stageId;
    private String numeroBadge;
    private LocalDate dateArriveeEffective;
    private LocalDate dateDepartEffective;
    private int joursConge;
    private Double noteTravail;
    private Double noteComportement;
    private Double noteRapport;
    
    // Champs pour l'affichage
    private String candidatNom;
    private String stageTitre;
    private String stageReference;
    
    // Constructeurs
    public Stagiaire() {}
    
    public Stagiaire(int candidatId, int stageId, String numeroBadge) {
        this.candidatId = candidatId;
        this.stageId = stageId;
        this.numeroBadge = numeroBadge;
        this.joursConge = 0;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCandidatId() { return candidatId; }
    public void setCandidatId(int candidatId) { this.candidatId = candidatId; }
    
    public int getStageId() { return stageId; }
    public void setStageId(int stageId) { this.stageId = stageId; }
    
    public String getNumeroBadge() { return numeroBadge; }
    public void setNumeroBadge(String numeroBadge) { this.numeroBadge = numeroBadge; }
    
    public LocalDate getDateArriveeEffective() { return dateArriveeEffective; }
    public void setDateArriveeEffective(LocalDate dateArriveeEffective) { this.dateArriveeEffective = dateArriveeEffective; }
    
    public LocalDate getDateDepartEffective() { return dateDepartEffective; }
    public void setDateDepartEffective(LocalDate dateDepartEffective) { this.dateDepartEffective = dateDepartEffective; }
    
    public int getJoursConge() { return joursConge; }
    public void setJoursConge(int joursConge) { this.joursConge = joursConge; }
    
    public Double getNoteTravail() { return noteTravail; }
    public void setNoteTravail(Double noteTravail) { this.noteTravail = noteTravail; }
    
    public Double getNoteComportement() { return noteComportement; }
    public void setNoteComportement(Double noteComportement) { this.noteComportement = noteComportement; }
    
    public Double getNoteRapport() { return noteRapport; }
    public void setNoteRapport(Double noteRapport) { this.noteRapport = noteRapport; }
    
    public String getCandidatNom() { return candidatNom; }
    public void setCandidatNom(String candidatNom) { this.candidatNom = candidatNom; }
    
    public String getStageTitre() { return stageTitre; }
    public void setStageTitre(String stageTitre) { this.stageTitre = stageTitre; }
    
    public String getStageReference() { return stageReference; }
    public void setStageReference(String stageReference) { this.stageReference = stageReference; }
    
    public Double getMoyenneNotes() {
        if (noteTravail != null && noteComportement != null && noteRapport != null) {
            return (noteTravail + noteComportement + noteRapport) / 3.0;
        }
        return null;
    }
}
