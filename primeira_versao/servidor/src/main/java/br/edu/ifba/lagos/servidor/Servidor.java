package br.edu.ifba.lagos.servidor;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Servidor {
    // URL base indicando que o servidor aceitará conexões de qualquer IP (0.0.0.0) na porta 8080
    private static final String BASE_URL = "http://0.0.0.0:8080/";

    /**
     * Configura e inicializa o servidor HTTP Grizzly.
     * O Jersey varre o pacote especificado em busca da classe 'Rotas.java'.
     */
    private static HttpServer iniciarServidor() {
        // Define o pacote do servidor para o Jersey mapear os endpoints HTTP (@POST / @GET)
        ResourceConfig configuracao = new ResourceConfig().packages("br.edu.ifba.lagos.servidor");

        // Cria e retorna a instância do servidor HTTP [cite: 74]
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), configuracao);
    }

    public static void main(String[] args) throws Exception {
        // Inicializa o servidor HTTP Grizzly
        HttpServer servidor = iniciarServidor();

        System.out.println("======================================================");
        System.out.println("  SERVIDOR DE MONITORAMENTO DE PH DE LAGOS ATIVO  ");
        System.out.println("  Aguardando conexões das Threads do Cliente...       ");
        System.out.println("======================================================");
        System.out.println("Pressione a tecla [ENTER] a qualquer momento para encerrar.");

        // Mantém o servidor em execução bloqueando a Thread principal até que um ENTER seja digitado
        System.in.read();

        // Finaliza o servidor limpando as portas ocupadas
        servidor.shutdown();
        System.out.println("Servidor encerrado com sucesso.");
    }
}