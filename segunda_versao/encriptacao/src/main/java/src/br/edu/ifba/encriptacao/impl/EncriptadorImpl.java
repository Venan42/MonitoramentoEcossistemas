package src.br.edu.ifba.encriptacao.impl;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import src.br.edu.ifba.encriptacao.encriptador.Encriptador;
import src.br.edu.ifba.encriptacao.excecoes.FalhaEncriptacao;

public class EncriptadorImpl extends Encriptador {

    // O(1) - Construtor repassa as referências em tempo constante
    public EncriptadorImpl(KeyPair chaves, String algoritmoDeEncriptacao) {
        super(chaves, algoritmoDeEncriptacao);
    }

    /**
     * Complexidade: O(N) onde N é o tamanho do texto (String) de entrada.
     *
     * Justificativa: A conversão da String para bytes e a codificação final em Base64
     * processam os dados de maneira estritamente linear. A operação interna do bloco
     * Cipher.doFinal possui custo constante O(1) dado que o tamanho máximo do bloco é
     * limitado estaticamente pelo tamanho da chave RSA (1024 bits).
     */
    @Override
    public String encriptar(String dados) throws FalhaEncriptacao {
        String encriptacao = "";

        synchronized (encriptacao) {
            try {
                Cipher cifrador = Cipher.getInstance(algoritmoDeEncriptacao);
                cifrador.init(Cipher.ENCRYPT_MODE, chaves.getPublic());

                byte[] cifragem = cifrador.doFinal(dados.getBytes(StandardCharsets.UTF_8));
                encriptacao = Base64.getEncoder().encodeToString(cifragem);
            }
            catch (NoSuchAlgorithmException |
                   NoSuchPaddingException |
                   InvalidKeyException |
                   IllegalBlockSizeException |
                   BadPaddingException e) {
                throw new FalhaEncriptacao("falha encriptando dados: " + e.getMessage());
            }

        }

        return encriptacao;
    }

    /**
     * Complexidade: O(N) onde N é o tamanho do texto criptografado em Base64.
     *
     * Justificativa: O decodificador Base64 processa o texto sequencialmente de forma linear O(N).
     * A decifração do bloco pelo Cipher opera em tempo fixo O(1) devido ao tamanho de bloco
     * delimitado pela chave assimétrica.
     */
    @Override
    public String desencriptar(String encriptacao) throws FalhaEncriptacao {
        String dados;

        try {
            Cipher cifrador = Cipher.getInstance(algoritmoDeEncriptacao);
            cifrador.init(Cipher.DECRYPT_MODE, chaves.getPrivate());

            byte[] bytes = Base64.getDecoder().decode(encriptacao);
            byte[] bytesDecriptados = cifrador.doFinal(bytes);

            dados = new String(bytesDecriptados, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException |
                 NoSuchPaddingException |
                 InvalidKeyException |
                 IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new FalhaEncriptacao("falha encriptando dados: " + e.getMessage());
        }

        return dados;
    }
}