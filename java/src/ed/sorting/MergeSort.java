package ed.sorting;

public class MergeSort {

    public static void mergeSort(int[] vetor){
        int[] auxiliar = new int[vetor.length];

        ordenar(vetor, auxiliar, 0, vetor.length - 1);
    }

    private static void ordenar(int[] vetor, int[] auxiliar, int inicio, int fim){
        if (inicio >= fim){
            return;
        }

        int meio = inicio + (fim - inicio) / 2;

        ordenar(vetor, auxiliar, inicio, meio);
        ordenar(vetor, auxiliar, meio + 1, fim);
        intercalar(vetor, auxiliar, inicio, meio, fim);
    }

    private static void intercalar(int[] vetor, int[] auxiliar, int inicio, int meio, int fim){
        for (int k = inicio; k <= fim; k++){
            auxiliar[k] = vetor[k];
        }

        int i = inicio;
        int j = meio + 1;

        for (int k = inicio; k <= fim; k++){
            if (i > meio){
                vetor[k] = auxiliar[j++];
            } else if (j > fim){
                vetor[k] = auxiliar[i++];
            } else if (auxiliar[j] < auxiliar[i]){
                vetor[k] = auxiliar[j++];
            } else {
                vetor[k] = auxiliar[i++];
            }
        }
    }

}

