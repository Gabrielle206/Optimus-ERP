package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TituloDAO {

    public void salvar(Titulo titulo) {
        String sql = "INSERT INTO Titulos(id, pago, pessoa_id, tipo_titulo, data_venda) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo.getId());
            pstmt.setInt(2, titulo.isPago() ? 1 : 0);
            pstmt.setString(3, titulo.getPessoaId());
            pstmt.setString(4, titulo.getTipoTitulo());
            pstmt.setString(5, titulo.getDate().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar título: " + e.getMessage());
        }
    }

    public void atualizar(Titulo titulo) {
        String sql = "UPDATE Titulos SET pago = ?, pessoa_id = ?, tipo_titulo = ?, data_venda = ? WHERE id = ? ";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, titulo.isPago() ? 1 : 0);
            pstmt.setString(2, titulo.getPessoaId());
            pstmt.setString(3, titulo.getTipoTitulo());
            pstmt.setString(4, titulo.getDate().toString());
            pstmt.setString(5, titulo.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar título: " + e.getMessage());
        }
    }

    public void excluir(String id) {
        String sqlItens = "DELETE FROM ItensTitulo WHERE id_titulo = ?";
        String sqlTitulo = "DELETE FROM Titulos WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtItens = conn.prepareStatement(sqlItens);
                 PreparedStatement pstmtTitulo = conn.prepareStatement(sqlTitulo)) {

                pstmtItens.setString(1, id);
                pstmtItens.executeUpdate();

                pstmtTitulo.setString(1, id);
                pstmtTitulo.executeUpdate();
                
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro ao excluir título e seus itens: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Erro de conexão ao excluir título: " + e.getMessage());
        }
    }

    public List<Titulo> listarTodos() {
        List<Titulo> titulos = new ArrayList<>();
        String sql = "SELECT * FROM Titulos";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Titulo titulo = new Titulo(
                        rs.getString("id"),
                        rs.getInt("pago") == 1,
                        rs.getString("pessoa_id"),
                        rs.getString("tipo_titulo"),
                        java.time.LocalDate.parse(rs.getString("data_venda")));
                titulos.add(titulo);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar títulos: " + e.getMessage());
        }
        return titulos;
    }
}