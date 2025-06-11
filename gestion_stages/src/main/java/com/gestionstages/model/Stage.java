package com.gestionstages.model;

public class Stage {
    private int id;
    private String reference;
    private String titre;
    private String sujet;
    private int duree; // en jours
    private int responsableId;
    private String responsableNom;
    
    // Constructeurs
    public Stage() {}
    
    public Stage(String reference, String titre, String sujet, int duree, int responsableId) {
        this.reference = reference;
        this.titre = titre;
        this.sujet = sujet;
        this.duree = duree;
        this.responsableId = responsableId;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    
    public int getResponsableId() { return responsableId; }
    public void setResponsableId(int responsableId) { this.responsableId = responsableId; }
    
    public String getResponsableNom() { return responsableNom; }
    public void setResponsableNom(String responsableNom) { this.responsableNom = responsableNom; }
    
    // Méthode toString pour afficher correctement dans les ComboBox
    @Override
    public String toString() {
        return reference + " - " + titre;
    }
}
