package ed.util;

import java.util.*;

/**
 * Utilitário para geração de cenários de teste conforme as diretrizes do projeto:
 * - Valores pertencentes ao intervalo [1, 100.000], permitindo repetições.
 * - Quatro cenários:
 *   1. Aleatório
 *   2. Já ordenado (crescente)
 *   3. Inversamente ordenado (decrescente)
 *   4. Parcialmente ordenado (~15% das posições selecionadas e embaralhadas)
 */
public class GeradorCenarios {

    public static final int VALOR_MIN = 1;
    public static final int VALOR_MAX = 100_000;
    public static final double PERCENTUAL_EMBARALHAMENTO_PADRAO = 0.15; // 15%

    public enum Cenario {
        ALEATORIO("Aleatório"),
        ORDENADO("Já Ordenado"),
        INVERSO("Ordem Inversa"),
        PARCIALMENTE_ORDENADO("Parcialmente Ordenado (~15%)");

        private final String descricao;

        Cenario(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    /**
     * Gera um vetor de tamanho n com valores aleatórios no intervalo [VALOR_MIN, VALOR_MAX].
     */
    public static int[] gerarAleatorio(int tamanho, Random random) {
        int[] vetor = new int[tamanho];
        int range = VALOR_MAX - VALOR_MIN + 1;
        for (int i = 0; i < tamanho; i++) {
            vetor[i] = VALOR_MIN + random.nextInt(range);
        }
        return vetor;
    }

    public static int[] gerarAleatorio(int tamanho) {
        return gerarAleatorio(tamanho, new Random());
    }

    /**
     * Gera um vetor de tamanho n previamente ordenado em ordem crescente.
     */
    public static int[] gerarOrdenado(int tamanho, Random random) {
        int[] vetor = gerarAleatorio(tamanho, random);
        Arrays.sort(vetor);
        return vetor;
    }

    public static int[] gerarOrdenado(int tamanho) {
        return gerarOrdenado(tamanho, new Random());
    }

    /**
     * Gera um vetor de tamanho n previamente ordenado em ordem decrescente (inversa).
     */
    public static int[] gerarInverso(int tamanho, Random random) {
        int[] vetor = gerarOrdenado(tamanho, random);
        // Inverte o vetor ordenado
        int i = 0;
        int j = tamanho - 1;
        while (i < j) {
            int temp = vetor[i];
            vetor[i] = vetor[j];
            vetor[j] = temp;
            i++;
            j--;
        }
        return vetor;
    }

    public static int[] gerarInverso(int tamanho) {
        return gerarInverso(tamanho, new Random());
    }

    /**
     * Gera um vetor parcialmente ordenado:
     * Considera um vetor inicialmente ordenado e seleciona aleatoriamente
     * aproximadamente 15% das posições, embaralhando apenas os elementos dessas posições.
     */
    public static int[] gerarParcialmenteOrdenado(int tamanho, double percentual, Random random) {
        int[] vetor = gerarOrdenado(tamanho, random);
        if (tamanho <= 1) {
            return vetor;
        }

        int k = (int) Math.round(tamanho * percentual);
        if (k < 2 && tamanho >= 2) {
            k = 2;
        }
        if (k > tamanho) {
            k = tamanho;
        }

        // Seleciona k posições distintas usando algoritmo de amostragem (Reservoir / shuffle de índices)
        // Para garantir seleção aleatória de índices sem repetição:
        int[] todosIndices = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            todosIndices[i] = i;
        }
        // Fisher-Yates parcial para selecionar k índices
        for (int i = 0; i < k; i++) {
            int idxAleatorio = i + random.nextInt(tamanho - i);
            int tmp = todosIndices[i];
            todosIndices[i] = todosIndices[idxAleatorio];
            todosIndices[idxAleatorio] = tmp;
        }

        int[] posicoesEscolhidas = Arrays.copyOf(todosIndices, k);
        // Ordena os índices para manter referência estável das posições no vetor original
        Arrays.sort(posicoesEscolhidas);

        // Extrai os elementos dessas posições
        int[] elementos = new int[k];
        for (int i = 0; i < k; i++) {
            elementos[i] = vetor[posicoesEscolhidas[i]];
        }

        // Embaralha apenas os elementos dessas posições (Fisher-Yates)
        for (int i = k - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = elementos[i];
            elementos[i] = elementos[j];
            elementos[j] = tmp;
        }

        // Recoloca os elementos embaralhados de volta nas posições escolhidas
        for (int i = 0; i < k; i++) {
            vetor[posicoesEscolhidas[i]] = elementos[i];
        }

        return vetor;
    }

    public static int[] gerarParcialmenteOrdenado(int tamanho) {
        return gerarParcialmenteOrdenado(tamanho, PERCENTUAL_EMBARALHAMENTO_PADRAO, new Random());
    }

    /**
     * Gera o vetor de acordo com o cenário especificado.
     */
    public static int[] gerarPorCenario(Cenario cenario, int tamanho, Random random) {
        switch (cenario) {
            case ALEATORIO:
                return gerarAleatorio(tamanho, random);
            case ORDENADO:
                return gerarOrdenado(tamanho, random);
            case INVERSO:
                return gerarInverso(tamanho, random);
            case PARCIALMENTE_ORDENADO:
                return gerarParcialmenteOrdenado(tamanho, PERCENTUAL_EMBARALHAMENTO_PADRAO, random);
            default:
                throw new IllegalArgumentException("Cenário desconhecido: " + cenario);
        }
    }
}
