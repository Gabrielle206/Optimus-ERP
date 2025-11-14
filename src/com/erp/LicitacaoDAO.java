package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LicitacaoDAO {
	
	public void salvar(Licitacao licitacao) {
        
        String sql = "INSERT INTO Licitacao (id, titulo, descricao, valor_estimado, orgao) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licitacao.getId());
            pstmt.setString(2, licitacao.getTitulo());
            pstmt.setString(3, licitacao.getDescricao());
            pstmt.setDouble(4, licitacao.getValorEstimado());
            pstmt.setString(5, licitacao.getOrgao());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar licitação: " + e.getMessage());
        }
    }
	
	
	public void atualizar(Licitacao licitacao) {
       
        String sql = "UPDATE Licitacao SET titulo = ?, descricao = ?, valor_estimado = ?, orgao = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licitacao.getTitulo());
            pstmt.setString(2, licitacao.getDescricao());
            pstmt.setDouble(3, licitacao.getValorEstimado());
            pstmt.setString(4, licitacao.getOrgao());
            pstmt.setString(5, licitacao.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar licitação: " + e.getMessage());
        }
    }
	
	 public void excluir(String id) {
	        String sql = "DELETE FROM Licitacao WHERE id = ?";

	        try (Connection conn = DatabaseManager.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, id);
	            pstmt.executeUpdate();

	        } catch (SQLException e) {
	            System.out.println("Erro ao excluir licitação: " + e.getMessage());
	        }
	    }
	 
	 public List<Licitacao> listarTodas() {
	        List<Licitacao> licitacoes = new ArrayList<>();
	        
	        String sql = "SELECT * FROM Licitacao";  

	        try (Connection conn = DatabaseManager.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(sql)) {

	            while (rs.next()) {
	                
	                Licitacao licitacao = new Licitacao(
                        rs.getString("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getDouble("valor_estimado"),
                        rs.getString("orgao")
                    );
	                licitacoes.add(licitacao);
	            }

	        } catch (SQLException e) {
	            System.out.println("Erro ao listar licitações: " + e.getMessage());
	        }

	        return licitacoes;
	    }

}
