package simulador;

public interface Alocador {
    String getNome();
    boolean alocar(Processo processo);
    void desalocar(Processo processo);
}