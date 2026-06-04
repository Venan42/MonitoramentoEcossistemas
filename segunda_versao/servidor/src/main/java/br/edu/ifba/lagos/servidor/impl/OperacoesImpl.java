package br.edu.ifba.lagos.servidor.impl;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import br.edu.ifba.lagos.servidor.operacoes.Operacoes;

public class OperacoesImpl implements Operacoes<Lago, Amostra> {

    private static final int LIMIAR_ROTACIONAMENTO = 40;

    private Map<Lago, Queue<Amostra>> bancoDeDados = new TreeMap<>();
    private Map<Lago, Integer> triosNeutrosPorLago = new TreeMap<>();

    /**
     * Complexidade: O(Log N) onde N é a quantidade de lagos monitorados.
     *
     * Justificativa: A busca e inserção de chaves no TreeMap demandam tempo logarítmico O(Log N).
     */
    @Override
    public void gravar(Lago lago, Amostra amostra) {
        Queue<Amostra> leituras = new LinkedList<>();
        if (bancoDeDados.containsKey(lago)) {
            leituras = bancoDeDados.get(lago);
        } else {
            bancoDeDados.put(lago, leituras);
        }

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Limiar protetivo de memória
        if (leituras.size() >= LIMIAR_ROTACIONAMENTO) {
            leituras.poll();
            System.out.println("Limiar atingido para o " + lago.getNome() + ". Amostra antiga descartada.");
        }

        leituras.add(amostra);
        System.out.println("Nova amostra de pH salva para o lago: " + lago.getNome());
    }

    /**
     * Complexidade: O(Log N) onde N é a quantidade de lagos monitorados.
     *
     * Justificativa: A pesquisa pela chave do lago e a atualização do saldo de trios acumulados
     * no TreeMap operam sob custo logarítmico balanceado da árvore interna.
     */
    @Override
    public void gravar(Lago lago, int triosNeutros) {
        System.out.println(triosNeutros > 0
                ? "Alerta: O " + lago.getNome() + " processou a borda e detectou " + triosNeutros + " trios de pH neutro."
                : "Estabilidade: O " + lago.getNome() + " processou a borda e não encontrou trios neutros.");

        if (triosNeutrosPorLago.containsKey(lago)) {
            triosNeutros += triosNeutrosPorLago.get(lago);
        }
        triosNeutrosPorLago.put(lago, triosNeutros);
    }

    /**
     * Complexidade: O(M) onde M representa a quantidade de lagos mapeados.
     *
     * Justificativa: Executa uma varredura linear por meio de um laço simples que itera sobre
     * todos os valores inteiros contidos na coleção do mapa para consolidar a soma aritmética global.
     */
    @Override
    public int detectarTriosNeutros() {
        int contador = 0;
        for (Integer trios : triosNeutrosPorLago.values()) {
            contador += trios;
        }
        return contador;
    }
}