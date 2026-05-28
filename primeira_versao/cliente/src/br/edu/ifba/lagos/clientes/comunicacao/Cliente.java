package br.edu.ifba.lagos.clientes.comunicacao;

import br.edu.ifba.lagos.clientes.sensoriamento.Sensoriamento;

public interface Cliente<Monitorado, Leitura> {

    public void configurar(Monitorado monitorado, Sensoriamento<Leitura> sensoriamento);

    public Resultado enviar(Leitura leitura) throws Exception;

}
