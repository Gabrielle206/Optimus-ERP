package com.erp;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.*;

public class JanelaCadastroProduto extends JDialog implements LanguageObserver {
    private static final long serialVersionUID = 1L;

    private JTextField campoId, campoNome, campoPrecoCusto, campoPrecoVenda, campoQuantidade;
    private ProdutoDAO produtoDAO;
    private Produto produtoExistente;

    private JLabel labelId, labelNome, labelPrecoCusto, labelPrecoVenda, labelQuantidade;
    private JButton botaoSalvar, botaoVoltar;

    public JanelaCadastroProduto(Frame parent, Produto produtoParaEditar) {
        super(parent, true);
        this.produtoExistente = produtoParaEditar;
        this.produtoDAO = new ProdutoDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        if (produtoExistente != null) {
            preencherFormulario();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaCadastroProduto.this);
            }
        });
    }

    private void initComponents() {
        setSize(400, 300);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }
        JPanel painelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelId = new JLabel();
        painelFormulario.add(labelId);
        campoId = new JTextField();
        painelFormulario.add(campoId);

        labelNome = new JLabel();
        painelFormulario.add(labelNome);
        campoNome = new JTextField();
        painelFormulario.add(campoNome);

        labelPrecoCusto = new JLabel();
        painelFormulario.add(labelPrecoCusto);
        campoPrecoCusto = new JTextField();
        painelFormulario.add(campoPrecoCusto);

        labelPrecoVenda = new JLabel();
        painelFormulario.add(labelPrecoVenda);
        campoPrecoVenda = new JTextField();
        painelFormulario.add(campoPrecoVenda);

        labelQuantidade = new JLabel();
        painelFormulario.add(labelQuantidade);
        campoQuantidade = new JTextField();
        painelFormulario.add(campoQuantidade);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton();
        botaoVoltar = new JButton();
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoVoltar);

        add(painelFormulario, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        botaoSalvar.addActionListener(e -> salvarProduto());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();

        if (produtoExistente != null) {
            setTitle(messages.getString("janelaCadastroProduto.titulo.edicao"));
        } else {
            setTitle(messages.getString("janelaCadastroProduto.titulo.cadastro"));
        }

        labelId.setText(messages.getString("janelaCadastroProduto.label.id"));
        labelNome.setText(messages.getString("janelaCadastroProduto.label.nome"));
        labelPrecoCusto.setText(messages.getString("janelaCadastroProduto.label.precoCusto"));
        labelPrecoVenda.setText(messages.getString("janelaCadastroProduto.label.precoVenda"));
        labelQuantidade.setText(messages.getString("janelaCadastroProduto.label.quantidade"));
        botaoSalvar.setText(messages.getString("janelaCadastroProduto.botao.salvar"));
        botaoVoltar.setText(messages.getString("janelaListagemProdutos.botao.voltar"));
    }

    private void preencherFormulario() {
        campoId.setText(produtoExistente.getId());
        campoId.setEditable(false);
        campoNome.setText(produtoExistente.getNome());
        campoPrecoCusto.setText(String.valueOf(produtoExistente.getPrecoCusto()).replace('.', ','));
        campoPrecoVenda.setText(String.valueOf(produtoExistente.getPrecoVenda()).replace('.', ','));
        campoQuantidade.setText(produtoExistente.getQuantidade());
    }

    private void salvarProduto() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        String id = campoId.getText().trim();
        String nome = campoNome.getText().trim();
        String precoCustoStr = campoPrecoCusto.getText().trim();
        String precoVendaStr = campoPrecoVenda.getText().trim();
        String quantidade = campoQuantidade.getText().trim();
        double precoCusto, precoVenda;

        if (nome.isEmpty() || precoCustoStr.isEmpty() || precoVendaStr.isEmpty() || quantidade.isEmpty()) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroProduto.dialogo.erroValidacao.mensagem"), messages.getString("janelaCadastroProduto.dialogo.erroValidacao.titulo"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            precoCusto = Double.parseDouble(precoCustoStr.replace(",", "."));
            precoVenda = Double.parseDouble(precoVendaStr.replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroProduto.dialogo.erroFormato.mensagem"), messages.getString("janelaCadastroProduto.dialogo.erroFormato.titulo"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (produtoExistente == null) {
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroProduto.dialogo.idObrigatorio.mensagem"), messages.getString("janelaCadastroProduto.dialogo.erroValidacao.titulo"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            Produto novoProduto = new Produto(id, nome, precoCusto, precoVenda, quantidade);
            produtoDAO.salvar(novoProduto);
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroProduto.dialogo.salvoSucesso"));
        } else {
            produtoExistente.setNome(nome);
            produtoExistente.setPrecoCusto(precoCusto);
            produtoExistente.setPrecoVenda(precoVenda);
            produtoExistente.setQuantidade(quantidade);
            produtoDAO.atualizar(produtoExistente);
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroProduto.dialogo.atualizadoSucesso"));
        }

        LanguageManager.getInstance().removeObserver(this);
        dispose();
    }
}
