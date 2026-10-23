package ed.sorting;

public class BubbleSort {

    public static void bubbleSort(int[] vetor){
        int n = vetor.length;

        for (int i = 0; i < n - 1; i++){
            for (int j = 0; j < n - 1 - i; j++){
                if (vetor[j] > vetor[j+1]){
                    trocar(vetor, j, j+1);
                }
            }
        }

    }

    private static void trocar(int[] vetor, int i, int j){
        int temp = vetor[i];
        vetor[i] = vetor[j];
        vetor[j] = temp;
    }

}
