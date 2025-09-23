package simulador;

public class BestFit implements Alocador {
    private Memoria memoria;

    public BestFit(Memoria memoria) {
        this.memoria = memoria;
    }
    
    @Override
    public String getNome() {
        return "Best-Fit";
    }

    @Override
    public boolean alocar(Processo processo) {
        BlocoMemoria melhorBloco = null;
        BlocoMemoria atualBloco = memoria.getPrimeiroBloco();

        while (atualBloco != null) {
            if (!atualBloco.isOcupado() && atualBloco.getTamanho() >= processo.getTamanho()) {
                if (melhorBloco == null || atualBloco.getTamanho() < melhorBloco.getTamanho()) {
                    melhorBloco = atualBloco;
                }
            }
            atualBloco = atualBloco.getProximo();
        }

        if (melhorBloco != null) {
            memoria.alocar(melhorBloco, processo);
            // System.out.println("Processo " + processo.getId() + " alocado com sucesso!");
            return true;
        } else {
            // System.out.println("Não há espaço suficiente para o processo " + processo.getId());
            return false;
        }
    }

    @Override
    public void desalocar(Processo processo) {
        BlocoMemoria atualBloco = memoria.getPrimeiroBloco();

        while (atualBloco != null) {
            if (atualBloco.isOcupado() && atualBloco.getProcesso().getId() == processo.getId()) {
                memoria.desalocar(atualBloco);
                // System.out.println("Processo " + processo.getId() + " desalocado com sucesso!");
                return;
            }
            atualBloco = atualBloco.getProximo();
        }
        // System.out.println("Processo " + processo.getId() + " nao encontrado para desalocar.");
    }
}