package simulador;
//Classe criada para testar a criação dos processos e o bloco de memoria
public class Main {
    public static void main(String[] args) {
        GeradorDeProcessos gerador = new GeradorDeProcessos();

        for (int i = 0; i < 5; i++) {
            Processo p = gerador.gerarProcesso();
            System.out.println(p);
        }

        BlocoMemoria bloco = new BlocoMemoria(100);
        Processo p1 = gerador.gerarProcesso();
        bloco.alocar(p1);

        System.out.println(bloco);

        bloco.liberar();
        System.out.println(bloco);
    }
}
