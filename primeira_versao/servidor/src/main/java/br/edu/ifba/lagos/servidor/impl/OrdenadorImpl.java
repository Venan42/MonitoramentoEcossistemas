package br.edu.ifba.lagos.servidor.impl;

import br.edu.ifba.lagos.servidor.ordenador.Ordenador;
import br.edu.ifba.lagos.servidor.ordenador.TipoOrdenacao;
import java.util.ArrayList;
import java.util.List;

public class OrdenadorImpl extends Ordenador<Amostra> {

    public OrdenadorImpl(List<Amostra> dados, TipoOrdenacao tipo) {
        super(dados, tipo);
    }

    /**
     * Complexidade: O(N LogN)
     * Justificativa: O algoritmo divide a lista sucessivamente ao meio (LogN) e
     * realiza a intercalação dos elementos comparando-os (N).
     */
    @Override
    public void ordenar() {
        if (dados == null || dados.size() <= 1) return;
        mergeSort(dados, 0, dados.size() - 1);
    }

    /**
     * Complexidade: O(N LogN)
     * Justificativa: Método recursivo que subdivide o problema em instâncias menores
     * até atingir o caso base de listas com um único elemento.
     */
    private void mergeSort(List<Amostra> lista, int esq, int dir) {
        if (esq < dir) {
            int meio = (esq + dir) / 2;
            mergeSort(lista, esq, meio);
            mergeSort(lista, meio + 1, dir);
            merge(lista, esq, meio, dir);
        }
    }

    /**
     * Complexidade: O(N)
     * Justificativa: Percorre as sublistas temporárias L e R linearmente para
     * reconstruir a lista principal de forma ordenada.
     */
    private void merge(List<Amostra> lista, int esq, int meio, int dir) {
        List<Amostra> L = new ArrayList<>(lista.subList(esq, meio + 1));
        List<Amostra> R = new ArrayList<>(lista.subList(meio + 1, dir + 1));

        int i = 0, j = 0, k = esq;

        while (i < L.size() && j < R.size()) {
            boolean condicao;

            if (tipo == TipoOrdenacao.POR_PH) {
                condicao = L.get(i).getValorPH() <= R.get(j).getValorPH();
            } else {
                condicao = L.get(i).getLago().getNome().compareTo(R.get(j).getLago().getNome()) <= 0;
            }

            if (condicao) {
                lista.set(k++, L.get(i++));
            } else {
                lista.set(k++, R.get(j++));
            }
        }

        while (i < L.size()) lista.set(k++, L.get(i++));
        while (j < R.size()) lista.set(k++, R.get(j++));
    }
}