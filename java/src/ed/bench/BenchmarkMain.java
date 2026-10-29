package ed.bench;

import java.util.Arrays;

/**
 * Ponto de entrada unificado para execução dos benchmarks de ordenação e busca.
 */
public class BenchmarkMain {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("           PROJETO UNIDADE 1 - ESTRUTURAS DE DADOS (EDA / LEDA)");
        System.out.println("                  SUÍTE COMPLETA DE BENCHMARKS EM JAVA");
        System.out.println("===============================================================================");

        if (args.length > 0 && (args[0].equalsIgnoreCase("busca") || args[0].equalsIgnoreCase("search"))) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            BenchBusca.main(subArgs);
            return;
        }

        if (args.length > 0 && (args[0].equalsIgnoreCase("ordenacao") || args[0].equalsIgnoreCase("sort") || args[0].equalsIgnoreCase("sorting"))) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            BenchOrdenacao.main(subArgs);
            return;
        }

        // Se nenhum argumento ou "todos", executa busca primeiro (rápida) e depois ordenação
        System.out.println("\n>>> [1/2] EXECUTANDO BENCHMARK DE BUSCA...");
        BenchBusca.main(args);

        System.out.println("\n>>> [2/2] EXECUTANDO BENCHMARK DE ORDENAÇÃO...");
        BenchOrdenacao.main(args);

        System.out.println("\n===============================================================================");
        System.out.println("                TODOS OS EXPERIMENTOS FORAM CONCLUÍDOS!");
        System.out.println("  Arquivos gerados na pasta 'data/':");
        System.out.println("    - resultados_busca_agregado.csv");
        System.out.println("    - resultados_busca_detalhado.csv");
        System.out.println("    - resultados_ordenacao_agregado.csv");
        System.out.println("    - resultados_ordenacao_detalhado.csv");
        System.out.println("===============================================================================");
    }
}
