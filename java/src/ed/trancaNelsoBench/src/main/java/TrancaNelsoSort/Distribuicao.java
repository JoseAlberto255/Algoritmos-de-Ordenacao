package TrancaNelsoSort;

import java.util.Arrays;
import java.util.Random;

public enum Distribuicao {

    //Bem distribuído: uniforme em todo o intervalo.
    UNIFORME,

    //Concentrado no meio do intervalo (sino com desvio de 5% do intervalo)
    GAUSSIANA,

    //Mal distribuído: todos os valores começam com os mesmos 3 dígitos (123)
    PREFIXO_COMUM,

    //Muitas repetições: só 100 valores diferentes
    POUCOS_DISTINTOS,

    //Já ordenado
    ORDENADO,

    //Ordenado ao contrário
    INVERSO,

    //Ordenado, com cerca de 1% de trocas aleatórias
    QUASE_ORDENADO;

    public static final int DIGITOS = 11;
    public static final long LIMITE = 100_000_000_000L;

    private static final long PREFIXO = 123L * 100_000_000L;
    private static final long TAMANHO_PREFIXO = 100_000_000L;

    public long[] gerar(int n, long semente) {
        Random r = new Random(semente);
        long[] v = new long[n];

        switch (this) {
            case UNIFORME:
                preencherAleatorio(v, r);
                break;

            case GAUSSIANA:
                for (int i = 0; i < n; i++) {
                    double x = LIMITE / 2.0 + r.nextGaussian() * (LIMITE / 20.0);
                    v[i] = (long) Math.max(0, Math.min(LIMITE - 1, x));
                }
                break;

            case PREFIXO_COMUM:
                for (int i = 0; i < n; i++) {
                    v[i] = PREFIXO + Math.floorMod(r.nextLong(), TAMANHO_PREFIXO);
                }
                break;

            case POUCOS_DISTINTOS: {
                long[] possiveis = new long[100];
                preencherAleatorio(possiveis, r);
                for (int i = 0; i < n; i++) {
                    v[i] = possiveis[r.nextInt(possiveis.length)];
                }
                break;
            }

            case ORDENADO:
                preencherAleatorio(v, r);
                Arrays.sort(v);
                break;

            case INVERSO:
                preencherAleatorio(v, r);
                Arrays.sort(v);
                for (int i = 0, j = n - 1; i < j; i++, j--) {
                    long troca = v[i];
                    v[i] = v[j];
                    v[j] = troca;
                }
                break;

            case QUASE_ORDENADO:
                preencherAleatorio(v, r);
                Arrays.sort(v);
                for (int k = 0; k < n / 100; k++) {
                    int i = r.nextInt(n);
                    int j = r.nextInt(n);
                    long troca = v[i];
                    v[i] = v[j];
                    v[j] = troca;
                }
                break;
        }

        return v;
    }

    private static void preencherAleatorio(long[] v, Random r) {
        for (int i = 0; i < v.length; i++) {
            v[i] = Math.floorMod(r.nextLong(), LIMITE);
        }
    }
}
