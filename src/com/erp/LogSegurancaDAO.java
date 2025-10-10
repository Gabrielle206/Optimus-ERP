package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogSegurancaDAO {

    public void registrarTentativa(String usuarioId, boolean sucesso) {
        String sql = "INSERT INTO LogsSeguranca(usuario_id, ip_origem, sucesso) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuarioId);
            pstmt.setString(2, "127.0.0.1"); 
            pstmt.setInt(3, sucesso ? 1 : 0);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao registrar log de segurança: " + e.getMessage());
        }
    }
}