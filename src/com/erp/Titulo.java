package com.erp;

import java.time.LocalDate;

public class Titulo {
    private String id;
    private boolean pago;
    private String pessoaId;
    private String tipoTitulo;
    private LocalDate data;
    
    public Titulo(String id, boolean pago, String pessoaId, String tipoTitulo, LocalDate data) {
        this.id = id;
        this.pago = pago;
        this.pessoaId = pessoaId;
        this.tipoTitulo = tipoTitulo;
        this.data = data; 
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public boolean isPago() { return pago; }

    public void setPago(boolean pago) { this.pago = pago; } 
    
    public String getPessoaId() { return pessoaId; }
    public void setPessoaId(String pessoaId) { this.pessoaId = pessoaId; }
    public String getTipoTitulo() { return tipoTitulo; }
    public void setTipoTitulo(String tipoTitulo) { this.tipoTitulo = tipoTitulo; }
    public LocalDate getDate(){return data;}
    public void setData(LocalDate data){ this.data = data; }
}