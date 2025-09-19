package simulador;

public class Processo {
    private static int contador = 0;
    private int id;
    private int tamanho;

    public Processo(int tamanho) {
        this.id = ++contador;
        this.tamanho = tamanho;
    }

    public int getId() {
        return id;
    }

    public int getTamanho() {
        return tamanho;
    }

    @Override
    public String toString() {
        return "Processo{" +
                "id=" + id +
                ", tamanho=" + tamanho +
                '}';
    }
}
