package com.erp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.swing.*;

public class JanelaCadastroPessoa extends JDialog implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private JTextField campoId;
    private JTextField campoNome;
    private JComboBox<String> comboTipo;
    private JTextField campoTelefone;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmarSenha;
    private PessoaDAO pessoaDAO;
    private Pessoa pessoaExistente;
    
    private JLabel labelId, labelNome, labelTipo, labelTelefone, labelSenha, labelConfirmarSenha;
    private JButton botaoSalvar, botaoVoltar;
    private JPanel painelSenha;

    public JanelaCadastroPessoa(Frame parent, Pessoa pessoaParaEditar) {
        this(parent, pessoaParaEditar, false);
    }

    public JanelaCadastroPessoa(Frame parent, Pessoa pessoaParaEditar, boolean primeiroAdmin) {
        super(parent, true);
        this.pessoaExistente = pessoaParaEditar;
        this.pessoaDAO = new PessoaDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        if (pessoaExistente != null) {
            preencherFormulario();
        }
        
        if (primeiroAdmin) {
            ResourceBundle messages = LanguageManager.getInstance().getMessages();
            setTitle(messages.getString("janelaCadastroPessoa.titulo.cadastro") + " - " + messages.getString("janelaCadastroPessoa.tipos.administrador"));
            comboTipo.setSelectedIndex(3);
            comboTipo.setEnabled(false);
        }
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaCadastroPessoa.this);
            }
        });
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelFormularioCompleto = new JPanel();
        painelFormularioCompleto.setLayout(new BoxLayout(painelFormularioCompleto, BoxLayout.Y_AXIS)); 

        JPanel painelFormulario = new JPanel(new GridLayout(0, 2, 5, 5));
        labelId = new JLabel();
        painelFormulario.add(labelId);
        campoId = new JTextField();
        painelFormulario.add(campoId);

        labelNome = new JLabel();
        painelFormulario.add(labelNome);
        campoNome = new JTextField();
        painelFormulario.add(campoNome);

        labelTipo = new JLabel();
        painelFormulario.add(labelTipo);
        comboTipo = new JComboBox<>();
        painelFormulario.add(comboTipo);

        labelTelefone = new JLabel();
        painelFormulario.add(labelTelefone);
        campoTelefone = new JTextField();
        painelFormulario.add(campoTelefone);

        painelSenha = new JPanel(new GridLayout(0, 2, 5, 5));
        labelSenha = new JLabel();
        campoSenha = new JPasswordField();
        labelConfirmarSenha = new JLabel();
        campoConfirmarSenha = new JPasswordField();
        painelSenha.add(labelSenha);
        painelSenha.add(campoSenha);
        painelSenha.add(labelConfirmarSenha);
        painelSenha.add(campoConfirmarSenha);

        painelFormularioCompleto.add(painelFormulario);
        painelFormularioCompleto.add(painelSenha);

        painelPrincipal.add(painelFormularioCompleto, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton();
        botaoVoltar = new JButton();
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoVoltar);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);
        
        comboTipo.addActionListener(e -> atualizarVisibilidadeSenha());

        botaoSalvar.addActionListener(e -> salvarPessoa());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    private void atualizarVisibilidadeSenha() {
        boolean mostrar = comboTipo.getSelectedIndex() == 3;
        painelSenha.setVisible(mostrar);
        pack(); 
    }
    
    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        
        if (pessoaExistente != null) {
            setTitle(messages.getString("janelaCadastroPessoa.titulo.edicao"));
        } else {
            setTitle(messages.getString("janelaCadastroPessoa.titulo.cadastro"));
        }
        
        labelId.setText(messages.getString("janelaCadastroPessoa.label.id"));
        labelNome.setText(messages.getString("janelaCadastroPessoa.label.nome"));
        labelTipo.setText(messages.getString("janelaCadastroPessoa.label.tipo"));
        labelTelefone.setText(messages.getString("janelaCadastroPessoa.label.telefone"));
        labelSenha.setText(messages.getString("janelaCadastroPessoa.label.senha"));
        labelConfirmarSenha.setText(messages.getString("janelaCadastroPessoa.label.confirmarSenha"));
        botaoSalvar.setText(messages.getString("janelaCadastroPessoa.botao.salvar"));
        botaoVoltar.setText(messages.getString("janelaListagemPessoas.botao.voltar"));

        int selectedIndex = comboTipo.getSelectedIndex();
        comboTipo.removeAllItems();
        comboTipo.addItem(messages.getString("janelaCadastroPessoa.tipos.selecione"));
        comboTipo.addItem(messages.getString("janelaCadastroPessoa.tipos.cliente"));
        comboTipo.addItem(messages.getString("janelaCadastroPessoa.tipos.fornecedor"));
        comboTipo.addItem(messages.getString("janelaCadastroPessoa.tipos.funcionario"));
        comboTipo.setSelectedIndex(selectedIndex >= 0 ? selectedIndex : 0);
        
        atualizarVisibilidadeSenha();
    }

    private void preencherFormulario() {
        campoId.setText(pessoaExistente.getId());
        campoId.setEditable(false);
        campoNome.setText(pessoaExistente.getNome());
        comboTipo.setSelectedIndex(pessoaExistente.getTipo());
        campoTelefone.setText(pessoaExistente.getTelefone());
    }

    private void salvarPessoa() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        String id = campoId.getText();
        String nome = campoNome.getText();
        int tipo = comboTipo.getSelectedIndex();
        String telefone = campoTelefone.getText();
        char[] senha = campoSenha.getPassword();
        char[] confirmarSenha = campoConfirmarSenha.getPassword();
        boolean isFuncionario = tipo == 3;

        if (nome.isEmpty() || tipo == 0) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroPessoa.dialogo.erroValidacao.mensagem"), messages.getString("janelaCadastroPessoa.dialogo.erroValidacao.titulo"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isFuncionario) {
            if (pessoaExistente == null && senha.length == 0) {
                JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroPessoa.dialogo.senhaObrigatoria.mensagem"), messages.getString("janelaCadastroPessoa.dialogo.senhasNaoCoincidem.titulo"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (senha.length > 0 && !Arrays.equals(senha, confirmarSenha)) {
                JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroPessoa.dialogo.senhasNaoCoincidem.mensagem"), messages.getString("janelaCadastroPessoa.dialogo.senhasNaoCoincidem.titulo"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (pessoaExistente == null) {
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroPessoa.dialogo.idObrigatorio.mensagem"), messages.getString("janelaCadastroPessoa.dialogo.erroValidacao.titulo"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            Pessoa novaPessoa = new Pessoa(id, tipo, nome, telefone);
            if (isFuncionario) {
                novaPessoa.setSenha(new String(senha));
            }
            pessoaDAO.salvar(novaPessoa);
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroPessoa.dialogo.salvoSucesso"));
        } else {
            pessoaExistente.setNome(nome);
            pessoaExistente.setTipo(tipo);
            pessoaExistente.setTelefone(telefone);
            if (isFuncionario && senha.length > 0) {
                pessoaExistente.setSenha(new String(senha));
            }
            pessoaDAO.atualizar(pessoaExistente);
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroPessoa.dialogo.atualizadoSucesso"));
        }
        
        LanguageManager.getInstance().removeObserver(this);
        dispose();
    }
}
