package com.gestionstages.dao;

import com.gestionstages.model.Candidat;
import com.gestionstages.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatDAO {
    
    public void ajouterCandidat(Candidat candidat) throws SQLException {
        String sql = "INSERT INTO candidats (nom, prenom, email, telephone, cv_fichier, type_envoi, date_debut_souhaitee, ecole_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, candidat.getNom());
            stmt.setString(2, candidat.getPrenom());
            stmt.setString(3, candidat.getEmail());
            stmt.setString(4, candidat.getTelephone());
            stmt.setString(5, candidat.getCvFichier());
            stmt.setString(6, candidat.getTypeEnvoi().name());
            stmt.setDate(7, Date.valueOf(candidat.getDateDebutSouhaitee()));
            stmt.setInt(8, candidat.getEcoleId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout du candidat, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    candidat.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de l'ajout du candidat, aucun ID généré.");
                }
            }
        }
    }
    
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidats WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public Candidat getCandidatByEmail(String email) throws SQLException {
        String sql = "SELECT c.*, e.nom as ecole_nom "
           + "FROM candidats c "
           + "LEFT JOIN ecoles e ON c.ecole_id = e.id "
           + "WHERE c.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCandidat(rs);
                }
            }
        }
        return null;
    }
    
    public int getLastInsertedId() throws SQLException {
        String sql = "SELECT LAST_INSERT_ID()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    public List<Candidat> getAllCandidats() throws SQLException {
        List<Candidat> candidats = new ArrayList<>();
        String sql = "SELECT c.*, e.nom as ecole_nom "
           + "FROM candidats c "
           + "LEFT JOIN ecoles e ON c.ecole_id = e.id "
           + "ORDER BY c.nom, c.prenom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                candidats.add(mapResultSetToCandidat(rs));
            }
        }
        return candidats;
    }
    
    public Candidat getCandidatById(int id) throws SQLException {
        String sql = "SELECT c.*, e.nom as ecole_nom "
           + "FROM candidats c "
           + "LEFT JOIN ecoles e ON c.ecole_id = e.id "
           + "WHERE c.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCandidat(rs);
                }
            }
        }
        return null;
    }
    
    private Candidat mapResultSetToCandidat(ResultSet rs) throws SQLException {
        Candidat candidat = new Candidat();
        candidat.setId(rs.getInt("id"));
        candidat.setNom(rs.getString("nom"));
        candidat.setPrenom(rs.getString("prenom"));
        candidat.setEmail(rs.getString("email"));
        candidat.setTelephone(rs.getString("telephone"));
        candidat.setCvFichier(rs.getString("cv_fichier"));
        
        String typeEnvoi = rs.getString("type_envoi");
        if (typeEnvoi != null) {
            candidat.setTypeEnvoi(Candidat.TypeEnvoi.valueOf(typeEnvoi));
        }
        
        Date dateDebut = rs.getDate("date_debut_souhaitee");
        if (dateDebut != null) {
            candidat.setDateDebutSouhaitee(dateDebut.toLocalDate());
        }
        
        candidat.setEcoleId(rs.getInt("ecole_id"));
        candidat.setEcoleNom(rs.getString("ecole_nom"));
        return candidat;
    }
}
