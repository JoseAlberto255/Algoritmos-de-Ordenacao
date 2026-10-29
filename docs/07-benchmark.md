# Benchmark — Guia completo

Documentação da suíte de benchmarks Java do projeto EDA/LEDA (Unidade 1): o que ela mede, por que existe *warm-up*, como o código funciona, como executar e quais modos já cobrem o enunciado **sem alterar a base de código**.

---

## 1. Visão geral

A suíte mede tempo de execução de algoritmos de **busca** e **ordenação** com `System.nanoTime()`, repete cada combinação várias vezes, descarta as primeiras iterações (warm-up) e exporta resultados em CSV na pasta `data/`.

| Peça | Pacote / arquivo | Papel |
|------|------------------|--------|
| Entrada unificada | `ed.bench.BenchmarkMain` | Roda busca, ordenação ou ambos |
| Benchmark de busca | `ed.bench.BenchBusca` | 5 algoritmos de busca |
| Benchmark de ordenação | `ed.bench.BenchOrdenacao` | 9 algoritmos + `Arrays.sort` |
| Cenários de entrada | `ed.util.GeradorCenarios` | Vetores [1, 100 000] e 4 cenários |
| Estatísticas | `ed.util.Estatisticas` | Média, desvio, mediana, min/max (após warm-up) |
| Exportação | `ed.util.CsvWriter` | CSV agregado e detalhado |
| Corretude | `ed.util.Verificador` | Checagem **fora** da medição de tempo |
| Orquestração | `Makefile` (raiz) | `compile`, `test`, `bench`, `analise` |

Flags da JVM usadas pelo Makefile:

```text
-Xss64m -Xms512m -Xmx4g
```

- `-Xss64m` — pilha maior (recursão / pior caso do QuickSort e buscas recursivas).
- `-Xms512m` / `-Xmx4g` — heap adequado para vetores grandes e cópias por iteração.

---

## 2. Por que existe warm-up?

Cada combinação (algoritmo × cenário × tamanho) roda **25 execuções**:

| Fase | Iterações | Uso |
|------|-----------|-----|
| Warm-up | 1–5 | Executadas e gravadas no CSV detalhado, **descartadas** nas estatísticas |
| Medição | 6–25 | **20** tempos usados em média, desvio, mediana, min e max |

Constantes no código:

- `TOTAL_EXECUCOES_PADRAO = 25`
- `WARMUP_EXECUCOES_PADRAO = 5`

### Motivos técnicos

1. **JIT (Just-In-Time)** — Na JVM, as primeiras chamadas costumam ser interpretadas; só depois o HotSpot compila e otimiza métodos “quentes”. Incluir essas rodadas na média favorece artificialmente quem “esqueceu” o warm-up ou distorce a comparação.
2. **Caches frios** — Cache de CPU, predição de desvios e alocações iniciais deixam as primeiras iterações mais lentas e ruidosas.
3. **Carregamento de classes** — Custo de *class loading* e inicialização aparece no começo e não representa o comportamento em regime.

### O que o código faz

- Em `BenchBusca` / `BenchOrdenacao`, `isWarmup = (iter < warmup)`.
- Em `Estatisticas`, os tempos válidos são `todosTemposNanos[warmup … fim]`.
- No CSV detalhado, a coluna `IsWarmup` marca `true`/`false` para auditoria; o CSV agregado já usa só as 20 medidas (ou registra `FALHA` se o algoritmo foi interrompido).

Sem warm-up, o desvio e o coeficiente de variação sobem e a comparação entre algoritmos deixa de ser justa.

---

## 3. Como o código funciona

### 3.1 Fluxo geral

```text
make compile  →  java ed.bench.BenchBusca / BenchOrdenacao
                      │
                      ├─ para cada tamanho N
                      │     ├─ para cada cenário (ordenação)  OU  1 cenário (busca)
                      │     │     ├─ para iter = 0 .. 24
                      │     │     │     ├─ gera vetor (Random seed 42)
                      │     │     │     ├─ para cada algoritmo
                      │     │     │     │     ├─ clone() do vetor  (fora do relógio)
                      │     │     │     │     ├─ nanoTime → algoritmo → nanoTime
                      │     │     │     │     └─ Verificador (fora do relógio)
                      │     │     └─ Estatisticas (descarta warm-up)
                      └─ CsvWriter → data/*.csv
```

### 3.2 Garantias de fairness

- **Mesma entrada por iteração** — Um vetor é gerado por iteração; todos os algoritmos daquela rodada recebem `clone()` desse vetor.
- **Cópia fora da medição** — Só a chamada ao algoritmo fica entre os dois `nanoTime`.
- **Seed fixa** — `new Random(42)` torna a sequência de entradas reproduzível entre execuções do mesmo programa.
- **Valores no intervalo do enunciado** — `[1, 100 000]`, com repetição permitida (`GeradorCenarios`).

### 3.3 Benchmark de busca (`BenchBusca`)

| Aspecto | Comportamento |
|---------|----------------|
| Entrada | Vetor **já ordenado** (`gerarOrdenado`) |
| Chave | `-1` (ausente → pior caso das buscas lineares / binárias no sentido de “não achar”) |
| Algoritmos | Linear iterativa, linear recursiva, linear duas pontas, binária iterativa, binária recursiva |
| Tamanhos padrão | `50 000`, `150 000`, `500 000` (ou lista numérica via args / `SIZES_BUSCA`) |
| Limite de tempo por iteração | Não aplica o corte de 5 s (diferente da ordenação) |
| CSV | `data/resultados_busca_agregado.csv`, `data/resultados_busca_detalhado.csv` |

### 3.4 Benchmark de ordenação (`BenchOrdenacao`)

| Aspecto | Comportamento |
|---------|----------------|
| Cenários | Aleatório; Já ordenado; Ordem inversa; Parcialmente ordenado (~15% das posições embaralhadas) |
| Algoritmos | Bubble, Bubble otimizado, Selection, Selection estável, Insertion, Merge, Quick, Quick+shuffle, Counting, `Arrays.sort` |
| Tamanhos padrão (enunciado) | `50 000`, `150 000`, `500 000` |
| Modo `--todos-direto` / `--ajustado` | `5 000`, `15 000`, `50 000` — viabiliza **todos** os algoritmos (incl. O(n²)) em tempo razoável |
| Limite | Se uma iteração > **5 s**, o algoritmo é interrompido nas iterações seguintes e marcado como falha |
| Erros tratados | `StackOverflowError`, `OutOfMemoryError`, demais `Throwable` |
| CSV | `data/resultados_ordenacao_agregado.csv`, `data/resultados_ordenacao_detalhado.csv` |

Classificação usada pelas flags (campo `quadratico` em `AlgoritmoInfo`):

| Grupo | Algoritmos |
|-------|------------|
| Quadráticos | BubbleSort, BubbleSortOtimizado, SelectionSort, SelectionSortEstavel, InsertionSort |
| Eficientes (+ baseline) | MergeSort, QuickSort, QuickSortShuffle, CountingSort, Arrays.sort |

### 3.5 Estatísticas e CSV

**Agregado** (após warm-up): média, desvio padrão amostral, mediana, min, max (ms e ns), coeficiente de variação (%), status `SUCESSO` ou `FALHA` + mensagem.

**Detalhado**: uma linha por execução, com `IsWarmup`.

---

## 4. Como inicializar e executar

### 4.1 Pré-requisitos

Na raiz do repositório:

```bash
# Linux / macOS
chmod +x scripts/setup.sh && ./scripts/setup.sh

# Ou só checar
make verify
make info
```

Necessário: **JDK 11+** (benchmarks). Python 3 + matplotlib só para `make tabelas` / `make graficos` / `make analise`.

### 4.2 Compilar

```bash
make compile
```

Classes em `java/out/`.

### 4.3 Sanity check (rápido)

Tamanhos pequenos; confirma corretude e que a pipeline sobe:

```bash
make test              # busca + ordenação
make test-busca
make test-ordenacao
```

Equivalente interno aproximado:

- Busca: N = `1000 5000`
- Ordenação: `--todos-direto 500 1000`

### 4.4 Benchmarks via Makefile (recomendado)

| Comando | O que roda |
|---------|------------|
| `make bench` | Busca (tamanhos do enunciado) + ordenação (`--todos-direto` por padrão) |
| `make bench-busca` | Só busca — `SIZES_BUSCA` default `50000 150000 500000` |
| `make bench-ordenacao` | Ordenação — `SIZES_ORDENACAO` default `--todos-direto` |
| `make bench-eficientes` | Só não-quadráticos, tamanhos do enunciado (`50k/150k/500k`) |
| `make bench-quadraticos` | Só O(n²), tamanhos do enunciado |
| `make all` | `compile` → `bench` → `analise` |

Tamanhos / flags customizados **sem editar código**:

```bash
make bench-busca SIZES_BUSCA="10000 100000 1000000"

make bench-ordenacao SIZES_ORDENACAO="--eficientes 10000 50000"
make bench-ordenacao SIZES_ORDENACAO="--quadraticos 5000 15000"
make bench-ordenacao SIZES_ORDENACAO="--todos-direto"
make bench-ordenacao SIZES_ORDENACAO="50000 150000 500000"
```

### 4.5 Execução direta com `java`

```bash
make compile
mkdir -p data

# Busca — tamanhos do enunciado
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchBusca

# Busca — tamanhos explícitos
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchBusca 50000 150000 500000

# Ordenação — modos (ver seção 5)
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchOrdenacao --todos-direto
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchOrdenacao --eficientes
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchOrdenacao --quadraticos
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchOrdenacao 50000 150000 500000

# Suíte unificada
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchmarkMain
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchmarkMain busca 50000 150000
java -cp java/out -Xss64m -Xms512m -Xmx4g ed.bench.BenchmarkMain ordenacao --eficientes
```

### 4.6 Análise pós-benchmark

```bash
make tabelas    # Markdown a partir dos CSV
make graficos   # PNG em analysis/graficos/
make analise    # ambos
make open-data
make open-graphs
```

---

## 5. Casos cobertos eficientemente **sem alterar o código**

Tudo abaixo já é suportado por flags CLI e alvos do Makefile.

### 5.1 Matriz de cobertura — busca

| Caso | Como obter | Cobertura |
|------|------------|-----------|
| Pior caso (chave ausente `-1`) em vetor ordenado | `make bench-busca` (padrão) | Enunciado Item 2/3 |
| Três portes do enunciado (50k / 150k / 500k) | Default de `SIZES_BUSCA` | Completo |
| Portes customizados / escala | `SIZES_BUSCA="..."` | Exploratório, sem mudar fontes |
| Sanity | `make test-busca` | Rápido |

Todos os 5 algoritmos de busca rodam no mesmo fluxo; não há filtro `--lineares` no código atual — a comparação linear vs binária já sai no mesmo CSV.

### 5.2 Matriz de cobertura — ordenação

| Caso / objetivo | Comando (sem mudar código) | Por que é o modo “eficiente” |
|-----------------|----------------------------|------------------------------|
| Comparar **todos** os algoritmos em tempo viável | `make bench-ordenacao` (usa `--todos-direto`) ou `SIZES_ORDENACAO="--todos-direto"` | N = 5k / 15k / 50k evita timeout dos O(n²) nos portes do enunciado |
| Portes do **enunciado** só com algoritmos **eficientes** (+ `Arrays.sort`) | `make bench-eficientes` ou `--eficientes` / `--rapido` | 50k / 150k / 500k sem Bubble/Selection/Insertion |
| Isolar família **quadrática** | `make bench-quadraticos` ou `--quadraticos` | Mesmos portes do enunciado; espere tempo alto em N grande |
| Portes do enunciado com **todos** (pode ser lento / limite 5 s) | `SIZES_ORDENACAO="50000 150000 500000"` (sem `--todos-direto`) | Alinha ao PDF; O(n²) pode falhar por `TempoLimiteExcedido` |
| Mix flags + N | `SIZES_ORDENACAO="--eficientes 10000 50000"` | Exploração fina |
| Sanity | `make test-ordenacao` | N pequeno + `--todos-direto` |

### 5.3 Quatro cenários de entrada (ordenação)

Sempre gerados por `GeradorCenarios` em **toda** execução de `BenchOrdenacao` (não há flag para desligar um cenário):

1. **Aleatório**
2. **Já ordenado** (melhor caso típico de Insertion / Bubble otimizado)
3. **Ordem inversa** (pior caso clássico de vários O(n²) e QuickSort ingênuo)
4. **Parcialmente ordenado (~15%)** — ~15% das posições embaralhadas

Valores: `[1, 100 000]`, repetições permitidas (relevante para Counting Sort e estabilidade conceitual).

### 5.4 O que o Makefile já escolhe por padrão

| Alvo | Comportamento default | Interpretação prática |
|------|----------------------|------------------------|
| `make bench` | Busca nos portes do enunciado + ordenação em `--todos-direto` | Pipeline “completa e viável”: busca no tamanho oficial; ordenação comparando todos os algoritmos em N ajustados |
| `make bench-eficientes` | Ordenação eficiente nos portes oficiais | Melhor para gráficos de escala 50k–500k |
| `make bench-quadraticos` | Só O(n²) nos portes oficiais | Use com cautela em N = 500 000 |
| `make test` | N pequeno | Validação, não relatório final |

### 5.5 Artefatos gerados (sem configuração extra)

| Arquivo | Conteúdo |
|---------|----------|
| `data/resultados_busca_agregado.csv` | Resumo estatístico da busca |
| `data/resultados_busca_detalhado.csv` | Cada iteração (+ flag warm-up) |
| `data/resultados_ordenacao_agregado.csv` | Resumo estatístico da ordenação |
| `data/resultados_ordenacao_detalhado.csv` | Cada iteração (+ flag warm-up) |

---

## 6. Limitações conscientes (já tratadas no código)

- **Timeout de 5 s por iteração** (só ordenação): evita campanhas intermináveis com O(n²) em N grande; o algoritmo entra como `FALHA` no agregado.
- **StackOverflow**: registrado; a flag `-Xss64m` do Makefile mitiga, mas pior casos recursivos extremos ainda podem falhar.
- **Warm-up fixo em 5 / total 25**: não há CLI para mudar esses números hoje — alterar exigiria mudar código; para o relatório do projeto, o padrão do enunciado já está embutido.
- **`BenchmarkMain` sem subcomando**: passa os mesmos `args` para busca e ordenação; flags como `--eficientes` são ignoradas pela busca (só números importam em `BenchBusca`). Prefira `make bench-busca` / `make bench-ordenacao` ou `BenchmarkMain busca|ordenacao ...`.

---

## 7. Checklist rápido para o relatório

1. `make verify && make compile && make test`
2. Busca oficial: `make bench-busca`
3. Ordenação comparativa de todos: `make bench-ordenacao` (ou confiar em `make bench`)
4. Escala 50k–500k nos eficientes: `make bench-eficientes`
5. `make analise` e anexar CSV / gráficos de `data/` e `analysis/graficos/`

Isso cobre o protocolo de medição (25 runs, 5 warm-up, nanoTime, cenários, chaves e portes) usando apenas o que o repositório já expõe.
)
