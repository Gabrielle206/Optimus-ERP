package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContratoDAO {

    public void salvar(Contrato contrato) {
        String sql = "INSERT INTO Contrato (id, id_fornecedor, objeto_contrato, valor, status, assinatura, blockchain_tx_hash, " +
                     "smart_contract_address, nota_fiscal_validada) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            CryptoManager crypto = CryptoManager.getInstance();

            pstmt.setString(1, contrato.getId());
            pstmt.setString(2, contrato.getIdFornecedor());
            pstmt.setString(3, contrato.getObjetoContrato());
            pstmt.setString(4, crypto.encrypt(String.valueOf(contrato.getValor()))); 
            pstmt.setString(5, contrato.getStatus());
            pstmt.setString(6, contrato.getAssinatura());
            pstmt.setString(7, contrato.getBlockchainTxHash());
            pstmt.setString(8, contrato.getSmartContarctAddress());
            pstmt.setInt(9, contrato.getNotaFiscalValidada());

            pstmt.executeUpdate();
            System.out.println("Contrato salvo com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar contrato: " + e.getMessage());  
        }
    }

    public void atualizar(Contrato contrato) {
        String sql = "UPDATE Contrato SET objeto_contrato = ?, valor = ?, status = ?, assinatura = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            CryptoManager crypto = CryptoManager.getInstance();

            pstmt.setString(1, contrato.getObjetoContrato());
            pstmt.setString(2, crypto.encrypt(String.valueOf(contrato.getValor()))); 
            pstmt.setString(3, contrato.getStatus());
            pstmt.setString(4, contrato.getAssinatura());
            pstmt.setString(5, contrato.getId());

            pstmt.executeUpdate();
            System.out.println("Contrato atualizado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar contrato: " + e.getMessage());
        }
    }
    
    public Contrato buscarPorId(String id) {
        String sql = "SELECT * FROM Contrato WHERE id = ?";
        Contrato contrato = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            CryptoManager crypto = CryptoManager.getInstance();
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                try {
                    contrato = new Contrato(
                        rs.getString("id"),
                        rs.getString("id_fornecedor"),
                        rs.getString("objeto_contrato"),
                        Double.parseDouble(crypto.decrypt(rs.getString("valor"))), 
                        null, null, null, null,  
                        rs.getString("status"),
                        rs.getString("assinatura"),
                        rs.getString("blockchain_tx_hash"),
                        rs.getString("smart_contract_address"),
                        rs.getInt("nota_fiscal_validada")
                    );
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter valor descriptografado: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar contrato: " + e.getMessage());
        }

        return contrato;
    }

    public List<Contrato> listarTodos() {
        String sql = "SELECT * FROM Contrato";
        List<Contrato> lista = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            CryptoManager crypto = CryptoManager.getInstance();

            while (rs.next()) {
                try {
                    Contrato contrato = new Contrato(
                        rs.getString("id"),
                        rs.getString("id_fornecedor"),
                        rs.getString("objeto_contrato"),
                        Double.parseDouble(crypto.decrypt(rs.getString("valor"))),
                        null, null, null, null,
                        rs.getString("status"),
                        rs.getString("assinatura"),
                        rs.getString("blockchain_tx_hash"),
                        rs.getString("smart_contract_address"),
                        rs.getInt("nota_fiscal_validada")
                    );
                    lista.add(contrato);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter valor descriptografado em listarTodos: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar contratos: " + e.getMessage());
        }

        return lista;
    }
}
