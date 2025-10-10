package com.erp;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL; 
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import java.awt.FlowLayout;

public class JanelaPrincipal extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private JLabel labelTitulo;
    private JButton botaoPessoas, botaoProdutos, botaoTitulos, botaoListagemVendas, botaoBalanco;

    public JanelaPrincipal() {
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaPrincipal.this);
            }
        });
    }

    private void initComponents() {
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 700, 500);

        URL backgroundURL = getClass().getResource("/iconeerp/opcao3.png");
        JLabel labelback;
        if (backgroundURL != null) {
            labelback = new JLabel(new ImageIcon(backgroundURL));
        } else {
            System.err.println("Erro: Não foi possível encontrar a imagem de fundo 'opcao3.png'");
            labelback = new JLabel();
        }
        labelback.setBounds(0, 0, 700, 500);
        layeredPane.add(labelback, Integer.valueOf(0));

        JPanel painelBotoesPrincipais = new JPanel(new GridLayout(5, 1, 10, 20));
        painelBotoesPrincipais.setOpaque(false);
        painelBotoesPrincipais.setBounds(50, 60, 600, 400);
        painelBotoesPrincipais.setBorder(BorderFactory.createEmptyBorder(65, 150, 20, 150));

        botaoPessoas = new JButton();
        botaoProdutos = new JButton();
        botaoTitulos = new JButton();
        botaoListagemVendas = new JButton();
        botaoBalanco = new JButton();

        Font fontBotoes = new Font("Arial", Font.PLAIN, 14);
        botaoPessoas.setFont(fontBotoes);
        botaoProdutos.setFont(fontBotoes);
        botaoTitulos.setFont(fontBotoes);
        botaoListagemVendas.setFont(fontBotoes);
        botaoBalanco.setFont(fontBotoes);

        botaoPessoas.addActionListener(e -> new JanelaListagemPessoas().setVisible(true));
        botaoProdutos.addActionListener(e -> new JanelaListagemProdutos().setVisible(true));
        botaoTitulos.addActionListener(e -> new JanelaListagemTitulos().setVisible(true));
        botaoListagemVendas.addActionListener(e -> new JanelaListagemVendas().setVisible(true));
        botaoBalanco.addActionListener(e -> new JanelaBalanco().setVisible(true));

        painelBotoesPrincipais.add(botaoPessoas);
        painelBotoesPrincipais.add(botaoProdutos);
        painelBotoesPrincipais.add(botaoTitulos);
        painelBotoesPrincipais.add(botaoListagemVendas);
        painelBotoesPrincipais.add(botaoBalanco);
        layeredPane.add(painelBotoesPrincipais, Integer.valueOf(1));

        labelTitulo = new JLabel("", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Eras Demi ITC", Font.BOLD, 25));
        labelTitulo.setBounds(0, 20, 700, 40);
        layeredPane.add(labelTitulo, Integer.valueOf(2));

        JPanel painelIdiomas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelIdiomas.setOpaque(false);
        painelIdiomas.setBounds(450, 420, 220, 40);

        JButton botaoPT = new JButton("Português");
        JButton botaoES = new JButton("Español");

        botaoPT.addActionListener(e -> LanguageManager.getInstance().setLocale(new Locale("pt", "BR")));
        botaoES.addActionListener(e -> LanguageManager.getInstance().setLocale(new Locale("es", "MX")));

        painelIdiomas.add(botaoPT);
        painelIdiomas.add(botaoES);
        layeredPane.add(painelIdiomas, Integer.valueOf(3));

        add(layeredPane);
    }

    @Override
    public void updateLanguage() {
        var messages = LanguageManager.getInstance().getMessages();
        setTitle(messages.getString("janelaPrincipal.titulo"));
        labelTitulo.setText(messages.getString("janelaPrincipal.boasVindas"));
        botaoPessoas.setText(messages.getString("janelaPrincipal.botao.gerenciarPessoas"));
        botaoProdutos.setText(messages.getString("janelaPrincipal.botao.gerenciarProdutos"));
        botaoTitulos.setText(messages.getString("janelaPrincipal.botao.gerenciarTitulos"));
        botaoListagemVendas.setText(messages.getString("janelaPrincipal.botao.listagemVendas"));
        botaoBalanco.setText(messages.getString("janelaPrincipal.botao.balanco"));
    }
}