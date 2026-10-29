package ed.search;

public class BuscaLinearDuasPontas {

    public static int buscaLinearDuasPontas(int[] vetor, int chave){
        int inicio = 0;
        int fim = vetor.length - 1;

        while (inicio <= fim){
            if (vetor[inicio] == chave){
                return inicio;
            }

            if (vetor[fim] == chave){
                return fim;
            }

            inicio++;
            fim--;
        }

        return -1;
    }

}