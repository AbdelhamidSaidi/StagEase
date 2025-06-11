package com.gestionstages.dao;

import com.gestionstages.model.Utilisateur;
import com.gestionstages.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {
    
    public Utilisateur authentifier(String email, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
        }
        return null;
    }
    
    public List<Utilisateur> getAllUtilisateurs() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        }
        return utilisateurs;
    }
    
    public List<Utilisateur> getResponsablesStages() throws SQLException {
        List<Utilisateur> responsables = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE type_utilisateur = 'RESPONSABLE_STAGES' ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                responsables.add(mapResultSetToUtilisateur(rs));
            }
        }
        return responsables;
    }
    
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id"));
        utilisateur.setNom(rs.getString("nom"));
        utilisateur.setPrenom(rs.getString("prenom"));
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setTelephone(rs.getString("telephone"));
        utilisateur.setMotDePasse(rs.getString("mot_de_passe"));
        utilisateur.setTypeUtilisateur(Utilisateur.TypeUtilisateur.valueOf(rs.getString("type_utilisateur")));
        return utilisateur;
    }
}
