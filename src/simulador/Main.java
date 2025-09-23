package simulador;

public class Main {
    public static void main(String[] args) {
        final int TAMANHO_MIN_PROCESSO = 10;
        final int TAMANHO_MAX_PROCESSO = 100;
        GeradorDeProcessos gerador = new GeradorDeProcessos();

        System.out.println();
        for (int i = 0; i < 5; i++) {
            Processo p = gerador.gerarProcesso(TAMANHO_MIN_PROCESSO, TAMANHO_MAX_PROCESSO);
            System.out.println(p);
        }

        BlocoMemoria bloco = new BlocoMemoria(100);
        Processo p1 = gerador.gerarProcesso(TAMANHO_MIN_PROCESSO, TAMANHO_MAX_PROCESSO);
        bloco.alocar(p1);

        System.out.println(bloco);

        bloco.liberar();
        System.out.println(bloco);
    }
}
