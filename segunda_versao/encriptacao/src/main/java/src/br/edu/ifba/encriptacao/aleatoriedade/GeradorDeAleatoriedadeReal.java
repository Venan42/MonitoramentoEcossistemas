package src.br.edu.ifba.encriptacao.aleatoriedade;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import javax.imageio.ImageIO;

// Classes da JCodec que substituem o FFmpeg do Bytedeco
import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import src.br.edu.ifba.encriptacao.excecoes.FalhaGeracaoDeChaves;

public class GeradorDeAleatoriedadeReal extends SecureRandom {

    private FrameGrab grabber;
    private File arquivoVideo;

    public GeradorDeAleatoriedadeReal(String caminhoVideo) throws FalhaGeracaoDeChaves {
        try {
            this.arquivoVideo = new File(caminhoVideo);
            this.grabber = FrameGrab.createFrameGrab(NIOUtils.readableChannel(arquivoVideo));
            System.out.println("JCodec inicializado com sucesso para o vídeo: " + caminhoVideo);
        } catch (Exception e) {
            throw new FalhaGeracaoDeChaves("Falha de inicialização do JCodec: " + e.getMessage());
        }
    }

    private BufferedImage proximaImagem() throws FalhaGeracaoDeChaves {
        try {
            Picture quadro = grabber.getNativeFrame();

            if (quadro == null) {
                this.grabber = FrameGrab.createFrameGrab(NIOUtils.readableChannel(arquivoVideo));
                quadro = grabber.getNativeFrame();
            }

            // Converte a estrutura de imagem interna do JCodec para uma BufferedImage padrão do Java
            return AWTUtil.toBufferedImage(quadro);
        } catch (Exception e) {
            throw new FalhaGeracaoDeChaves("Falha ao capturar quadro do vídeo: " + e.getMessage());
        }
    }

    @Override
    public int nextInt() {
        int val = 0;
        int[] aleatoriedade = getAleatoriedade();
        if (aleatoriedade != null && aleatoriedade.length >= 4) {
            val |= aleatoriedade[0] << 24;
            val |= aleatoriedade[1] << 16;
            val |= aleatoriedade[2] << 8;
            val |= aleatoriedade[3];
        }
        return val;
    }

    @Override
    public long nextLong() {
        long val = 0;
        int[] aleatoriedade = getAleatoriedade();
        if (aleatoriedade != null && aleatoriedade.length >= 8) {
            val |= (long) aleatoriedade[0] << 56;
            val |= (long) aleatoriedade[1] << 48;
            val |= (long) aleatoriedade[2] << 40;
            val |= (long) aleatoriedade[3] << 32;
            val |= (long) aleatoriedade[4] << 24;
            val |= (long) aleatoriedade[5] << 16;
            val |= (long) aleatoriedade[6] << 8;
            val |= aleatoriedade[7];
        }
        return val;
    }

    private int[] getAleatoriedade() {
        int[] aleatoriedade = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BufferedImage img = proximaImagem();

            if (img != null) {
                // Transforma a matriz de pixels do frame em um fluxo de bytes comprimidos (JPEG)
                ImageIO.write(img, "jpeg", stream);
                byte[] bytes = stream.toByteArray();

                // Mapeia os bytes para inteiros de 0 a 255 para servir de semente caótica
                aleatoriedade = new int[bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    aleatoriedade[i] = bytes[i] & 0xff;
                }
            }
        } catch (IOException | FalhaGeracaoDeChaves e) {
            e.printStackTrace();
        }
        return aleatoriedade;
    }

    public void finalizar() throws FalhaGeracaoDeChaves {
        System.out.println("[JCodec] Leitura de vídeo finalizada.");
    }
}