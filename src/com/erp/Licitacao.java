package com.erp;
import java.util.UUID;

public class Licitacao {
    private String id;
    private String titulo;
    private String descricao;
    private double valorEstimado;
    private String orgao;

    public Licitacao(String titulo, String descricao, double valorEstimado, String orgao) {
        this.id = UUID.randomUUID().toString(); 
        this.titulo = titulo;
        this.descricao = descricao;
        this.valorEstimado = valorEstimado;
        this.orgao = orgao;
    }
  
    public Licitacao(String id, String titulo, String descricao, double valorEstimado, String orgao) {
        this.id = id; 
        this.titulo = titulo;
        this.descricao = descricao;
        this.valorEstimado = valorEstimado;
        this.orgao = orgao;
    }

	public String getId() { return id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public double getValorEstimado() { return valorEstimado; }
    public void setValorEstimado(double valorEstimado) { this.valorEstimado = valorEstimado; }
    
    public String getOrgao() { return orgao; }
    public void setOrgao(String orgao) { this.orgao = orgao; }

}