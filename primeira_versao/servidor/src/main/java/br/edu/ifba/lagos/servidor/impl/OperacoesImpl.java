package br.edu.ifba.lagos.servidor.impl;

import br.edu.ifba.lagos.servidor.operacoes.Operacoes;
import br.edu.ifba.lagos.servidor.ordenador.Ordenador;
import br.edu.ifba.lagos.servidor.ordenador.TipoOrdenacao;

import java.util.*;

public class OperacoesImpl implements Operacoes<Lago, Amostra> {

    // O "Banco de Dados" em memória do Servidor. O TreeMap garante os lagos ordenados por Chave.
    private Map<Lago, List<Amostra>> bancoDeDados = new TreeMap<>();

    /**
     * Complexidade: O(Log N)
     * Justificativa: A verificação e inserção da chave (Lago) no TreeMap possui custo logarítmico,
     * enquanto a inserção da amostra na lista interna possui custo constante O(1).
     */
    @Override
    public void gravar(Lago lago, Amostra amostra) {
        if (!bancoDeDados.containsKey(lago)) {
            bancoDeDados.put(lago, new ArrayList<>());
        }

        bancoDeDados.get(lago).add(amostra);
        System.out.println("Gravada nova leitura para o lago: " + lago.getNome() + " | pH: " + amostra.getValorPH());
    }

    /**
     * Complexidade: O(1)
     * Justificativa: Retorna apenas a referência do mapa do banco de dados, sem laços de repetição.
     */
    @Override
    public Map<Lago, List<Amostra>> getBancoDeDados() {
        return this.bancoDeDados;
    }

    /**
     * Complexidade: O(N * M)
     * Justificativa: O laço externo percorre todos os lagos (N) do banco de dados e o laço interno
     * percorre todas as amostras (M) contidas em cada um deles.
     */
    @Override
    public void imprimir() {
        for (Lago lago : bancoDeDados.keySet()) {
            System.out.println("Amostras do lago " + lago + ":");
            for (Amostra amostra : bancoDeDados.get(lago)) {
                System.out.println(amostra);
            }
        }
    }

    /**
     * Complexidade: O(T^3) onde T é o total de amostras acumuladas (N * M)
     * Justificativa: Existem 3 laços aninhados que percorrem a lista consolidada de todas as amostras
     * do banco de dados para encontrar combinações de trios.
     */
    @Override
    public int buscarTriosDePHNeutro() {
        int contadorTrios = 0;
        int t = bancoDeDados.size();
        List<Amostra> todasAsAmostras = bancoDeDados.values().stream()
                .flatMap(List::stream)
                .toList();

        // Pega a primeira amostra
        for (int i = 0; i < t; i++) {
            // Pega a segunda amostra
            for (int j = i + 1; j < t; j++) {
                // Pega a terceira amostra
                for (int k = j + 1; k < t; k++) {

                    double ph1 = todasAsAmostras.get(i).getValorPH();
                    double ph2 = todasAsAmostras.get(j).getValorPH();
                    double ph3 = todasAsAmostras.get(k).getValorPH();

                    // Critério de neutralidade baseado na média ponderada/simples próxima de 7.0
                    double media = (ph1 + ph2 + ph3) / 3.0;
                    if (Math.abs(media - 7.0) < 0.1) {
                        contadorTrios++;
                    }
                }
            }
        }
        return contadorTrios;
    }

    /**
     * Complexidade: O(N * M log M)
     * Justificativa: Para cada um dos N lagos, as M amostras são ordenadas utilizando
     * o algoritmo Merge Sort (M log M).
     */
    @Override
    public Map<Lago, List<Amostra>> ordenar(TipoOrdenacao tipoOrdenacao) {
        Map<Lago, List<Amostra>> amostrasOrdenadas = new TreeMap<>();

        for (Lago lago : bancoDeDados.keySet()) {
            System.out.println("Ordenando amostras do lago: " + lago.getNome());

            List<Amostra> listaDeAmostras = bancoDeDados.get(lago);
            Ordenador<Amostra> ordenador = new OrdenadorImpl(listaDeAmostras, tipoOrdenacao);
            ordenador.ordenar();

            amostrasOrdenadas.put(lago, listaDeAmostras);
        }

        return amostrasOrdenadas;
    }

    /**
     * Complexidade: O(N * M)
     * Justificativa: Itera-se por todos os N lagos salvos e suas respectivas M amostras
     * uma única vez para ler os valores e realizar os cálculos estatísticos.
     */
    @Override
    public void analisarQualidadeLagos() {
        System.out.println("\n--- ANÁLISE DE QUALIDADE EFETIVA POR LAGO ---");

        for (Map.Entry<Lago, List<Amostra>> entrada : bancoDeDados.entrySet()) {
            Lago lago = entrada.getKey();
            List<Amostra> listaDeAmostras = entrada.getValue();

            if (listaDeAmostras.isEmpty()) continue;

            double soma = 0;
            double min = 14.0;
            double max = 0.0;

            for (Amostra a : listaDeAmostras) {
                double ph = a.getValorPH();
                soma += ph;
                if (ph < min) min = ph;
                if (ph > max) max = ph;
            }

            double media = soma / listaDeAmostras.size();
            String classificacao = classificarPH(media);

            System.out.printf("Lago: %s | Média: %.2f | Mínimo: %.2f | Máximo: %.2f\n",
                    lago.getNome(), media, min, max);
            System.out.println("Status Ambiental: " + classificacao + "\n");
        }
    }

    /**
     * Complexidade: O(1)
     * Justificativa: Realiza apenas comparações condicionais simples, sem estruturas de repetição.
     */
    private String classificarPH(double ph) {
        if (ph < 6.5) return "ÁCIDO (Risco para biodiversidade)";
        if (ph > 7.5) return "ALCALINO (Desequilíbrio detectado)";
        return "NEUTRA (Parâmetros de segurança OK)";
    }
}