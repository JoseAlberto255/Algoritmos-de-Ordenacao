# =============================================================================
# Makefile — Projeto Algoritmos de Ordenação e Busca (EDA / LEDA 2026.2)
# =============================================================================
# Compatível com Linux, macOS e Windows (Git Bash / WSL).
#
# INÍCIO RÁPIDO:
#   make verify           → verifica dependências e estrutura
#   make compile          → compila todos os fontes Java
#   make test             → teste rápido de sanidade (tamanhos pequenos)
#   make bench            → benchmark completo (busca + ordenação)
#   make analise          → gera tabelas Markdown + gráficos PNG
#   make all              → pipeline completo: compile + bench + analise
#   make help             → lista todos os comandos disponíveis
# =============================================================================

# ---------------------------------------------------------------------------
# Detecção de Sistema Operacional
# ---------------------------------------------------------------------------
ifeq ($(OS),Windows_NT)
    DETECTED_OS := Windows
    # No Windows, prefere 'python'; fallback para 'python3' se necessário
    PYTHON      := $(shell where python 2>nul | head -1 || echo python)
    OPEN_CMD    := explorer
    NULL_DEV    := nul
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Darwin)
        DETECTED_OS := macOS
        OPEN_CMD    := open
    else
        DETECTED_OS := Linux
        OPEN_CMD    := xdg-open
    endif
    PYTHON   := $(shell command -v python3 2>/dev/null || command -v python 2>/dev/null || echo python3)
    NULL_DEV := /dev/null
endif

MKDIR_P := mkdir -p
RM_RF   := rm -rf

# ---------------------------------------------------------------------------
# Diretórios do Projeto
# ---------------------------------------------------------------------------
SRC_DIR      := java/src
OUT_DIR      := java/out
DATA_DIR     := data
ANALYSIS_DIR := analysis
GRAPHS_DIR   := $(ANALYSIS_DIR)/graficos

# ---------------------------------------------------------------------------
# Configurações Java
# ---------------------------------------------------------------------------
JAVAC       := javac
JAVA        := java
JAVAC_FLAGS := -encoding UTF-8
JVM_FLAGS   := -Xss64m -Xms512m -Xmx4g

# Classes principais
CLASS_BENCH_BUSCA     := ed.bench.BenchBusca
CLASS_BENCH_ORDENACAO := ed.bench.BenchOrdenacao

# ---------------------------------------------------------------------------
# Descoberta recursiva de fontes Java (compatível sem 'find' externo)
# ---------------------------------------------------------------------------
rwildcard = $(foreach d,$(wildcard $(1:=/*)),$(call rwildcard,$d,$2) \
              $(filter $(subst *,%,$2),$d))
JAVA_SOURCES := $(call rwildcard,$(SRC_DIR),*.java)

# ---------------------------------------------------------------------------
# Tamanhos padrão (sobrescrevíveis via linha de comando)
# Exemplo: make bench-busca SIZES_BUSCA="10000 100000"
# ---------------------------------------------------------------------------
SIZES_BUSCA     ?= 50000 150000 500000
SIZES_ORDENACAO ?= --todos-direto

# Tamanhos reduzidos para testes de sanidade rápidos
TEST_SIZES_BUSCA     := 1000 5000
TEST_SIZES_ORDENACAO := --todos-direto 500 1000

# ---------------------------------------------------------------------------
# .PHONY — Alvos que não correspondem a arquivos reais
# ---------------------------------------------------------------------------
.PHONY: all help compile clean clean-data clean-graphs clean-all \
        bench bench-busca bench-ordenacao bench-eficientes bench-quadraticos \
        test test-busca test-ordenacao \
        tabelas graficos analise \
        verify info open-graphs open-data

# ---------------------------------------------------------------------------
# Alvo padrão: exibe ajuda
# ---------------------------------------------------------------------------
.DEFAULT_GOAL := help

# ===========================================================================
# HELP — Lista todos os comandos disponíveis
# ===========================================================================
help:
	@echo ""
	@echo "======================================================================="
	@echo "  PROJETO EDA/LEDA 2026.2 — Algoritmos de Ordenação e Busca"
	@echo "  Sistema detectado : $(DETECTED_OS)"
	@echo "  Python detectado  : $(PYTHON)"
	@echo "======================================================================="
	@echo ""
	@echo "  AMBIENTE"
	@echo "    make verify            Verifica dependências (Java, Python, pastas)"
	@echo "    make info              Exibe versões de Java e Python instaladas"
	@echo ""
	@echo "  COMPILAÇÃO"
	@echo "    make compile           Compila todos os fontes Java"
	@echo "    make clean             Remove compilados + CSVs + gráficos"
	@echo "    make clean-data        Remove apenas os CSVs de resultado"
	@echo "    make clean-graphs      Remove apenas os gráficos gerados"
	@echo "    make clean-all         Limpeza completa (inclui pasta data/)"
	@echo ""
	@echo "  TESTES DE SANIDADE (rápidos, tamanhos pequenos)"
	@echo "    make test              Testa busca + ordenação com N pequeno"
	@echo "    make test-busca        Testa apenas busca"
	@echo "    make test-ordenacao    Testa apenas ordenação"
	@echo ""
	@echo "  BENCHMARKS (tamanhos completos do projeto)"
	@echo "    make bench             Executa busca + ordenação (todos)"
	@echo "    make bench-busca       Benchmark de busca"
	@echo "    make bench-ordenacao   Benchmark de ordenação (todos algoritmos)"
	@echo "    make bench-eficientes  Benchmark — algoritmos eficientes"
	@echo "    make bench-quadraticos Benchmark — algoritmos quadráticos"
	@echo ""
	@echo "  ANÁLISE DE RESULTADOS"
	@echo "    make tabelas           Gera tabelas Markdown a partir dos CSVs"
	@echo "    make graficos          Gera gráficos PNG (requer matplotlib)"
	@echo "    make analise           Tabelas + gráficos"
	@echo ""
	@echo "  PIPELINE COMPLETO"
	@echo "    make all               compile → bench → analise"
	@echo ""
	@echo "  UTILITÁRIOS"
	@echo "    make open-graphs       Abre a pasta de gráficos no explorador"
	@echo "    make open-data         Abre a pasta de CSVs no explorador"
	@echo ""
	@echo "  TAMANHOS CUSTOMIZADOS:"
	@echo "    make bench-busca SIZES_BUSCA='10000 100000'"
	@echo "    make bench-ordenacao SIZES_ORDENACAO='--eficientes 10000 50000'"
	@echo "======================================================================="
	@echo ""

# ===========================================================================
# VERIFICAÇÃO DE DEPENDÊNCIAS
# ===========================================================================
verify:
	@echo ""
	@echo "======================================================================="
	@echo "  VERIFICANDO DEPENDÊNCIAS E ESTRUTURA DO PROJETO"
	@echo "  Sistema: $(DETECTED_OS)"
	@echo "======================================================================="
	@echo ""
	@echo "--- [1/5] JDK (javac) ---"
	@$(JAVAC) -version 2>&1 && echo "  OK" || \
		(echo "  ERRO: 'javac' nao encontrado. Instale o JDK 11+ e adicione ao PATH." && false)
	@echo ""
	@echo "--- [2/5] JRE (java) ---"
	@$(JAVA) -version 2>&1 && echo "  OK" || \
		(echo "  ERRO: 'java' nao encontrado. Instale o JDK 11+ e adicione ao PATH." && false)
	@echo ""
	@echo "--- [3/5] Python ($(PYTHON)) ---"
	@$(PYTHON) --version 2>&1 && echo "  OK" || \
		echo "  AVISO: Python nao encontrado. Scripts de analise indisponiveis."
	@echo ""
	@echo "--- [4/5] matplotlib (Python) ---"
	@$(PYTHON) -c "import matplotlib; print('  matplotlib', matplotlib.__version__, '- OK')" 2>$(NULL_DEV) || \
		echo "  AVISO: matplotlib nao instalado. Execute: pip install matplotlib"
	@echo ""
	@echo "--- [5/5] Estrutura de diretórios ---"
	@test -d $(SRC_DIR)            && echo "  OK : $(SRC_DIR)"            || echo "  ERRO: $(SRC_DIR) ausente"
	@test -d $(SRC_DIR)/ed/bench   && echo "  OK : $(SRC_DIR)/ed/bench"   || echo "  ERRO: $(SRC_DIR)/ed/bench ausente"
	@test -d $(SRC_DIR)/ed/sorting && echo "  OK : $(SRC_DIR)/ed/sorting" || echo "  ERRO: $(SRC_DIR)/ed/sorting ausente"
	@test -d $(SRC_DIR)/ed/search  && echo "  OK : $(SRC_DIR)/ed/search"  || echo "  ERRO: $(SRC_DIR)/ed/search ausente"
	@test -d $(SRC_DIR)/ed/util    && echo "  OK : $(SRC_DIR)/ed/util"    || echo "  ERRO: $(SRC_DIR)/ed/util ausente"
	@test -d $(ANALYSIS_DIR)       && echo "  OK : $(ANALYSIS_DIR)"       || echo "  ERRO: $(ANALYSIS_DIR) ausente"
	@echo ""
	@echo "  Fontes Java encontrados: $(words $(JAVA_SOURCES)) arquivo(s)"
	@echo ""
	@echo "=== Verificacao concluida ==="
	@echo ""

# ===========================================================================
# INFORMAÇÕES DO AMBIENTE
# ===========================================================================
info:
	@echo ""
	@echo "=== Informações do Ambiente ==="
	@echo "  SO          : $(DETECTED_OS)"
	@echo "  Python      : $(PYTHON)"
	@echo "  SRC_DIR     : $(SRC_DIR)"
	@echo "  OUT_DIR     : $(OUT_DIR)"
	@echo "  DATA_DIR    : $(DATA_DIR)"
	@echo ""
	@echo "Java runtime:"
	@$(JAVA) -version 2>&1
	@echo ""
	@echo "Java compiler:"
	@$(JAVAC) -version 2>&1
	@echo ""
	@echo "Python:"
	@$(PYTHON) --version 2>&1 || echo "  nao encontrado"
	@echo ""
	@echo "Fontes Java: $(words $(JAVA_SOURCES)) arquivo(s)"
	@echo "================================"
	@echo ""

# ===========================================================================
# COMPILAÇÃO
# ===========================================================================
compile: _check_javac
	@echo ""
	@echo "=== Compilando $(words $(JAVA_SOURCES)) fontes Java ==="
	@$(MKDIR_P) $(OUT_DIR)
	$(JAVAC) $(JAVAC_FLAGS) -d $(OUT_DIR) $(JAVA_SOURCES)
	@echo "=== Compilacao concluida! Classes em: $(OUT_DIR)/ ==="
	@echo ""

# Marca o OUT_DIR como indicador de compilacao concluida
$(OUT_DIR): compile

# ===========================================================================
# TESTES DE SANIDADE (rápidos — tamanhos pequenos)
# ===========================================================================
test: compile test-busca test-ordenacao
	@echo ""
	@echo "======================================================================="
	@echo "  TESTE DE SANIDADE CONCLUIDO!"
	@echo "  Todos os algoritmos executaram e passaram na verificacao de corretude."
	@echo "======================================================================="
	@echo ""

test-busca: compile
	@echo ""
	@echo "=== [TESTE] Busca — tamanhos: $(TEST_SIZES_BUSCA) ==="
	@$(MKDIR_P) $(DATA_DIR)
	$(JAVA) -cp $(OUT_DIR) $(JVM_FLAGS) $(CLASS_BENCH_BUSCA) $(TEST_SIZES_BUSCA)
	@echo "=== [TESTE] Busca OK ==="
	@echo ""

test-ordenacao: compile
	@echo ""
	@echo "=== [TESTE] Ordenacao — tamanhos: $(TEST_SIZES_ORDENACAO) ==="
	@$(MKDIR_P) $(DATA_DIR)
	$(JAVA) -cp $(OUT_DIR) $(JVM_FLAGS) $(CLASS_BENCH_ORDENACAO) $(TEST_SIZES_ORDENACAO)
	@echo "=== [TESTE] Ordenacao OK ==="
	@echo ""

# ===========================================================================
# BENCHMARKS (tamanhos completos do projeto)
# ===========================================================================
bench: compile bench-busca bench-ordenacao
	@echo ""
	@echo "======================================================================="
	@echo "  SUITE COMPLETA DE BENCHMARKS CONCLUIDA!"
	@echo "  Resultados salvos em: $(DATA_DIR)/"
	@echo "  Execute 'make analise' para gerar tabelas e graficos."
	@echo "======================================================================="
	@echo ""

bench-busca: compile
	@echo ""
	@echo "=== Benchmark de Busca — N: $(SIZES_BUSCA) ==="
	@$(MKDIR_P) $(DATA_DIR)
	$(JAVA) -cp $(OUT_DIR) $(JVM_FLAGS) $(CLASS_BENCH_BUSCA) $(SIZES_BUSCA)
	@echo "=== Benchmark de Busca concluido! ==="
	@echo ""

bench-ordenacao: compile
	@echo ""
	@echo "=== Benchmark de Ordenacao (todos os algoritmos) ==="
	@$(MKDIR_P) $(DATA_DIR)
	$(JAVA) -cp $(OUT_DIR) $(JVM_FLAGS) $(CLASS_BENCH_ORDENACAO) $(SIZES_ORDENACAO)
	@echo "=== Benchmark de Ordenacao concluido! ==="
	@echo ""

bench-eficientes: compile
	@echo ""
	@echo "=== Benchmark — Algoritmos Eficientes ==="
	@$(MKDIR_P) $(DATA_DIR)
	$(JAVA) -cp $(OUT_DIR) $(JVM_FLAGS) $(CLASS_BENCH_ORDENACAO) --eficientes
	@echo "=== Concluido! ==="
	@echo ""

bench-quadraticos: compile
	@echo ""
	@echo "=== Benchmark — Algoritmos Quadraticos ==="
	@$(MKDIR_P) $(DATA_DIR)
	$(JAVA) -cp $(OUT_DIR) $(JVM_FLAGS) $(CLASS_BENCH_ORDENACAO) --quadraticos
	@echo "=== Concluido! ==="
	@echo ""

# ===========================================================================
# ANÁLISE DE RESULTADOS
# ===========================================================================
tabelas: _check_python _check_csv
	@echo ""
	@echo "=== Gerando tabelas Markdown ==="
	$(PYTHON) $(ANALYSIS_DIR)/gerar_tabelas.py
	@echo "=== Tabelas exibidas acima ==="
	@echo ""

graficos: _check_python _check_matplotlib _check_csv
	@echo ""
	@echo "=== Gerando graficos PNG ==="
	@$(MKDIR_P) $(GRAPHS_DIR)
	$(PYTHON) $(ANALYSIS_DIR)/gerar_graficos.py
	@echo "=== Graficos gerados em: $(GRAPHS_DIR)/ ==="
	@echo ""

analise: tabelas graficos
	@echo "=== Analise completa concluida ==="
	@echo ""

# ===========================================================================
# PIPELINE COMPLETO
# ===========================================================================
all: compile bench analise
	@echo ""
	@echo "======================================================================="
	@echo "  PIPELINE COMPLETO CONCLUIDO!"
	@echo "  CSVs     : $(DATA_DIR)/"
	@echo "  Graficos : $(GRAPHS_DIR)/"
	@echo "======================================================================="
	@echo ""

# ===========================================================================
# UTILITÁRIOS — Abrir pastas
# ===========================================================================
open-graphs:
	@echo "Abrindo: $(GRAPHS_DIR)"
	@$(OPEN_CMD) $(GRAPHS_DIR) 2>$(NULL_DEV) || \
		echo "  Nao foi possivel abrir automaticamente. Caminho: $(GRAPHS_DIR)"

open-data:
	@echo "Abrindo: $(DATA_DIR)"
	@$(OPEN_CMD) $(DATA_DIR) 2>$(NULL_DEV) || \
		echo "  Nao foi possivel abrir automaticamente. Caminho: $(DATA_DIR)"

# ===========================================================================
# LIMPEZA
# ===========================================================================
clean: clean-graphs
	@echo "=== Removendo arquivos compilados ==="
	@$(RM_RF) $(OUT_DIR)
	@echo "  $(OUT_DIR)/ removido."
	@echo ""

clean-data:
	@echo "=== Removendo CSVs de resultado ==="
	@$(RM_RF) $(DATA_DIR)
	@echo "  $(DATA_DIR)/ removido."
	@echo ""

clean-graphs:
	@echo "=== Removendo graficos ==="
	@$(RM_RF) $(GRAPHS_DIR)
	@echo "  $(GRAPHS_DIR)/ removido."
	@echo ""

clean-all: clean clean-data
	@echo "=== Limpeza completa concluida ==="
	@echo ""

# ===========================================================================
# ALVOS AUXILIARES INTERNOS (prefixo _ = uso interno)
# ===========================================================================
.PHONY: _check_javac _check_python _check_matplotlib _check_csv

_check_javac:
	@command -v $(JAVAC) >$(NULL_DEV) 2>&1 || \
		(echo "ERRO: 'javac' nao encontrado. Instale o JDK 11+ e adicione ao PATH." && false)

_check_python:
	@command -v $(PYTHON) >$(NULL_DEV) 2>&1 || \
		(echo "ERRO: Python nao encontrado. Instale Python 3 e adicione ao PATH." && false)

_check_matplotlib:
	@$(PYTHON) -c "import matplotlib" 2>$(NULL_DEV) || \
		(echo "ERRO: matplotlib nao instalado. Execute: pip install matplotlib" && false)

_check_csv:
	@(test -f $(DATA_DIR)/resultados_ordenacao_agregado.csv || \
	  test -f $(DATA_DIR)/resultados_busca_agregado.csv) || \
		(echo "AVISO: Nenhum CSV encontrado em $(DATA_DIR)/. Execute 'make bench' primeiro." )
