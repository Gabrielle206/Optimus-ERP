package com.erp;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

public class JanelaConfiguracaoMFA extends JDialog implements LanguageObserver {

    private static final long serialVersionUID = 1L;
    
    private JLabel labelInstrucao1, labelInstrucao2, labelQrCode;
    private JTextField campoCodigo;
    private JButton botaoVerificar;
    
    private PessoaDAO pessoaDAO;
    private MfaManager mfaManager;
    private Pessoa pessoa;
    private String secretKey;

    public JanelaConfiguracaoMFA(Frame parent, Pessoa pessoa) {
        super(parent, true);
        this.pessoa = pessoa;
        this.pessoaDAO = new PessoaDAO();
        this.mfaManager = new MfaManager();
        this.secretKey = mfaManager.criarNovoSegredo();
        
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaConfiguracaoMFA.this);
            }
        });
    }

    private void initComponents() {
        setSize(500, 450); 
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel painelInstrucoes = new JPanel();
        painelInstrucoes.setLayout(new BoxLayout(painelInstrucoes, BoxLayout.Y_AXIS));
        painelInstrucoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        labelInstrucao1 = new JLabel();
        painelInstrucoes.add(labelInstrucao1);
        
        JPanel painelQr = new JPanel();
        labelQrCode = new JLabel();
        gerarQrCode();
        painelQr.add(labelQrCode);

        JPanel painelVerificacao = new JPanel(new FlowLayout());
        labelInstrucao2 = new JLabel();
        campoCodigo = new JTextField(8);
        botaoVerificar = new JButton();
        painelVerificacao.add(labelInstrucao2);
        painelVerificacao.add(campoCodigo);
        painelVerificacao.add(botaoVerificar);
        
        add(painelInstrucoes, BorderLayout.NORTH);
        add(painelQr, BorderLayout.CENTER);
        add(painelVerificacao, BorderLayout.SOUTH);
        
        botaoVerificar.addActionListener(e -> verificarECadastrar());
    }
    
    private void gerarQrCode() {
        String qrCodeUrl = mfaManager.gerarUrlQrCode(secretKey, pessoa.getId());
        
        try {
            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.MARGIN, 1); 
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 300, 300, hintMap);
            
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            labelQrCode.setIcon(new ImageIcon(qrImage));
            
        } catch (Exception e) {
            e.printStackTrace();
            labelQrCode.setText("Erro ao gerar QR Code.");
        }
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        setTitle(messages.getString("janelaMFA.titulo"));
        labelInstrucao1.setText(messages.getString("janelaMFA.instrucao1"));
        labelInstrucao2.setText(messages.getString("janelaMFA.instrucao2"));
        botaoVerificar.setText(messages.getString("janelaMFA.botao.verificar"));
    }
    
    private void verificarECadastrar() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        try {
            int codigo = Integer.parseInt(campoCodigo.getText());
            if (mfaManager.validarCodigo(secretKey, codigo)) {
                pessoa.setSecretKey(secretKey);
                pessoaDAO.atualizar(pessoa);
                JOptionPane.showMessageDialog(this, 
                    messages.getString("janelaMFA.dialogo.sucesso.mensagem"),
                    messages.getString("janelaMFA.dialogo.sucesso.titulo"),
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    messages.getString("janelaMFA.dialogo.erro.codigoInvalido"),
                    messages.getString("janelaMFA.dialogo.erro.titulo"),
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                messages.getString("janelaMFA.dialogo.erro.codigoInvalido"),
                messages.getString("janelaMFA.dialogo.erro.titulo"),
                JOptionPane.ERROR_MESSAGE);
        }
    }
}