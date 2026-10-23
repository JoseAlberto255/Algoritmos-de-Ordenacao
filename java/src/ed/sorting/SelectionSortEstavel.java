package ed.sorting;

public class SelectionSortEstavel {

    public static void selectionSortEstavel(int[] vetor){
        int n = vetor.length;

        for (int i = 0; i < n - 1; i++){
            int menor = i;

            for (int j = i + 1; j < n; j++){
                if (vetor[j] < vetor[menor]){
                    menor = j;
                }
            }

            int valor = vetor[menor];

            while (menor > i){
                vetor[menor] = vetor[menor - 1];
                menor--;
            }

            vetor[i] = valor;
        }
    }

}