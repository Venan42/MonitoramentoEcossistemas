package br.edu.ifba.lagos.servidor.operacoes;

import br.edu.ifba.lagos.servidor.impl.Amostra;
import br.edu.ifba.lagos.servidor.ordenador.TipoOrdenacao;

import java.util.List;
import java.util.Map;

public interface Operacoes<Lago, Amostra> {
    // Método para o Servidor receber dados do cliente via POST
    void gravar(Lago lago, Amostra leitura);

    // Método para expor o mapa do banco de dados em memória
    Map<Lago, List<Amostra>> getBancoDeDados();

    // Métodos de processamento do Servidor exigidos na Versão 1
    void imprimir();

    int buscarTriosDePHNeutro();

    Map<Lago, List<Amostra>> ordenar(TipoOrdenacao tipoOrdenacao);

    void analisarQualidadeLagos();
}