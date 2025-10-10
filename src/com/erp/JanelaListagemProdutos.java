package com.erp;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class JanelaListagemProdutos extends JFrame implements LanguageObserver {
    private static final long serialVersionUID = 1L;

    private ProdutoDAO produtoDAO;
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;

    private JButton botaoNovo, botaoEditar, botaoExcluir, botaoVoltar;

    public JanelaListagemProdutos() {
        this.produtoDAO = new ProdutoDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaListagemProdutos.this);
            }
        });
    }

    private void initComponents() {
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon("iconeerp/logo1.png");
        setIconImage(img.getImage());

        JPanel painelPrincipal = new JPanel(new BorderLayout());

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloTabela);
        painelPrincipal.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoNovo = new JButton();
        botaoEditar = new JButton();
        botaoExcluir = new JButton();
        botaoVoltar = new JButton();

        painelBotoes.add(botaoNovo);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoVoltar);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);

        botaoNovo.addActionListener(e -> abrirJanelaCadastro(null));
        botaoEditar.addActionListener(e -> editarProdutoSelecionado());
        botaoExcluir.addActionListener(e -> excluirProdutoSelecionado());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();

        setTitle(messages.getString("janelaListagemProdutos.titulo"));

        String[] colunas = {
                messages.getString("janelaListagemProdutos.coluna.id"),
                messages.getString("janelaListagemProdutos.coluna.nome"),
                messages.getString("janelaListagemProdutos.coluna.precoCusto"),
                messages.getString("janelaListagemProdutos.coluna.precoVenda"),
                messages.getString("janelaListagemProdutos.coluna.quantidade")
        };
        modeloTabela.setColumnIdentifiers(colunas);

        botaoNovo.setText(messages.getString("janelaListagemProdutos.botao.novo"));
        botaoEditar.setText(messages.getString("janelaListagemProdutos.botao.editar"));
        botaoExcluir.setText(messages.getString("janelaListagemProdutos.botao.excluir"));
        botaoVoltar.setText(messages.getString("janelaListagemProdutos.botao.voltar"));

        carregarDados();
    }

    private void carregarDados() {
        var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();

        modeloTabela.setRowCount(0);
        List<Produto> produtos = produtoDAO.listarTodos();
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);

        for (Produto p : produtos) {
            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    formatadorMoeda.format(p.getPrecoCusto()),
                    formatadorMoeda.format(p.getPrecoVenda()),
                    p.getQuantidade()
            });
        }
    }

    private void abrirJanelaCadastro(Produto produto) {
        new JanelaCadastroProduto(this, produto).setVisible(true);
        carregarDados();
    }

    private void editarProdutoSelecionado() {
        var messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemProdutos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemProdutos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tabelaProdutos.getValueAt(linhaSelecionada, 0);
        Produto produtoSelecionado = produtoDAO.buscarPorId(id);
        if (produtoSelecionado != null) {
            abrirJanelaCadastro(produtoSelecionado);
        }
    }

    private void excluirProdutoSelecionado() {
        var messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemProdutos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemProdutos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idParaExcluir = (String) tabelaProdutos.getValueAt(linhaSelecionada, 0);
        int resposta = JOptionPane.showConfirmDialog(this,
                String.format(messages.getString("janelaListagemProdutos.dialogo.confirmarExclusao.mensagem"), idParaExcluir),
                messages.getString("janelaListagemProdutos.dialogo.confirmarExclusao.titulo"),
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            produtoDAO.excluir(idParaExcluir);
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemProdutos.dialogo.exclusaoSucesso"));
            carregarDados();
        }
    }
}