package simulador;

public class Memoria {

    private BlocoMemoria primeiro;
    private final int TAMANHO_TOTAL = 1000;

    public Memoria() {
        primeiro = new BlocoMemoria(TAMANHO_TOTAL);
    }

    public int getTamanhoTotal() {
        return TAMANHO_TOTAL;
    }

    public void alocar(BlocoMemoria bloco, Processo processo) {
        if (bloco.getTamanho() > processo.getTamanho()) {
            int resto = bloco.getTamanho() - processo.getTamanho();
            BlocoMemoria novoBloco = new BlocoMemoria(resto);
            
            novoBloco.setProximo(bloco.getProximo());
            bloco.setProximo(novoBloco);
            
            bloco.setTamanho(processo.getTamanho());
        }
        
        bloco.alocar(processo);
    }

    public void desalocar(BlocoMemoria bloco) {
        bloco.liberar();
        
        BlocoMemoria proximo = bloco.getProximo();
        if (proximo != null && !proximo.isOcupado()) {
            bloco.setTamanho(bloco.getTamanho() + proximo.getTamanho());
            bloco.setProximo(proximo.getProximo());
        }
        
        BlocoMemoria anterior = null;
        BlocoMemoria atual = primeiro;
        
        while (atual != null && atual != bloco) {
            anterior = atual;
            atual = atual.getProximo();
        }
        
        if (anterior != null && !anterior.isOcupado()) {
            anterior.setTamanho(anterior.getTamanho() + bloco.getTamanho());
            anterior.setProximo(bloco.getProximo());
        }
    }

    public BlocoMemoria getPrimeiroBloco() {
        return primeiro;
    }

    public void exibirEstadoMemoria() {
        BlocoMemoria atual = primeiro;
        System.out.print("Estado da Memória: ");
        
        while (atual != null) {
            System.out.print(atual);
            if (atual.getProximo() != null) {
                System.out.print(" -> ");
            }
            atual = atual.getProximo();
        }
        
        System.out.println();
    }
}