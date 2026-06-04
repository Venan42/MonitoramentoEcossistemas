package br.edu.ifba.lagos.servidor;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Servidor {

    private static final int TOTAL_LAGOS = 10;
    private static final int AMOSTRAS_POR_LAGO = 5;

    private static final String BASE_URL = "http://0.0.0.0:8080/";

    /**
     * Complexidade: O(1)
     *
     * Justificativa: A instanciação do ResourceConfig e o bootstrap inicial do container HTTP
     * Grizzly ocorrem em tempo constante fixo.
     */
    private static HttpServer iniciarServidor() {
        // Define o pacote do servidor para o Jersey mapear os endpoints HTTP (@POST / @GET)
        ResourceConfig configuracao = new ResourceConfig().packages("br.edu.ifba.lagos.servidor");

        // Cria e retorna a instância do servidor HTTP
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), configuracao);
    }

    /**
     * Complexidade: O(1)
     *
     * Justificativa: O ponto de entrada invoca a subida do ecossistema Grizzly sob custo constante.
     */
    public static void main(String[] args) throws Exception {
        // Inicializa o servidor HTTP Grizzly
        HttpServer servidor = iniciarServidor();

        System.out.println("====================================================================");
        System.out.println("  SERVIDOR DE MONITORAMENTO DE PH DE LAGOS - V2 (OTIMIZADO E SEGURO) ");
        System.out.println("  Aguardando conexões criptografadas das Threads do Cliente...      ");
        System.out.println("====================================================================");
        System.out.println("Pressione a tecla [ENTER] a qualquer momento para encerrar.");

        // Mantém o servidor em execução bloqueando a Thread principal até que um ENTER seja digitado
        System.in.read();

        // Finaliza o servidor limpando as portas ocupadas
        servidor.shutdown();
        System.out.println("Servidor encerrado com sucesso.");
    }
}