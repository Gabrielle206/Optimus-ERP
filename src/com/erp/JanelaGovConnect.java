package com.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class JanelaGovConnect extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private GovConnectClient govConnectClient;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton botaoGerarLicitacao, botaoEnviarProposta, botaoExcluir, botaoVoltar;

    private List<Licitacao> licitacoes = new ArrayList<>();
    private List<Proposta> propostas = new ArrayList<>();

    public JanelaGovConnect() {
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
        var locale = LanguageManager.getInstance().getCurrentLocale();

        modeloTabela.setRowCount(0);
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);

        for (Licitacao lic : licitacoes) {
            modeloTabela.addRow(new Object[]{
                    lic.getId(),
                    lic.getTitulo(),
                    lic.getOrgao(),
                    formatadorMoeda.format(lic.getValorEstimado())
            });
        }
    }

    private void gerarLicitacao() {
        List<Licitacao> geradas = LicitacaoGenerator.gerarLicitacoes();
        licitacoes.addAll(geradas);
        carregarDados();
    }

    private void enviarProposta() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma licitação para enviar proposta.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String licitacaoId = (String) modeloTabela.getValueAt(linha, 0);

        Proposta proposta = new Proposta(
                UUID.randomUUID().toString(),
                licitacaoId,
                "Empresa Optimus Ltda.",
                50000.0,
                "Descrição simulada.",
                LocalDate.now()
        );

        propostas.add(proposta);
        govConnectClient.enviarProposta(proposta);

        JOptionPane.showMessageDialog(this, "Proposta enviada com sucesso.",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirLicitacaoSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma licitação para excluir.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idParaExcluir = (String) modeloTabela.getValueAt(linhaSelecionada, 0);

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir esta licitação?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            licitacoes.removeIf(l -> l.getId().equals(idParaExcluir));
            JOptionPane.showMessageDialog(this, "Excluído com sucesso.");
            carregarDados();
        }
    }
}
