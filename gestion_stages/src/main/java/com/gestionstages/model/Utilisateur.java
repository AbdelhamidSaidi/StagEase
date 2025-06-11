    package com.gestionstages.model;
    
    public class Utilisateur extends Personne {
        private String motDePasse;
        private TypeUtilisateur typeUtilisateur;
    
        public enum TypeUtilisateur {
            RESPONSABLE_STAGES, RESPONSABLE_PERSONNEL
        }
    
        // Constructeurs
        public Utilisateur() {
            super();
        }
    
        public Utilisateur(String nom, String prenom, String email, String telephone,
                           String motDePasse, TypeUtilisateur typeUtilisateur) {
            super(nom, prenom, telephone, email);
            this.motDePasse = motDePasse;
            this.typeUtilisateur = typeUtilisateur;
        }
    
        // Getters et Setters
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
        public TypeUtilisateur getTypeUtilisateur() { return typeUtilisateur; }
        public void setTypeUtilisateur(TypeUtilisateur typeUtilisateur) { this.typeUtilisateur = typeUtilisateur; }
    
        public String getNomComplet() {
            return getPrenom() + " " + getNom();
        }
    }
