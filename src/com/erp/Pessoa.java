package com.erp;

import java.time.LocalDateTime;

public class Pessoa {
    private String id;
    private String nome;
    private int tipo;
    private String telefone;
    private String senha;
    private int tentativasFalhas;
    private LocalDateTime bloqueadoAte;
    private String secretKey; 

    public Pessoa(String id, int tipo, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.telefone = telefone;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getTipo() { return tipo; }
    public void setTipo(int tipo) { this.tipo = tipo; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public int getTentativasFalhas() { return tentativasFalhas; }
    public void setTentativasFalhas(int tentativasFalhas) { this.tentativasFalhas = tentativasFalhas; }
    public LocalDateTime getBloqueadoAte() { return bloqueadoAte; }
    public void setBloqueadoAte(LocalDateTime bloqueadoAte) { this.bloqueadoAte = bloqueadoAte; }
    public String getSecretKey() { return secretKey; } 
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; } 

    @Override
    public String toString() {
        return this.nome;
    }
}