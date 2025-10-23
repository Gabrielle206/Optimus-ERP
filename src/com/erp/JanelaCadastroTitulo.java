package com.erp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class JanelaCadastroTitulo extends JDialog implements LanguageObserver {

    private static final long serialVersionUID = 1L;
    private JComboBox<Pessoa> comboPessoas;
    private JComboBox<String> comboTipoTitulo;
    private JCheckBox checkPago;
    private JButton botaoAdicionarProduto, botaoAdicionarServico, botaoRemoverItem, botaoSalvar, botaoVoltar;
    private JTable tabelaItens;
    private DefaultTableModel modeloTabelaItens;
    private JLabel labelValorTotal, labelPessoa, labelTipo, labelPago;
    private TitledBorder painelMestreBorder, painelDetalhesBorder;
    private TituloDAO tituloDAO;
    private PessoaDAO pessoaDAO;
    private ProdutoDAO produtoDAO;
    private ItemTituloDAO itemTituloDAO;
    private List<ItemTitulo> itensDoTitulo = new ArrayList<>();
    private List<Produto> produtosDisponiveis;
    private Titulo tituloExistente;

    public JanelaCadastroTitulo(Frame parent, Titulo tituloParaEditar) {
        super(parent, true);
        this.tituloExistente = tituloParaEditar;

        this.tituloDAO = new TituloDAO();
        this.pessoaDAO = new PessoaDAO();
        this.produtoDAO = new ProdutoDAO();
        this.itemTituloDAO = new ItemTituloDAO();

        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        if (tituloExistente != null) {
            preencherFormulario();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaCadastroTitulo.this);
            }
        });
    }

    private void initComponents() {
        setSize(800, 600);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }

        JPanel painelMestre = new JPanel(new GridLayout(3, 2, 10, 10));
        painelMestreBorder = BorderFactory.createTitledBorder("");
        painelMestre.setBorder(painelMestreBorder);

        labelPessoa = new JLabel();
        painelMestre.add(labelPessoa);
        comboPessoas = new JComboBox<>();
        painelMestre.add(comboPessoas);

        labelTipo = new JLabel();
        painelMestre.add(labelTipo);
        comboTipoTitulo = new JComboBox<>();
        painelMestre.add(comboTipoTitulo);

        labelPago = new JLabel();
        painelMestre.add(labelPago);
        checkPago = new JCheckBox();
        painelMestre.add(checkPago);

        carregarPessoasEProdutos();

        JPanel painelDetalhes = new JPanel(new BorderLayout());
        painelDetalhesBorder = BorderFactory.createTitledBorder("");
        painelDetalhes.setBorder(painelDetalhesBorder);

        modeloTabelaItens = new DefaultTableModel();
        tabelaItens = new JTable(modeloTabelaItens);
        painelDetalhes.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        JPanel painelBotoesItens = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botaoAdicionarProduto = new JButton();
        botaoAdicionarServico = new JButton();
        botaoRemoverItem = new JButton();
        painelBotoesItens.add(botaoAdicionarProduto);
        painelBotoesItens.add(botaoAdicionarServico);
        painelBotoesItens.add(botaoRemoverItem);
        painelDetalhes.add(painelBotoesItens, BorderLayout.SOUTH);

        JPanel painelInferior = new JPanel(new BorderLayout());
        labelValorTotal = new JLabel();
        labelValorTotal.setFont(new Font("Arial", Font.BOLD, 16));
        painelInferior.add(labelValorTotal, BorderLayout.WEST);

        JPanel painelBotoesSalvar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton();
        botaoVoltar = new JButton();
        painelBotoesSalvar.add(botaoSalvar);
        painelBotoesSalvar.add(botaoVoltar);
        painelInferior.add(painelBotoesSalvar, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(painelMestre, BorderLayout.NORTH);
        getContentPane().add(painelDetalhes, BorderLayout.CENTER);
        getContentPane().add(painelInferior, BorderLayout.SOUTH);

        botaoAdicionarProduto.addActionListener(e -> adicionarItemProduto());
        botaoAdicionarServico.addActionListener(e -> adicionarItemServico());
        botaoRemoverItem.addActionListener(e -> removerItem());
        botaoSalvar.addActionListener(e -> salvarTituloCompleto());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();

        setTitle(tituloExistente == null ? messages.getString("janelaCadastroTitulo.titulo.cadastro") : messages.getString("janelaCadastroTitulo.titulo.edicao"));

        painelMestreBorder.setTitle(messages.getString("janelaCadastroTitulo.painel.dados"));
        painelDetalhesBorder.setTitle(messages.getString("janelaCadastroTitulo.painel.itens"));

        labelPessoa.setText(messages.getString("janelaCadastroTitulo.label.pessoa"));
        labelTipo.setText(messages.getString("janelaCadastroTitulo.label.tipo"));
        labelPago.setText(messages.getString("janelaCadastroTitulo.label.pago"));

        String[] colunas = {
                messages.getString("janelaCadastroTitulo.coluna.descricao"),
                messages.getString("janelaCadastroTitulo.coluna.quantidade"),
                messages.getString("janelaCadastroTitulo.coluna.valorUnitario"),
                messages.getString("janelaCadastroTitulo.coluna.valorTotal")
        };
        modeloTabelaItens.setColumnIdentifiers(colunas);

        botaoAdicionarProduto.setText(messages.getString("janelaCadastroTitulo.botao.addProduto"));
        botaoAdicionarServico.setText(messages.getString("janelaCadastroTitulo.botao.addServico"));
        botaoRemoverItem.setText(messages.getString("janelaCadastroTitulo.botao.removerItem"));
        botaoSalvar.setText(messages.getString("janelaCadastroTitulo.botao.salvar"));
        botaoVoltar.setText(messages.getString("janelaListagemProdutos.botao.voltar"));

        comboTipoTitulo.removeAllItems();
        comboTipoTitulo.addItem(messages.getString("janelaCadastroTitulo.tipo.receber"));
        comboTipoTitulo.addItem(messages.getString("janelaCadastroTitulo.tipo.pagar"));

        atualizarTabelaEValorTotal();
        repaint();
    }

    private void preencherFormulario() {
        if (tituloExistente != null) {
            checkPago.setSelected(tituloExistente.isPago());

            ResourceBundle messages = LanguageManager.getInstance().getMessages();
            String tipoReceber = messages.getString("janelaCadastroTitulo.tipo.receber");
            if (tituloExistente.getTipoTitulo().equals(tipoReceber)) {
                comboTipoTitulo.setSelectedIndex(0);
            } else {
                comboTipoTitulo.setSelectedIndex(1);
            }

            for (int i = 0; i < comboPessoas.getItemCount(); i++) {
                if (comboPessoas.getItemAt(i).getId().equals(tituloExistente.getPessoaId())) {
                    comboPessoas.setSelectedIndex(i);
                    break;
                }
            }
            this.itensDoTitulo = itemTituloDAO.listarPorTituloId(tituloExistente.getId());
            atualizarTabelaEValorTotal();
        }
    }

    private void carregarPessoasEProdutos() {
        List<Pessoa> pessoas = pessoaDAO.listarTodos();
        comboPessoas.removeAllItems();
        for (Pessoa p : pessoas) { comboPessoas.addItem(p); }
        this.produtosDisponiveis = produtoDAO.listarTodos();
    }

    private void atualizarTabelaEValorTotal() {
        var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();
        var messages = languageManager.getMessages();

        modeloTabelaItens.setRowCount(0);
        double valorTotal = 0;
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);

        for (ItemTitulo item : itensDoTitulo) {
            double totalItem = item.getQuantidade() * item.getValorUnitario();
            modeloTabelaItens.addRow(new Object[]{
                    item.getDescricao(),
                    item.getQuantidade(),
                    formatadorMoeda.format(item.getValorUnitario()),
                    formatadorMoeda.format(totalItem)
            });
            valorTotal += totalItem;
        }
        labelValorTotal.setText(messages.getString("janelaCadastroTitulo.label.valorTotal") + " " + formatadorMoeda.format(valorTotal));
    }

    private void adicionarItemProduto() {
        var messages = LanguageManager.getInstance().getMessages();
        if (produtosDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.nenhumProduto"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JComboBox<Produto> comboProdutos = new JComboBox<>(produtosDisponiveis.toArray(new Produto[0]));
        JTextField campoQuantidade = new JTextField("1");

        JPanel painelAdicionar = new JPanel(new GridLayout(2, 2, 5, 5));
        painelAdicionar.add(new JLabel(messages.getString("janelaCadastroTitulo.dialogo.addProduto.label.produto")));
        painelAdicionar.add(comboProdutos);
        painelAdicionar.add(new JLabel(messages.getString("janelaCadastroTitulo.dialogo.addProduto.label.quantidade")));
        painelAdicionar.add(campoQuantidade);

        int result = JOptionPane.showConfirmDialog(this, painelAdicionar, messages.getString("janelaCadastroTitulo.dialogo.addProduto.titulo"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Produto produtoSelecionado = (Produto) comboProdutos.getSelectedItem();
                int quantidade = Integer.parseInt(campoQuantidade.getText());
                String tipoTitulo = (String) comboTipoTitulo.getSelectedItem();
                double valorUnitario = messages.getString("janelaCadastroTitulo.tipo.pagar").equals(tipoTitulo) ? produtoSelecionado.getPrecoCusto() : produtoSelecionado.getPrecoVenda();

                itensDoTitulo.add(new ItemTitulo(0, "", produtoSelecionado.getId(), produtoSelecionado.getNome(), quantidade, valorUnitario));
                atualizarTabelaEValorTotal();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.erroFormato.mensagem"), messages.getString("janelaCadastroTitulo.dialogo.erroFormato.titulo"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void adicionarItemServico() {
        var messages = LanguageManager.getInstance().getMessages();
        JTextField campoDescricao = new JTextField();
        JTextField campoValor = new JTextField();

        JPanel painelAdicionar = new JPanel(new GridLayout(2, 2, 5, 5));
        painelAdicionar.add(new JLabel(messages.getString("janelaCadastroTitulo.dialogo.addServico.label.descricao")));
        painelAdicionar.add(campoDescricao);
        painelAdicionar.add(new JLabel(messages.getString("janelaCadastroTitulo.dialogo.addServico.label.valor")));
        painelAdicionar.add(campoValor);

        int result = JOptionPane.showConfirmDialog(this, painelAdicionar, messages.getString("janelaCadastroTitulo.dialogo.addServico.titulo"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String descricao = campoDescricao.getText();
                if (descricao.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.descricaoObrigatoria"), "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double valor = Double.parseDouble(campoValor.getText().replace(",","."));
                itensDoTitulo.add(new ItemTitulo(0, "", null, descricao, 1, valor));
                atualizarTabelaEValorTotal();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.valorInvalido"), messages.getString("janelaCadastroTitulo.dialogo.erroFormato.titulo"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerItem() {
        var messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaItens.getSelectedRow();
        if (linhaSelecionada >= 0) {
            itensDoTitulo.remove(linhaSelecionada);
            atualizarTabelaEValorTotal();
        } else {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.selecioneParaRemover"), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void salvarTituloCompleto() {
        var messages = LanguageManager.getInstance().getMessages();
        Pessoa pessoaSelecionada = (Pessoa) comboPessoas.getSelectedItem();
        if (pessoaSelecionada == null || itensDoTitulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.erroValidacao.mensagem"), messages.getString("janelaCadastroTitulo.dialogo.erroValidacao.titulo"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipoSelecionado = (String) comboTipoTitulo.getSelectedItem();

        if (tituloExistente == null) {
            String idTitulo = UUID.randomUUID().toString();
            Titulo novoTitulo = new Titulo(idTitulo, checkPago.isSelected(), pessoaSelecionada.getId(), tipoSelecionado, java.time.LocalDate.now());
            tituloDAO.salvar(novoTitulo);
            for (ItemTitulo item : itensDoTitulo) {
                item.setIdTitulo(idTitulo);
                itemTituloDAO.salvar(item);
            }
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.salvoSucesso"));
        } else {
            tituloExistente.setPago(checkPago.isSelected());
            tituloExistente.setPessoaId(pessoaSelecionada.getId());
            tituloExistente.setTipoTitulo(tipoSelecionado);
            tituloDAO.atualizar(tituloExistente);

            itemTituloDAO.excluirPorIdTitulo(tituloExistente.getId());
            for (ItemTitulo item : itensDoTitulo) {
                item.setIdTitulo(tituloExistente.getId());
                itemTituloDAO.salvar(item);
            }
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroTitulo.dialogo.atualizadoSucesso"));
        }

        LanguageManager.getInstance().removeObserver(this);
        dispose();
    }
}
