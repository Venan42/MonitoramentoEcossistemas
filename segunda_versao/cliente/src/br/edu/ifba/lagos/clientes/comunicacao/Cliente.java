package br.edu.ifba.lagos.clientes.comunicacao;

import br.edu.ifba.lagos.clientes.sensoriamento.Sensoriamento;

public interface Cliente<Lago, Amostra> {

    void configurar(Lago monitorado, Sensoriamento<Amostra> sensoriamento);

    Resultado enviar(Amostra leitura) throws Exception;

    Resultado enviar(int trios) throws Exception;

}
