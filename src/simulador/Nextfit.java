package simulador;

public class Nextfit implements Alocador { 
    private Memoria memoria;
    private BlocoMemoria cursor;

    public Nextfit(Memoria memoria) {
        this.memoria = memoria;
        this.cursor = memoria.getPrimeiroBloco();
    }

    @Override
    public String getNome() {
        return "Next-Fit";
    }

    @Override
    public boolean alocar(Processo processo) {
        if (memoria.getPrimeiroBloco() == null) {
            return false;
        }
        if (cursor == null) cursor = memoria.getPrimeiroBloco();

        BlocoMemoria inicio = cursor;
        BlocoMemoria atual = cursor;

        do {
            if (!atual.isOcupado() && atual.getTamanho() >= processo.getTamanho()) {
                memoria.alocar(atual, processo);
                cursor = atual.getProximo();
                if (cursor == null) {
                    cursor = memoria.getPrimeiroBloco();
                }
                return true;
            }
            atual = (atual.getProximo() != null) ? atual.getProximo() : memoria.getPrimeiroBloco();
        } while (atual != inicio);

        return false;
    }

    @Override
    public void desalocar(Processo processo) {
        BlocoMemoria atual = memoria.getPrimeiroBloco();
        while (atual != null) {
            if (atual.isOcupado() && atual.getProcesso().getId() == processo.getId()) {
                memoria.desalocar(atual);
                return;
            }
            atual = atual.getProximo();
        }
    }
}