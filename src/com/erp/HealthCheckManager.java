package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HealthCheckManager {

    private static final HealthCheckManager instance = new HealthCheckManager();

    private HealthCheckManager() {
    }

    public static HealthCheckManager getInstance() {
        return instance;
    }

    public List<String> runChecks() {
        List<String> resultados = new ArrayList<>();

        resultados.add(checkDatabaseConnection());

        if (resultados.get(0).startsWith("SUCESSO")) {
            resultados.add(checkTableIntegrity("Pessoas", true)); 
        }

        if (resultados.get(0).startsWith("SUCESSO")) {
            resultados.add(checkTableIntegrity("Configuracao", true)); 
        }
        
        if (resultados.get(0).startsWith("SUCESSO")) {
            resultados.add(checkTableIntegrity("Produtos", false)); 
        }

        return resultados;
    }

    private String checkDatabaseConnection() {
        try (Connection conn = DatabaseManager.getConnection()) { 
            if (conn.isValid(2)) { 
                return "SUCESSO: Conexão com o Banco de Dados (optimus.db) estabelecida.";
            } else {
                return "FALHA: Conexão com o Banco de Dados (optimus.db) inválida.";
            }
        } catch (SQLException e) {
            return "FALHA: Erro ao conectar ao Banco de Dados (optimus.db). " + e.getMessage();
        }
    }

    private String checkTableIntegrity(String nomeTabela, boolean naoPodeEstarVazia) {
        String sql = "SELECT COUNT(*) FROM " + nomeTabela;
        try (Connection conn = DatabaseManager.getConnection(); //
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                if (naoPodeEstarVazia && count == 0) {
                    return "FALHA: Verificação de integridade falhou. Tabela '" + nomeTabela + "' está vazia (0 registros).";
                }
                return "SUCESSO: Tabela '" + nomeTabela + "' acessível (" + count + " registros encontrados).";
            } else {
                return "FALHA: Não foi possível obter a contagem da tabela '" + nomeTabela + "'.";
            }
        } catch (SQLException e) {
            return "FALHA: Erro ao consultar tabela '" + nomeTabela + "'. (Possível corrupção ou tabela faltando) " + e.getMessage();
        }
    }
}
