package simulador;

public class Worstfit implements Alocador {
    private final Memoria memoria;

    public Worstfit(Memoria memoria) {
        this.memoria = memoria;
    }

    @Override
    public String getNome() {
        return "Worst-Fit";
    }

    @Override
    public boolean alocar(Processo processo) {
        BlocoMemoria atual = memoria.getPrimeiroBloco();
        BlocoMemoria piorBloco = null;

        while (atual != null) {
            if (!atual.isOcupado() && atual.getTamanho() >= processo.getTamanho()) {
                if (piorBloco == null || atual.getTamanho() > piorBloco.getTamanho()) {
                    piorBloco = atual;
                }
            }
            atual = atual.getProximo();
        }

        if (piorBloco != null) {
            memoria.alocar(piorBloco, processo);
            return true;
        } else {
            return false;
        }
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