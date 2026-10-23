package ed.sorting;

public class InsertionSort {

    public static void insertionSort(int[] vetor){
        int n = vetor.length;

        for (int i = 1; i < n; i++){
            int chave = vetor[i];
            int j = i - 1;

            while (j >= 0 && vetor[j] > chave){
                vetor[j+1] = vetor[j];
                j--;
            }

            vetor[j+1] = chave;
        }
    }

}