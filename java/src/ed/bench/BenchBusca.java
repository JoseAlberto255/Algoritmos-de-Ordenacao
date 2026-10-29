package ed.bench;

import ed.search.*;
import ed.util.CsvWriter;
import ed.util.Estatisticas;
import ed.util.GeradorCenarios;
import ed.util.Verificador;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Benchmark dos Algoritmos de Busca conforme o Item 2 e Item 3 do projeto EDA:
 * - Vetor previamente ordenado.
 * - Busca pelo elemento -1 (ausente, pior caso).
 * - Algoritmos comparados:
 *   1. Busca Linear Iterativa
 *   2. Busca Linear Recursiva
 *   3. Busca Linear Duas Pontas
 *   4. Busca Binária Iterativa
 *   5. Busca Binária Recursiva
 * - Medição com System.nanoTime().
 * - Três tamanhos de vetor (por padrão: 50.000, 150.000, 500.000, ou ajustáveis via argumentos).
 * - Pelo menos 25 execuções (5 primeiras descartadas como warm-up, 20 medidas).
 * - Cópia idêntica da mesma entrada para todos os algoritmos a cada iteração.
 * - Verificação de retorno == -1 após cada execução (fora da medição).
 * - Tratamento gracioso de limites de pilha (StackOverflowError).
 * - Exportação para CSV (resultados agregados e detalhados).
 */
public class BenchBusca {

    public static final int TOTAL_EXECUCOES_PADRAO = 25;
    public static final int WARMUP_EXECUCOES_PADRAO = 5;
    public static final int CHAVE_AUSENTE = -1;

    public static class AlgoritmoBuscaInfo {
        private final String nome;
        private final BiFunction<int[], Integer, Integer> funcao;
        private final boolean linear;

        public AlgoritmoBuscaInfo(String nome, BiFunction<int[], Integer, Integer> funcao, boolean linear) {
            this.nome = nome;
            this.funcao = funcao;
            this.linear = linear;
        }

        public String getNome() { return nome; }
        public BiFunction<int[], Integer, Integer> getFuncao() { return funcao; }
        public boolean isLinear() { return linear; }
    }

    public static List<AlgoritmoBuscaInfo> getTodosAlgoritmos() {
        List<AlgoritmoBuscaInfo> lista = new ArrayList<>();
        lista.add(new AlgoritmoBuscaInfo("BuscaLinearIterativa", BuscaLinearIterativa::buscaLinearIterativa, true));
        lista.add(new AlgoritmoBuscaInfo("BuscaLinearRecursiva", BuscaLinearRecursiva::buscaLinearRecursiva, true));
        lista.add(new AlgoritmoBuscaInfo("BuscaLinearDuasPontas", BuscaLinearDuasPontas::buscaLinearDuasPontas, true));
        lista.add(new AlgoritmoBuscaInfo("BuscaBinariaIterativa", BuscaBinariaIterativa::buscaBinariaIterativa, false));
        lista.add(new AlgoritmoBuscaInfo("BuscaBinariaRecursiva", BuscaBinariaRecursiva::buscaBinariaRecursiva, false));
        return lista;
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("         INICIANDO BENCHMARK DE ALGORITMOS DE BUSCA (EDA / LEDA)");
        System.out.println("===============================================================================");

        int[] tamanhos;
        int totalExec = TOTAL_EXECUCOES_PADRAO;
        int warmup = WARMUP_EXECUCOES_PADRAO;

        if (args != null && args.length > 0 && !args[0].startsWith("-")) {
            tamanhos = new int[args.length];
            for (int i = 0; i < args.length; i++) {
                tamanhos[i] = Integer.parseInt(args[i].trim());
            }
        } else {
            // Tamanhos sugeridos no enunciado do projeto: 50.000, 150.000, 500.000
            tamanhos = new int[]{50_000, 150_000, 500_000};
        }

        System.out.printf("Configuração:%n");
        System.out.printf("- Tamanhos de vetor a avaliar: %s%n", Arrays.toString(tamanhos));
        System.out.printf("- Total de execuções por teste: %d (Warm-up descartado: %d, Medidas: %d)%n",
                totalExec, warmup, totalExec - warmup);
        System.out.printf("- Vetor de entrada: Previamente Ordenado com valores em [1, 100.000]%n");
        System.out.printf("- Chave de busca: %d (ausente, representando o pior caso)%n", CHAVE_AUSENTE);
        System.out.println("-------------------------------------------------------------------------------");

        executarBenchmark(getTodosAlgoritmos(), tamanhos, totalExec, warmup,
                "data/resultados_busca_agregado.csv",
                "data/resultados_busca_detalhado.csv");
    }

    public static List<Estatisticas> executarBenchmark(
            List<AlgoritmoBuscaInfo> algoritmos,
            int[] tamanhos,
            int totalExec,
            int warmup,
            String arquivoCsvAgregado,
            String arquivoCsvDetalhado) {

        List<Estatisticas> todasEstatisticas = new ArrayList<>();
        List<CsvWriter.RegistroExecucao> registrosDetalhados = new ArrayList<>();
        Random randomBase = new Random(42);
        String cenarioNome = "Vetor Ordenado (Chave Ausente = -1)";

        for (int tamanho : tamanhos) {
            System.out.println("\n###############################################################################");
            System.out.printf("                     AVALIANDO BUSCA PARA N = %,d%n", tamanho);
            System.out.println("###############################################################################");

            Map<String, long[]> temposPorAlgoritmo = new LinkedHashMap<>();
            Map<String, String> erroPorAlgoritmo = new LinkedHashMap<>();

            for (AlgoritmoBuscaInfo alg : algoritmos) {
                temposPorAlgoritmo.put(alg.getNome(), new long[totalExec]);
            }

            for (int iter = 0; iter < totalExec; iter++) {
                boolean isWarmup = (iter < warmup);

                // Conforme PDF: vetor previamente ordenado com elementos em [1, 100.000]
                int[] vetorOrdenado = GeradorCenarios.gerarOrdenado(tamanho, randomBase);

                for (AlgoritmoBuscaInfo alg : algoritmos) {
                    String nomeAlg = alg.getNome();

                    if (erroPorAlgoritmo.containsKey(nomeAlg)) {
                        continue;
                    }

                    // Cópia idêntica para isolamento completo
                    int[] copia = vetorOrdenado.clone();

                    long tempoNanos;
                    try {
                        long t0 = System.nanoTime();
                        int indiceEncontrado = alg.getFuncao().apply(copia, CHAVE_AUSENTE);
                        long t1 = System.nanoTime();
                        tempoNanos = t1 - t0;

                        // Verificação de correção (FORA da medição)
                        Verificador.verificarBusca(indiceEncontrado, -1, nomeAlg);

                        temposPorAlgoritmo.get(nomeAlg)[iter] = tempoNanos;
                        registrosDetalhados.add(new CsvWriter.RegistroExecucao(
                                nomeAlg, cenarioNome, tamanho, iter + 1, isWarmup, tempoNanos
                        ));

                    } catch (StackOverflowError soe) {
                        String msg = "StackOverflowError (limite de pilha JVM na recursão; use -Xss64m)";
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

            // Exibe estatísticas para cada algoritmo de busca
            for (AlgoritmoBuscaInfo alg : algoritmos) {
                String nome = alg.getNome();
                Estatisticas est;
                if (erroPorAlgoritmo.containsKey(nome)) {
                    est = new Estatisticas(nome, cenarioNome, tamanho, erroPorAlgoritmo.get(nome));
                } else {
                    est = new Estatisticas(nome, cenarioNome, tamanho, totalExec, warmup,
                            temposPorAlgoritmo.get(nome));
                }
                todasEstatisticas.add(est);
                System.out.println("  " + est.toString());
            }
        }

        // Salva arquivos CSV
        try {
            CsvWriter.salvarAgregado(todasEstatisticas, arquivoCsvAgregado);
            System.out.printf("%n[OK] Resultados de busca agregados salvos com sucesso em: %s%n", arquivoCsvAgregado);
            CsvWriter.salvarDetalhado(registrosDetalhados, arquivoCsvDetalhado);
            System.out.printf("[OK] Resultados de busca detalhados salvos com sucesso em: %s%n", arquivoCsvDetalhado);
        } catch (IOException e) {
            System.err.printf("Erro ao salvar arquivos CSV de busca: %s%n", e.getMessage());
        }

        return todasEstatisticas;
    }
}
