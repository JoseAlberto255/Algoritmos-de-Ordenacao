package ed.search;

public class BuscaBinariaIterativa {

    public static int buscaBinariaIterativa(int[] vetor, int chave){
        int inicio = 0;
        int fim = vetor.length - 1;

        while (inicio <= fim){
            int meio = inicio + (fim - inicio) / 2;

            if (vetor[meio] == chave){
                return meio;
            } else if (vetor[meio] < chave){
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }

        return -1;
    }

}