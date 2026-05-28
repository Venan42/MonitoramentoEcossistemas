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
     * Complexidade: O(N * M log M)
     * Justificativa: Para cada um dos N lagos do banco de dados, as M amostras internas são
     * ordenadas utilizando o algoritmo Merge Sort (M log M).
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
     * Complexidade: O(T^3) onde T é o total de amostras acumuladas (N * M)
     * Justificativa: Existem 3 laços aninhados que percorrem a lista consolidada de todas as amostras
     * do banco de dados para encontrar combinações de trios.
     */
    @Override
    public void buscarTriosDePHNeutro() {
        System.out.println("\n--- Busca trios de mistura neutra ---");
        int contadorTrios = 0;

        List<Amostra> listaAmostras = new ArrayList<>();
        for (Map.Entry<Lago, List<Amostra>> entrada : bancoDeDados.entrySet()) {
            listaAmostras.addAll(entrada.getValue());
        }
        int n = listaAmostras.size();

        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {

                    Amostra a1 = listaAmostras.get(i);
                    Amostra a2 = listaAmostras.get(j);
                    Amostra a3 = listaAmostras.get(k);

                    if (a1.getLago().getId() != a2.getLago().getId() &&
                            a1.getLago().getId() != a3.getLago().getId() &&
                            a2.getLago().getId() != a3.getLago().getId()) {

                        double mediaTrio = (a1.getValorPH() + a2.getValorPH() + a3.getValorPH()) / 3.0;

                        if (mediaTrio >= 6.9 && mediaTrio <= 7.1) {
                            contadorTrios++;
                            System.out.printf("Lagos [%d, %d, %d] -> Média pH: %.2f\n",
                                    a1.getLago().getId(), a2.getLago().getId(), a3.getLago().getId(), mediaTrio);
                        }
                    }
                }
            }
        }
        System.out.println("Total de trios de lagos com pH neutro encontrados: " + contadorTrios);
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