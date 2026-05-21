package br.edu.ifba.lagos.sensoriamento;

import br.edu.ifba.lagos.impl.Lago;

import java.util.List;

public interface Sensoriamento<TipoDado> {
    List<TipoDado> gerarAmostras(Lago lago, int totalAmostras);
}
