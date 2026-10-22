package ed.sorting;

public class CountingSort {

    public static coutingSort(int[] vetorA, int k){

        int[] vetorB = new int[vetorA.length]
        int[] vetorC = new int[k]

        for (int i = 0; i < vetorA.length; i++){
            vetorC[vetorA[i]] = vetorC[vetorA[i]] + 1;
        }

        for (int i = 1; i <= k; i++) {
            vetorC[i] = vetorC[i] + vetorC[i - 1];
        }

        for (int i = vetorA.length -1; i >= 0; i--) {
            vetorB[vetorC[vetorA[i]] - 1] = vetorA[i];
            vetorC[vetorA[i]] = vetorC[vetorA[i]] - 1;
        }

    }

}