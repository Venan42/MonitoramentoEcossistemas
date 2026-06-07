package br.edu.ifba.lagos.servidor;

import br.edu.ifba.lagos.servidor.impl.Lago;
import br.edu.ifba.lagos.servidor.impl.Amostra;
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
    private static Operacoes<Lago, Amostra> operacoes = null;

    private static synchronized Operacoes<Lago, Amostra> getOperacoes() {
        if (operacoes == null) {
            operacoes = new OperacoesImpl();
        }
        return operacoes;
    }

    /**
     * Acesso: GET http://localhost:8080/lagos/
     * Complexidade: O(1)
     * Justificativa: A instanciação dos objetos locais ocorre em O(1), mas a invocação do método
     * gravar() interage com uma estrutura de dados de árvore balanceada (TreeMap) no Servidor,
     * gerando um custo logarítmico para pesquisar e posicionar a nova amostra.
     */
    @GET
    @Path("/")
    public Response getInformacoes() {
        return Response.ok("Serviço de Atendimento e Monitoramento de Lagos ativo, v1.0", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Acesso: POST http://localhost:8080/lagos/{id}/{nome}/{ph}
     * Complexidade: O(Log N) onde N é a quantidade de lagos monitorados no sistema.
     * Justificativa: A instanciação dos objetos locais ocorre em O(1), mas a invocação do método
     * gravar() interage com uma estrutura de dados de árvore balanceada (TreeMap) no Servidor,
     * gerando um custo logarítmico para pesquisar e posicionar a nova amostra.
     */
    @POST
    @Path("{id}/{nome}/{ph}")
    public Response gravarLeitura(@PathParam("id") int id, @PathParam("nome") String nome, @PathParam("ph") double ph) {
        Lago lago = new Lago(id, nome);
        Amostra leitura = new Amostra(lago, ph);

        // Salva incrementalmente no banco de dados em memória do Servidor
        getOperacoes().gravar(lago, leitura);

        return Response.ok().build();
    }

    /**
     * Acesso: GET http://localhost:8080/lagos/imprimir
     * Complexidade: O(M) onde M representa o número total de amostras armazenadas na memória.
     * Justificativa: O método necessita realizar uma varredura linear completa passando por cada
     * elemento da coleção de dados para formatar e imprimir o histórico no console.
     */
    @GET
    @Path("imprimir")
    public Response imprimirLagos() {
        getOperacoes().imprimir();
        return Response.ok("Relatório de dados brutos impresso no console do Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Acesso: GET http://localhost:8080/lagos/ordenar/ph
     * Complexidade: O(M Log M) onde M é o total de amostras cadastradas.
     * Justificativa: Dispara internamente o algoritmo Merge Sort, que possui comportamento
     * assintótico semi-linear estável O(M Log M) em todos os casos (pior, melhor e médio).
     */
    @GET
    @Path("ordenar/ph")
    public Response ordenarPorPH() {
        getOperacoes().ordenar(TipoOrdenacao.POR_PH);
        return Response.ok("Algoritmo Merge Sort (por pH) executado no Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Acesso: GET http://localhost:8080/lagos/ordenar/nome
     * Complexidade: O(M Log M) onde M é o total de amostras cadastradas.
     * Justificativa: De maneira idêntica à ordenação por pH, invoca o Merge Sort para dividir
     * e conquistar a coleção de dados baseando-se no critério lexicográfico do nome do lago.
     */
    @GET
    @Path("ordenar/nome")
    public Response ordenarPorNome() {
        getOperacoes().ordenar(TipoOrdenacao.POR_NOME_LAGO);
        return Response.ok("Algoritmo Merge Sort (por Nome) executado no Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Acesso: GET http://localhost:8080/lagos/trios
     * Complexidade de Tempo: O(T³) onde T representa o número total de amostras armazenadas no Servidor.
     * Justificativa: Na Versão 1, o sistema adota uma arquitetura puramente centralizada.
     * Toda a carga computacional é executada no Servidor, que realiza uma busca exaustiva
     * através de três laços de repetição rigidamente aninhados para avaliar todas as combinações
     * de 3 em 3 amostras da coleção em busca de pH neutro, configurando um custo cúbico.
     */
    @GET
    @Path("trios")
    public Response buscarTrios() {
        // Na Versão 1, o Servidor processa o laço cúbico na nuvem sob demanda
        getOperacoes().buscarTriosDePHNeutro();

        return Response.ok("Algoritmo O(T³) de busca de trios executado no Servidor.", MediaType.TEXT_PLAIN).build();
    }

    /**
     * Acesso: GET http://localhost:8080/lagos/analisar
     * Complexidade: O(M) onde M é a quantidade total de amostras registradas.
     * Justificativa: Realiza uma varredura puramente linear, iterando sobre a totalidade de dados
     * coletados uma única vez para extrair as métricas e médias estatísticas do ecossistema.
     */
    @GET
    @Path("analisar")
    public Response analisarQualidade() {
        getOperacoes().analisarQualidadeLagos();
        return Response.ok("Análise estatística de qualidade processada no Servidor.", MediaType.TEXT_PLAIN).build();
    }
}