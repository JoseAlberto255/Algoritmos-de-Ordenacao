#!/usr/bin/env bash
# =============================================================================
# setup.sh — Script de configuração do ambiente (Linux / macOS)
# Projeto: Algoritmos de Ordenação e Busca (EDA / LEDA 2026.2)
# =============================================================================
# Uso:
#   chmod +x scripts/setup.sh
#   ./scripts/setup.sh
# =============================================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

OK="${GREEN}[OK]${NC}"
WARN="${YELLOW}[AVISO]${NC}"
ERR="${RED}[ERRO]${NC}"
INFO="${BLUE}[INFO]${NC}"

echo ""
echo "======================================================================="
echo "  SETUP — Projeto EDA/LEDA 2026.2"
echo "  Algoritmos de Ordenação e Busca"
echo "======================================================================="
echo ""

# ---------------------------------------------------------------------------
# 1. Verificar Java (JDK)
# ---------------------------------------------------------------------------
echo "--- [1/4] Verificando Java (JDK) ---"

if command -v javac &>/dev/null; then
    JAVA_VERSION=$(javac -version 2>&1)
    echo -e "$OK Java instalado: $JAVA_VERSION"
else
    echo -e "$ERR 'javac' nao encontrado."
    echo "  Instale o JDK 11 ou superior:"
    echo ""
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "  macOS:  brew install openjdk@17"
        echo "          ou baixe em: https://adoptium.net"
    else
        echo "  Ubuntu/Debian:  sudo apt install default-jdk"
        echo "  Fedora/RHEL:    sudo dnf install java-17-openjdk-devel"
        echo "  Arch:           sudo pacman -S jdk17-openjdk"
        echo "  Ou baixe em:    https://adoptium.net"
    fi
    echo ""
    JAVA_MISSING=true
fi

# ---------------------------------------------------------------------------
# 2. Verificar Python 3
# ---------------------------------------------------------------------------
echo ""
echo "--- [2/4] Verificando Python 3 ---"

PYTHON_CMD=""
if command -v python3 &>/dev/null; then
    PYTHON_CMD="python3"
elif command -v python &>/dev/null; then
    PY_VERSION=$(python --version 2>&1)
    if echo "$PY_VERSION" | grep -q "Python 3"; then
        PYTHON_CMD="python"
    fi
fi

if [ -n "$PYTHON_CMD" ]; then
    PY_VER=$($PYTHON_CMD --version 2>&1)
    echo -e "$OK Python encontrado: $PY_VER (comando: $PYTHON_CMD)"
else
    echo -e "$ERR Python 3 nao encontrado."
    echo "  Instale Python 3:"
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "  macOS:  brew install python3"
    else
        echo "  Ubuntu/Debian:  sudo apt install python3 python3-pip"
        echo "  Fedora/RHEL:    sudo dnf install python3 python3-pip"
        echo "  Arch:           sudo pacman -S python python-pip"
    fi
    PYTHON_MISSING=true
fi

# ---------------------------------------------------------------------------
# 3. Verificar / Instalar pip e matplotlib
# ---------------------------------------------------------------------------
echo ""
echo "--- [3/4] Verificando matplotlib ---"

if [ -n "$PYTHON_CMD" ]; then
    if $PYTHON_CMD -c "import matplotlib" &>/dev/null; then
        MPLOTLIB_VERSION=$($PYTHON_CMD -c "import matplotlib; print(matplotlib.__version__)")
        echo -e "$OK matplotlib instalado: v$MPLOTLIB_VERSION"
    else
        echo -e "$WARN matplotlib nao encontrado. Instalando..."

        # Verifica pip
        if $PYTHON_CMD -m pip --version &>/dev/null; then
            $PYTHON_CMD -m pip install matplotlib --quiet && \
                echo -e "$OK matplotlib instalado com sucesso!" || \
                echo -e "$ERR Falha ao instalar matplotlib. Tente: pip install matplotlib"
        else
            echo -e "$ERR pip nao encontrado."
            echo "  Instale pip primeiro:"
            echo "    curl https://bootstrap.pypa.io/get-pip.py | $PYTHON_CMD"
        fi
    fi
else
    echo -e "$WARN Pulando verificacao do matplotlib (Python nao encontrado)."
fi

# ---------------------------------------------------------------------------
# 4. Verificar estrutura do projeto
# ---------------------------------------------------------------------------
echo ""
echo "--- [4/4] Verificando estrutura do projeto ---"

DIRS=(
    "java/src/ed/bench"
    "java/src/ed/sorting"
    "java/src/ed/search"
    "java/src/ed/util"
    "analysis"
)

ALL_OK=true
for dir in "${DIRS[@]}"; do
    if [ -d "$dir" ]; then
        echo -e "  $OK $dir"
    else
        echo -e "  ${RED}[ERRO]${NC} Pasta ausente: $dir"
        ALL_OK=false
    fi
done

# ---------------------------------------------------------------------------
# Resumo final
# ---------------------------------------------------------------------------
echo ""
echo "======================================================================="
echo "  RESUMO DO SETUP"
echo "======================================================================="

if [ "${JAVA_MISSING:-false}" = true ]; then
    echo -e "  $ERR Java: NAO INSTALADO — benchmarks nao poderao ser executados"
else
    echo -e "  $OK Java: OK"
fi

if [ "${PYTHON_MISSING:-false}" = true ]; then
    echo -e "  $WARN Python: NAO ENCONTRADO — analise de graficos indisponivel"
else
    echo -e "  $OK Python ($PYTHON_CMD): OK"
fi

if [ "$ALL_OK" = true ]; then
    echo -e "  $OK Estrutura do projeto: OK"
else
    echo -e "  $ERR Estrutura do projeto: INCOMPLETA"
fi

echo ""
if [ "${JAVA_MISSING:-false}" = false ]; then
    echo "  Ambiente pronto! Para comecar:"
    echo ""
    echo "    make help              # Ver todos os comandos"
    echo "    make verify            # Verificar dependencias"
    echo "    make test              # Teste rapido de sanidade"
    echo "    make bench             # Benchmarks completos"
    echo "    make all               # Pipeline completo"
fi
echo "======================================================================="
echo ""
