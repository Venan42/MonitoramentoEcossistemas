package br.edu.ifba.lagos.servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifba.lagos.servidor.impl.Amostra;
import br.edu.ifba.lagos.servidor.impl.Lago;
import br.edu.ifba.lagos.servidor.impl.OperacoesImpl;
import br.edu.ifba.lagos.servidor.operacoes.Operacoes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("lagos")
public class Rotas {

    private static Operacoes<Lago, Amostra> operacoes = null;

    // O(1) - Recuperação controlada por Singleton em tempo constante
    private static synchronized Operacoes<Lago, Amostra> getOperacoes() {
        if (operacoes == null) {
            operacoes = new OperacoesImpl();
        }
        return operacoes;
    }

    private static final String INFORMACOES = "Serviço de Monitoramento de Lagos - Versão 2 (Otimizada e Segura)";
    private static final String ALGORITMO_DE_ENCRIPTACAO = "RSA";

    private PrivateKey chave = null;

    /**
     * Complexidade: O(1)
     *
     * Justificativa: A localização do arquivo via caminhos NIO absolutos e o parsing
     * dos bytes da chave assimétrica estática (1024 bits) operam em tempo fixo e limitado.
     */
    private PrivateKey getChavePrivada() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (chave == null) {
            java.nio.file.Path diretorioPrincipal = Paths.get(System.getProperty("user.dir"));
            java.nio.file.Path caminhoChave = diretorioPrincipal.resolve(Paths.get("segunda_versao", "servidor", "chave", "ch_privada.chv")).normalize();

            File arquivo = caminhoChave.toFile();
            if (!arquivo.exists()) {
                throw new IOException("Chave privada RSA ausente no caminho resolvido: " + arquivo.getAbsolutePath());
            }

            try (FileInputStream stream = new FileInputStream(arquivo)) {
                byte[] bytes = stream.readAllBytes();
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
                KeyFactory kf = KeyFactory.getInstance(ALGORITMO_DE_ENCRIPTACAO);
                chave = kf.generatePrivate(spec);
            }
        }
        return chave;
    }

    /**
     * Complexidade: O(1)
     *
     * Justificativa: A decifração executada pela API nativa sobre blocos controlados
     * pelo algoritmo RSA possui tempo de processamento constante independente do volume do sistema.
     */
    private String desencriptar(byte[] encriptado) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ALGORITMO_DE_ENCRIPTACAO);
        cipher.init(Cipher.DECRYPT_MODE, getChavePrivada());
        byte[] desencriptado = cipher.doFinal(encriptado);
        return new String(desencriptado);
    }

    @GET
    @Path("/")
    public Response getInformacoes() {
        return Response.ok(INFORMACOES, MediaType.TEXT_PLAIN).build();
    }

    /**
     * Complexidade: O(Log N) onde N é o total de lagos monitorados.
     *
     * Justificativa: As ações de decodificação Base64, decriptação assimétrica e parsing JSON
     * são lineares sobre o pacote fixo O(1). O limitador assintótico real é o método gravar()
     * que realiza inserções em árvore balanceada TreeMap O(Log N).
     */
    @POST
    @Path("/leituras/{dados}")
    public Response gravarLeitura(@PathParam("dados") String dados) {
        Response resposta = Response.serverError().build();
        System.out.println("Payload de pH encriptado recebido: " + dados);

        try {
            String json = desencriptar(Base64.getUrlDecoder().decode(dados));
            ObjectMapper mapeador = new ObjectMapper();
            JsonNode dic = mapeador.readTree(json);

            Lago lago = new Lago(dic.get("id").asInt(), dic.get("nome").asText());
            Amostra leitura = new Amostra(lago, dic.get("ph").asDouble());

            getOperacoes().gravar(lago, leitura);
            resposta = Response.ok().build();
        } catch (Exception e) {
            System.out.println("Erro de integridade: Falha ao descriptografar ou processar pacote de pH.");
            e.printStackTrace();
        }

        return resposta;
    }

    /**
     * Complexidade: O(Log N) onde N é a quantidade de lagos cadastrados.
     *
     * Justificativa: A extração do dado do JSON roda sob custo fixo, delegando o teto assintótico
     * para a busca e atualização do contador de trios neutros associado à chave no TreeMap O(Log N).
     */
    @POST
    @Path("/trios/{dados}")
    public Response gravarTrios(@PathParam("dados") String dados) {
        Response resposta = Response.serverError().build();
        System.out.println("Alerta de trios neutros encriptado recebido: " + dados);

        try {
            String json = desencriptar(Base64.getUrlDecoder().decode(dados));
            ObjectMapper mapeador = new ObjectMapper();
            JsonNode dic = mapeador.readTree(json);

            Lago lago = new Lago(dic.get("id").asInt(), dic.get("nome").asText());
            int trios = dic.get("trios").asInt();

            getOperacoes().gravar(lago, trios);
            resposta = Response.ok().build();
        } catch (Exception e) {
            System.out.println("Erro de integridade: Falha ao decifrar relatório consolidado de trios.");
            e.printStackTrace();
        }

        return resposta;
    }

    /**
     * Complexidade: O(M) onde M representa a quantidade de lagos que já computaram trios.
     *
     * Justificativa: Invoca a consolidação global do sistema, que executa um laço linear simples
     * varrendo sequencialmente todos os valores acumulados no mapa de controle.
     */
    @GET
    @Path("trios")
    public Response detectarTrios() {
        int totalTrios = getOperacoes().detectarTriosNeutros();
        return Response.ok("Total de combinações de trios com pH neutro consolidados no ecossistema: " + totalTrios, MediaType.TEXT_PLAIN).build();
    }
}