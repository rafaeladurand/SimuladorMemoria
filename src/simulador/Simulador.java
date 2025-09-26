package simulador;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulador {

    private static final int DURACAO_SIMULACAO_SEGUNDOS = 100;
    private static final int N_REPETICOES_EXPERIMENTO = 1;

    private static final int TAMANHO_MIN_PROCESSO = 10;
    private static final int TAMANHO_MAX_PROCESSO = 100;
    
    private static final boolean MODO_VERBOSE = false;

    public static void main(String[] args) {
        System.out.println("Iniciando Simulação de Alocação de Memória...");
        System.out.printf("Duração da simulação: %d segundos.\n\n", DURACAO_SIMULACAO_SEGUNDOS);

        List<String> nomesAlgoritmos = List.of("First-Fit", "Best-Fit", "Next-Fit", "Worst-Fit");

        for (String nomeAlgoritmo : nomesAlgoritmos) {
            executarSimulacaoParaAtividade(nomeAlgoritmo);
        }
    }

    private static void executarSimulacaoParaAtividade(String nomeAlgoritmo) {
        Random random = new Random();

        System.out.println("======================================================");
        System.out.printf("Executando Simulação para o Algoritmo: %s\n", nomeAlgoritmo);
        System.out.println("------------------------------------------------------");

        Metricas resultadoDaRodada = rodarSimulacaoUnica(nomeAlgoritmo, random);
        imprimirResultadosFinais(nomeAlgoritmo, resultadoDaRodada);
    }

    private static Metricas rodarSimulacaoUnica(String nomeAlgoritmo, Random random) {
        Processo.resetarContadorId();
        Memoria memoria = new Memoria();
        Alocador alocador = null;

        switch (nomeAlgoritmo) {
            case "Best-Fit":  alocador = new BestFit(memoria); break;
            case "First-Fit": alocador = new FirstFit(memoria); break;
            case "Worst-Fit": alocador = new Worstfit(memoria); break;
            case "Next-Fit":  alocador = new Nextfit(memoria); break;
            default:
                System.out.println("Algoritmo não reconhecido: " + nomeAlgoritmo);
                return new Metricas(memoria);
        }
        
        GeradorDeProcessos gerador = new GeradorDeProcessos();
        Metricas metricasDaRodada = new Metricas(memoria);
        List<Processo> processosAtivos = new ArrayList<>();

        for (int tempoAtual = 0; tempoAtual < DURACAO_SIMULACAO_SEGUNDOS; tempoAtual++) {
            if (MODO_VERBOSE) {
                System.out.printf("\n--- Tempo: %ds ---\n", tempoAtual);
            }

            if (!processosAtivos.isEmpty()) {
                int processosARemover = 1 + random.nextInt(2); 
                
                for (int i = 0; i < processosARemover && !processosAtivos.isEmpty(); i++) {
                    int indiceAleatorio = random.nextInt(processosAtivos.size());
                    Processo processoRemovido = processosAtivos.remove(indiceAleatorio);
                    alocador.desalocar(processoRemovido);
                    if (MODO_VERBOSE) {
                        System.out.printf("   [SAÍDA] Processo %d removido.\n", processoRemovido.getId());
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                Processo novoProcesso = gerador.gerarProcesso(TAMANHO_MIN_PROCESSO, TAMANHO_MAX_PROCESSO);
                
                if (alocador.alocar(novoProcesso)) {
                    processosAtivos.add(novoProcesso); 
                    metricasDaRodada.registrarAlocacaoBemSucedida();
                    if (MODO_VERBOSE) {
                        System.out.printf("   [ENTRADA] Processo %d (tam: %d) alocado.\n", novoProcesso.getId(), novoProcesso.getTamanho());
                    }
                } else {
                    metricasDaRodada.registrarAlocacaoFalha();
                    if (MODO_VERBOSE) {
                        System.out.printf("   [FALHA] Processo %d (tam: %d) não pôde ser alocado.\n", novoProcesso.getId(), novoProcesso.getTamanho());
                    }
                }
            }
            
            metricasDaRodada.capturarMetricasDoTick();
        }
        return metricasDaRodada;
    }

    private static void imprimirResultadosFinais(String nomeAlgoritmo, Metricas metricas) {
        long totalAlocacoes = metricas.getAlocacoesBemSucedidas() + metricas.getAlocacoesFalhas();
        double taxaSucesso = (totalAlocacoes == 0) ? 0 : (double) metricas.getAlocacoesBemSucedidas() / totalAlocacoes * 100.0;
        
        System.out.println("\n------------------------------------------------------");
        System.out.println("Métricas Finais:");
        System.out.printf("Taxa de Sucesso de Alocação: %.2f%%\n", taxaSucesso);
        System.out.printf("Utilização Média da Memória: %.2f%%\n", metricas.getMediaUtilizacaoMemoria());
        System.out.printf("Média de Blocos Livres (Fragmentação): %.2f blocos\n", metricas.getMediaFragmentacaoExterna());
        System.out.println("======================================================\n");
    }
}

class Metricas {
    private Memoria memoria;
    private long alocacoesBemSucedidas = 0;
    private long alocacoesFalhas = 0;
    private List<Integer> contagemBlocosLivresPorTick = new java.util.ArrayList<>();
    private List<Double> utilizacaoMemoriaPorTick = new java.util.ArrayList<>();

    public Metricas(Memoria memoria) { this.memoria = memoria; }
    public void registrarAlocacaoBemSucedida() { this.alocacoesBemSucedidas++; }
    public void registrarAlocacaoFalha() { this.alocacoesFalhas++; }
    public long getAlocacoesBemSucedidas() { return alocacoesBemSucedidas; }
    public long getAlocacoesFalhas() { return alocacoesFalhas; }

    public void capturarMetricasDoTick() {
        int blocosLivres = 0;
        int memoriaOcupada = 0;
        BlocoMemoria atual = memoria.getPrimeiroBloco();
        while (atual != null) {
            if (atual.isOcupado()) {
                memoriaOcupada += atual.getTamanho();
            } else {
                blocosLivres++;
            }
            atual = atual.getProximo();
        }
        contagemBlocosLivresPorTick.add(blocosLivres);
        double utilizacao = (memoria.getTamanhoTotal() == 0) ? 0 : (double) memoriaOcupada / memoria.getTamanhoTotal() * 100.0;
        utilizacaoMemoriaPorTick.add(utilizacao);
    }
    
    public double getMediaFragmentacaoExterna() {
        return contagemBlocosLivresPorTick.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public double getMediaUtilizacaoMemoria() {
        return utilizacaoMemoriaPorTick.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}