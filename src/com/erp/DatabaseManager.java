package com.erp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:optimus.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initializeDatabase() {
        String sqlPessoas = "CREATE TABLE IF NOT EXISTS Pessoas ("
                + "id TEXT PRIMARY KEY,"
                + "nome TEXT NOT NULL,"
                + "tipo INTEGER NOT NULL,"
                + "telefone TEXT,"
                + "senha TEXT,"
                + "tentativas_falhas INTEGER DEFAULT 0,"
                + "bloqueado_ate DATETIME,"
                + "secret_key TEXT"
                + ");";

        String sqlProdutos = "CREATE TABLE IF NOT EXISTS Produtos ("
                + "id TEXT PRIMARY KEY,"
                + "nome TEXT NOT NULL,"
                + "preco_custo REAL NOT NULL,"
                + "preco_venda REAL NOT NULL,"
                + "quantidade TEXT NOT NULL"
                + ");";

        String sqlTitulos = "CREATE TABLE IF NOT EXISTS Titulos ("
                + "id TEXT PRIMARY KEY,"
                + "pago INTEGER NOT NULL,"
                + "pessoa_id TEXT NOT NULL,"
                + "tipo_titulo TEXT NOT NULL,"
                + "data_venda DATE DEFAULT (DATE('now')),"
                + "FOREIGN KEY (pessoa_id) REFERENCES Pessoas(id)"
                + ");";

        String sqlItensTitulo = "CREATE TABLE IF NOT EXISTS ItensTitulo ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_titulo TEXT NOT NULL,"
                + "id_produto TEXT,"
                + "descricao TEXT,"
                + "quantidade INTEGER NOT NULL,"
                + "valor_unitario REAL NOT NULL,"
                + "FOREIGN KEY (id_titulo) REFERENCES Titulos(id),"
                + "FOREIGN KEY (id_produto) REFERENCES Produtos(id)"
                + ");";

        String sqlListagemVendas = "CREATE TABLE IF NOT EXISTS RegistroVendas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "pessoa_id TEXT NOT NULL,"
                + "telefone TEXT NOT NULL,"
                + "id_produto TEXT,"
                + "quantidade INTEGER NOT NULL,"
                + "data_venda DATE,"
                + "id_titulo TEXT NOT NULL,"
                + "id_itens_titulo INTEGER NOT NULL,"
                + "FOREIGN KEY (pessoa_id) REFERENCES Pessoas(id),"
                + "FOREIGN KEY (id_produto) REFERENCES Produtos(id),"
                + "FOREIGN KEY (id_titulo) REFERENCES Titulos(id),"
                + "FOREIGN KEY (id_itens_titulo) REFERENCES ItensTitulo(id)"
                + ");";

        String sqlLogsSeguranca = "CREATE TABLE IF NOT EXISTS LogsSeguranca ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "usuario_id TEXT NOT NULL,"
                + "ip_origem TEXT,"
                + "sucesso INTEGER NOT NULL"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
        	
            stmt.execute(sqlPessoas);
            stmt.execute(sqlProdutos);
            stmt.execute(sqlTitulos);
            stmt.execute(sqlItensTitulo);
            stmt.execute(sqlListagemVendas);
            stmt.execute(sqlLogsSeguranca);
            
            System.out.println("Banco de dados inicializado com sucesso.");

        } catch (SQLException e) {
            System.out.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }
}