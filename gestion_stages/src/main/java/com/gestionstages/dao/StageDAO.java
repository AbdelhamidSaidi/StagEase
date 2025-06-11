package com.gestionstages.dao;

import com.gestionstages.model.Stage;
import com.gestionstages.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StageDAO {
    
    public void ajouterStage(Stage stage) throws SQLException {
        String sql = "INSERT INTO stages (reference, titre, sujet, duree, responsable_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, stage.getReference());
            stmt.setString(2, stage.getTitre());
            stmt.setString(3, stage.getSujet());
            stmt.setInt(4, stage.getDuree());
            stmt.setInt(5, stage.getResponsableId());
            
            stmt.executeUpdate();
        }
    }
    
    public List<Stage> getAllStages() throws SQLException {
        List<Stage> stages = new ArrayList<>();
        String sql = "SELECT s.*, CONCAT(u.prenom, ' ', u.nom) as responsable_nom "
                   + "FROM stages s "
                   + "JOIN utilisateurs u ON s.responsable_id = u.id "
                   + "ORDER BY s.reference";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stages.add(mapResultSetToStage(rs));
            }
        }
        return stages;
    }
    
    public Stage getStageById(int id) throws SQLException {
        String sql = "SELECT s.*, CONCAT(u.prenom, ' ', u.nom) as responsable_nom "
                   + "FROM stages s "
                   + "JOIN utilisateurs u ON s.responsable_id = u.id "
                   + "WHERE s.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStage(rs);
                }
            }
        }
        return null;
    }
    
    public List<Stage> getStagesByResponsable(int responsableId) throws SQLException {
        List<Stage> stages = new ArrayList<>();
        String sql = "SELECT s.*, CONCAT(u.prenom, ' ', u.nom) as responsable_nom "
                   + "FROM stages s "
                   + "JOIN utilisateurs u ON s.responsable_id = u.id "
                   + "WHERE s.responsable_id = ? "
                   + "ORDER BY s.reference";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, responsableId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stages.add(mapResultSetToStage(rs));
                }
            }
        }
        return stages;
    }
    
    public int countStagesByResponsable(int responsableId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM stages WHERE responsable_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, responsableId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public void supprimerStage(int stageId) throws SQLException {
        String sql = "DELETE FROM stages WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stageId);
            stmt.executeUpdate();
        }
    }
    
    public String genererReferenceStage() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(reference, 10) AS UNSIGNED)) "
                   + "FROM stages WHERE reference LIKE 'STG-2024-%'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int maxNum = 0;
            if (rs.next()) {
                maxNum = rs.getInt(1);
            }
            
            return String.format("STG-2024-%03d", maxNum + 1);
        }
    }
    
    private Stage mapResultSetToStage(ResultSet rs) throws SQLException {
        Stage stage = new Stage();
        stage.setId(rs.getInt("id"));
        stage.setReference(rs.getString("reference"));
        stage.setTitre(rs.getString("titre"));
        stage.setSujet(rs.getString("sujet"));
        stage.setDuree(rs.getInt("duree"));
        stage.setResponsableId(rs.getInt("responsable_id"));
        stage.setResponsableNom(rs.getString("responsable_nom"));
        return stage;
    }
}
