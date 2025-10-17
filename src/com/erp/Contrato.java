package com.erp;

import java.time.LocalDate;

public class Contrato {
	private String id;
	private String idFornecedor;
	private String objetoContrato;
	private double valor;
	private LocalDate dataCriacao;
	private String dataAssinaturaEmpresa;
	private String dataAssinaturaFornecedor;
	private String dataVencimento;
	private String status;
	private String assinatura;
	private String blockchainTxHash;
	private String smartContarctAddress;
	private int notaFiscalValidada;
	
	public Contrato (String id, String idFornecedor, String objetoContrato, double valor, LocalDate dataCriacao, 
			String dataAssinaturaEmpresa, String dataAssinaturaFornecedor, String dataVencimento, String status,
			String assinatura, String blockchainTxHash, String smartContarctAddress, int notaFiscalValidada) {

			this.id = id;
	        this.idFornecedor = idFornecedor;
	        this.objetoContrato = objetoContrato;
	        this.valor = valor;
	        this.dataCriacao = dataCriacao;
			this.dataAssinaturaEmpresa = dataAssinaturaEmpresa;
	        this.dataAssinaturaFornecedor = dataAssinaturaFornecedor;
	        this.dataVencimento = dataVencimento;
	        this.status = status;
	        this.assinatura = assinatura;
	        this.blockchainTxHash = blockchainTxHash;
	        this.smartContarctAddress = smartContarctAddress;
	        this.notaFiscalValidada = notaFiscalValidada;
	        
	}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getIdFornecedor() { return idFornecedor; }
	public void setIdFornecedor(String idFornecedor) { this.idFornecedor = idFornecedor; }

	public String getObjetoContrato() { return objetoContrato; }
	public void setObjetoContrato(String objetoContrato) { this.objetoContrato = objetoContrato; }

	public double getValor() { return valor; }
	public void setValor(double valor) { this.valor = valor; }

	public LocalDate getDataCriacao() { return dataCriacao; }
	public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

	public String getDataAssinaturaEmpresa() { return dataAssinaturaEmpresa; }
	public void setDataAssinaturaEmpresa(String dataAssinaturaEmpresa) { this.dataAssinaturaEmpresa = dataAssinaturaEmpresa; }

	public String getDataAssinaturaFornecedor() { return dataAssinaturaFornecedor; }
	public void setDataAssinaturaFornecedor(String dataAssinaturaFornecedor) { this.dataAssinaturaFornecedor = dataAssinaturaFornecedor; }

	public String getDataVencimento() { return dataVencimento; }
	public void setDataVencimento(String dataVencimento) { this.dataVencimento = dataVencimento; }

	public String getStatus() {	return status; }
	public void setStatus(String status) { this.status = status; }

	public String getAssinatura() { return assinatura; }
	public void setAssinatura(String assinatura) { this.assinatura = assinatura; }

	public String getBlockchainTxHash() { return blockchainTxHash; }
	public void setBlockchainTxHash(String blockchainTxHash) { this.blockchainTxHash = blockchainTxHash; }

	public String getSmartContarctAddress() { return smartContarctAddress; }
	public void setSmartContarctAddress(String smartContarctAddress) { this.smartContarctAddress = smartContarctAddress; }

	public int getNotaFiscalValidada() { return notaFiscalValidada; }
	public void setNotaFiscalValidada(int notaFiscalValidada) { this.notaFiscalValidada = notaFiscalValidada; }

}
