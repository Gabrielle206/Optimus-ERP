package com.erp;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class JanelaLogin extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;
    
    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;
    private JLabel labelUsuario, labelSenha;
    
    private PessoaDAO pessoaDAO;
    private LogSegurancaDAO logDAO;
    private MfaManager mfaManager;

    public JanelaLogin() {
        this.pessoaDAO = new PessoaDAO();
        this.logDAO = new LogSegurancaDAO();
        this.mfaManager = new MfaManager();
        LanguageManager.getInstance().addObserver(this);
        
        initComponents();
        updateLanguage();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setResizable(false);
        
        ImageIcon img = new ImageIcon("iconeerp/logo1.png");
        setIconImage(img.getImage());
        
        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        labelUsuario = new JLabel();
        campoUsuario = new JTextField();
        labelSenha = new JLabel();
        campoSenha = new JPasswordField();
        botaoEntrar = new JButton();
        
        painel.add(labelUsuario);
        painel.add(campoUsuario);
        painel.add(labelSenha);
        painel.add(campoSenha);
        painel.add(new JLabel());
        painel.add(botaoEntrar);
        
        add(painel);
        
        botaoEntrar.addActionListener(e -> tentarLogin());
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        setTitle(messages.getString("janelaLogin.titulo"));
        labelUsuario.setText(messages.getString("janelaLogin.label.usuario"));
        labelSenha.setText(messages.getString("janelaLogin.label.senha"));
        botaoEntrar.setText(messages.getString("janelaLogin.botao.entrar"));
    }
    
    private void tentarLogin() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        String idUsuario = campoUsuario.getText();
        String senha = new String(campoSenha.getPassword());

        Pessoa pessoaAutenticada = pessoaDAO.autenticar(idUsuario, senha);

        if (pessoaAutenticada != null) {
            if (pessoaAutenticada.getSecretKey() != null && !pessoaAutenticada.getSecretKey().isEmpty()) {
                String codigoMfaStr = JOptionPane.showInputDialog(this, messages.getString("janelaMFA.instrucao2"));
                try {
                    int codigoMfa = Integer.parseInt(codigoMfaStr);
                    if (mfaManager.validarCodigo(pessoaAutenticada.getSecretKey(), codigoMfa)) {
                        logDAO.registrarTentativa(idUsuario, true);
                        abrirSistema();
                    } else {
                        logDAO.registrarTentativa(idUsuario, false);
                        JOptionPane.showMessageDialog(this, messages.getString("janelaMFA.dialogo.erro.codigoInvalido"), messages.getString("janelaMFA.dialogo.erro.titulo"), JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException | NullPointerException ex) {
                    logDAO.registrarTentativa(idUsuario, false);
                    JOptionPane.showMessageDialog(this, messages.getString("janelaMFA.dialogo.erro.codigoInvalido"), messages.getString("janelaMFA.dialogo.erro.titulo"), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logDAO.registrarTentativa(idUsuario, true);
                abrirSistema();
            }
        } else {
            logDAO.registrarTentativa(idUsuario, false);
            JOptionPane.showMessageDialog(this, messages.getString("janelaLogin.dialogo.erro.credenciaisInvalidas"), messages.getString("janelaLogin.dialogo.erro.titulo"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirSistema() {
        new JanelaPrincipal().setVisible(true);
        dispose();
    }
}