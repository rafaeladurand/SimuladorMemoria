package simulador;

public class Worstfit {
    private final Memoria memoria;

    public Worstfit(Memoria memoria) {
        this.memoria = memoria;
    }

    public void alocar(Processo processo) {
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
            System.out.println("Processo " + processo.getId()
                    + " alocado (WorstFit) no bloco de tamanho " + piorBloco.getTamanho());
        } else {
            System.out.println("Nao ha espaco suficiente para o processo " + processo.getId());
        }
    }

    public void desalocar(Processo processo) {
        BlocoMemoria atual = memoria.getPrimeiroBloco();
        while (atual != null) {
            if (atual.isOcupado() && atual.getProcesso().getId() == processo.getId()) {
                memoria.desalocar(atual);
                System.out.println("Processo " + processo.getId() + " desalocado (WorstFit).");
                return;
            }
            atual = atual.getProximo();
        }
        System.out.println("Processo " + processo.getId() + " nao encontrado na memoria");
    }
}
