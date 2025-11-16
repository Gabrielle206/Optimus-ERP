package com.erp;

import java.util.List;

public class GovConnectClient {

    private static GovConnectClient instance;

    private GovConnectClient() {
        System.out.println("[GovConnect] Servidor de comunicação iniciado.");
    }

    public static GovConnectClient getInstance() {
        if (instance == null) {
            instance = new GovConnectClient();
        }
        return instance;
    }

    public List<Licitacao> buscarLicitacoes(String filtro) {
        System.out.println("[GovConnect] Solicitando licitações geradas ao LicitacaoGenerator...");
        LicitacaoGenerator generator = new LicitacaoGenerator();
        List<Licitacao> lista = LicitacaoGenerator.gerarLicitacoes();

        System.out.println("[GovConnect] " + lista.size() + " licitações recebidas.");
        return lista;
    }

    public void enviarProposta(Proposta proposta) {
        System.out.println("[GovConnect] Enviando proposta (simulação JSON):");
        String json = "{\n" +
                "  \"id\": \"" + proposta.getId() + "\",\n" +
                "  \"licitacao\": \"" + proposta.getLicitacaoId() + "\",\n" +
                "  \"empresa\": \"" + proposta.getEmpresa() + "\",\n" +
                "  \"valor\": " + proposta.getValor() + ",\n" +
                "  \"descricao\": \"" + proposta.getDescricao() + "\",\n" +
                "  \"data_envio\": \"" + proposta.getDataEnvio() + "\"\n" +
                "}";
        System.out.println(json);
        System.out.println("[GovConnect] Proposta enviada com sucesso (simulação).");
    }
}
