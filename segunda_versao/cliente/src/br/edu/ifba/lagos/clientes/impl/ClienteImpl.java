package br.edu.ifba.lagos.clientes.impl;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

import br.edu.ifba.lagos.clientes.comunicacao.Cliente;
import br.edu.ifba.lagos.clientes.comunicacao.Resultado;
import br.edu.ifba.lagos.clientes.sensoriamento.Sensoriamento;

public class ClienteImpl implements Cliente<Lago, Amostra>, Runnable {

    private static final int TOTAL_DE_AMOSTRAS = 40;

    private static final String URL_SERVIDOR = "http://localhost:8080";
    private static final String URL_LAGOS = URL_SERVIDOR + "/lagos/";

    private static final String ALGORITMO_ENCRIPTACAO = "RSA";

    private Lago monitorado = null;
    private Sensoriamento<Amostra> sensoriamento = null;
    private PublicKey chave = null;

    private List<Amostra> historicoLocal = new ArrayList<>();

    /**
     * Complexidade: O(1)
     * Justificativa: Atribui as referências aos objetos locais e invoca o carregamento da
     * chave pública em tempo constante delimitado.
     */
    @Override
    public void configurar(Lago monitorado, Sensoriamento<Amostra> sensoriamento) {
        try {
            this.monitorado = monitorado;
            this.sensoriamento = sensoriamento;
            this.chave = getChave();
        } catch (Exception e) {
            System.out.println("Falha crítica ao configurar cliente e carregar credenciais criptográficas.");
            e.printStackTrace();
        }
    }

    /**
     * Complexidade: O(1)
     * Justificativa: A resolução do caminho absoluto via java.nio e a leitura sequencial dos
     * bytes que constituem a chave pública de tamanho fixo (1024 bits) operam em tempo constante.
     */
    private PublicKey getChave() throws Exception {
        Path diretorioPrincipal = Paths.get(System.getProperty("user.dir"));

        // Caminho relativo padrão se o projeto for executado a partir do módulo do cliente
        File arquivo = diretorioPrincipal.resolve(Paths.get("chave", "ch_publica.chv")).toFile();

        // Caminho relativo se executado a partir da raiz do projeto geral
        if (!arquivo.exists()) {
            arquivo = diretorioPrincipal.resolve(Paths.get("segunda_versao", "cliente", "chave", "ch_publica.chv")).toFile();
        }

        // Se nenhum dos dois existir, um erro é lançado.
        if (!arquivo.exists()) {
            throw new java.io.FileNotFoundException("Chave pública não encontrada em nenhum dos caminhos mapeados.");
        }

        try (FileInputStream stream = new FileInputStream(arquivo)) {
            byte[] bytes = stream.readAllBytes();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITMO_ENCRIPTACAO);
            return kf.generatePublic(spec);
        }
    }

    /**
     * Complexidade: O(1)
     * Justificativa: O processo de cifragem assimétrica RSA é executado sobre blocos de texto
     * curtos e padronizados, consumindo tempo de CPU constante e rigidamente controlado.
     */
    private byte[] encriptar(String dados) throws Exception {
        Cipher cifrador = Cipher.getInstance(ALGORITMO_ENCRIPTACAO);
        cifrador.init(Cipher.ENCRYPT_MODE, chave);
        return cifrador.doFinal(dados.getBytes());
    }

    /**
     * Complexidade: O(1)
     * Justificativa: A montagem manual do JSON nativo elimina overhead de bibliotecas em O(1).
     * A encriptação RSA e o envio síncrono POST via HttpURLConnection operam sobre payloads
     * atômicos de tamanho fixo e restrito.
     */
    @SuppressWarnings("deprecation")
    @Override
    public Resultado enviar(Amostra leitura) throws Exception {
        Resultado resultado = Resultado.SUCESSO;

        // Montagem manual do JSON usando String nativa do Java
        String jsonPuro = "{\"id\":" + monitorado.getId() +
                ",\"nome\":\"" + monitorado.getNome() +
                "\",\"ph\":" + leitura.getValorPH() + "}";

        String dadosCriptografados = Base64.getUrlEncoder().encodeToString(encriptar(jsonPuro));
        URL urlEnvio = new URL(URL_LAGOS + "leituras/" + dadosCriptografados);

        HttpURLConnection conexao = (HttpURLConnection) urlEnvio.openConnection();
        conexao.setRequestMethod("POST");
        if (conexao.getResponseCode() != 200) {
            conexao.disconnect();
            throw new Exception("Erro de conexão com o servidor ao enviar leitura bruta.");
        }
        conexao.disconnect();

        return resultado;
    }

    /**
     * Complexidade: O(1)
     * Justificativa: De maneira idêntica ao envio de amostras, a montagem manual do payload
     * e o envio síncrono do totalizador de trios neutros operam sob custo constante O(1).
     */
    @SuppressWarnings("deprecation")
    @Override
    public Resultado enviar(int trios) throws Exception {
        Resultado resultado = Resultado.SUCESSO;

        // SUBSTITUIÇÃO: Montagem manual e limpa do JSON de trios usando String nativa do Java
        String jsonPuro = "{\"id\":" + monitorado.getId() +
                ",\"nome\":\"" + monitorado.getNome() +
                "\",\"trios\":" + trios + "}";

        String dadosCriptografados = Base64.getUrlEncoder().encodeToString(encriptar(jsonPuro));
        URL urlEnvio = new URL(URL_LAGOS + "trios/" + dadosCriptografados);

        HttpURLConnection conexao = (HttpURLConnection) urlEnvio.openConnection();
        conexao.setRequestMethod("POST");
        if (conexao.getResponseCode() != 200) {
            conexao.disconnect();
            throw new Exception("Erro de conexão com o servidor ao enviar consolidação de trios.");
        }
        conexao.disconnect();

        return resultado;
    }

    /**
     * Complexidade: O(T³) onde T representa o tamanho do histórico acumulado na memória da borda.
     * Justificativa: O método executa três loops puramente aninhados para computar de forma exaustiva
     * todas as combinações temporais possíveis de amostras coletadas. Os índices controlados de forma
     * incremental (`i`, `i+1`, `j+1`) evitam colisões de leitura sem alterar a categoria cúbica do método.
     */
    @Override
    private int buscarTriosPHNeutroLocal(List<Amostra> historico) {
        int contadorTrios = 0;
        int t = historico.size();

        for (int i = 0; i < t; i++) {
            for (int j = i + 1; j < t; j++) {
                for (int k = j + 1; k < t; k++) {
                    double ph1 = historico.get(i).getValorPH();
                    double ph2 = historico.get(j).getValorPH();
                    double ph3 = historico.get(k).getValorPH();

                    double media = (ph1 + ph2 + ph3) / 3.0;
                    if (Math.abs(media - 7.0) < 0.1) {
                        contadorTrios++;
                    }
                }
            }
        }
        return contadorTrios;
    }

    /**
     * Complexidade: O(K * T³) onde K é o TOTAL_DE_AMOSTRAS geradas e T é a progressão do histórico local.
     * Justificativa: O laço principal itera linearmente sobre as novas amostras coletadas via sensoriamento.
     * A cada passo, a amostra é injetada no vetor de histórico e submetida à função polinomial cúbica
     * `calcularTriosPHNeutroLocal`, tornando-se o fator dominante da Thread em tempo de execução.
     */
    @Override
    public void run() {
        List<Amostra> amostrasGeradas = sensoriamento.gerarAmostras(monitorado, TOTAL_DE_AMOSTRAS);

        for (Amostra amostra : amostrasGeradas) {
            System.out.println("[" + monitorado.getNome() + "] Capturando dados de sensoriamento químico...");

            try {
                historicoLocal.add(amostra);

                enviar(amostra);

                int totalTriosNeutros = buscarTriosPHNeutroLocal(historicoLocal);

                enviar(totalTriosNeutros);

                Thread.sleep(50);
            } catch (Exception e) {
                System.out.println("Falha na transmissão ou processamento da Thread: " + monitorado.getNome());
                e.printStackTrace();
            }
        }
        System.out.println("Ciclo de monitoramento concluído com sucesso para o nó: " + monitorado.getNome());
    }
}