package com.erp;
import java.time.LocalDate;

public class Proposta {
	 	private String id;
	    private String licitacaoId;
	    private String empresa;
	    private double valor;
	    private String descricao;
	    private LocalDate data_envio;
	   
	    public Proposta(String id, String licitacaoId, String empresa, double valor, 
	                    String descricao, LocalDate data_envio) {
	        this.id = id;
	        this.licitacaoId = licitacaoId;
	        this.empresa = empresa;
	        this.valor = valor;
	        this.descricao = descricao;
	        this.data_envio = data_envio;
	    }

	    public String getId() { return id; }
	    public void setId(String id) { this.id = id; }

	    public String getLicitacaoId() { return licitacaoId; }
	    public void setLicitacaoId(String licitacaoId) { this.licitacaoId = licitacaoId; }

	    public String getEmpresa() { return empresa; }
	    public void setEmpresa(String empresa) { this.empresa = empresa; }

	    public double getValor() { return valor; }
	    public void setValor(double valor) { this.valor = valor; }

	    public String getDescricao() { return descricao; }
	    public void setDescricao(String descricao) { this.descricao = descricao; }
	    
	    public LocalDate getDataEnvio() { return data_envio; }
	    public void setDataEnvio(LocalDate data_envio) { this.data_envio = data_envio; }
}
