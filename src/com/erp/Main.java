package com.erp;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("O driver JDBC do SQLite não foi encontrado.");
            e.printStackTrace();
            return; 
        }

        DatabaseManager.initializeDatabase();
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            PessoaDAO pessoaDAO = new PessoaDAO();
            
            if (pessoaDAO.listarTodos().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Bem-vindo ao Optimus ERP! Por favor, registe o primeiro utilizador administrador.");
                JanelaCadastroPessoa janelaCadastroAdmin = new JanelaCadastroPessoa(null, null, true);
                janelaCadastroAdmin.setVisible(true);
                
                if (pessoaDAO.listarTodos().isEmpty()) {
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(null, "Utilizador administrador registado com sucesso! Por favor, faça o login.");
                }
            }
            
            new JanelaLogin().setVisible(true);
        });
    }
}