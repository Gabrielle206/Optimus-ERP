package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void salvar(Produto produto) {
        String sql = "INSERT INTO Produtos(id, nome, preco_custo, preco_venda, quantidade) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, produto.getId());
            pstmt.setString(2, produto.getNome());
            pstmt.setDouble(3, produto.getPrecoCusto());
            pstmt.setDouble(4, produto.getPrecoVenda());
            pstmt.setString(5, produto.getQuantidade());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE Produtos SET nome = ?, preco_custo = ?, preco_venda = ?, quantidade = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setDouble(2, produto.getPrecoCusto());
            pstmt.setDouble(3, produto.getPrecoVenda());
            pstmt.setString(4, produto.getQuantidade());
            pstmt.setString(5, produto.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public void excluir(String id) {
        String sql = "DELETE FROM Produtos WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir produto: " + e.getMessage());
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM Produtos";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto produto = new Produto(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco_custo"),
                        rs.getDouble("preco_venda"),
                        rs.getString("quantidade"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
        return produtos;
    }

    public Produto buscarPorId(String id) {
        String sql = "SELECT * FROM Produtos WHERE id = ?";
        Produto produto = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                produto = new Produto(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco_custo"),
                        rs.getDouble("preco_venda"),
                        rs.getString("quantidade"));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto por ID: " + e.getMessage());
        }
        return produto;
    }
}