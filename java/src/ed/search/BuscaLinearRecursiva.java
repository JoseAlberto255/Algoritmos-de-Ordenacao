package ed.searching;

public class BuscaLinearRecursiva {

    public static int buscaLinearRecursiva(int[] vetor, int chave){
        return buscar(vetor, chave, 0);
    }

    private static int buscar(int[] vetor, int chave, int indice){
        if (indice >= vetor.length){
            return -1;
        }

        if (vetor[indice] == chave){
            return indice;
        }

        return buscar(vetor, chave, indice + 1);
    }

}