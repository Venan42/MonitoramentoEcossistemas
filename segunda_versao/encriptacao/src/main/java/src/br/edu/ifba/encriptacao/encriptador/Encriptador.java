package src.br.edu.ifba.encriptacao.encriptador;

import java.security.KeyPair;

import src.br.edu.ifba.encriptacao.excecoes.FalhaEncriptacao;

public abstract class Encriptador {
    
    protected KeyPair chaves = null;
    protected String algoritmoDeEncriptacao = null;


    public Encriptador(KeyPair chaves, String algoritmoDeEncriptacao) {
        this.chaves = chaves;
        this.algoritmoDeEncriptacao = algoritmoDeEncriptacao;
    }

    public abstract String encriptar(String dados) throws FalhaEncriptacao;

    public abstract String desencriptar(String encriptacao) throws FalhaEncriptacao;

}
