package ed.searching;

public class BuscaBinariaRecursiva {

    public static int buscaBinariaRecursiva(int[] vetor, int chave){
        return buscar(vetor, chave, 0, vetor.length - 1);
    }

    private static int buscar(int[] vetor, int chave, int inicio, int fim){
        if (inicio > fim){
            return -1;
        }

        int meio = inicio + (fim - inicio) / 2;

        if (vetor[meio] == chave){
            return meio;
        } else if (vetor[meio] < chave){
            return buscar(vetor, chave, meio + 1, fim);
        } else {
            return buscar(vetor, chave, inicio, meio - 1);
        }
    }

}