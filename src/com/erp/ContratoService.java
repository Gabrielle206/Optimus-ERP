package com.erp;

import java.time.LocalDate;
import java.util.List;

public class ContratoService {

    private ContratoDAO contratoDAO;
    private BlockchainManager blockchainManager;

    public ContratoService() {
        this.contratoDAO = new ContratoDAO();
        this.blockchainManager = BlockchainManager.getInstance();
    }

    public boolean assinarContrato(String contratoId, boolean isEmpresa) {
        Contrato contrato = contratoDAO.buscarPorId(contratoId);
        if (contrato == null) {
            System.err.println("Erro: Tentativa de assinar contrato não existente: " + contratoId);
            return false;
        }

        if (isEmpresa) {
            contrato.setDataAssinaturaEmpresa(LocalDate.now());
        } else {
            contrato.setDataAssinaturaFornecedor(LocalDate.now());
        }

        if (contrato.getDataAssinaturaEmpresa() != null &&
            contrato.getDataAssinaturaFornecedor() != null &&
            contrato.getStatus().equals("Em Elaboracao")) {

            System.out.println("Ambas as partes assinaram. Ativando contrato na blockchain...");

            String txHash = blockchainManager.registrarContrato(contrato);
            contrato.setBlockchainTxHash(txHash);
            
            String scAddress = blockchainManager.criarContratoInteligente(contrato);
            contrato.setSmartContractAddress(scAddress);

            contrato.setStatus("Ativo");
        }

        contratoDAO.atualizar(contrato);
        return true;
    }

    public boolean validarNotaFiscal(String contratoId) {
        Contrato contrato = contratoDAO.buscarPorId(contratoId);
        if (contrato == null) {
            System.err.println("Erro: Tentativa de validar NF para contrato não existente: " + contratoId);
            return false;
        }

        if (!contrato.getStatus().equals("Ativo")) {
            System.err.println("Aviso: Nota fiscal só pode ser validada para contratos 'Ativos'.");
            return false;
        }

        contrato.setNotaFiscalValidada(true);
        System.out.println("Nota fiscal do contrato " + contratoId + " marcada como VALIDADA.");
        
        contratoDAO.atualizar(contrato);
        
        verificarDisparoPagamento(contrato);
        
        return true;
    }

    public void verificarVencimentos() {
        System.out.println("\n--- SERVIÇO DE VERIFICAÇÃO DE VENCIMENTOS ---");
        List<Contrato> contratos = contratoDAO.listarTodos();
        LocalDate hoje = LocalDate.now();
        LocalDate limiteNotificacao = hoje.plusDays(10);

        for (Contrato contrato : contratos) {
            if (contrato.getStatus().equals("Ativo")) {
                if (!contrato.getDataVencimento().isAfter(limiteNotificacao)) {
                    System.out.println("[NOTIFICAÇÃO SIM] O contrato " + contrato.getId() + 
                                       " (Fornecedor: " + contrato.getFornecedorId() + 
                                       ") vence em: " + contrato.getDataVencimento());
                }
            }
        }
        System.out.println("--- FIM DA VERIFICAÇÃO ---\n");
    }
    
    private void verificarDisparoPagamento(Contrato contrato) {
        LocalDate hoje = LocalDate.now();
        
        if (contrato.isNotaFiscalValidada() && !hoje.isBefore(contrato.getDataVencimento())) {
            
            System.out.println("Condições de pagamento atingidas para o contrato " + contrato.getId() + ".");
            
            boolean pago = blockchainManager.dispararPagamento(contrato.getSmartContractAddress());
            
            if (pago) {
                contrato.setStatus("Pago");
                contratoDAO.atualizar(contrato);
                System.out.println("Status do contrato " + contrato.getId() + " atualizado para 'Pago'.");
            } else {
                System.err.println("Erro na simulação de pagamento do contrato " + contrato.getId() + ".");
            }
        }
    }
}
