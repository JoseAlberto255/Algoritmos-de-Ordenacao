package ed.sorting;

public class QuickSort {

    public static void quickSort(int[] vetor){
        ordenar(vetor, 0, vetor.length - 1);
    }

    private static void ordenar(int[] vetor, int inicio, int fim){
        if (inicio < fim){
            int p = particionar(vetor, inicio, fim);

            ordenar(vetor, inicio, p - 1);
            ordenar(vetor, p + 1, fim);
        }
    }

    private static int particionar(int[] vetor, int inicio, int fim){
        int pivo = vetor[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++){
            if (vetor[j] <= pivo){
                i++;
                trocar(vetor, i, j);
            }
        }

        trocar(vetor, i + 1, fim);

        return i + 1;
    }

    private static void trocar(int[] vetor, int i, int j){
        int temp = vetor[i];
        vetor[i] = vetor[j];
        vetor[j] = temp;
    }

}