package com.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

public class JanelaListagemTitulos extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private TituloDAO tituloDAO;
    private PessoaDAO pessoaDAO;
    private ItemTituloDAO itemTituloDAO;
    private JTable tabelaTitulos;
    private DefaultTableModel modeloTabela;

    private JButton botaoNovo, botaoEditar, botaoExcluir, botaoVoltar;

    public JanelaListagemTitulos() {
        this.tituloDAO = new TituloDAO();
        this.pessoaDAO = new PessoaDAO();
        this.itemTituloDAO = new ItemTituloDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaListagemTitulos.this);
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

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaTitulos = new JTable(modeloTabela);
        painelPrincipal.add(new JScrollPane(tabelaTitulos), BorderLayout.CENTER);

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
        botaoEditar.addActionListener(e -> editarTituloSelecionado());
        botaoExcluir.addActionListener(e -> excluirTituloSelecionado());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();

        setTitle(messages.getString("janelaListagemTitulos.titulo"));

        String[] colunas = {
                messages.getString("janelaListagemTitulos.coluna.id"),
                messages.getString("janelaListagemTitulos.coluna.pessoa"),
                messages.getString("janelaListagemTitulos.coluna.valorTotal"),
                messages.getString("janelaListagemTitulos.coluna.tipo"),
                messages.getString("janelaListagemTitulos.coluna.status"),
                messages.getString("janelaListagemTitulos.coluna.data")
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
        var messages = languageManager.getMessages();

        modeloTabela.setRowCount(0);

        Map<String, String> mapaNomesPessoas = new HashMap<>();
        pessoaDAO.listarTodos().forEach(p -> mapaNomesPessoas.put(p.getId(), p.getNome()));

        List<Titulo> titulos = tituloDAO.listarTodos();
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);
        DateTimeFormatter formatadorData = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);

        for (Titulo titulo : titulos) {
            double valorTotal = itemTituloDAO.listarPorTituloId(titulo.getId()).stream()
                    .mapToDouble(item -> item.getQuantidade() * item.getValorUnitario())
                    .sum();
            modeloTabela.addRow(new Object[]{
                    titulo.getId(),
                    mapaNomesPessoas.getOrDefault(titulo.getPessoaId(), "N/A"),
                    formatadorMoeda.format(valorTotal),
                    titulo.getTipoTitulo(),
                    titulo.isPago() ? messages.getString("janelaListagemTitulos.status.pago") : messages.getString("janelaListagemTitulos.status.pendente"),
                    titulo.getDate().format(formatadorData)
            });
        }
    }

    private void abrirJanelaCadastro(Titulo titulo) {
        new JanelaCadastroTitulo(this, titulo).setVisible(true);
        carregarDados();
    }

    private void editarTituloSelecionado() {
        var messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaTitulos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tabelaTitulos.getValueAt(linhaSelecionada, 0);
        Titulo tituloSelecionado = tituloDAO.listarTodos().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        if (tituloSelecionado != null) {
            abrirJanelaCadastro(tituloSelecionado);
        }
    }

    private void excluirTituloSelecionado() {
        var messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaTitulos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idParaExcluir = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        int resposta = JOptionPane.showConfirmDialog(this,
                messages.getString("janelaListagemTitulos.dialogo.confirmarExclusao.mensagem"),
                messages.getString("janelaListagemTitulos.dialogo.confirmarExclusao.titulo"),
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            tituloDAO.excluir(idParaExcluir);
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemTitulos.dialogo.exclusaoSucesso"));
            carregarDados();
        }
    }
}
