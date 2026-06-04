package br.edu.ifba.lagos.servidor.operacoes;

import java.util.Map;
import java.util.Queue;

public interface Operacoes<Lago, Amostra> {

    // O(1) no espaço - Grava e rotaciona os dados caso atinja o limite
    public void gravar(Lago lago, Amostra leitura);

    // O(1) - Retorna o banco de dados em memória
    public Map<Lago, Queue<Amostra>> getBancoDeDados();
}