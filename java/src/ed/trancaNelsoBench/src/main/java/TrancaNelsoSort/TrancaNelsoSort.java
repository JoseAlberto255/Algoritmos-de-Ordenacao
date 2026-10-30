
package TrancaNelsoSort;

public class TrancaNelsoSort{

    private static final int BASE = 10;
    private static final int LIMIAR = 16;

    public static void radixSort(long[] valores, int digitos) {
        if (valores.length > 1 && !estaOrdenado(valores)) {
            new Ordenador(valores, digitos).ordenar(0, valores.length, 0);
        }
    }

    private static long potenciaDeDez(int expoente) {
        long p = 1;
        for (int i = 0; i < expoente; i++) {
            p *= BASE;
        }
        return p;
    }

    private static boolean estaOrdenado(long[] valores) {
        for (int i = 1; i < valores.length; i++) {
            if (valores[i - 1] > valores[i]) {
                return false;
            }
        }
        return true;
    }

    private static final class Ordenador {

        private final long[] vetor;
        private final long[] auxiliar;
        private final byte[] digitoDe;
        private final long[] peso;
        private final int digitos;

        Ordenador(long[] vetor, int digitos) {
            this.vetor = vetor;
            this.digitos = digitos;
            this.auxiliar = new long[vetor.length];
            this.digitoDe = new byte[vetor.length];
            this.peso = new long[digitos];

            for (int nivel = 0; nivel < digitos; nivel++) {
                peso[nivel] = potenciaDeDez(digitos - 1 - nivel);
            }
        }

        void ordenar(int inicio, int fim, int nivel) {
            int tamanho = fim - inicio;

            if (tamanho <= LIMIAR) {
                insertionSort(inicio, fim);
                return;
            }

            if (nivel == digitos) {
                return;
            }

            long pesoAtual = peso[nivel];
            int[] contagem = new int[BASE];

            long menor = Long.MAX_VALUE;
            long maior = Long.MIN_VALUE;

            for (int i = inicio; i < fim; i++) {
                long valor = vetor[i];

                menor = Math.min(menor, valor);
                maior = Math.max(maior, valor);

                int digito = (int) ((valor / pesoAtual) % BASE);
                digitoDe[i] = (byte) digito;
                contagem[digito]++;
            }

            if (menor == maior) {
                return;
            }

            for (int d = 0; d < BASE; d++) {
                if (contagem[d] == tamanho) {
                    ordenar(inicio, fim, nivel + 1);
                    return;
                }
            }

            int[] proximaPosicao = new int[BASE];
            int posicao = inicio;

            for (int d = 0; d < BASE; d++) {
                proximaPosicao[d] = posicao;
                posicao += contagem[d];
            }

            for (int i = inicio; i < fim; i++) {
                auxiliar[proximaPosicao[digitoDe[i]]++] = vetor[i];
            }

            System.arraycopy(auxiliar, inicio, vetor, inicio, tamanho);

            int grupoInicio = inicio;

            for (int d = 0; d < BASE; d++) {
                int tamanhoGrupo = contagem[d];

                if (tamanhoGrupo > 1) {
                    ordenar(grupoInicio, grupoInicio + tamanhoGrupo, nivel + 1);
                }

                grupoInicio += tamanhoGrupo;
            }
        }

        private void insertionSort(int inicio, int fim) {
            for (int i = inicio + 1; i < fim; i++) {
                long atual = vetor[i];
                int j = i - 1;

                while (j >= inicio && vetor[j] > atual) {
                    vetor[j + 1] = vetor[j];
                    j--;
                }

                vetor[j + 1] = atual;
            }
        }
    }
}
