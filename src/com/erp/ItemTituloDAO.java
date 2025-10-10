package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemTituloDAO {

    public void salvar(ItemTitulo item) {
        String sql = "INSERT INTO ItensTitulo(id_titulo, id_produto, descricao, quantidade, valor_unitario) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getIdTitulo());
            pstmt.setString(2, item.getIdProduto());
            pstmt.setString(3, item.getDescricao());
            pstmt.setInt(4, item.getQuantidade());

            pstmt.setDouble(5, item.getValorUnitario());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar item do título: " + e.getMessage());
        }
    }

    public void excluirPorIdTitulo(String idTitulo) {
        String sql = "DELETE FROM ItensTitulo WHERE id_titulo = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idTitulo);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir itens por ID do título: " + e.getMessage());
        }
    }
    
    public List<ItemTitulo> listarPorTituloId(String idTitulo) {
        List<ItemTitulo> itens = new ArrayList<>();
        String sql = "SELECT * FROM ItensTitulo WHERE id_titulo = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idTitulo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemTitulo item = new ItemTitulo(
                            rs.getInt("id"),
                            rs.getString("id_titulo"),
                            rs.getString("id_produto"),
                            rs.getString("descricao"), 
                            rs.getInt("quantidade"),
                            rs.getDouble("valor_unitario"));
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar itens do título: " + e.getMessage());
        }
        return itens;
    }
}