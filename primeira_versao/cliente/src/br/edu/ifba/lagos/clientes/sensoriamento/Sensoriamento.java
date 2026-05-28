package br.edu.ifba.lagos.clientes.sensoriamento;

import br.edu.ifba.lagos.clientes.impl.Lago;

import java.util.List;

public interface Sensoriamento<TipoDado> {
    List<TipoDado> gerarAmostras(Lago lago, int totalAmostras);
}
