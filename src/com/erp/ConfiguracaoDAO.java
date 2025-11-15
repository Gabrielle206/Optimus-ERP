package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracaoDAO {

    public String buscarValor(String chave) {
        String sql = "SELECT valor FROM Configuracao WHERE chave = ?";
        String valor = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, chave);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    valor = rs.getString("valor");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar configuração: " + e.getMessage());
        }
        return valor;
    }

    public void salvarValor(String chave, String valor) {

        String sql = "UPDATE Configuracao SET valor = ? WHERE chave = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, valor);
            pstmt.setString(2, chave);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar configuração: " + e.getMessage());
        }
    }
}
