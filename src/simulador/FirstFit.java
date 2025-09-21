package simulador;

public class FirstFit {
    private Memoria memoria;

    public FirstFit(Memoria memoria) {
        this.memoria = memoria;
    }

    public void alocar(Processo processo){
        BlocoMemoria atualBloco = memoria.getPrimeiroBloco();

        while (atualBloco != null){
            if(!atualBloco.isOcupado() && atualBloco.getTamanho() >= processo.getTamanho()){
                memoria.alocar(atualBloco, processo);
                System.out.println("Processo " + processo.getId() + "alocado no bloco de tamanho " + atualBloco.getTamanho());
                return;
            }
            atualBloco = atualBloco.getProximo();
        }
        System.out.println("Nao ha espaco suficiente para o processo " + processo.getId());
    }

    public void desalocar(Processo processo){
        BlocoMemoria atualBloco = memoria.getPrimeiroBloco();

        while (atualBloco != null){
            if (atualBloco.isOcupado() && atualBloco.getProcesso().getId() == processo.getId()){
                memoria.desalocar(atualBloco);
                System.out.println("Processo "+ processo.getId() + "desalocado");
                return;
            }
            atualBloco = atualBloco.getProximo();
        }
        System.out.println("Processo "+ processo.getId() + "nao encontrado na memoria");
    }
}
