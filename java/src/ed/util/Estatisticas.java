package ed.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Encapsula as estatísticas calculadas sobre os tempos de execução de um experimento.
 * As primeiras 5 iterações de warm-up são descartadas, restando as 20 iterações medidas.
 */
public class Estatisticas {

    private final String algoritmo;
    private final String cenario;
    private final int tamanho;
    private final int totalExecucoes;
    private final int warmupsDescartados;
    private final int execucoesMedidas;

    private final double mediaMs;
    private final double desvioPadraoMs;
    private final double medianaMs;
    private final double minMs;
    private final double maxMs;
    private final double coeficienteVariacaoPercent;

    private final long mediaNanos;
    private final long desvioPadraoNanos;
    private final long medianaNanos;
    private final long minNanos;
    private final long maxNanos;

    private final long[] temposValidosNanos;
    private final boolean sucesso;
    private final String mensagemErro;

    public Estatisticas(String algoritmo, String cenario, int tamanho,
                        int totalExecucoes, int warmupsDescartados,
                        long[] todosTemposNanos) {
        this.algoritmo = algoritmo;
        this.cenario = cenario;
        this.tamanho = tamanho;
        this.totalExecucoes = totalExecucoes;
        this.warmupsDescartados = warmupsDescartados;
        this.sucesso = true;
        this.mensagemErro = null;

        int n = todosTemposNanos.length - warmupsDescartados;
        if (n <= 0) {
            throw new IllegalArgumentException("Número de execuções válidas deve ser maior que zero.");
        }
        this.execucoesMedidas = n;

        this.temposValidosNanos = new long[n];
        System.arraycopy(todosTemposNanos, warmupsDescartados, this.temposValidosNanos, 0, n);

        // Média em nanos
        double soma = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (long t : temposValidosNanos) {
            soma += t;
            if (t < min) min = t;
            if (t > max) max = t;
        }
        double media = soma / n;
        this.mediaNanos = Math.round(media);
        this.minNanos = min;
        this.maxNanos = max;

        // Desvio padrão amostral
        double somaDiferencasQuadradas = 0;
        for (long t : temposValidosNanos) {
            double dif = t - media;
            somaDiferencasQuadradas += dif * dif;
        }
        double variancia = (n > 1) ? (somaDiferencasQuadradas / (n - 1)) : 0.0;
        double desvioPadrao = Math.sqrt(variancia);
        this.desvioPadraoNanos = Math.round(desvioPadrao);

        // Mediana
        long[] ordenados = temposValidosNanos.clone();
        Arrays.sort(ordenados);
        if (n % 2 == 1) {
            this.medianaNanos = ordenados[n / 2];
        } else {
            this.medianaNanos = Math.round((ordenados[(n / 2) - 1] + (double) ordenados[n / 2]) / 2.0);
        }

        // Conversões para milissegundos (com precisão decimal)
        this.mediaMs = media / 1_000_000.0;
        this.desvioPadraoMs = desvioPadrao / 1_000_000.0;
        this.medianaMs = this.medianaNanos / 1_000_000.0;
        this.minMs = min / 1_000_000.0;
        this.maxMs = max / 1_000_000.0;
        this.coeficienteVariacaoPercent = (media > 0) ? (desvioPadrao / media) * 100.0 : 0.0;
    }

    /**
     * Construtor para registrar falhas/erros (ex.: StackOverflowError ou TimeOut)
     */
    public Estatisticas(String algoritmo, String cenario, int tamanho, String mensagemErro) {
        this.algoritmo = algoritmo;
        this.cenario = cenario;
        this.tamanho = tamanho;
        this.totalExecucoes = 0;
        this.warmupsDescartados = 0;
        this.execucoesMedidas = 0;
        this.sucesso = false;
        this.mensagemErro = mensagemErro;

        this.mediaMs = Double.NaN;
        this.desvioPadraoMs = Double.NaN;
        this.medianaMs = Double.NaN;
        this.minMs = Double.NaN;
        this.maxMs = Double.NaN;
        this.coeficienteVariacaoPercent = Double.NaN;

        this.mediaNanos = -1;
        this.desvioPadraoNanos = -1;
        this.medianaNanos = -1;
        this.minNanos = -1;
        this.maxNanos = -1;
        this.temposValidosNanos = new long[0];
    }

    public String getAlgoritmo() { return algoritmo; }
    public String getCenario() { return cenario; }
    public int getTamanho() { return tamanho; }
    public int getTotalExecucoes() { return totalExecucoes; }
    public int getWarmupsDescartados() { return warmupsDescartados; }
    public int getExecucoesMedidas() { return execucoesMedidas; }

    public double getMediaMs() { return mediaMs; }
    public double getDesvioPadraoMs() { return desvioPadraoMs; }
    public double getMedianaMs() { return medianaMs; }
    public double getMinMs() { return minMs; }
    public double getMaxMs() { return maxMs; }
    public double getCoeficienteVariacaoPercent() { return coeficienteVariacaoPercent; }

    public long getMediaNanos() { return mediaNanos; }
    public long getDesvioPadraoNanos() { return desvioPadraoNanos; }
    public long getMedianaNanos() { return medianaNanos; }
    public long getMinNanos() { return minNanos; }
    public long getMaxNanos() { return maxNanos; }
    public long[] getTemposValidosNanos() { return temposValidosNanos; }

    public boolean isSucesso() { return sucesso; }
    public String getMensagemErro() { return mensagemErro; }

    @Override
    public String toString() {
        if (!sucesso) {
            return String.format("[%s | %s | N=%d] FALHA: %s", algoritmo, cenario, tamanho, mensagemErro);
        }
        return String.format(Locale.US,
            "[%s | %s | N=%d] Média: %.4f ms (±%.4f ms), Mediana: %.4f ms, Min: %.4f ms, Max: %.4f ms, CV: %.2f%%",
            algoritmo, cenario, tamanho, mediaMs, desvioPadraoMs, medianaMs, minMs, maxMs, coeficienteVariacaoPercent
        );
    }
}
