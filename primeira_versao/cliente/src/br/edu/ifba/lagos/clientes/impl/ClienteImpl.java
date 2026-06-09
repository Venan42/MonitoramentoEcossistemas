package br.edu.ifba.lagos.clientes.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.edu.ifba.lagos.clientes.comunicacao.Cliente;
import br.edu.ifba.lagos.clientes.comunicacao.Resultado;
import br.edu.ifba.lagos.clientes.sensoriamento.Sensoriamento;

public class ClienteImpl implements Cliente<Lago, Amostra>, Runnable {
    // Constante que dita o limite fixo de envio por thread
    private static final int AMOSTRAS_POR_LAGO = 5;

    private static final String URL_SERVIDOR = "http://localhost:8080";
    private static final String URL_LAGOS = URL_SERVIDOR + "/lagos/";

    private Lago lago = null;
    private Sensoriamento<Amostra> sensoriamento = null;

    /**
     * Complexidade: O(1)
     * Justificativa: Realiza a atribuição de referências aos atributos da classe
     * em tempo constante.
     */
    @Override
    public void configurar(Lago lago, Sensoriamento<Amostra> sensoriamento) {
        this.lago = lago;
        this.sensoriamento = sensoriamento;
    }

    /**
     * Complexidade: O(1)
     * Justificativa: A concatenação de strings para a montagem dos parâmetros na URL
     * e o disparo da requisição síncrona POST via HttpURLConnection operam sobre payloads
     * atômicos e fixos, consumindo tempo constante independente do volume total de dados.
     */
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

    /**
     * Complexidade: O(M) onde M é a quantidade de Amostras
     * Justificativa: O método delega a geração de dados ao sensoriamento e executa um laço
     * 'for' que itera pelas M amostras.
     */
    @Override
    public void run() {
        List<Amostra> amostras = sensoriamento.gerarAmostras(lago, AMOSTRAS_POR_LAGO);

        for (Amostra amostra : amostras) {
            System.out.println("Amostra sendo enviada...");

            try {
                enviar(amostra);

                Thread.sleep(50); // Delay constante entre transmissões
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}