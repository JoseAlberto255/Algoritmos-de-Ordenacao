package ed.bench;

import ed.sorting.*;
import ed.util.CsvWriter;
import ed.util.Estatisticas;
import ed.util.GeradorCenarios;
import ed.util.GeradorCenarios.Cenario;
import ed.util.Verificador;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Benchmark dos Algoritmos de Ordenação conforme o Item 3 do projeto EDA:
 * - Medição de tempos de execução com System.nanoTime().
 * - Três tamanhos de vetor (por padrão: 50.000, 150.000, 500.000, com ajuste disponível para viabilidade).
 * - 4 cenários: Aleatório, Já Ordenado, Ordem Inversa, Parcialmente Ordenado (~15% posições embaralhadas).
 * - Pelo menos 25 execuções (5 primeiras descartadas como warm-up, 20 medidas).
 * - Entrada idêntica (cópia fresca) para todos os algoritmos a cada iteração.
 * - Verificação de ordenação após cada execução (fora da medição).
 * - Comparação com Arrays.sort(int[]) da biblioteca padrão Java.
 * - Tratamento gracioso de limites de pilha (StackOverflowError) e tempo excessivo.
 * - Exportação para CSV (resultados agregados e detalhados).
 */
public class BenchOrdenacao {

    public static final int TOTAL_EXECUCOES_PADRAO = 25;
    public static final int WARMUP_EXECUCOES_PADRAO = 5;
    public static final long LIMITE_TEMPO_ITERACAO_NANOS = 5_000_000_000L; // 5 segundos por iteração individual

    public static class AlgoritmoInfo {
        private final String nome;
        private final Consumer<int[]> funcao;
        private final boolean quadratico;

        public AlgoritmoInfo(String nome, Consumer<int[]> funcao, boolean quadratico) {
            this.nome = nome;
            this.funcao = funcao;
            this.quadratico = quadratico;
        }

        public String getNome() { return nome; }
        public Consumer<int[]> getFuncao() { return funcao; }
        public boolean isQuadratico() { return quadratico; }
    }

    public static List<AlgoritmoInfo> getTodosAlgoritmos() {
        List<AlgoritmoInfo> lista = new ArrayList<>();
        lista.add(new AlgoritmoInfo("BubbleSort", BubbleSort::bubbleSort, true));
        lista.add(new AlgoritmoInfo("BubbleSortOtimizado", BubbleSortOtimizado::bubbleSortOtimizado, true));
        lista.add(new AlgoritmoInfo("SelectionSort", SelectionSort::selectionSort, true));
        lista.add(new AlgoritmoInfo("SelectionSortEstavel", SelectionSortEstavel::selectionSortEstavel, true));
        lista.add(new AlgoritmoInfo("InsertionSort", InsertionSort::insertionSort, true));
        lista.add(new AlgoritmoInfo("MergeSort", MergeSort::mergeSort, false));
        lista.add(new AlgoritmoInfo("QuickSort", QuickSort::quickSort, false));
        lista.add(new AlgoritmoInfo("QuickSortShuffle", QuickSortShuffle::quickSortShuffle, false));
        lista.add(new AlgoritmoInfo("CountingSort", CountingSort::countingSort, false));
        lista.add(new AlgoritmoInfo("Arrays.sort", Arrays::sort, false));
        return lista;
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("       INICIANDO BENCHMARK DE ALGORITMOS DE ORDENAÇÃO (EDA / LEDA)");
        System.out.println("===============================================================================");

        List<Integer> tamanhosList = new ArrayList<>();
        boolean apenasEficientes = false;
        boolean apenasQuadraticos = false;
        boolean modoTodosDireto = false;
        int totalExec = TOTAL_EXECUCOES_PADRAO;
        int warmup = WARMUP_EXECUCOES_PADRAO;

        if (args != null) {
            for (String arg : args) {
                String a = arg.trim();
                if (a.equalsIgnoreCase("--eficientes") || a.equalsIgnoreCase("--rapido")) {
                    apenasEficientes = true;
                } else if (a.equalsIgnoreCase("--quadraticos")) {
                    apenasQuadraticos = true;
                } else if (a.equalsIgnoreCase("--todos-direto") || a.equalsIgnoreCase("--ajustado")) {
                    modoTodosDireto = true;
                } else if (a.matches("\\d+")) {
                    tamanhosList.add(Integer.parseInt(a));
                }
            }
        }

        int[] tamanhos;
        if (!tamanhosList.isEmpty()) {
            tamanhos = tamanhosList.stream().mapToInt(Integer::intValue).toArray();
        } else if (modoTodosDireto) {
            // Tamanhos ajustados para viabilizar comparação direta de TODOS os algoritmos em tempo razoável
            tamanhos = new int[]{5_000, 15_000, 50_000};
        } else {
            // Tamanhos sugeridos no enunciado do projeto: 50.000, 150.000, 500.000
            tamanhos = new int[]{50_000, 150_000, 500_000};
        }

        List<AlgoritmoInfo> algoritmos = getTodosAlgoritmos();
        if (apenasEficientes) {
            algoritmos.removeIf(AlgoritmoInfo::isQuadratico);
        } else if (apenasQuadraticos) {
            algoritmos.removeIf(a -> !a.isQuadratico());
        }

        System.out.println("Configuração:");
        System.out.printf("- Tamanhos de vetor a avaliar: %s%n", Arrays.toString(tamanhos));
        System.out.printf("- Algoritmos selecionados (%d): ", algoritmos.size());
        for (int i = 0; i < algoritmos.size(); i++) {
            System.out.print(algoritmos.get(i).getNome() + (i < algoritmos.size() - 1 ? ", " : "\n"));
        }
        System.out.printf("- Total de execuções por teste: %d (Warm-up descartado: %d, Medidas: %d)%n",
                totalExec, warmup, totalExec - warmup);
        System.out.printf("- Valores dos elementos: [1, 100.000] com repetição permitida%n");
        System.out.printf("- Cenários: %s%n", Arrays.toString(Cenario.values()));
        System.out.println("-------------------------------------------------------------------------------");

        executarBenchmark(algoritmos, tamanhos, Cenario.values(), totalExec, warmup,
                "data/resultados_ordenacao_agregado.csv",
                "data/resultados_ordenacao_detalhado.csv");
    }

    public static List<Estatisticas> executarBenchmark(
            List<AlgoritmoInfo> algoritmos,
            int[] tamanhos,
            Cenario[] cenarios,
            int totalExec,
            int warmup,
            String arquivoCsvAgregado,
            String arquivoCsvDetalhado) {

        List<Estatisticas> todasEstatisticas = new ArrayList<>();
        List<CsvWriter.RegistroExecucao> registrosDetalhados = new ArrayList<>();
        Random randomBase = new Random(42);

        for (int tamanho : tamanhos) {
            System.out.println("\n###############################################################################");
            System.out.printf("                     AVALIANDO TAMANHO N = %,d%n", tamanho);
            System.out.println("###############################################################################");

            for (Cenario cenario : cenarios) {
                System.out.printf("%n=== Cenário: %s (N = %,d) ===%n", cenario.getDescricao(), tamanho);

                Map<String, long[]> temposPorAlgoritmo = new LinkedHashMap<>();
                Map<String, String> erroPorAlgoritmo = new LinkedHashMap<>();

                for (AlgoritmoInfo alg : algoritmos) {
                    temposPorAlgoritmo.put(alg.getNome(), new long[totalExec]);
                }

                // Executa as 25 iterações (5 warmup + 20 medidas)
                for (int iter = 0; iter < totalExec; iter++) {
                    boolean isWarmup = (iter < warmup);

                    // Conforme especificação: mesma entrada para todos os algoritmos na iteração
                    int[] vetorOriginal = GeradorCenarios.gerarPorCenario(cenario, tamanho, randomBase);

                    for (AlgoritmoInfo alg : algoritmos) {
                        String nomeAlg = alg.getNome();

                        if (erroPorAlgoritmo.containsKey(nomeAlg)) {
                            continue;
                        }

                        // Cópia estrita fora da medição
                        int[] copia = vetorOriginal.clone();

                        long tempoNanos;
                        try {
                            long t0 = System.nanoTime();
                            alg.getFuncao().accept(copia);
                            long t1 = System.nanoTime();
                            tempoNanos = t1 - t0;

                            // Verificação de correção (FORA da medição)
                            Verificador.verificarOrdenado(copia, nomeAlg);

                            temposPorAlgoritmo.get(nomeAlg)[iter] = tempoNanos;
                            registrosDetalhados.add(new CsvWriter.RegistroExecucao(
                                    nomeAlg, cenario.getDescricao(), tamanho, iter + 1, isWarmup, tempoNanos
                            ));

                            // Se uma única iteração exceder o tempo limite, interrompe iterações subsequentes deste algoritmo
                            if (tempoNanos > LIMITE_TEMPO_ITERACAO_NANOS && iter < totalExec - 1) {
                                String msg = String.format("TempoLimiteExcedido (%.2f s > limite de 5s por iteração; inviabiliza 25 repetições)", tempoNanos / 1e9);
                                erroPorAlgoritmo.put(nomeAlg, msg);
                                System.out.printf("  [!] %s interrompido após iteração %d (%s)%n", nomeAlg, iter + 1, msg);
                            }

                        } catch (StackOverflowError soe) {
                            String msg = "StackOverflowError (limite de pilha JVM; recursão profunda no pior caso)";
                            erroPorAlgoritmo.put(nomeAlg, msg);
                            System.err.printf("  [!] %s falhou na iteração %d com %s%n", nomeAlg, iter + 1, msg);
                        } catch (OutOfMemoryError oome) {
                            String msg = "OutOfMemoryError (memória insuficiente)";
                            erroPorAlgoritmo.put(nomeAlg, msg);
                            System.err.printf("  [!] %s falhou na iteração %d com %s%n", nomeAlg, iter + 1, msg);
                        } catch (Throwable t) {
                            String msg = t.getClass().getSimpleName() + ": " + t.getMessage();
                            erroPorAlgoritmo.put(nomeAlg, msg);
                            System.err.printf("  [!] %s falhou na iteração %d com %s%n", nomeAlg, iter + 1, msg);
                        }
                    }
                }

                // Estatísticas e log
                for (AlgoritmoInfo alg : algoritmos) {
                    String nome = alg.getNome();
                    Estatisticas est;
                    if (erroPorAlgoritmo.containsKey(nome)) {
                        est = new Estatisticas(nome, cenario.getDescricao(), tamanho, erroPorAlgoritmo.get(nome));
                    } else {
                        est = new Estatisticas(nome, cenario.getDescricao(), tamanho, totalExec, warmup,
                                temposPorAlgoritmo.get(nome));
                    }
                    todasEstatisticas.add(est);
                    System.out.println("  " + est.toString());
                }
            }
        }

        // Salva arquivos CSV
        try {
            CsvWriter.salvarAgregado(todasEstatisticas, arquivoCsvAgregado);
            System.out.printf("%n[OK] Resultados agregados salvos com sucesso em: %s%n", arquivoCsvAgregado);
            CsvWriter.salvarDetalhado(registrosDetalhados, arquivoCsvDetalhado);
            System.out.printf("[OK] Resultados detalhados salvos com sucesso em: %s%n", arquivoCsvDetalhado);
        } catch (IOException e) {
            System.err.printf("Erro ao salvar arquivos CSV: %s%n", e.getMessage());
        }

        return todasEstatisticas;
    }
}
