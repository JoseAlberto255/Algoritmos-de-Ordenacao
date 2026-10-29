@echo off
REM =============================================================================
REM setup.bat — Script de configuracao do ambiente (Windows)
REM Projeto: Algoritmos de Ordenacao e Busca (EDA / LEDA 2026.2)
REM =============================================================================
REM Uso: Execute este script a partir da raiz do repositorio:
REM        scripts\setup.bat
REM =============================================================================

setlocal EnableDelayedExpansion
chcp 65001 >nul 2>&1

echo.
echo =======================================================================
echo   SETUP ^- Projeto EDA/LEDA 2026.2
echo   Algoritmos de Ordenacao e Busca
echo =======================================================================
echo.

set "JAVA_OK=false"
set "PYTHON_OK=false"
set "MATPLOTLIB_OK=false"
set "STRUCT_OK=true"

REM ---------------------------------------------------------------------------
REM 1. Verificar Java (JDK)
REM ---------------------------------------------------------------------------
echo --- [1/4] Verificando Java (JDK) ---

where javac >nul 2>&1
if %ERRORLEVEL% == 0 (
    for /f "tokens=*" %%v in ('javac -version 2^>^&1') do set "JAVAC_VER=%%v"
    echo   [OK] Java instalado: !JAVAC_VER!
    set "JAVA_OK=true"
) else (
    echo   [ERRO] 'javac' nao encontrado no PATH.
    echo.
    echo   Instale o JDK 11 ou superior:
    echo     ^> Baixe em: https://adoptium.net  ^(recomendado^)
    echo     ^> Ou use o winget: winget install EclipseAdoptium.Temurin.17.JDK
    echo.
    echo   Apos instalar, adicione ao PATH:
    echo     C:\Program Files\Eclipse Adoptium\jdk-XX\bin
    echo.
)

REM ---------------------------------------------------------------------------
REM 2. Verificar Python
REM ---------------------------------------------------------------------------
echo.
echo --- [2/4] Verificando Python ---

where python >nul 2>&1
if %ERRORLEVEL% == 0 (
    for /f "tokens=*" %%v in ('python --version 2^>^&1') do set "PY_VER=%%v"
    echo   [OK] Python encontrado: !PY_VER!
    set "PYTHON_CMD=python"
    set "PYTHON_OK=true"
) else (
    where python3 >nul 2>&1
    if %ERRORLEVEL% == 0 (
        for /f "tokens=*" %%v in ('python3 --version 2^>^&1') do set "PY_VER=%%v"
        echo   [OK] Python encontrado: !PY_VER! ^(comando: python3^)
        set "PYTHON_CMD=python3"
        set "PYTHON_OK=true"
    ) else (
        echo   [AVISO] Python nao encontrado no PATH.
        echo.
        echo   Instale Python 3:
        echo     ^> Baixe em: https://www.python.org/downloads/
        echo     ^> Ou use o winget: winget install Python.Python.3
        echo     ^> Marque "Add Python to PATH" durante a instalacao!
        echo.
    )
)

REM ---------------------------------------------------------------------------
REM 3. Verificar / Instalar matplotlib
REM ---------------------------------------------------------------------------
echo.
echo --- [3/4] Verificando matplotlib ---

if "!PYTHON_OK!" == "true" (
    !PYTHON_CMD! -c "import matplotlib" >nul 2>&1
    if %ERRORLEVEL% == 0 (
        for /f "tokens=*" %%v in ('!PYTHON_CMD! -c "import matplotlib; print(matplotlib.__version__)"') do set "MPL_VER=%%v"
        echo   [OK] matplotlib instalado: v!MPL_VER!
        set "MATPLOTLIB_OK=true"
    ) else (
        echo   [AVISO] matplotlib nao encontrado. Instalando...
        !PYTHON_CMD! -m pip install matplotlib --quiet
        if %ERRORLEVEL% == 0 (
            echo   [OK] matplotlib instalado com sucesso!
            set "MATPLOTLIB_OK=true"
        ) else (
            echo   [ERRO] Falha ao instalar matplotlib.
            echo   Tente manualmente: pip install matplotlib
        )
    )
) else (
    echo   [AVISO] Pulando verificacao do matplotlib ^(Python nao encontrado^).
)

REM ---------------------------------------------------------------------------
REM 4. Verificar estrutura do projeto
REM ---------------------------------------------------------------------------
echo.
echo --- [4/4] Verificando estrutura do projeto ---

set "DIRS[0]=java\src\ed\bench"
set "DIRS[1]=java\src\ed\sorting"
set "DIRS[2]=java\src\ed\search"
set "DIRS[3]=java\src\ed\util"
set "DIRS[4]=analysis"

for /L %%i in (0,1,4) do (
    if exist "!DIRS[%%i]!\" (
        echo   [OK] !DIRS[%%i]!
    ) else (
        echo   [ERRO] Pasta ausente: !DIRS[%%i]!
        set "STRUCT_OK=false"
    )
)

REM ---------------------------------------------------------------------------
REM Verificar se GNU Make esta disponivel
REM ---------------------------------------------------------------------------
echo.
echo --- [EXTRA] Verificando GNU Make ---
where make >nul 2>&1
if %ERRORLEVEL% == 0 (
    for /f "tokens=*" %%v in ('make --version 2^>^&1 ^| findstr /i "make"') do (
        echo   [OK] %%v
        goto :make_found
    )
) else (
    echo   [AVISO] 'make' nao encontrado no PATH.
    echo.
    echo   Para usar o Makefile no Windows, instale uma das opcoes:
    echo     1. Git for Windows ^(inclui Git Bash com make^):
    echo        https://git-scm.com/download/win
    echo     2. Chocolatey: choco install make
    echo     3. winget:     winget install GnuWin32.Make
    echo     4. WSL ^(Windows Subsystem for Linux^):
    echo        wsl --install
    echo.
    echo   Apos instalar, execute os comandos 'make' pelo Git Bash.
)
:make_found

REM ---------------------------------------------------------------------------
REM Resumo Final
REM ---------------------------------------------------------------------------
echo.
echo =======================================================================
echo   RESUMO DO SETUP
echo =======================================================================

if "!JAVA_OK!" == "true" (
    echo   [OK]    Java        : OK
) else (
    echo   [ERRO]  Java        : NAO INSTALADO ^- benchmarks indisponiveis
)

if "!PYTHON_OK!" == "true" (
    echo   [OK]    Python      : OK
) else (
    echo   [AVISO] Python      : NAO ENCONTRADO ^- analise de graficos indisponivel
)

if "!MATPLOTLIB_OK!" == "true" (
    echo   [OK]    matplotlib  : OK
) else (
    echo   [AVISO] matplotlib  : NAO DISPONIVEL
)

if "!STRUCT_OK!" == "true" (
    echo   [OK]    Estrutura   : OK
) else (
    echo   [ERRO]  Estrutura   : INCOMPLETA
)

echo.
if "!JAVA_OK!" == "true" (
    echo   Ambiente pronto! Para comecar ^(via Git Bash^):
    echo.
    echo     make help              ^# Ver todos os comandos
    echo     make verify            ^# Verificar dependencias
    echo     make test              ^# Teste rapido de sanidade
    echo     make bench             ^# Benchmarks completos
    echo     make all               ^# Pipeline completo
)
echo =======================================================================
echo.

endlocal
pause
