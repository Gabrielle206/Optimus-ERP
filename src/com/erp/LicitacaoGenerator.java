package com.erp;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class LicitacaoGenerator {
	
	private static final String[] Titulo = {
			 "Construção de Equipamentos Públicos",
		     "Pavimentação e Drenagem Urbana",
			 "Aquisição de Materiais e Equipamentos",
			 "Reforma e Manutenção de Prédios Públicos",
			 "Serviços de Limpeza e Conservação",
			 "Manutenção da Iluminação Pública",
			 "Fornecimento de Gêneros Alimentícios",
			 "Aquisição de Medicamentos e Insumos",
			 "Execução de Obras de Infraestrutura",
			 "Implantação de Sistemas de Saneamento"
	};
	
	private static final String[] Descricao = {
			 "Licitação destinada à execução de serviços e obras públicas conforme especificações técnicas.",
			 "Processo licitatório para contratação de empresa especializada na execução dos serviços.",
			 "Aquisição de bens e materiais para atender às demandas das secretarias municipais.",
			 "Projeto voltado à melhoria da infraestrutura e do atendimento à população.",
			 "Contratação com recursos próprios e/ou convênios federais e estaduais.",
			 "Fornecimento contínuo de produtos e prestação de serviços à administração pública."
	};
	
	private static final String[] Orgao = {
			"Prefeitura Municipal",
		    "Secretaria de Administração",
		    "Secretaria de Obras e Infraestrutura",
		    "Secretaria de Educação",
		    "Secretaria de Saúde",
		    "Secretaria de Serviços Públicos"
	};
	
	private static final Random random = new Random();
	
	public static List<Licitacao> gerarLicitacoes() {
        List<Licitacao> lista = new ArrayList<>();

        int quantidade = 1;
		for (int i = 0; i < quantidade ; i++) {
            String titulo = Titulo[random.nextInt(Titulo.length)];
            String descricao = Descricao[random.nextInt(Descricao.length)];
            String orgao = Orgao[random.nextInt(Orgao.length)];

            double valor = 100000 + (random.nextDouble() * 4900000);

            Licitacao l = new Licitacao(titulo, descricao, valor, orgao); 
            lista.add(l);
        }
        
        return lista;
        
	}

}