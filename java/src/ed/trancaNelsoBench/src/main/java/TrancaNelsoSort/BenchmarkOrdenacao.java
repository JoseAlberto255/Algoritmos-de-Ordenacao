package TrancaNelsoSort;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Como rodar (a partir da raiz do projeto, a pasta que contém "java"):
 *
 *   cd java/src/ed/trancaNelsoBench
 *   mvn clean package
 *   java -jar target/benchmarks.jar BenchmarkOrdenacao
 *
 * Para salvar a saída em arquivo, acrescente ao último comando: > resultados.txt
 *
 * Requisitos: Maven e Java 17 ou superior no PATH.
 * O mvn clean package gera target/benchmarks.jar, com o JMH embutido.
 * A execução demora cerca de 30 minutos ou mais: 21 combinações de tamanho
 * e distribuição, com 6 métodos em cada uma.
 *
 * Tempo médio para ordenar CPFs (11 dígitos, como long) em diferentes distribuições.
 *
 * Cada invocação copia a massa original para um buffer de trabalho antes de ordenar.
 * O metodo controleCopia mede só essa cópia: o custo é o mesmo para todos os
 * algoritmos, então dá para subtrair do resultado se quiser o tempo puro de ordenação.
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
@Fork(value = 1, jvmArgs = {"-Xms2g", "-Xmx2g"})
@State(Scope.Thread)
public class BenchmarkOrdenacao {

    @Param({"10000", "100000", "1000000"})
    public int tamanho;

    @Param({"UNIFORME", "GAUSSIANA", "PREFIXO_COMUM", "POUCOS_DISTINTOS", "ORDENADO", "INVERSO", "QUASE_ORDENADO"})
    public String distribuicao;

    private long[] origem;
    private long[] trabalho;

    @Setup(Level.Trial)
    public void preparar() {
        origem = Distribuicao.valueOf(distribuicao).gerar(tamanho, 42L);
        trabalho = new long[tamanho];
        verificarCorretude();
    }

    private void verificarCorretude() {
        long[] esperado = origem.clone();
        Arrays.sort(esperado);

        long[] a = origem.clone();
        TrancaNelsoSort.radixSort(a, Distribuicao.DIGITOS);
        exigir("TrancaNelso", Arrays.equals(esperado, a));

        long[] b = origem.clone();
        Algoritmos.radixLsd(b, Distribuicao.DIGITOS);
        exigir("RadixLSD", Arrays.equals(esperado, b));

        long[] c = origem.clone();
        Algoritmos.mergeSort(c);
        exigir("MergeSort", Arrays.equals(esperado, c));

        long[] d = origem.clone();
        Algoritmos.quickSort(d);
        exigir("QuickSort", Arrays.equals(esperado, d));
    }

    private void exigir(String algoritmo, boolean ok) {
        if (!ok) {
            throw new IllegalStateException(
                    algoritmo + " ordenou errado: distribuicao=" + distribuicao + " n=" + tamanho);
        }
    }


    @Benchmark
    public void controleCopia(Blackhole bh) {
        System.arraycopy(origem, 0, trabalho, 0, tamanho);
        bh.consume(trabalho);
    }

    @Benchmark
    public void arraysSort(Blackhole bh) {
        System.arraycopy(origem, 0, trabalho, 0, tamanho);
        Algoritmos.arraysSort(trabalho);
        bh.consume(trabalho);
    }

    @Benchmark
    public void mergeSort(Blackhole bh) {
        System.arraycopy(origem, 0, trabalho, 0, tamanho);
        Algoritmos.mergeSort(trabalho);
        bh.consume(trabalho);
    }

    @Benchmark
    public void radixLsd(Blackhole bh) {
        System.arraycopy(origem, 0, trabalho, 0, tamanho);
        Algoritmos.radixLsd(trabalho, Distribuicao.DIGITOS);
        bh.consume(trabalho);
    }

    @Benchmark
    public void quickSort(Blackhole bh) {
        System.arraycopy(origem, 0, trabalho, 0, tamanho);
        Algoritmos.quickSort(trabalho);
        bh.consume(trabalho);
    }

    @Benchmark
    public void trancaNelsoSort(Blackhole bh) {
        System.arraycopy(origem, 0, trabalho, 0, tamanho);
        TrancaNelsoSort.radixSort(trabalho, Distribuicao.DIGITOS);
        bh.consume(trabalho);
    }
}
