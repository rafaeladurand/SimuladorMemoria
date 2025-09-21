package simulador;

public class BestFit {
    private Memoria memoria;

    public BestFit(Memoria memoria) {
        this.memoria = memoria;
    }

    public void alocar (Processo processo) {
        BlocoMemoria melhorBloco = null;
        BlocoMemoria atualBloco = memoria.getPrimeiroBloco();

        while (atualBloco != null) {
            if (!atualBloco.isOcupado() && atualBloco.getTamanho() >= processo.getTamanho()) {
                if (melhorBloco == null || atualBloco.getTamanho() < processo.getTamanho()) {
                    melhorBloco = atualBloco;
                }
            }
            atualBloco = atualBloco.getProximo();
        }

        if (melhorBloco != null) {
            memoria.alocar(melhorBloco, processo);
            System.out.println("Processo " + processo.getId() + " alocado com sucesso!" + processo.getId());
        }else{
            System.out.println("Não há espaço suficiente.");
        }
    }

    public void desalocar(Processo processo) {
        BlocoMemoria atualBloco = memoria.getPrimeiroBloco();

        while (atualBloco != null) {
            if (atualBloco.isOcupado() && atualBloco.getProcesso().getId() == processo.getTamanho()) {
                memoria.desalocar(atualBloco);
                System.out.println("Processo " + processo.getId() + " desalocado com sucesso!" + processo.getId());
                return;
            }
            atualBloco = atualBloco.getProximo();
        }

        System.out.println("Processo " + processo.getId() + " nao encontrado.");
    }
}
