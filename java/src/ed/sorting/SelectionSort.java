package ed.sorting;

public class SelectionSort {

    public static void selectionSort(int[] vetor){
        int n = vetor.length;

        for (int i = 0; i < n - 1; i++){
            int menor = i;

            for (int j = i + 1; j < n; j++){
                if (vetor[j] < vetor[menor]){
                    menor = j;
                }
            }

            trocar(vetor, i, menor);
        }
    }

    private static void trocar(int[] vetor, int i, int j){
        int temp = vetor[i];
        vetor[i] = vetor[j];
        vetor[j] = temp;
    }

} //kk