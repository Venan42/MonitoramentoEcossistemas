package src.br.edu.ifba.encriptacao.chaves;

import java.security.KeyPair;
import java.security.SecureRandom;

import src.br.edu.ifba.encriptacao.excecoes.FalhaGeracaoDeChaves;

public interface GeradorDeChaves<GeradorDeAleatoriedade extends SecureRandom> {
    
    void inicializar(GeradorDeAleatoriedade geradorDeAleatoriedade, String algoritmoDeEncriptacao);

    KeyPair gerarChaves()  throws FalhaGeracaoDeChaves;

    KeyPair gerarChaves(String caminhoChavePrivada, String caminhoChavePublica)  throws FalhaGeracaoDeChaves;

    void finalizar() throws FalhaGeracaoDeChaves;

}
