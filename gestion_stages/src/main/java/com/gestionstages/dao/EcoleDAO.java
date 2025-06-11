package com.gestionstages.dao;

import com.gestionstages.model.Ecole;
import com.gestionstages.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EcoleDAO {
    
    public List<Ecole> getAllEcoles() throws SQLException {
        List<Ecole> ecoles = new ArrayList<>();
        String sql = "SELECT * FROM ecoles ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ecoles.add(mapResultSetToEcole(rs));
            }
        }
        return ecoles;
    }
    
    public Ecole getEcoleById(int id) throws SQLException {
        String sql = "SELECT * FROM ecoles WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEcole(rs);
                }
            }
        }
        return null;
    }
    
    public void ajouterEcole(Ecole ecole) throws SQLException {
        String sql = "INSERT INTO ecoles (nom, adresse) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ecole.getNom());
            stmt.setString(2, ecole.getAdresse());
            
            stmt.executeUpdate();
        }
    }
    
    private Ecole mapResultSetToEcole(ResultSet rs) throws SQLException {
        Ecole ecole = new Ecole();
        ecole.setId(rs.getInt("id"));
        ecole.setNom(rs.getString("nom"));
        ecole.setAdresse(rs.getString("adresse"));
        return ecole;
    }
}
