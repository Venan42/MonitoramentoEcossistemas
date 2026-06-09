package br.edu.ifba.lagos.clientes.comunicacao;

import br.edu.ifba.lagos.clientes.sensoriamento.Sensoriamento;

public interface Cliente<Lago, Amostra> {

    public void configurar(Lago lago, Sensoriamento<Amostra> sensoriamento);

    public Resultado enviar(Amostra amostra) throws Exception;

}
