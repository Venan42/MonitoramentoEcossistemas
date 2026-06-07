package br.edu.ifba.lagos.clientes.impl;


import br.edu.ifba.lagos.clientes.sensoriamento.Sensoriamento;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SensoriamentoImpl implements Sensoriamento<Amostra> {

    private static final double PH_NORMAL = 7; // valor de pH normal para ecossistemas de água doce

    // O pH pode ter valores negativos ou maiores que 14, mas é muito raro na natureza.
    // Para efeitos práticos, adotou-se o valor mínimo como 0 e o máximo como 14.
    private static final int VALOR_MINIMO_PH = 0;
    private static final int VALOR_MAXIMO_PH = 14;

    /**
     * Complexidade: O(N)
     * Justificativa: O método executa um laço for que itera exatamente N vezes
     * (totalAmostras) para gerar e instanciar cada amostra individualmente.
     */
    @Override
    public List<Amostra> gerarAmostras(Lago lago, int totalAmostras) {
        List<Amostra> amostras = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < totalAmostras; i++) {
            double phGerado;

            // Possibilidade pequena de instabilidade (5% de chance)
            if (random.nextInt(100) < 5) {
                // Gera qualquer valor direto na escala de 0 a 14
                phGerado = random.nextDouble() * VALOR_MAXIMO_PH;
            } else {
                // Variação comum (mais controlada ao redor do PH_NORMAL)
                // Multiplicando por 4 e subtraindo 2, temos variação de -2.0 a +2.0
                double variacaoSimples = (random.nextDouble() * 4.0) - 2.0;
                phGerado = PH_NORMAL + variacaoSimples;
            }

            if (phGerado < VALOR_MINIMO_PH) phGerado = VALOR_MINIMO_PH;
            if (phGerado > VALOR_MAXIMO_PH) phGerado = VALOR_MAXIMO_PH;

            Amostra amostra = new Amostra(lago, phGerado);
            amostras.add(amostra);
        }

        return amostras;
    }
}
