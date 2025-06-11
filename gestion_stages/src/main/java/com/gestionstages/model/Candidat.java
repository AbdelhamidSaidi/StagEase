package com.gestionstages.model;

import java.time.LocalDate;

public class Candidat extends Personne {
    private String cvFichier;
    private TypeEnvoi typeEnvoi;
    private LocalDate dateDebutSouhaitee;
    private int ecoleId;
    private String ecoleNom;

    public enum TypeEnvoi {
        EMAIL, POSTAL
    }

    // Constructeurs
    public Candidat() {
        super();
    }

    public Candidat(String nom, String prenom, String email, String telephone,
                    String cvFichier, TypeEnvoi typeEnvoi, LocalDate dateDebutSouhaitee, int ecoleId) {
        super(nom, prenom, telephone, email);
        this.cvFichier = cvFichier;
        this.typeEnvoi = typeEnvoi;
        this.dateDebutSouhaitee = dateDebutSouhaitee;
        this.ecoleId = ecoleId;
    }

    // Getters et Setters
    public String getCvFichier() { return cvFichier; }
    public void setCvFichier(String cvFichier) { this.cvFichier = cvFichier; }

    public TypeEnvoi getTypeEnvoi() { return typeEnvoi; }
    public void setTypeEnvoi(TypeEnvoi typeEnvoi) { this.typeEnvoi = typeEnvoi; }

    public LocalDate getDateDebutSouhaitee() { return dateDebutSouhaitee; }
    public void setDateDebutSouhaitee(LocalDate dateDebutSouhaitee) { this.dateDebutSouhaitee = dateDebutSouhaitee; }

    public int getEcoleId() { return ecoleId; }
    public void setEcoleId(int ecoleId) { this.ecoleId = ecoleId; }

    public String getEcoleNom() { return ecoleNom; }
    public void setEcoleNom(String ecoleNom) { this.ecoleNom = ecoleNom; }

    public String getNomComplet() {
        return getPrenom() + " " + getNom();
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}
