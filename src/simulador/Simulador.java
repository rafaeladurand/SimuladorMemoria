package simulador;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Simulador {

    private static final int DURACAO_SIMULACAO_SEGUNDOS = 100;
    private static final int N_REPETICOES_EXPERIMENTO = 100;

    private static final double PROBABILIDADE_CHEGADA_PROCESSO = 0.7;
    private static final int TAMANHO_MIN_PROCESSO = 10;
    private static final int TAMANHO_MAX_PROCESSO = 100;
    private static final int VIDA_MIN_PROCESSO = 5;
    private static final int VIDA_MAX_PROCESSO = 20;


    public static void main(String[] args) {
        System.out.println("Iniciando Experimento de Simulação de Alocação de Memória...");
        System.out.printf("Configurações: %d repetições de %d segundos cada.\n\n",
                N_REPETICOES_EXPERIMENTO, DURACAO_SIMULACAO_SEGUNDOS);

        List<String> nomesAlgoritmos = List.of("First-Fit", "Best-Fit", "Next-Fit", "Worst-Fit");

        for (String nomeAlgoritmo : nomesAlgoritmos) {
            executarExperimento(nomeAlgoritmo);
        }
    }

    private static void executarExperimento(String nomeAlgoritmo) {
        long totalAlocacoesBemSucedidas = 0;
        long totalAlocacoesFalhas = 0;
        double mediaGeralFragmentacao = 0;
        double mediaGeralUtilizacao = 0;
        Random random = new Random();

        for (int i = 0; i < N_REPETICOES_EXPERIMENTO; i++) {
            Metricas resultadoDaRodada = rodarSimulacaoUnica(nomeAlgoritmo, random);
            
            totalAlocacoesBemSucedidas += resultadoDaRodada.getAlocacoesBemSucedidas();
            totalAlocacoesFalhas += resultadoDaRodada.getAlocacoesFalhas();
            mediaGeralFragmentacao += resultadoDaRodada.getMediaFragmentacaoExterna();
            mediaGeralUtilizacao += resultadoDaRodada.getMediaUtilizacaoMemoria();
        }

        double taxaSucesso = (double) totalAlocacoesBemSucedidas / (totalAlocacoesBemSucedidas + totalAlocacoesFalhas) * 100.0;
        double mediaFinalFragmentacao = mediaGeralFragmentacao / N_REPETICOES_EXPERIMENTO;
        double mediaFinalUtilizacao = mediaGeralUtilizacao / N_REPETICOES_EXPERIMENTO;

        imprimirResultadosFinais(nomeAlgoritmo, taxaSucesso, mediaFinalFragmentacao, mediaFinalUtilizacao);
    }

    private static Metricas rodarSimulacaoUnica(String nomeAlgoritmo, Random random) {
        Processo.resetarContadorId();
        Memoria memoria = new Memoria();
        Alocador alocador = null;
        
        switch (nomeAlgoritmo) {
            case "Best-Fit":
                alocador = new BestFit(memoria);
                break;

            case "First-Fit":
                alocador = new FirstFit(memoria);
                break;

            case "Worst-Fit":
                alocador = new Worstfit(memoria);
                break;

            case "Next-Fit":
                alocador = new Nextfit(memoria);
                break;
        
            default:
                System.out.println("Não foi possível reconhecer o algoritimo: " + nomeAlgoritmo);
                break;
        }
        
        GeradorDeProcessos gerador = new GeradorDeProcessos();
        Metricas metricasDaRodada = new Metricas(memoria);

        Map<Processo, Integer> processosAtivos = new HashMap<>();

        for (int tempoAtual = 0; tempoAtual < DURACAO_SIMULACAO_SEGUNDOS; tempoAtual++) {

            Iterator<Map.Entry<Processo, Integer>> iterator = processosAtivos.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Processo, Integer> entry = iterator.next();
                Processo p = entry.getKey();
                int vidaRestante = entry.getValue() - 1; 

                if (vidaRestante <= 0) {
                    alocador.desalocar(p);
                    iterator.remove();
                } else {
                    entry.setValue(vidaRestante);
                }
            }

            if (random.nextDouble() < PROBABILIDADE_CHEGADA_PROCESSO) {
                Processo novoProcesso = gerador.gerarProcesso(TAMANHO_MIN_PROCESSO, TAMANHO_MAX_PROCESSO);
                
                if (alocador.alocar(novoProcesso)) {
                    int tempoDeVida = random.nextInt(VIDA_MAX_PROCESSO - VIDA_MIN_PROCESSO + 1) + VIDA_MIN_PROCESSO;
                    processosAtivos.put(novoProcesso, tempoDeVida);
                    metricasDaRodada.registrarAlocacaoBemSucedida();
                } else {
                    metricasDaRodada.registrarAlocacaoFalha();
                }
            }

            metricasDaRodada.capturarMetricasDoTick();
        }
        return metricasDaRodada;
    }

    private static void imprimirResultadosFinais(String nomeAlgoritmo, double taxaSucesso, double mediaFragmentacao, double mediaUtilizacao) {
        System.out.println("======================================================");
        System.out.printf("Resultados Finais para o Algoritmo: %s\n", nomeAlgoritmo);
        System.out.println("------------------------------------------------------");
        System.out.printf("Taxa de Sucesso de Alocação: %.2f%%\n", taxaSucesso);
        System.out.printf("Utilização Média da Memória: %.2f%%\n", mediaUtilizacao);
        System.out.printf("Média de Blocos Livres (Fragmentação): %.2f blocos\n", mediaFragmentacao);
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
        double utilizacao = (double) memoriaOcupada / memoria.getTamanhoTotal() * 100.0;
        utilizacaoMemoriaPorTick.add(utilizacao);
    }
    
    public double getMediaFragmentacaoExterna() {
        return contagemBlocosLivresPorTick.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public double getMediaUtilizacaoMemoria() {
        return utilizacaoMemoriaPorTick.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}

