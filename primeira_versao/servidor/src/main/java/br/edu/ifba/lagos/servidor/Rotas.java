package br.edu.ifba.lagos.servidor;

import br.edu.ifba.lagos.servidor.impl.Amostra;
import br.edu.ifba.lagos.servidor.impl.Lago;
import br.edu.ifba.lagos.servidor.impl.OperacoesImpl;
import br.edu.ifba.lagos.servidor.operacoes.Operacoes;
import br.edu.ifba.lagos.servidor.ordenador.TipoOrdenacao;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("lagos")
public class Rotas {

    // Instância única das operações (Padrão Singleton estruturado pelo professor)
    private static Operacoes<Lago, Amostra> operacoes = null;

    private static synchronized Operacoes<Lago, Amostra> getOperacoes() {
        if (operacoes == null) {
            operacoes = new OperacoesImpl();
        }
        return operacoes;
    }

    /**
     * Endpoint básico para testar se o servidor HTTP está respondendo na rede.
     * Acesso: GET http://localhost:8080/lagos/
     */
    @GET
    @Path("/")
    public Response getInformacoes() {
        return Response.ok("Serviço de Atendimento e Monitoramento de Lagos ativo, v1.0", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Endpoint utilizado pelas Threads do Cliente para enviar os dados coletados de cada lago.
     * Acesso: POST http://localhost:8080/lagos/{id}/{nome}/{ph}
     */
    @POST
    @Path("{id}/{nome}/{ph}")
    public Response gravarLeitura(
            @PathParam("id") int id,
            @PathParam("nome") String nome,
            @PathParam("ph") double ph) {

        // Reconstrói os objetos recebidos via parâmetros da URL HTTP
        Lago lago = new Lago(id, nome);
            Amostra leitura = new Amostra(lago, ph); // 0 como ID padrão temporário da leitura

        // Salva incrementalmente no banco de dados em memória do Servidor
        getOperacoes().gravar(lago, leitura);

        return Response.ok().build();
    }

    /**
     * Endpoint para imprimir todas as amostras brutas salvas no Servidor.
     * Acesso: GET http://localhost:8080/lagos/imprimir
     */
    @GET
    @Path("imprimir")
    public Response imprimirLagos() {
        getOperacoes().imprimir();
        return Response.ok("Relatório de dados brutos impresso no console do Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Endpoint para disparar o Merge Sort e ordenar as amostras internas por pH.
     * Acesso: GET http://localhost:8080/lagos/ordenar/ph
     */
    @GET
    @Path("ordenar/ph")
    public Response ordenarPorPH() {
        getOperacoes().ordenar(TipoOrdenacao.POR_PH);
        return Response.ok("Algoritmo Merge Sort (por pH) executado no Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Endpoint para disparar o Merge Sort e ordenar as amostras alfabeticamente por Nome do Lago.
     * Acesso: GET http://localhost:8080/lagos/ordenar/nome
     */
    @GET
    @Path("ordenar/nome")
    public Response ordenarPorNome() {
        getOperacoes().ordenar(TipoOrdenacao.POR_NOME_LAGO);
        return Response.ok("Algoritmo Merge Sort (por Nome) executado no Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Endpoint de altíssima complexidade O(T³) para buscar combinações de trios com pH neutro.
     * Acesso: GET http://localhost:8080/lagos/trios
     */
    @GET
    @Path("trios")
    public Response buscarTrios() {
        getOperacoes().buscarTriosDePHNeutro();
        return Response.ok("Algoritmo O(T³) de busca de trios executado no Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Endpoint para rodar o processamento estatístico de qualidade dos lagos.
     * Acesso: GET http://localhost:8080/lagos/analisar
     */
    @GET
    @Path("analisar")
    public Response analisarQualidade() {
        getOperacoes().analisarQualidadeLagos();
        return Response.ok("Análise estatística de qualidade processada no Servidor.", MediaType.TEXT_PLAIN).build();
    }
}