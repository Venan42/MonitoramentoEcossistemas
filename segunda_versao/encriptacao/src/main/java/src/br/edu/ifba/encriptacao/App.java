package src.br.edu.ifba.encriptacao;

import src.br.edu.ifba.encriptacao.aleatoriedade.GeradorDeAleatoriedadeReal;
import src.br.edu.ifba.encriptacao.chaves.GeradorDeChaves;
import src.br.edu.ifba.encriptacao.impl.GeradorDeChavesImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class App {
    private static final Path CAMINHO_PROJETO = Paths.get(System.getProperty("user.dir"));
    private static final Path RAIZ = CAMINHO_PROJETO.getParent();

    private static final Path CAMINHO_VIDEO = CAMINHO_PROJETO.resolve(Paths.get( "video", "JapanWalking.mp4")).normalize();
    private static final Path CAMINHO_CHAVE_PUBLICA = RAIZ.resolve(Paths.get( "cliente", "chave", "ch_publica.chv")).normalize();
    private static final Path CAMINHO_CHAVE_PRIVADA = RAIZ.resolve(Paths.get( "servidor", "chave", "ch_privada.chv")).normalize();

    private static final String ALGORITMO_DE_ENCRIPTACAO = "RSA";
    private static final int DESLOCAMENTO_MAXIMO = 100;

    public static void main(String[] args) throws Exception {
        System.out.println("dir " + CAMINHO_PROJETO);
        // Garante que as pastas serão criadas
        CAMINHO_VIDEO.getParent().toFile().mkdirs();
        CAMINHO_CHAVE_PUBLICA.toFile().mkdirs();
        CAMINHO_CHAVE_PRIVADA.toFile().mkdirs();

        System.out.println("Iniciando captura de aleatoriedade real a partir do vídeo...");
        GeradorDeAleatoriedadeReal geradorDeAleatoriedadeReal = new GeradorDeAleatoriedadeReal(CAMINHO_VIDEO.toAbsolutePath().toString());

        GeradorDeChaves<GeradorDeAleatoriedadeReal> geradorDeChaves = new GeradorDeChavesImpl();
        geradorDeChaves.inicializar(geradorDeAleatoriedadeReal, ALGORITMO_DE_ENCRIPTACAO);

        SecureRandom randomizador = new SecureRandom();
        int deslocamento = randomizador.nextInt(DESLOCAMENTO_MAXIMO);

        for (int i = 0; i <= deslocamento; i++) {
            System.out.println("Deslocando " + (i + 1) + " frames para aumentar a entropia...");
            geradorDeAleatoriedadeReal.nextInt();
        }

        System.out.println("Gerando par de chaves RSA de 1024 bits...");
        // Passando os caminhos limpos e absolutos como String para o gravador de chaves
        geradorDeChaves.gerarChaves(CAMINHO_CHAVE_PRIVADA.toAbsolutePath().toString(), CAMINHO_CHAVE_PUBLICA.toAbsolutePath().toString());
        geradorDeChaves.finalizar();

        System.out.println("Chaves geradas com sucesso e distribuídas para Cliente e Servidor.");
    }
}