package com.gestionstages.model;

public class Ecole {
    private int id;
    private String nom;
    private String adresse;
    
    // Constructeurs
    public Ecole() {}
    
    public Ecole(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    @Override
    public String toString() {
        return nom;
    }
}
