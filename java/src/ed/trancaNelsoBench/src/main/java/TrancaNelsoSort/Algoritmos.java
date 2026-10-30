package TrancaNelsoSort;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public final class Algoritmos {

    private static final int BASE = 10;

    private Algoritmos() {
    }

    public static void arraysSort(long[] vetor) {
        Arrays.sort(vetor);
    }

    public static void mergeSort(long[] vetor) {
        mergeSort(vetor, new long[vetor.length], 0, vetor.length);
    }

    private static void mergeSort(long[] vetor, long[] auxiliar, int inicio, int fim) {
        if (fim - inicio <= 1) {
            return;
        }
        int meio = (inicio + fim) >>> 1;
        mergeSort(vetor, auxiliar, inicio, meio);
        mergeSort(vetor, auxiliar, meio, fim);
        intercalar(vetor, auxiliar, inicio, meio, fim);
    }

    private static void intercalar(long[] vetor, long[] auxiliar, int inicio, int meio, int fim) {
        System.arraycopy(vetor, inicio, auxiliar, inicio, fim - inicio);
        int i = inicio;
        int j = meio;
        int k = inicio;
        while (i < meio && j < fim) {
            vetor[k++] = auxiliar[j] < auxiliar[i] ? auxiliar[j++] : auxiliar[i++];
        }
        while (i < meio) {
            vetor[k++] = auxiliar[i++];
        }
        while (j < fim) {
            vetor[k++] = auxiliar[j++];
        }
    }

    public static void radixLsd(long[] vetor, int digitos) {
        int n = vetor.length;
        long[] auxiliar = new long[n];
        long peso = 1;
        for (int passo = 0; passo < digitos; passo++) {
            int[] posicao = new int[BASE + 1];
            for (long valor : vetor) {
                posicao[(int) ((valor / peso) % BASE) + 1]++;
            }
            for (int d = 0; d < BASE; d++) {
                posicao[d + 1] += posicao[d];
            }
            for (long valor : vetor) {
                auxiliar[posicao[(int) ((valor / peso) % BASE)]++] = valor;
            }
            System.arraycopy(auxiliar, 0, vetor, 0, n);
            peso *= BASE;
        }
    }

    public static void countingSort(long[] vetor, int digitos) {
        if (digitos < 1 || digitos > 8) {
            throw new IllegalArgumentException("counting sort limitado a 1..8 dígitos: " + digitos);
        }
        int k = 1;
        for (int i = 0; i < digitos; i++) {
            k *= BASE;
        }

        int[] inicio = new int[k + 1];
        for (long valor : vetor) {
            inicio[(int) valor + 1]++;
        }
        for (int v = 1; v <= k; v++) {
            inicio[v] += inicio[v - 1];
        }

        long[] saida = new long[vetor.length];
        for (long valor : vetor) {
            saida[inicio[(int) valor]++] = valor;
        }
        System.arraycopy(saida, 0, vetor, 0, vetor.length);
    }

    public static void quickSort(long[] vetor) {
        quickSort(vetor, 0, vetor.length - 1);
    }

    private static void quickSort(long[] vetor, int inicio, int fim) {
        while (inicio < fim) {
            int p = ThreadLocalRandom.current().nextInt(inicio, fim + 1);
            long pivo = vetor[p];

            int menor = inicio;
            int i = inicio;
            int maior = fim;
            while (i <= maior) {
                if (vetor[i] < pivo) {
                    trocar(vetor, menor++, i++);
                } else if (vetor[i] > pivo) {
                    trocar(vetor, i, maior--);
                } else {
                    i++;
                }
            }

            if (menor - inicio < fim - maior) {
                quickSort(vetor, inicio, menor - 1);
                inicio = maior + 1;
            } else {
                quickSort(vetor, maior + 1, fim);
                fim = menor - 1;
            }
        }
    }

    private static void trocar(long[] vetor, int i, int j) {
        long temp = vetor[i];
        vetor[i] = vetor[j];
        vetor[j] = temp;
    }
}
