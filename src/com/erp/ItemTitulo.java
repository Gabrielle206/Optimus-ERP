package com.erp;

public class ItemTitulo {
    private int id;
    private String idTitulo;
    private String idProduto; 
    private String descricao; 
    private int quantidade;
    private double valorUnitario;

    public ItemTitulo(int id, String idTitulo, String idProduto, String descricao, int quantidade, double valorUnitario) {
        this.id = id;
        this.idTitulo = idTitulo;
        this.idProduto = idProduto;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdTitulo() {
        return idTitulo;
    }

    public void setIdTitulo(String idTitulo) {
        this.idTitulo = idTitulo;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public double getValorTotal() {
        return this.quantidade * this.valorUnitario;
    }
}