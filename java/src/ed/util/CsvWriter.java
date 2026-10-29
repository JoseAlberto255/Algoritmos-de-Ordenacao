package ed.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/**
 * Utilitário para exportar os resultados dos benchmarks em arquivos CSV.
 */
public class CsvWriter {

    /**
     * Salva o resumo estatístico agregado dos experimentos em CSV.
     */
    public static void salvarAgregado(List<Estatisticas> lista, String caminhoArquivo) throws IOException {
        File file = new File(caminhoArquivo);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Cabeçalho
            writer.println("Algoritmo,Cenario,Tamanho,Media_ms,DesvioPadrao_ms,Mediana_ms,Min_ms,Max_ms,CV_percent,Media_nanos,DesvioPadrao_nanos,Mediana_nanos,Min_nanos,Max_nanos,Status,Mensagem");

            for (Estatisticas est : lista) {
                if (est.isSucesso()) {
                    writer.printf(Locale.US,
                        "%s,%s,%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.2f,%d,%d,%d,%d,%d,SUCESSO,\n",
                        est.getAlgoritmo(),
                        est.getCenario(),
                        est.getTamanho(),
                        est.getMediaMs(),
                        est.getDesvioPadraoMs(),
                        est.getMedianaMs(),
                        est.getMinMs(),
                        est.getMaxMs(),
                        est.getCoeficienteVariacaoPercent(),
                        est.getMediaNanos(),
                        est.getDesvioPadraoNanos(),
                        est.getMedianaNanos(),
                        est.getMinNanos(),
                        est.getMaxNanos()
                    );
                } else {
                    writer.printf(Locale.US,
                        "%s,%s,%d,,,,,,,,,,,,FALHA,\"%s\"\n",
                        est.getAlgoritmo(),
                        est.getCenario(),
                        est.getTamanho(),
                        est.getMensagemErro() != null ? est.getMensagemErro().replace("\"", "'") : ""
                    );
                }
            }
        }
    }

    /**
     * Registro individual de cada execução para exportação detalhada.
     */
    public static class RegistroExecucao {
        public final String algoritmo;
        public final String cenario;
        public final int tamanho;
        public final int iteracao;
        public final boolean isWarmup;
        public final long tempoNanos;
        public final double tempoMs;

        public RegistroExecucao(String algoritmo, String cenario, int tamanho,
                                int iteracao, boolean isWarmup, long tempoNanos) {
            this.algoritmo = algoritmo;
            this.cenario = cenario;
            this.tamanho = tamanho;
            this.iteracao = iteracao;
            this.isWarmup = isWarmup;
            this.tempoNanos = tempoNanos;
            this.tempoMs = tempoNanos / 1_000_000.0;
        }
    }

    /**
     * Salva todas as medições individuais (incluindo warm-up com flag) em CSV.
     */
    public static void salvarDetalhado(List<RegistroExecucao> lista, String caminhoArquivo) throws IOException {
        File file = new File(caminhoArquivo);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Algoritmo,Cenario,Tamanho,Iteracao,IsWarmup,Tempo_nanos,Tempo_ms");
            for (RegistroExecucao reg : lista) {
                writer.printf(Locale.US,
                    "%s,%s,%d,%d,%b,%d,%.6f\n",
                    reg.algoritmo,
                    reg.cenario,
                    reg.tamanho,
                    reg.iteracao,
                    reg.isWarmup,
                    reg.tempoNanos,
                    reg.tempoMs
                );
            }
        }
    }
}
