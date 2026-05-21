package br.edu.ifba.lagos.impl;

import br.edu.ifba.lagos.operacoes.Operacoes;
import br.edu.ifba.lagos.ordenador.Ordenador;
import br.edu.ifba.lagos.ordenador.TipoOrdenacao;

import java.util.*;

public class OperacoesImpl implements Operacoes<Lago, Amostra> {

    /**
     * Complexidade: O(N)
     * Justificativa: Existe um laço for que itera linearmente pelos N elementos da lista de lagos.
     */
    @Override
    public void imprimir(List<Lago> lagosMonitorados) {
        for (Lago lago : lagosMonitorados) {
            System.out.println("Lago sendo monitorado: " + lago);
        }
    }

    /**
     * Complexidade: O(N * M)
     * Justificativa: O laço externo percorre todos os lagos (N) e o laço interno
     * percorre todas as amostras (M) contidas em cada lago.
     */
    @Override
    public void imprimir(Map<Lago, List<Amostra>> amostras) {
        for (Lago lago : amostras.keySet()) {
            System.out.println("Amostras do lago " + lago + ":");
            for (Amostra amostra : amostras.get(lago)) {
                System.out.println(amostra);
            }
        }
    }

    /**
     * Complexidade: O(N * M log M)
     * Justificativa: Para cada um dos N lagos, as M amostras são ordenadas utilizando
     * o algoritmo Merge Sort (M log M).
     */
    @Override
    public Map<Lago, List<Amostra>> ordenar(Map<Lago, List<Amostra>> amostras,
                                            TipoOrdenacao tipoOrdenacao) {
        Map<Lago, List<Amostra>> amostrasOrdenadas = new TreeMap<>();

        for (Lago lago : amostras.keySet()) {
            System.out.println("Ordenando amostras do lago: " + lago.getNome());

            List<Amostra> listaDeAmostras = amostras.get(lago);
            Ordenador<Amostra> ordenador = new OrdenadorImpl(listaDeAmostras, tipoOrdenacao);
            ordenador.ordenar();

            amostrasOrdenadas.put(lago, listaDeAmostras);
        }

        return amostrasOrdenadas;
    }

    /**
     * Complexidade: O(T^3) onde T é o total de amostras (N * M)
     * Justificativa: Existem 3 laços aninhados que percorrem a lista consolidada de todas as amostras
     * para encontrar combinações de trios.
     */
    @Override
    public void buscarTriosDePHNeutro(Map<Lago, List<Amostra>> amostras) {
        System.out.println("\n--- Busca trios de mistura neutra ---");
        int contadorTrios = 0;

        List<Amostra> listaAmostras = new ArrayList<>();
        for (Map.Entry<Lago, List<Amostra>> entrada : amostras.entrySet()) {
            List<Amostra> listaDeAmostras = entrada.getValue();
            listaAmostras.addAll(listaDeAmostras);
        }
        int n = listaAmostras.size();

        // Primeira amostra
        for (int i = 0; i < n - 2; i++) {

            // Segunda Amostra
            for (int j = i + 1; j < n - 1; j++) {

                // Terceira Amostra
                for (int k = j + 1; k < n; k++) {

                    Amostra a1 = listaAmostras.get(i);
                    Amostra a2 = listaAmostras.get(j);
                    Amostra a3 = listaAmostras.get(k);

                    // Apenas amostras de diferentes lagos são calculadas
                    if (a1.getLago().getId() != a2.getLago().getId() && a1.getLago().getId() != a3.getLago().getId() && a2.getLago().getId() != a3.getLago().getId()) {

                        double mediaTrio = (a1.getValorPH() + a2.getValorPH() + a3.getValorPH()) / 3.0;

                        // Verifica se a média forma uma água neutra (entre 6.9 e 7.1)
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
     * Justificativa: Itera-se por todos os N lagos e suas respectivas M amostras
     * uma única vez para realizar os cálculos estatísticos.
     */
    @Override
    public void analisarQualidadeLagos(Map<Lago, List<Amostra>> amostras) {
        System.out.println("\n--- ANÁLISE DE QUALIDADE EFETIVA POR LAGO ---");

        for (Map.Entry<Lago, List<Amostra>> entrada : amostras.entrySet()) {
            Lago lago = entrada.getKey();
            List<Amostra> listaDeAmostras = entrada.getValue();

            if (listaDeAmostras.isEmpty()) continue;

            double soma = 0;
            double min = 14.0; // Começa no teto da escala
            double max = 0.0;  // Começa no piso da escala

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
     * Justificativa: Realiza apenas comparações simples de valores, sem estruturas de repetição.
     */
    private String classificarPH(double ph) {
        if (ph < 6.5) return "ÁCIDO (Risco para biodiversidade)";
        if (ph > 7.5) return "ALCALINO (Desequilíbrio detectado)";
        return "NEUTRA (Parâmetros de segurança OK)";
    }
}
