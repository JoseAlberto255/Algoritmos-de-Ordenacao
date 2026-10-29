package ed.util;

/**
 * Utilitário para validação da correção dos algoritmos:
 * - Para ordenação: valida se o array final está em ordem não-decrescente (ordenado).
 * - Para busca: valida se o índice retornado corresponde ao esperado (-1 no pior caso especificado).
 *
 * Importante: A verificação deve ser executada FORA da medição com System.nanoTime().
 */
public class Verificador {

    /**
     * Retorna true se o vetor estiver ordenado de forma não-decrescente.
     */
    public static boolean isOrdenado(int[] vetor) {
        if (vetor == null || vetor.length <= 1) {
            return true;
        }
        for (int i = 0; i < vetor.length - 1; i++) {
            if (vetor[i] > vetor[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Valida que o vetor está ordenado. Se não estiver, lança uma exceção informativa.
     */
    public static void verificarOrdenado(int[] vetor, String nomeAlgoritmo) {
        if (vetor == null || vetor.length <= 1) {
            return;
        }
        for (int i = 0; i < vetor.length - 1; i++) {
            if (vetor[i] > vetor[i + 1]) {
                throw new AssertionError(String.format(
                    "Algoritmo '%s' falhou na ordenação! Elemento no índice %d (%d) é maior que no índice %d (%d).",
                    nomeAlgoritmo, i, vetor[i], i + 1, vetor[i + 1]
                ));
            }
        }
    }

    /**
     * Valida se o resultado da busca corresponde ao esperado.
     */
    public static void verificarBusca(int resultadoObtido, int resultadoEsperado, String nomeAlgoritmo) {
        if (resultadoObtido != resultadoEsperado) {
            throw new AssertionError(String.format(
                "Algoritmo '%s' falhou na busca! Esperado: %d, Obtido: %d",
                nomeAlgoritmo, resultadoEsperado, resultadoObtido
            ));
        }
    }
}
