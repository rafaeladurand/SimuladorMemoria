package simulador;

import java.util.Random;

public class GeradorDeProcessos {
    private Random random = new Random();

    public Processo gerarProcesso(int minSize, int maxSize) {
        int tamanho = random.nextInt(41) + 10;
        return new Processo(tamanho);
    }
}
