package com.erp;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

public class JanelaCadastroContrato extends JFrame implements LanguageObserver {

    private JComboBox<String> comboFornecedorId;
    private JComboBox<String> comboStatus; 
    private JTextField campoIdContrato;
    private JTextField campoObjetoContrato;
    private JTextField campoValor;
    private JTextField campoDataVencimento;
    private ContratoDAO contratoDAO;
    private PessoaDAO pessoaDAO;
    private JButton botaoSalvar;
   
    
    private JLabel labelIdContrato,labelFornecedorId, labelObjeto, labelValor, labelVencimento, labelStatus;
    
    public JanelaCadastroContrato() {
        contratoDAO = new ContratoDAO();
        pessoaDAO = new PessoaDAO();
        LanguageManager.getInstance().addObserver(this);
        initComponents();
        carregarFornecedores();
        updateLanguage();
    }
    
  
    
    private void initComponents() {
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon("iconeerp/logo1.png");
        setIconImage(img.getImage());

        JPanel painelCadastro = new JPanel(new GridLayout(8, 2, 5, 5));
        painelCadastro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        labelIdContrato = new JLabel();
        painelCadastro.add(labelIdContrato);
        campoIdContrato = new JTextField();
        painelCadastro.add(campoIdContrato);
        
       
        labelFornecedorId = new JLabel();
        painelCadastro.add(labelFornecedorId);
        comboFornecedorId = new JComboBox<>();
        painelCadastro.add(comboFornecedorId);
        
        labelObjeto = new JLabel();
        painelCadastro.add(labelObjeto);
        campoObjetoContrato = new JTextField();
        painelCadastro.add(campoObjetoContrato);
        
        labelValor = new JLabel();
        painelCadastro.add(labelValor);
        campoValor = new JTextField();
        painelCadastro.add(campoValor);
        
        labelVencimento = new JLabel();
        painelCadastro.add(labelVencimento);
        campoDataVencimento = new JTextField();
        painelCadastro.add(campoDataVencimento);
        
        labelStatus = new JLabel();
        painelCadastro.add(labelStatus);
        comboStatus = new JComboBox<>(new String[]{"Em Elaboracao", "Ativo"});
        comboStatus.setSelectedItem("Em Elaboracao"); 
        painelCadastro.add(comboStatus);
        
        botaoSalvar = new JButton();
        painelCadastro.add(botaoSalvar);
        
        
        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarContrato();
            }
        });
        
        add(painelCadastro);
    } 
    
    private void carregarFornecedores() {
    	try {
    		
    	comboFornecedorId.removeAllItems();
    	
    	 List<Pessoa> pessoas = pessoaDAO.listarTodos();
    	
    	 List<Pessoa> fornecedores = pessoas.stream()
    			 .filter(p -> p.getTipo() == 2)
    			 .collect(Collectors.toList());
    	
    
    	for(Pessoa fornecedor : fornecedores) {
    		comboFornecedorId.addItem(fornecedor.getId() + "-" + fornecedor.getNome());
    	}
    	
    	if(fornecedores.isEmpty()) {
    		comboFornecedorId.addItem("Nenhum Fornecedor Cadastrado");
    	}
    }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores: " + e.getMessage(),  "Erro", JOptionPane.ERROR_MESSAGE);
            comboFornecedorId.addItem("Erro ao carregar");
        }
    }

    private void salvarContrato() {
        try {
            if (campoIdContrato.getText().trim().isEmpty() ||
            	comboFornecedorId.getSelectedItem() == null || 
                campoObjetoContrato.getText().trim().isEmpty() ||
                campoValor.getText().trim().isEmpty() ||
                campoDataVencimento.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = campoIdContrato.getText().trim();
            String fornecedorId = (String) comboFornecedorId.getSelectedItem();
            String objetoContrato = campoObjetoContrato.getText().trim();
            double valor = Double.parseDouble(campoValor.getText().trim());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataVencimento = LocalDate.parse(campoDataVencimento.getText().trim(), formatter);
            LocalDate dataCriacao = LocalDate.now();
            String status = (String) comboStatus.getSelectedItem();
            
           
            
            Contrato contrato = new Contrato(
                    id,                   // String id
                    fornecedorId,         // String fornecedorId  
                    objetoContrato,       // String objetoContrato
                    valor,                // double valor
                    dataCriacao,          // LocalDate dataCriacao
                    dataVencimento,       // LocalDate dataVencimento
                    status                // String status
                );
            
            contratoDAO.salvar(contrato);
            
            JOptionPane.showMessageDialog(this, "Contrato salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar contrato: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
     
    private void limparCampos() {
    	campoIdContrato.setText("");
        comboFornecedorId.setSelectedIndex(0);
        campoObjetoContrato.setText("");
        campoValor.setText("");
        campoDataVencimento.setText("");
        comboStatus.setSelectedItem("Em Elaboracao");
    }
     
    @Override
    public void updateLanguage() {
    	labelIdContrato.setText("ID do Contrato:");
        labelFornecedorId.setText("ID do Fornecedor:");
        labelObjeto.setText("Objeto do Contrato:");
        labelValor.setText("Valor:");
        labelVencimento.setText("Data Vencimento:");
        labelStatus.setText("Status:");
        botaoSalvar.setText("Salvar");
        
    }
} 
