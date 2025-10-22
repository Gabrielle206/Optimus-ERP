package com.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class JanelaListagemContratos extends JFrame implements LanguageObserver {
	private static final long serialVersionUID = 1L;
	
	private ContratoDAO contratoDAO;
	private JTable tabelaContratos;
	private DefaultTableModel modeloTabela;
	
	private JButton botaoNovo, botaoEditar, botaoExcluir, botaoAssinarEmpresa, botaoAssinarFornecedor, botaoValidarNota, botaoVoltar;
	
	public JanelaListagemContratos() {
		System.out.println("Janela de contratos foi criada!");
		this.contratoDAO = new ContratoDAO();
		LanguageManager.getInstance().addObserver(this);
		
		initComponents();
		updateLanguage();
		
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) {
				LanguageManager.getInstance().removeObserver(JanelaListagemContratos.this);
			}
		});}
	
	private void initComponents() {
		setSize(800, 600);
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
        
        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
		}
};
	tabelaContratos = new JTable(modeloTabela);
	painelPrincipal.add(new JScrollPane(tabelaContratos), BorderLayout.CENTER);

	JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
    botaoNovo = new JButton();
    botaoEditar = new JButton();
    botaoExcluir = new JButton();
    botaoAssinarEmpresa = new JButton();
    botaoAssinarFornecedor = new JButton();
    botaoValidarNota = new JButton();
    botaoVoltar = new JButton();

    painelBotoes.add(botaoNovo);
    painelBotoes.add(botaoEditar);
    painelBotoes.add(botaoExcluir);
    painelBotoes.add(botaoAssinarEmpresa);
    painelBotoes.add(botaoAssinarFornecedor);
    painelBotoes.add(botaoValidarNota);
    painelBotoes.add(botaoVoltar);
    painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

    add(painelPrincipal);
    
    botaoNovo.addActionListener(e -> abrirJanelaCadastro(null));
    botaoEditar.addActionListener(e -> editarContratoSelecionado());
    botaoExcluir.addActionListener(e -> excluirContratoSelecionado());
    botaoAssinarEmpresa.addActionListener(e -> assinaturaEmpresa());
    botaoAssinarFornecedor.addActionListener(e -> assinaturaFornecedor());
    botaoValidarNota.addActionListener(e -> validarNota());
    botaoVoltar.addActionListener(e -> {
        LanguageManager.getInstance().removeObserver(this);
        dispose();
    });
}

	@Override
	public void updateLanguage() {
		 ResourceBundle messages = LanguageManager.getInstance().getMessages();
	        
	        setTitle(messages.getString("janelaListagemContratos.titulo"));
	        
	        String[] colunas = {
	            messages.getString("janelaListagemContratos.coluna.id"),
	            messages.getString("janelaListagemContratos.coluna.fornecedor"),
	            messages.getString("janelaListagemContratos.coluna.objeto"),
	            messages.getString("janelaListagemContratos.coluna.valor"),
	            messages.getString("janelaListagemContratos.coluna.status"),
	            messages.getString("janelaListagemContratos.coluna.vencimento"),
	            messages.getString("janelaListagemContratos.coluna.txhash"),
	            messages.getString("janelaListagemContratos.coluna.enderecocontrato")
	        };
	        
	        modeloTabela.setColumnIdentifiers(colunas);

	        botaoNovo.setText(messages.getString("janelaListagemContratos.botao.novo"));
	        botaoEditar.setText(messages.getString("janelaListagemContratos.botao.editar"));
	        botaoExcluir.setText(messages.getString("janelaListagemContratos.botao.excluir"));
	        botaoAssinarEmpresa.setText(messages.getString("janelaListagemContratos.botao.assinaturaEmpresa"));
	        botaoAssinarFornecedor.setText(messages.getString("janelaListagemContratos.botao.assinaturaFornecedor"));
	        botaoValidarNota.setText(messages.getString("janelaListagemContratos.botao.validarNota"));
	        botaoVoltar.setText(messages.getString("janelaListagemContratos.botao.voltar"));
	        
	        carregarDados();
		}
	private void carregarDados() {
		var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();
        var messages = languageManager.getMessages();
        
        modeloTabela.setRowCount(0);
        Map<String, Contrato> mapaContratos = new HashMap<>();
        contratoDAO.listarTodos().forEach(p -> mapaContratos.put(p.getId(), p));
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);
        List<Contrato> contratos = contratoDAO.listarTodos();
        DateTimeFormatter formatadorData = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        
        for (Contrato c : contratos) {
           modeloTabela.addRow(new Object[]{
        		   c.getId(),
        		   c.getFornecedorId(),
        		   c.getObjetoContrato(),
        		   formatadorMoeda.format(c.getValor()),
        		   c.getStatus(),
        		   c.getDataVencimento(),
        		   c.getBlockchainTxHash(),
        		   c.getSmartContractAddress()}
        		   
           );}
	}
	
	private void abrirJanelaCadastro(Contrato contrato) {
        new JanelaCadastroContrato().setVisible(true);
        carregarDados();
    }
	
	private void editarContratoSelecionado() {
		var messages = LanguageManager.getInstance().getMessages();
		int linhaSelecionada = tabelaContratos.getSelectedRow();
		if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemContratos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemContratos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
		String id = (String) tabelaContratos.getValueAt(linhaSelecionada, 0);
        Contrato contratoSelecionado = contratoDAO.buscarPorId(id);
        if (contratoSelecionado != null) {
            abrirJanelaCadastro(contratoSelecionado);
            carregarDados();
        }
	}
	
	private void excluirContratoSelecionado() {
		 var messages = LanguageManager.getInstance().getMessages();
	        int linhaSelecionada = tabelaContratos.getSelectedRow();
	        if (linhaSelecionada == -1) {
	            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemContratos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemContratos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        String idParaExcluir = (String) tabelaContratos.getValueAt(linhaSelecionada, 0);
	        int resposta = JOptionPane.showConfirmDialog(this,
	                String.format(messages.getString("janelaListagemContratos.dialogo.confirmarExclusao.mensagem"), idParaExcluir),
	                messages.getString("janelaListagemContratos.dialogo.confirmarExclusao.titulo"),
	                JOptionPane.YES_NO_OPTION);
	        if (resposta == JOptionPane.YES_OPTION) {
	            contratoDAO.excluir(idParaExcluir);
	            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemContratos.dialogo.exclusaoSucesso"));
	            carregarDados();
	        }
	    }
	
	public void atualizarStatus(Contrato contrato) {
		if(contrato.getDataAssinaturaEmpresa() != null && contrato.getDataAssinaturaFornecedor() != null) {
			contrato.setStatus("Ativo");
		} else {
			contrato.setStatus("Em elaboração");
		}
		carregarDados();
	}
	
	private void assinaturaEmpresa() {
		int linhaSelecionada = tabelaContratos.getSelectedRow();
		if (linhaSelecionada == -1) {
	        JOptionPane.showMessageDialog(this, "Selecione um contrato para assinar pela empresa.");
	        return;
	    }
		String idContrato = (String) tabelaContratos.getValueAt(linhaSelecionada, 0);
	    Contrato contrato = contratoDAO.buscarPorId(idContrato);
		contrato.setDataAssinaturaEmpresa(LocalDate.now());
		atualizarStatus(contrato);
		contratoDAO.atualizar(contrato);
		carregarDados();
		JOptionPane.showMessageDialog(this, "Contrato assinado pela empresa com sucesso!");
	}
	
	private void assinaturaFornecedor() {
		int linhaSelecionada = tabelaContratos.getSelectedRow();
		 if (linhaSelecionada == -1) {
		        JOptionPane.showMessageDialog(this, "Selecione um contrato para assinar pelo fornecedor.");
		        return;
		    }
		String idContrato = (String) tabelaContratos.getValueAt(linhaSelecionada, 0);
		Contrato contrato = contratoDAO.buscarPorId(idContrato);
		contrato.setDataAssinaturaFornecedor(LocalDate.now());
		atualizarStatus(contrato);
		contratoDAO.atualizar(contrato);
		carregarDados();
		JOptionPane.showMessageDialog(this, "Contrato assinado pelo fornecedor com sucesso!");
	}
	
	private void validarNota() {
		var messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaContratos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemContratos.dialogo.selecioneParaValidarNota.mensagem"), messages.getString("janelaListagemContratos.dialogo.selecioneParaValidarNota.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idParaValidar = (String) tabelaContratos.getValueAt(linhaSelecionada, 0);
        int resposta = JOptionPane.showConfirmDialog(this,
                String.format(messages.getString("janelaListagemContratos.dialogo.confirmarValidacao.mensagem"), idParaValidar),
                messages.getString("janelaListagemContratos.dialogo.confirmarValidacao.titulo"),
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
        	Contrato contrato = contratoDAO.buscarPorId(idParaValidar);
        	contrato.setNotaFiscalValidada(true);
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemContratos.dialogo.validacaoSucesso"));
            carregarDados();
	}}}
	
