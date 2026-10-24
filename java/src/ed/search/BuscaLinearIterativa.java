package ed.searching;

public class BuscaLinearIterativa {

    public static int buscaLinearIterativa(int[] vetor, int chave){
        int n = vetor.length;

        for (int i = 0; i < n; i++){
            if (vetor[i] == chave){
                return i;
            }
        }

        return -1;
    }

}