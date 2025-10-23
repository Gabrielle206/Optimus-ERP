package com.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class JanelaListagemPessoas extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private PessoaDAO pessoaDAO;
    private JTable tabelaPessoas;
    private DefaultTableModel modeloTabela;
    
    private JButton botaoNovo, botaoEditar, botaoExcluir, botaoVoltar, botaoConfigurarMFA; 
    private JComboBox<String> comboFiltro;
    private JLabel labelFiltro;

    public JanelaListagemPessoas() {
        this.pessoaDAO = new PessoaDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaListagemPessoas.this);
            }
        });
    }
    
    private void initComponents(){
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelFiltro = new JLabel();
        comboFiltro = new JComboBox<>();
        painelFiltro.add(labelFiltro);
        painelFiltro.add(comboFiltro);
        painelPrincipal.add(painelFiltro, BorderLayout.NORTH);
        
        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPessoas = new JTable(modeloTabela);
        painelPrincipal.add(new JScrollPane(tabelaPessoas), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoNovo = new JButton();
        botaoEditar = new JButton();
        botaoExcluir = new JButton();
        botaoConfigurarMFA = new JButton(); 
        botaoVoltar = new JButton();

        painelBotoes.add(botaoNovo);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoConfigurarMFA); 
        painelBotoes.add(botaoVoltar);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);

        comboFiltro.addActionListener(e -> carregarDados());
        botaoNovo.addActionListener(e -> abrirJanelaCadastro(null));
        botaoEditar.addActionListener(e -> editarPessoaSelecionada());
        botaoExcluir.addActionListener(e -> excluirPessoaSelecionada());
        botaoConfigurarMFA.addActionListener(e -> configurarMfaSelecionado()); 
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        
        setTitle(messages.getString("janelaListagemPessoas.titulo"));
        
        String[] colunas = {
            messages.getString("janelaListagemPessoas.coluna.id"),
            messages.getString("janelaListagemPessoas.coluna.nome"),
            messages.getString("janelaListagemPessoas.coluna.tipo"),
            messages.getString("janelaListagemPessoas.coluna.telefone")
        };
        modeloTabela.setColumnIdentifiers(colunas);

        botaoNovo.setText(messages.getString("janelaListagemPessoas.botao.novo"));
        botaoEditar.setText(messages.getString("janelaListagemPessoas.botao.editar"));
        botaoExcluir.setText(messages.getString("janelaListagemPessoas.botao.excluir"));
        botaoVoltar.setText(messages.getString("janelaListagemPessoas.botao.voltar"));
        botaoConfigurarMFA.setText(messages.getString("janelaListagemPessoas.botao.configurarMFA"));
        
        labelFiltro.setText(messages.getString("janelaListagemPessoas.label.filtrarPor"));
        int selectedIndex = comboFiltro.getSelectedIndex();
        comboFiltro.removeAllItems();
        comboFiltro.addItem(messages.getString("janelaListagemPessoas.filtro.todos"));
        comboFiltro.addItem(messages.getString("janelaCadastroPessoa.tipos.cliente"));
        comboFiltro.addItem(messages.getString("janelaCadastroPessoa.tipos.fornecedor"));
        comboFiltro.addItem(messages.getString("janelaCadastroPessoa.tipos.funcionario"));
        comboFiltro.setSelectedIndex(selectedIndex >= 0 ? selectedIndex : 0);

        carregarDados();
    }

    private void carregarDados() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        modeloTabela.setRowCount(0);
        
        List<Pessoa> pessoas = pessoaDAO.listarTodos();
        int filtroIndex = comboFiltro.getSelectedIndex();

        List<Pessoa> pessoasFiltradas;
        if (filtroIndex > 0) {
            pessoasFiltradas = pessoas.stream()
                                      .filter(p -> p.getTipo() == filtroIndex)
                                      .collect(Collectors.toList());
        } else {
            pessoasFiltradas = pessoas;
        }

        for (Pessoa p : pessoasFiltradas) {
            String tipo;
            switch (p.getTipo()) {
                case 1: tipo = messages.getString("janelaCadastroPessoa.tipos.cliente"); break;
                case 2: tipo = messages.getString("janelaCadastroPessoa.tipos.fornecedor"); break;
                case 3: tipo = messages.getString("janelaCadastroPessoa.tipos.funcionario"); break;
                default: tipo = "N/A";
            }
            modeloTabela.addRow(new Object[]{p.getId(), p.getNome(), tipo, p.getTelefone()});
        }
    }

    private void abrirJanelaCadastro(Pessoa pessoa) {
        new JanelaCadastroPessoa(this, pessoa).setVisible(true);
        carregarDados();
    }
    
    private void configurarMfaSelecionado() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaPessoas.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemPessoas.dialogo.selecioneFuncionario.mensagem"), messages.getString("janelaListagemPessoas.dialogo.selecioneFuncionario.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        Pessoa pessoaSelecionada = pessoaDAO.buscarPorId(id);

        if (pessoaSelecionada != null && pessoaSelecionada.getTipo() == 3) { 
            new JanelaConfiguracaoMFA(this, pessoaSelecionada).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemPessoas.dialogo.selecioneFuncionario.mensagem"), messages.getString("janelaListagemPessoas.dialogo.selecioneFuncionario.titulo"), JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirPessoaSelecionada() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaPessoas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemPessoas.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemPessoas.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idParaExcluir = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        int resposta = JOptionPane.showConfirmDialog(this,
                String.format(messages.getString("janelaListagemPessoas.dialogo.confirmarExclusao.mensagem"), idParaExcluir),
                messages.getString("janelaListagemPessoas.dialogo.confirmarExclusao.titulo"),
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            pessoaDAO.excluir(idParaExcluir);
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemPessoas.dialogo.exclusaoSucesso"));
            carregarDados();
        }
    }

    private void editarPessoaSelecionada() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaPessoas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemPessoas.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemPessoas.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        Pessoa pessoaSelecionada = pessoaDAO.buscarPorId(id);
        if (pessoaSelecionada != null) {
            abrirJanelaCadastro(pessoaSelecionada);
        }
    }
}
