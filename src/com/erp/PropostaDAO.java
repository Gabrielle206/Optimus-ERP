package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PropostaDAO {

    public void salvar(Proposta proposta) {
        String sql = "INSERT INTO Proposta (id, licitacao_id, empresa, valor, descricao, data_envio) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proposta.getId());
            pstmt.setString(2, proposta.getLicitacaoId());
            pstmt.setString(3, proposta.getEmpresa());
            pstmt.setDouble(4, proposta.getValor());
            pstmt.setString(5, proposta.getDescricao());
            pstmt.setString(6, proposta.getDataEnvio() == null ? null : proposta.getDataEnvio().toString());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar proposta: " + e.getMessage());
        }
    }

    public void atualizar(Proposta proposta) {
        String sql = "UPDATE Proposta SET licitacao_id = ?, empresa = ?, valor = ?, descricao = ?, data_envio = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proposta.getLicitacaoId());
            pstmt.setString(2, proposta.getEmpresa());
            pstmt.setDouble(3, proposta.getValor());
            pstmt.setString(4, proposta.getDescricao());
            pstmt.setString(5, proposta.getDataEnvio() == null ? null : proposta.getDataEnvio().toString());
            pstmt.setString(6, proposta.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar proposta: " + e.getMessage());
        }
    }

    public void excluir(String id) {
        String sql = "DELETE FROM Proposta WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir proposta: " + e.getMessage());
        }
    }
    
    public Proposta buscarPorId(String id) {
        String sql = "SELECT * FROM Proposta WHERE id = ?";
        Proposta proposta = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String licitacaoId = rs.getString("licitacao_id");
                String empresa = rs.getString("empresa");
                double valor = rs.getDouble("valor");
                String descricao = rs.getString("descricao");
                String dataEnvioStr = rs.getString("data_envio");

                LocalDate dataEnvio = null;
                if (dataEnvioStr != null && !dataEnvioStr.isEmpty()) {
                    dataEnvio = LocalDate.parse(dataEnvioStr);
                }

                proposta = new Proposta(
                    id,
                    licitacaoId,
                    empresa,
                    valor,
                    descricao,
                    dataEnvio
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar proposta por ID: " + e.getMessage());
        }

        return proposta;
    }


    public List<Proposta> listarTodas(String licitacaoId) {
        List<Proposta> propostas = new ArrayList<>();
        
        String sql = "SELECT * FROM Proposta WHERE licitacao_id = ?"; 

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) { 
            
            pstmt.setString(1, licitacaoId); 
             
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String licitacaoIdResult = rs.getString("licitacao_id");
                    String empresa = rs.getString("empresa");
                    double valor = rs.getDouble("valor");
                    String descricao = rs.getString("descricao");
                    String dataEnvioStr = rs.getString("data_envio");

                    LocalDate dataEnvio = null;
                    if (dataEnvioStr != null && !dataEnvioStr.isEmpty()) {
                        dataEnvio = LocalDate.parse(dataEnvioStr);
                    }

                    Proposta proposta = new Proposta(
                        id,
                        licitacaoIdResult,
                        empresa,
                        valor,
                        descricao,
                        dataEnvio
                    );
                    propostas.add(proposta);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar propostas: " + e.getMessage());
        }

        return propostas;
    }
}