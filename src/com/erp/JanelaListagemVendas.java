package com.erp;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class JanelaListagemVendas extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private PessoaDAO pessoaDAO;
    private TituloDAO tituloDAO;
    private ItemTituloDAO itemTituloDAO;
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;

    private JButton botaoVoltar;

    public JanelaListagemVendas() {
        this.tituloDAO = new TituloDAO();
        this.pessoaDAO = new PessoaDAO();
        this.itemTituloDAO = new ItemTituloDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaListagemVendas.this);
            }
        });
    }

    private void initComponents() {
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }
        JPanel painelPrincipal = new JPanel(new BorderLayout());

        modeloTabela = new DefaultTableModel();
        tabelaVendas = new JTable(modeloTabela);
        painelPrincipal.add(new JScrollPane(tabelaVendas), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoVoltar = new JButton();
        painelBotoes.add(botaoVoltar);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);

        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();

        setTitle(messages.getString("janelaListagemVendas.titulo"));

        String[] colunas = {
                messages.getString("janelaListagemVendas.coluna.cliente"),
                messages.getString("janelaListagemVendas.coluna.telefone"),
                messages.getString("janelaListagemVendas.coluna.produto"),
                messages.getString("janelaListagemVendas.coluna.quantidade"),
                messages.getString("janelaListagemVendas.coluna.valorUnitario"),
                messages.getString("janelaListagemVendas.coluna.valorTotal"),
                messages.getString("janelaListagemVendas.coluna.data")
        };
        modeloTabela.setColumnIdentifiers(colunas);

        botaoVoltar.setText(messages.getString("janelaListagemProdutos.botao.voltar"));

        carregarDados();
    }

    private void carregarDados() {
        var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();

        modeloTabela.setRowCount(0);

        Map<String, Pessoa> mapaPessoas = new HashMap<>();
        pessoaDAO.listarTodos().forEach(p -> mapaPessoas.put(p.getId(), p));

        List<Titulo> titulos = tituloDAO.listarTodos();
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);
        DateTimeFormatter formatadorData = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);

        for (Titulo titulo : titulos) {
            Pessoa pessoaAssociada = mapaPessoas.get(titulo.getPessoaId());
            if (pessoaAssociada == null) continue;

            if (titulo.getTipoTitulo().equals("A Receber")) {
                List<ItemTitulo> itens = itemTituloDAO.listarPorTituloId(titulo.getId());
                for (ItemTitulo item : itens) {
                    modeloTabela.addRow(new Object[]{
                            pessoaAssociada.getNome(),
                            pessoaAssociada.getTelefone(),
                            item.getDescricao(),
                            item.getQuantidade(),
                            formatadorMoeda.format(item.getValorUnitario()),
                            formatadorMoeda.format(item.getValorUnitario() * item.getQuantidade()),
                            titulo.getDate().format(formatadorData)
                    });
                }
            }
        }
    }
}
