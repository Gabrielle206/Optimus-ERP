package com.erp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

public class JanelaBalanco extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private TituloDAO tituloDAO;
    private ItemTituloDAO itemTituloDAO;
    private ProdutoDAO produtoDAO;

    private JLabel labelReceitas, labelDespesas, labelBalanco;
    private JLabel tituloReceitas, tituloDespesas, tituloBalanco;

    public JanelaBalanco() {
        this.tituloDAO = new TituloDAO();
        this.itemTituloDAO = new ItemTituloDAO();
        this.produtoDAO = new ProdutoDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaBalanco.this);
            }
        });
    }

    private void initComponents() {
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon img = new ImageIcon("iconeerp/logo1.png");
        setIconImage(img.getImage());

        JPanel painelPrincipal = new JPanel(new GridLayout(3, 2, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font fonteLabels = new Font("Arial", Font.BOLD, 16);
        Font fonteValores = new Font("Arial", Font.PLAIN, 16);

        tituloReceitas = new JLabel();
        tituloReceitas.setFont(fonteLabels);
        labelReceitas = new JLabel();
        labelReceitas.setFont(fonteValores);
        labelReceitas.setForeground(new Color(0, 128, 0));

        tituloDespesas = new JLabel();
        tituloDespesas.setFont(fonteLabels);
        labelDespesas = new JLabel();
        labelDespesas.setFont(fonteValores);
        labelDespesas.setForeground(Color.RED);

        tituloBalanco = new JLabel();
        tituloBalanco.setFont(fonteLabels);
        labelBalanco = new JLabel();
        labelBalanco.setFont(fonteValores);

        painelPrincipal.add(tituloReceitas);
        painelPrincipal.add(labelReceitas);
        painelPrincipal.add(tituloDespesas);
        painelPrincipal.add(labelDespesas);
        painelPrincipal.add(tituloBalanco);
        painelPrincipal.add(labelBalanco);

        add(painelPrincipal);
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();

        setTitle(messages.getString("janelaBalanco.titulo"));
        tituloReceitas.setText(messages.getString("janelaBalanco.label.receitas"));
        tituloDespesas.setText(messages.getString("janelaBalanco.label.despesas"));
        tituloBalanco.setText(messages.getString("janelaBalanco.label.balanco"));

        calcularEExibirBalanco();
    }

    private void calcularEExibirBalanco() {
        var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();

        double totalReceitas = 0.0;
        double totalDespesas = 0.0;

        List<Titulo> titulos = tituloDAO.listarTodos();

        for (Titulo titulo : titulos) {
            if (titulo.isPago()) {
                List<ItemTitulo> itens = itemTituloDAO.listarPorTituloId(titulo.getId());

                if (titulo.getTipoTitulo().equals("A Receber")) {
                    for (ItemTitulo item : itens) {
                        totalReceitas += item.getValorUnitario() * item.getQuantidade();
                    }
                } else if (titulo.getTipoTitulo().equals("A Pagar")) {
                    for (ItemTitulo item : itens) {
                        if (item.getIdProduto() == null || item.getIdProduto().isEmpty()) {
                            totalDespesas += item.getValorUnitario() * item.getQuantidade();
                        } else {
                            Produto produto = produtoDAO.buscarPorId(item.getIdProduto());
                            if (produto != null) {
                                totalDespesas += produto.getPrecoCusto() * item.getQuantidade();
                            }
                        }
                    }
                }
            }
        }

        double balanco = totalReceitas - totalDespesas;

        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);
        labelReceitas.setText(formatadorMoeda.format(totalReceitas));
        labelDespesas.setText(formatadorMoeda.format(totalDespesas));
        labelBalanco.setText(formatadorMoeda.format(balanco));

        if (balanco >= 0) {
            labelBalanco.setForeground(new Color(0, 128, 0));
        } else {
            labelBalanco.setForeground(Color.RED);
        }
    }
}