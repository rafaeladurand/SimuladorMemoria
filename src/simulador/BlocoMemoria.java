package simulador;

public class BlocoMemoria {
    private int tamanho;
    private boolean ocupado;
    private Processo processo;
    private BlocoMemoria proximo;

    public BlocoMemoria(int tamanho) {
        this.tamanho = tamanho;
        this.ocupado = false;
        this.processo = null;
        this.proximo = null;
    }

    public int getTamanho() {
        return tamanho;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public Processo getProcesso() {
        return processo;
    }

    public BlocoMemoria getProximo() {
        return proximo;
    }

    public void setProximo(BlocoMemoria proximo) {
        this.proximo = proximo;
    }

    public void alocar(Processo processo) {
        if (!ocupado && processo.getTamanho() <= tamanho) {
            this.processo = processo;
            this.ocupado = true;
        }
    }

    public void liberar() {
        this.processo = null;
        this.ocupado = false;
    }

    @Override
    public String toString() {
        if (ocupado) {
            return "[Ocupado: " + processo + "]";
        } else {
            return "[Livre: " + tamanho + "]";
        }
    }
}
