package com.erp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

public class JanelaCadastroContrato extends JDialog implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private JComboBox<Pessoa> comboFornecedor;
    private JTextField campoObjeto, campoValor, campoDataVencimento;
    private JLabel labelFornecedor, labelObjeto, labelValor, labelDataVencimento;
    private JButton botaoSalvar, botaoVoltar;

    private ContratoDAO contratoDAO;
    private PessoaDAO pessoaDAO;
    private Contrato contratoExistente;

    public JanelaCadastroContrato(Frame parent, Contrato contratoParaEditar) {
        super(parent, true);
        this.contratoExistente = contratoParaEditar;
        this.contratoDAO = new ContratoDAO();
        this.pessoaDAO = new PessoaDAO();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        carregarFornecedores();

        if (contratoExistente != null) {
            preencherFormulario();
        } else {
            campoDataVencimento.setText(LocalDate.now().plusYears(1).toString());
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaCadastroContrato.this);
            }
        });
    }

    private void initComponents() {
        setSize(500, 300);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }


        JPanel painelFormulario = new JPanel(new GridLayout(0, 2, 10, 10));
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelFornecedor = new JLabel();
        painelFormulario.add(labelFornecedor);
        comboFornecedor = new JComboBox<>();
        painelFormulario.add(comboFornecedor);

        labelObjeto = new JLabel();
        painelFormulario.add(labelObjeto);
        campoObjeto = new JTextField();
        painelFormulario.add(campoObjeto);

        labelValor = new JLabel();
        painelFormulario.add(labelValor);
        campoValor = new JTextField();
        painelFormulario.add(campoValor);

        labelDataVencimento = new JLabel();
        painelFormulario.add(labelDataVencimento);
        campoDataVencimento = new JTextField();
        painelFormulario.add(campoDataVencimento);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton();
        botaoVoltar = new JButton();
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoVoltar);

        add(painelFormulario, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        botaoSalvar.addActionListener(e -> salvarContrato());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();

        if (contratoExistente != null) {
            setTitle(messages.getString("janelaCadastroContrato.titulo.edicao"));
        } else {
            setTitle(messages.getString("janelaCadastroContrato.titulo.cadastro"));
        }

        labelFornecedor.setText(messages.getString("janelaCadastroContrato.label.fornecedor"));
        labelObjeto.setText(messages.getString("janelaCadastroContrato.label.objeto"));
        labelValor.setText(messages.getString("janelaCadastroContrato.label.valor"));
        labelDataVencimento.setText(messages.getString("janelaCadastroContrato.label.dataVencimento"));
        botaoSalvar.setText(messages.getString("janelaCadastroPessoa.botao.salvar"));
        botaoVoltar.setText(messages.getString("janelaListagemPessoas.botao.voltar"));
        
        carregarFornecedores();
        preencherFormulario();
    }

    private void carregarFornecedores() {
        List<Pessoa> todasPessoas = pessoaDAO.listarTodos();
        List<Pessoa> fornecedores = todasPessoas.stream()
            .filter(p -> p.getTipo() == 2)
            .collect(Collectors.toList());
        
        comboFornecedor.removeAllItems();
        for (Pessoa f : fornecedores) {
            comboFornecedor.addItem(f);
        }
    }

    private void preencherFormulario() {
        if (contratoExistente != null) {
            campoObjeto.setText(contratoExistente.getObjetoContrato());
            campoValor.setText(String.valueOf(contratoExistente.getValor()));
            campoDataVencimento.setText(contratoExistente.getDataVencimento().toString());

            for (int i = 0; i < comboFornecedor.getItemCount(); i++) {
                if (comboFornecedor.getItemAt(i).getId().equals(contratoExistente.getFornecedorId())) {
                    comboFornecedor.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void salvarContrato() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        
        Pessoa fornecedorSelecionado = (Pessoa) comboFornecedor.getSelectedItem();
        String valorStr = campoValor.getText().trim();
        String dataVencStr = campoDataVencimento.getText().trim();

        if (fornecedorSelecionado == null || valorStr.isEmpty() || dataVencStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroContrato.dialogo.erroValidacao.mensagem"), messages.getString("janelaCadastroContrato.dialogo.erroValidacao.titulo"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        double valor;
        LocalDate dataVencimento;

        try {
            valor = Double.parseDouble(valorStr.replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroContrato.dialogo.erroFormato.valor"), messages.getString("janelaCadastroContrato.dialogo.erroFormato.titulo"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            dataVencimento = LocalDate.parse(dataVencStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroContrato.dialogo.erroFormato.data"), messages.getString("janelaCadastroContrato.dialogo.erroFormato.titulo"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String objeto = campoObjeto.getText().trim();

        if (contratoExistente == null) {
            String id = UUID.randomUUID().toString();
            Contrato novoContrato = new Contrato(
                id,
                fornecedorSelecionado.getId(),
                objeto,
                valor,
                LocalDate.now(),
                dataVencimento,
                messages.getString("janelaCadastroContrato.status.emElaboracao")
            );
            contratoDAO.salvar(novoContrato);
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroContrato.dialogo.salvoSucesso"));
        } else {
            contratoExistente.setFornecedorId(fornecedorSelecionado.getId());
            contratoExistente.setObjetoContrato(objeto);
            contratoExistente.setValor(valor);
            contratoExistente.setDataVencimento(dataVencimento);
            contratoDAO.atualizar(contratoExistente);
            JOptionPane.showMessageDialog(this, messages.getString("janelaCadastroContrato.dialogo.atualizadoSucesso"));
        }

        LanguageManager.getInstance().removeObserver(this);
        dispose();
    }
}
