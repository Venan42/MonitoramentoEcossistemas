package br.edu.ifba.lagos.servidor.operacoes;

import java.util.List;
import java.util.Map;

import br.edu.ifba.lagos.servidor.ordenador.TipoOrdenacao;

public interface Operacoes<Lago, Amostra> {

    // Método para o Servidor receber dados do cliente via POST
    public void gravar(Lago lago, Amostra leitura);

    // Método para expor o mapa do banco de dados em memória
    public Map<Lago, List<Amostra>> getBancoDeDados();

    // Métodos de processamento do Servidor exigidos na Versão 1
    public void imprimir();

    public Map<Lago, List<Amostra>> ordenar(TipoOrdenacao tipoOrdenacao);

    public void buscarTriosDePHNeutro();

    public void analisarQualidadeLagos();
}