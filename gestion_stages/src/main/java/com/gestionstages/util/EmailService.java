package com.gestionstages.util;

import com.gestionstages.model.Candidature;

public class EmailService {
    
    public static void envoyerNotificationSelection(Candidature candidature) {
        // Simulation d'envoi d'email
        System.out.println("=== EMAIL ENVOYÉ ===");
        System.out.println("À: Responsable du Personnel");
        System.out.println("Sujet: Candidat sélectionné pour un stage");
        System.out.println("Message: Le candidat " + candidature.getCandidatNom() + 
                          " a été sélectionné pour le stage: " + candidature.getStageTitre());
        System.out.println("Veuillez contacter le candidat pour confirmation.");
        System.out.println("==================");
    }
    
    public static void envoyerNotificationAnnulation(Candidature candidature) {
        // Simulation d'envoi d'email
        System.out.println("=== EMAIL ENVOYÉ ===");
        System.out.println("À: Responsable de Stage");
        System.out.println("Sujet: Candidature annulée");
        System.out.println("Message: La candidature de " + candidature.getCandidatNom() + 
                          " pour le stage " + candidature.getStageTitre() + " a été annulée.");
        System.out.println("Le candidat a refusé la proposition.");
        System.out.println("==================");
    }
    
    public static void envoyerNotificationAcceptation(Candidature candidature) {
        // Simulation d'envoi d'email
        System.out.println("=== EMAIL ENVOYÉ ===");
        System.out.println("À: École du candidat");
        System.out.println("Sujet: Convention de stage à signer");
        System.out.println("Message: Veuillez signer la convention de stage pour " + 
                          candidature.getCandidatNom() + " - Stage: " + candidature.getStageTitre());
        System.out.println("==================");
    }
}
