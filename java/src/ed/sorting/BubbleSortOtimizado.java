package ed.sorting;

public class BubbleSortOtimizado {

    public static void bubbleSortOtimizado(int[] vetor){
        int n = vetor.length;
        boolean trocou = true;

        for (int i = 0; i < n - 1 && trocou; i++){
            trocou = false;

            for (int j = 0; j < n - 1 - i; j++){
                if (vetor[j] > vetor[j+1]){
                    trocar(vetor, j, j+1);
                    trocou = true;
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