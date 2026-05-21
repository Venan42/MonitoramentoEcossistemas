package br.edu.ifba.lagos;

import br.edu.ifba.lagos.impl.Amostra;
import br.edu.ifba.lagos.impl.Lago;
import br.edu.ifba.lagos.impl.OperacoesImpl;
import br.edu.ifba.lagos.impl.SensoriamentoImpl;
import br.edu.ifba.lagos.operacoes.Operacoes;
import br.edu.ifba.lagos.ordenador.TipoOrdenacao;
import br.edu.ifba.lagos.sensoriamento.Sensoriamento;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    private static final int TOTAL_LAGOS = 10;
    private static final int AMOSTRAS_POR_LAGO = 5;

    public static void main(String[] args) throws Exception {
        Sensoriamento<Amostra> sensoriamento = new SensoriamentoImpl();

        // Gerando amostras para N lagos
        Map<Lago, List<Amostra>> amostras = new HashMap<>();
        for (int i = 0; i < TOTAL_LAGOS; i++) {
            Lago lago = new Lago(i, "Lago #" + i);
            amostras.put(lago, sensoriamento.gerarAmostras(lago, AMOSTRAS_POR_LAGO));
        }

        Operacoes<Lago, Amostra> operacoes = new OperacoesImpl();

        // d.1 imprimindo os lagos
        System.out.println("--- d.1: IMPRIMINDO OS LAGOS MONITORADOS ---");
        operacoes.imprimir(new ArrayList<>(amostras.keySet()));

        // d.2 imprimindo amostras por lago
        System.out.println("\n--- d.2: IMPRIMINDO AMOSTRAS POR LAGO ---");
        operacoes.imprimir(amostras);

        // d.3 ordenando os dados das amostras dos lagos por pH
        System.out.println("\n--- d.3: ORDENANDO OS LAGOS PELO pH (MERGE SORT) ---");
        Map<Lago, List<Amostra>> amostrasOrdenadas = operacoes.ordenar(amostras, TipoOrdenacao.POR_PH);
        operacoes.imprimir(amostrasOrdenadas);

        // d.4 encontra a quantidade de trios de amostras com pH neutro
        System.out.println("\n--- d.4: ENCONTRANDO TRIOS DE MISTURA NEUTRA (O(N^3)) ---");
        operacoes.buscarTriosDePHNeutro(amostrasOrdenadas);

        // extra: análise funcional de qualidade da água
        operacoes.analisarQualidadeLagos(amostrasOrdenadas);
    }
}