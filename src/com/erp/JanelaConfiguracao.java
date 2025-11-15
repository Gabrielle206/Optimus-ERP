package com.erp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;

public class JanelaConfiguracao extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> comboPlanos;
    private JLabel labelPlano;
    private JButton botaoSalvar, botaoVoltar, botaoVerificarIntegridade;

    private PlanoAssinaturaManager planoManager;
    private ConfiguracaoDAO configuracaoDAO;
    private HealthCheckManager healthCheckManager;

    public JanelaConfiguracao() {
        this.planoManager = PlanoAssinaturaManager.getInstance(); 
        this.configuracaoDAO = new ConfiguracaoDAO(); 
        this.healthCheckManager = HealthCheckManager.getInstance(); 
        LanguageManager.getInstance().addObserver(this); 

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaConfiguracao.this); 
            }
        });
    }

    private void initComponents() {
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        URL iconURL = getClass().getResource("/iconeerp/logo1.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Erro: Não foi possível encontrar o ícone 'logo1.png'");
        }
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        labelPlano = new JLabel();
        painelPrincipal.add(labelPlano, BorderLayout.WEST);

        comboPlanos = new JComboBox<>();
        painelPrincipal.add(comboPlanos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new BorderLayout());
        
        JPanel painelBotoesEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botaoVerificarIntegridade = new JButton();
        painelBotoesEsquerda.add(botaoVerificarIntegridade);

        JPanel painelBotoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton();
        botaoVoltar = new JButton();
        painelBotoesDireita.add(botaoSalvar);
        painelBotoesDireita.add(botaoVoltar);
        
        painelBotoes.add(painelBotoesEsquerda, BorderLayout.WEST);
        painelBotoes.add(painelBotoesDireita, BorderLayout.EAST);

        add(painelPrincipal, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        botaoVerificarIntegridade.addActionListener(e -> executarHealthCheck());
        botaoSalvar.addActionListener(e -> salvarConfiguracao());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this); 
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages(); 
        setTitle(messages.getString("janelaConfiguracao.titulo"));
        labelPlano.setText(messages.getString("janelaConfiguracao.label.plano"));
        botaoSalvar.setText(messages.getString("janelaConfiguracao.botao.salvar"));
        botaoVoltar.setText(messages.getString("janelaListagemProdutos.botao.voltar"));
        botaoVerificarIntegridade.setText(messages.getString("janelaConfiguracao.botao.verificar"));

        carregarDadosPlano();
    }

    private void carregarDadosPlano() {
        String planoAtual = planoManager.getPlanoAtual(); 
        String[] planosDisponiveis = planoManager.getNomesPlanosDisponiveis(); 

        comboPlanos.removeAllItems();
        for (String plano : planosDisponiveis) {
            comboPlanos.addItem(plano);
        }
        comboPlanos.setSelectedItem(planoAtual);
    }

    private void salvarConfiguracao() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages(); 
        String planoSelecionado = (String) comboPlanos.getSelectedItem();

        if (planoSelecionado != null) {
            configuracaoDAO.salvarValor("plano_assinatura", planoSelecionado); 
            
            JOptionPane.showMessageDialog(this,
                    messages.getString("janelaConfiguracao.dialogo.salvoSucesso.mensagem"),
                    messages.getString("janelaConfiguracao.dialogo.salvoSucesso.titulo"),
                    JOptionPane.INFORMATION_MESSAGE);
            
            LanguageManager.getInstance().removeObserver(this); 
            dispose();
        }
    }
    
    private void executarHealthCheck() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages(); 
        
        List<String> resultados = healthCheckManager.runChecks(); 

        StringBuilder relatorioBody = new StringBuilder();
        boolean falhaEncontrada = false;

        for (String res : resultados) {
            if (res.startsWith("FALHA")) {
                falhaEncontrada = true;
                relatorioBody.append("<font color='red'><b>").append(res).append("</b></font><br>");
            } else {
                relatorioBody.append("<font color='green'>").append(res).append("</font><br>");
            }
        }

        String titulo = messages.getString("janelaConfiguracao.dialogo.healthCheck.titulo");
        String mensagemCabecalho;
        int tipoMensagem;

        if (falhaEncontrada) {
            mensagemCabecalho = messages.getString("janelaConfiguracao.dialogo.healthCheck.falha");
            tipoMensagem = JOptionPane.ERROR_MESSAGE;
        } else {
            mensagemCabecalho = messages.getString("janelaConfiguracao.dialogo.healthCheck.sucesso");
            tipoMensagem = JOptionPane.INFORMATION_MESSAGE;
        }

        String htmlFinal = "<html><body style='width: 350px;'>" +
                           "<b>" + mensagemCabecalho + "</b>" +
                           "<br><br>" + 
                           relatorioBody.toString() +
                           "</body></html>";

        JLabel relatorioLabel = new JLabel(htmlFinal);
        
        JOptionPane.showMessageDialog(this,
                relatorioLabel,
                titulo,
                tipoMensagem);
    }
}
