package simulador;

public class Nextfit {
    private Memoria memoria;
    private BlocoMemoria cursor;

    public Nextfit(Memoria memoria) {
        this.memoria = memoria;
        this.cursor = memoria.getPrimeiroBloco();
    }

    public void alocar(Processo processo) {
        if (memoria.getPrimeiroBloco() == null) {
            System.out.println("Memória vazia.");
            return;
        }
        if (cursor == null) cursor = memoria.getPrimeiroBloco();

        BlocoMemoria inicio = cursor;
        BlocoMemoria atual = cursor;

        do {
            if (!atual.isOcupado() && atual.getTamanho() >= processo.getTamanho()) {
                memoria.alocar(atual, processo);
                BlocoMemoria prox = atual.getProximo();
                cursor = (prox != null) ? prox : memoria.getPrimeiroBloco();

                System.out.println("Processo " + processo.getId() +
                        " alocado (NextFit) no bloco de tamanho " + atual.getTamanho());
                return;
            }
            atual = (atual.getProximo() != null) ? atual.getProximo() : memoria.getPrimeiroBloco();
        } while (atual != inicio);

        System.out.println("Nao ha espaco suficiente para o processo " + processo.getId());
    }

    public void desalocar(Processo processo) {
        BlocoMemoria atual = memoria.getPrimeiroBloco();
        while (atual != null) {
            if (atual.isOcupado() && atual.getProcesso().getId() == processo.getId()) {
                memoria.desalocar(atual);
                System.out.println("Processo " + processo.getId() + " desalocado (NextFit).");
                return;
            }
            atual = atual.getProximo();
        }
        System.out.println("Processo " + processo.getId() + " nao encontrado na memoria");
    }
}

