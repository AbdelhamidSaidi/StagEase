package com.gestionstages.dao;

import com.gestionstages.model.Stagiaire;
import com.gestionstages.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StagiaireDAO {
    
    public void ajouterStagiaire(Stagiaire stagiaire) throws SQLException {
        String sql = "INSERT INTO stagiaires (candidat_id, stage_id, numero_badge, date_arrivee_effective) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, stagiaire.getCandidatId());
            stmt.setInt(2, stagiaire.getStageId());
            stmt.setString(3, stagiaire.getNumeroBadge());
            stmt.setDate(4, stagiaire.getDateArriveeEffective() != null ? 
                Date.valueOf(stagiaire.getDateArriveeEffective()) : null);
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stagiaire.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public List<Stagiaire> getAllStagiaires() throws SQLException {
        List<Stagiaire> stagiaires = new ArrayList<>();
        String sql = "SELECT st.*, "
           + "CONCAT(c.prenom, ' ', c.nom) as candidat_nom, "
           + "s.titre as stage_titre, "
           + "s.reference as stage_reference "
           + "FROM stagiaires st "
           + "JOIN candidats c ON st.candidat_id = c.id "
           + "JOIN stages s ON st.stage_id = s.id "
           + "ORDER BY st.id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stagiaires.add(mapResultSetToStagiaire(rs));
            }
        }
        return stagiaires;
    }
    
    public Stagiaire getStagiaireById(int id) throws SQLException {
        String sql = "SELECT st.*, "
           + "CONCAT(c.prenom, ' ', c.nom) as candidat_nom, "
           + "s.titre as stage_titre, "
           + "s.reference as stage_reference "
           + "FROM stagiaires st "
           + "JOIN candidats c ON st.candidat_id = c.id "
           + "JOIN stages s ON st.stage_id = s.id "
           + "WHERE st.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStagiaire(rs);
                }
            }
        }
        return null;
    }
    
    public List<Stagiaire> getStagiairesByResponsable(int responsableId) throws SQLException {
        List<Stagiaire> stagiaires = new ArrayList<>();
        String sql = "SELECT st.*, "
           + "CONCAT(c.prenom, ' ', c.nom) as candidat_nom, "
           + "s.titre as stage_titre, "
           + "s.reference as stage_reference "
           + "FROM stagiaires st "
           + "JOIN candidats c ON st.candidat_id = c.id "
           + "JOIN stages s ON st.stage_id = s.id "
           + "WHERE s.responsable_id = ? "
           + "ORDER BY st.id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, responsableId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stagiaires.add(mapResultSetToStagiaire(rs));
                }
            }
        }
        return stagiaires;
    }
    
    public void updateStagiaire(Stagiaire stagiaire) throws SQLException {
        String sql = "UPDATE stagiaires SET numero_badge = ?, date_arrivee_effective = ?, "
           + "date_depart_effective = ?, jours_conge = ?, "
           + "note_travail = ?, note_comportement = ?, note_rapport = ? "
           + "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, stagiaire.getNumeroBadge());
            stmt.setDate(2, stagiaire.getDateArriveeEffective() != null ? 
                Date.valueOf(stagiaire.getDateArriveeEffective()) : null);
            stmt.setDate(3, stagiaire.getDateDepartEffective() != null ? 
                Date.valueOf(stagiaire.getDateDepartEffective()) : null);
            stmt.setInt(4, stagiaire.getJoursConge());
            
            if (stagiaire.getNoteTravail() != null) {
                stmt.setDouble(5, stagiaire.getNoteTravail());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }
            
            if (stagiaire.getNoteComportement() != null) {
                stmt.setDouble(6, stagiaire.getNoteComportement());
            } else {
                stmt.setNull(6, Types.DECIMAL);
            }
            
            if (stagiaire.getNoteRapport() != null) {
                stmt.setDouble(7, stagiaire.getNoteRapport());
            } else {
                stmt.setNull(7, Types.DECIMAL);
            }
            
            stmt.setInt(8, stagiaire.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public String genererNumeroBadge() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(numero_badge, 7) AS UNSIGNED)) FROM stagiaires WHERE numero_badge LIKE 'BADGE-%'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int maxNum = 0;
            if (rs.next()) {
                maxNum = rs.getInt(1);
            }
            
            return String.format("BADGE-%04d", maxNum + 1);
        }
    }
    
    private Stagiaire mapResultSetToStagiaire(ResultSet rs) throws SQLException {
        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(rs.getInt("id"));
        stagiaire.setCandidatId(rs.getInt("candidat_id"));
        stagiaire.setStageId(rs.getInt("stage_id"));
        stagiaire.setNumeroBadge(rs.getString("numero_badge"));
        
        Date dateArrivee = rs.getDate("date_arrivee_effective");
        if (dateArrivee != null) {
            stagiaire.setDateArriveeEffective(dateArrivee.toLocalDate());
        }
        
        Date dateDepart = rs.getDate("date_depart_effective");
        if (dateDepart != null) {
            stagiaire.setDateDepartEffective(dateDepart.toLocalDate());
        }
        
        stagiaire.setJoursConge(rs.getInt("jours_conge"));
        
        Double noteTravail = rs.getDouble("note_travail");
        if (!rs.wasNull()) {
            stagiaire.setNoteTravail(noteTravail);
        }
        
        Double noteComportement = rs.getDouble("note_comportement");
        if (!rs.wasNull()) {
            stagiaire.setNoteComportement(noteComportement);
        }
        
        Double noteRapport = rs.getDouble("note_rapport");
        if (!rs.wasNull()) {
            stagiaire.setNoteRapport(noteRapport);
        }
        
        stagiaire.setCandidatNom(rs.getString("candidat_nom"));
        stagiaire.setStageTitre(rs.getString("stage_titre"));
        stagiaire.setStageReference(rs.getString("stage_reference"));
        
        return stagiaire;
    }
}
