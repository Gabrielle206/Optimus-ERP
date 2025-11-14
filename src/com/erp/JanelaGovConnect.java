package com.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class JanelaGovConnect extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private LicitacaoDAO licitacaoDAO;
    private PropostaDAO propostaDAO;
    private GovConnectClient govConnectClient;
    
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton botaoGerarLicitacao, botaoEnviarProposta, botaoExcluir, botaoVoltar;

    public JanelaGovConnect() {
        this.licitacaoDAO = new LicitacaoDAO();
        this.propostaDAO = new PropostaDAO();
        this.govConnectClient = GovConnectClient.getInstance();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaGovConnect.this);
            }
        });
    }

    private void initComponents() {
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon("iconeerp/logo1.png");
        setIconImage(img.getImage());

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        painelPrincipal.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new BorderLayout());
        
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botaoGerarLicitacao = new JButton();
        botaoEnviarProposta = new JButton();
        painelBotoesAcao.add(botaoGerarLicitacao);
        painelBotoesAcao.add(botaoEnviarProposta);
        
        JPanel painelBotoesCRUD = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoVoltar = new JButton();
        botaoExcluir = new JButton();
        painelBotoesCRUD.add(botaoVoltar);
        painelBotoesCRUD.add(botaoExcluir);

        painelBotoes.add(painelBotoesAcao, BorderLayout.WEST);
        painelBotoes.add(painelBotoesCRUD, BorderLayout.EAST);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);

        botaoGerarLicitacao.addActionListener(e -> gerarLicitacao());
        botaoEnviarProposta.addActionListener(e -> enviarProposta());
        botaoExcluir.addActionListener(e -> excluirLicitacaoSelecionado());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        setTitle("GovConnect"); 

        String[] colunas = {
            "ID",
            "Título", 
            "Órgão", 
            "Valor Estimado" 
        };
        modeloTabela.setColumnIdentifiers(colunas);

        botaoGerarLicitacao.setText("Gerar Licitação"); 
        botaoEnviarProposta.setText("Enviar Proposta"); 
        botaoVoltar.setText("Voltar"); 
        botaoExcluir.setText("Excluir");

        carregarDados();
    }

    private void carregarDados() {
        var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();
        var messages = languageManager.getMessages();

        modeloTabela.setRowCount(0);

        List<Licitacao> licitacoes = licitacaoDAO.listarTodas();
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);

        for (Licitacao licitacao : licitacoes) {
            modeloTabela.addRow(new Object[]{
                licitacao.getId(),
                licitacao.getTitulo(),
                licitacao.getOrgao(),
                formatadorMoeda.format(licitacao.getValorEstimado())
            });
        }
    }

    private void gerarLicitacao() {
        List<Licitacao> licitacoes = LicitacaoGenerator.gerarLicitacoes();
        for (Licitacao licitacao : licitacoes) {
            licitacaoDAO.salvar(licitacao);
        }
        carregarDados();
    }

    private void enviarProposta() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma licitação para enviar proposta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String licitacaoId = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        String idProposta = UUID.randomUUID().toString();
        String empresa = "Empresa Optimus Ltda.";
        double valor = 50000.0;
        String descricao = "Descrição simulada.";
        LocalDate dataEnvio = LocalDate.now();

        Proposta proposta = new Proposta(idProposta, licitacaoId, empresa, valor, descricao, dataEnvio);
        propostaDAO.salvar(proposta);
        govConnectClient.enviarProposta(proposta);

        JOptionPane.showMessageDialog(this, "Proposta enviada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void excluirLicitacaoSelecionado() {
        var messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
        	JOptionPane.showMessageDialog(this, 
                    messages.getString("janelaHistoricoProposta.dialogo.selecioneParaExcluir.mensagem"), 
                    messages.getString("janelaHistoricoProposta.dialogo.selecioneParaExcluir.titulo"),   
                    JOptionPane.WARNING_MESSAGE);
                return;
        }
        String idParaExcluir = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        int resposta = JOptionPane.showConfirmDialog(this,
                messages.getString("janelaHistoricoProposta.dialogo.confirmarExclusao.mensagem"),
                messages.getString("janelaHistoricoProposta.dialogo.confirmarExclusao.titulo"),
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            licitacaoDAO.excluir(idParaExcluir);
            JOptionPane.showMessageDialog(this, messages.getString("janelaHistoricoProposta.dialogo.exclusaoSucesso"));
            carregarDados();
        }
    }

}