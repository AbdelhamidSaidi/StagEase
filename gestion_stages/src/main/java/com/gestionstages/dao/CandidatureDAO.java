package com.gestionstages.dao;

import com.gestionstages.model.Candidature;
import com.gestionstages.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatureDAO {
    
    public void ajouterCandidature(Candidature candidature) throws SQLException {
        String sql = "INSERT INTO candidatures (candidat_id, stage_id, statut) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidature.getCandidatId());
            stmt.setInt(2, candidature.getStageId());
            stmt.setString(3, candidature.getStatut().name());
            
            stmt.executeUpdate();
        }
    }
    
    public List<Candidature> getAllCandidatures() throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String sql = "SELECT c.*, "
           + "CONCAT(cand.prenom, ' ', cand.nom) as candidat_nom, "
           + "s.titre as stage_titre, "
           + "cand.type_envoi "
           + "FROM candidatures c "
           + "JOIN candidats cand ON c.candidat_id = cand.id "
           + "JOIN stages s ON c.stage_id = s.id "
           + "ORDER BY c.id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                candidatures.add(mapResultSetToCandidature(rs));
            }
        }
        return candidatures;
    }
    
    public List<Candidature> getCandidaturesByStage(int stageId) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String sql = "SELECT c.*, "
           + "CONCAT(cand.prenom, ' ', cand.nom) as candidat_nom, "
           + "s.titre as stage_titre, "
           + "cand.type_envoi "
           + "FROM candidatures c "
           + "JOIN candidats cand ON c.candidat_id = cand.id "
           + "JOIN stages s ON c.stage_id = s.id "
           + "WHERE c.stage_id = ? "
           + "ORDER BY c.id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    candidatures.add(mapResultSetToCandidature(rs));
                }
            }
        }
        return candidatures;
    }
    
    public int countCandidaturesByStage(int stageId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidatures WHERE stage_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public List<Candidature> getCandidaturesByResponsable(int responsableId) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String sql = "SELECT c.*, "
           + "CONCAT(cand.prenom, ' ', cand.nom) as candidat_nom, "
           + "s.titre as stage_titre, "
           + "cand.type_envoi "
           + "FROM candidatures c "
           + "JOIN candidats cand ON c.candidat_id = cand.id "
           + "JOIN stages s ON c.stage_id = s.id "
           + "WHERE s.responsable_id = ? "
           + "ORDER BY c.id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, responsableId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    candidatures.add(mapResultSetToCandidature(rs));
                }
            }
        }
        return candidatures;
    }
    
    public void updateStatutCandidature(int candidatureId, Candidature.StatutCandidature statut) throws SQLException {
        String sql = "UPDATE candidatures SET statut = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, statut.name());
            stmt.setInt(2, candidatureId);
            
            stmt.executeUpdate();
        }
    }
    
    public void envoyerConvention(int candidatureId, String fichierConvention) throws SQLException {
        String sql = "UPDATE candidatures SET date_envoi_convention = ?, fichier_convention = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            stmt.setString(2, fichierConvention);
            stmt.setInt(3, candidatureId);
            
            stmt.executeUpdate();
        }
    }
    
    public void signerConvention(int candidatureId) throws SQLException {
        String sql = "UPDATE candidatures SET convention_signee = TRUE, date_signature_convention = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            stmt.setInt(2, candidatureId);
            
            stmt.executeUpdate();
        }
    }
    
    public void supprimerCandidature(int candidatureId) throws SQLException {
        String sql = "DELETE FROM candidatures WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidatureId);
            stmt.executeUpdate();
        }
    }
    
    public int countCandidaturesByCandidat(int candidatId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidatures WHERE candidat_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidatId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public boolean candidatDejaSelectionne(int candidatId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidatures WHERE candidat_id = ? AND statut IN ('SELECTIONNE', 'ACCEPTE')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidatId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean stageDejaSelectionne(int stageId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidatures WHERE stage_id = ? AND statut IN ('SELECTIONNE', 'ACCEPTE')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public List<Candidature> getCandidaturesAvecConventionSignee() throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String sql = "SELECT c.*, "
           + "CONCAT(cand.prenom, ' ', cand.nom) as candidat_nom, "
           + "s.titre as stage_titre, "
           + "cand.type_envoi "
           + "FROM candidatures c "
           + "JOIN candidats cand ON c.candidat_id = cand.id "
           + "JOIN stages s ON c.stage_id = s.id "
           + "WHERE c.convention_signee = TRUE AND c.statut = 'SELECTIONNE' "
           + "ORDER BY c.date_signature_convention DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                candidatures.add(mapResultSetToCandidature(rs));
            }
        }
        return candidatures;
    }
    
    public boolean toutesLesCandidaturesEnAttente(int candidatId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidatures WHERE candidat_id = ? AND statut != 'EN_ATTENTE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidatId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Toutes en attente si aucune n'est dans un autre statut
                }
            }
        }
        return false;
    }
    
    private Candidature mapResultSetToCandidature(ResultSet rs) throws SQLException {
        Candidature candidature = new Candidature();
        candidature.setId(rs.getInt("id"));
        candidature.setCandidatId(rs.getInt("candidat_id"));
        candidature.setStageId(rs.getInt("stage_id"));
        candidature.setStatut(Candidature.StatutCandidature.valueOf(rs.getString("statut")));
        candidature.setCandidatNom(rs.getString("candidat_nom"));
        candidature.setStageTitre(rs.getString("stage_titre"));
        
        // Gestion de la convention
        Date dateEnvoi = rs.getDate("date_envoi_convention");
        if (dateEnvoi != null) {
            candidature.setDateEnvoiConvention(dateEnvoi.toLocalDate());
        }
        
        candidature.setConventionSignee(rs.getBoolean("convention_signee"));
        candidature.setFichierConvention(rs.getString("fichier_convention"));
        
        Date dateSignature = rs.getDate("date_signature_convention");
        if (dateSignature != null) {
            candidature.setDateSignatureConvention(dateSignature.toLocalDate());
        }
        
        return candidature;
    }
}
