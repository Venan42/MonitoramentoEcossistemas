package br.edu.ifba.lagos.servidor.impl;

import br.edu.ifba.lagos.servidor.operacoes.Operacoes;
import java.util.*;

public class OperacoesImpl implements Operacoes<Lago, Amostra> {

    // Otimização 2: Uso de Fila (Queue) para limitar o acúmulo de dados na memória
    private static final int LIMIAR_ROTACIONAMENTO = 40;
    private Map<Lago, Queue<Amostra>> bancoDeDados = new TreeMap<>();

    /**
     * Complexidade de Tempo: O(Log N) devido à busca da chave no TreeMap.
     * Complexidade de Espaço: O(1) - Controle por limiar impede o crescimento infinito da memória.
     * Justificativa: Se a fila atingir o limite estipulado, o elemento mais antigo é removido (poll)
     * antes da inserção do novo dado, mantendo o consumo de memória fixo.
     */
    @Override
    public void gravar(Lago lago, Amostra amostra) {
        // Se o lago não existir no TreeMap, inicializa com uma estrutura de Fila
        if (!bancoDeDados.containsKey(lago)) {
            bancoDeDados.put(lago, new LinkedList<>());
        }

        Queue<Amostra> filaLeituras = bancoDeDados.get(lago);

        // Aplicação do Limiar de Rotacionamento
        if (filaLeituras.size() >= LIMIAR_ROTACIONAMENTO) {
            filaLeituras.poll(); // Remove a leitura mais antiga (Cabeça da Fila) com custo O(1)
            System.out.println("⚠️ Limite atingido para o " + lago.getNome() + ". Amostra antiga descartada.");
        }

        // Adiciona a nova leitura na cauda da fila
        filaLeituras.add(amostra);
        System.out.println("✅ [Banco de Dados] Gravado: " + lago.getNome() + " | pH: " + amostra.getValorPH());
    }

    /**
     * Complexidade: O(1)
     * Justificativa: Apenas retorna a referência do mapa de dados em tempo constante.
     */
    @Override
    public Map<Lago, Queue<Amostra>> getBancoDeDados() {
        return this.bancoDeDados;
    }
}