package br.edu.ifba.lagos.clientes.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.edu.ifba.lagos.clientes.comunicacao.Cliente;
import br.edu.ifba.lagos.clientes.comunicacao.Resultado;
import br.edu.ifba.lagos.clientes.sensoriamento.Sensoriamento;

public class ClienteImpl implements Cliente<Lago, Amostra>, Runnable {

    private static final int AMOSTRAS_POR_LAGO = 5;

    private static final String URL_SERVIDOR = "http://172.12.171.74:8080";
    private static final String URL_LAGOS = URL_SERVIDOR + "/lagos/";

    private Lago lago = null;
    private Sensoriamento<Amostra> sensoriamento = null;

    @Override
    public void configurar(Lago lago, Sensoriamento<Amostra> sensoriamento) {
        this.lago = lago;
        this.sensoriamento = sensoriamento;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Resultado enviar(Amostra amostra) throws Exception {
        Resultado resultado = Resultado.SUCESSO;

        URL urlEnvio = new URL(URL_LAGOS + lago.getId() + "/" + lago.getNome() + "/" + amostra.getValorPH());
        System.out.println(urlEnvio);
        HttpURLConnection conexao = (HttpURLConnection) urlEnvio.openConnection();
        conexao.setRequestMethod("POST");
        if (conexao.getResponseCode() != 200) {
            resultado = Resultado.ERRO;

            throw new Exception("erro de conexão com o servidor");
        }
        conexao.disconnect();

        return resultado;
    }

    @Override
    public void run() {
        List<Amostra> amostras = sensoriamento.gerarAmostras(lago, AMOSTRAS_POR_LAGO);

        for (Amostra amostra : amostras) {
            System.out.println("Amostra sendo enviada...");

            try {
                enviar(amostra);

                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
