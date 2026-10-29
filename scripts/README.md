# Scripts de Setup e Automação

Esta pasta contém os scripts auxiliares de configuração do ambiente.  
O **`Makefile` principal** está na raiz do repositório.

---

## Configuração inicial do ambiente

### Linux / macOS

```bash
chmod +x scripts/setup.sh
./scripts/setup.sh
```

### Windows

Execute pelo **Explorador de Arquivos** (duplo clique) ou pelo **Prompt de Comando**:

```bat
scripts\setup.bat
```

Os scripts verificam e instalam automaticamente:
- JDK (Java) — necessário para compilar e rodar os benchmarks
- Python 3 — necessário para gerar tabelas e gráficos
- `matplotlib` — necessário para gerar os gráficos PNG

---

## Usando o Makefile

> **Pré-requisito no Windows:** GNU Make via [Git Bash](https://git-scm.com/download/win), [Chocolatey](https://chocolatey.org/) (`choco install make`) ou [WSL](https://learn.microsoft.com/pt-br/windows/wsl/install).

Execute a partir da **raiz do repositório**:

| Comando                  | O que faz                                          |
|--------------------------|----------------------------------------------------|
| `make help`              | Lista todos os comandos disponíveis                |
| `make verify`            | Verifica dependências e estrutura do projeto       |
| `make info`              | Exibe versões de Java e Python instaladas          |
| `make compile`           | Compila todos os fontes Java                       |
| `make test`              | Teste rápido de sanidade (tamanhos pequenos)       |
| `make bench`             | Benchmarks completos (busca + ordenação)           |
| `make bench-busca`       | Somente benchmark de busca                         |
| `make bench-ordenacao`   | Somente benchmark de ordenação (todos algoritmos)  |
| `make bench-eficientes`  | Benchmark dos algoritmos eficientes                |
| `make bench-quadraticos` | Benchmark dos algoritmos quadráticos               |
| `make tabelas`           | Gera tabelas Markdown a partir dos CSVs            |
| `make graficos`          | Gera gráficos PNG (requer `matplotlib`)            |
| `make analise`           | Tabelas + gráficos                                 |
| `make all`               | Pipeline completo: compile → bench → analise       |
| `make clean`             | Remove compilados e gráficos                       |
| `make clean-all`         | Limpeza total (compilados + CSVs + gráficos)       |

### Tamanhos customizados

```bash
# Benchmark de busca com tamanhos específicos
make bench-busca SIZES_BUSCA="10000 100000 1000000"

# Benchmark de ordenação com flag específica
make bench-ordenacao SIZES_ORDENACAO="--eficientes 10000 50000"
```

---

## Resultados gerados

Após executar os benchmarks:

| Local                          | Conteúdo                              |
|--------------------------------|---------------------------------------|
| `data/`                        | Arquivos CSV com todos os resultados  |
| `analysis/graficos/`           | Gráficos PNG comparativos             |

Para abrir as pastas diretamente pelo terminal:

```bash
make open-data      # Abre pasta de CSVs
make open-graphs    # Abre pasta de gráficos
```
