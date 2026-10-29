"""
Script para processar os resultados CSV gerados pelo Benchmark Java
e gerar tabelas formatadas em Markdown e texto para o relatório da Unidade 1.
"""
import csv
import os
import sys

def encontrar_arquivo_csv(nome_arquivo):
    locais = [
        os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "data", nome_arquivo),
        os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))), "data", nome_arquivo),
        os.path.join("data", nome_arquivo),
        os.path.join("Algoritmos-de-Ordenacao", "data", nome_arquivo)
    ]
    for loc in locais:
        if os.path.exists(loc):
            return loc
    return locais[0]

def carregar_dados_agregados(caminho_csv):
    if not os.path.exists(caminho_csv):
        print(f"[!] Arquivo não encontrado: {caminho_csv}")
        return []
    
    dados = []
    with open(caminho_csv, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            dados.append(row)
    return dados

def formatar_tabela_markdown(cabecalhos, linhas):
    larguras = [len(h) for h in cabecalhos]
    for linha in linhas:
        for i, val in enumerate(linha):
            larguras[i] = max(larguras[i], len(str(val)))
    
    # Monta cabeçalho
    header_str = " | ".join(h.ljust(larguras[i]) for i, h in enumerate(cabecalhos))
    sep_str = "-|-".join("-" * larguras[i] for i in range(len(cabecalhos)))
    
    res = [f"| {header_str} |", f"| {sep_str} |"]
    for linha in linhas:
        linha_str = " | ".join(str(val).ljust(larguras[i]) for i, val in enumerate(linha))
        res.append(f"| {linha_str} |")
    return "\n".join(res)

def gerar_tabelas_ordenacao(dados):
    if not dados:
        return
    print("\n" + "="*80)
    print("                     TABELAS DE ORDENAÇÃO (TEMPO EM MS)")
    print("="*80)
    
    # Agrupa por Tamanho e Cenário
    cenarios = sorted(list(set(d['Cenario'] for d in dados)))
    tamanhos = sorted(list(set(int(d['Tamanho']) for d in dados)))
    algoritmos = sorted(list(set(d['Algoritmo'] for d in dados)))
    
    for tam in tamanhos:
        print(f"\n### Tamanho N = {tam:,}\n")
        cabecalhos = ["Algoritmo"] + [c for c in cenarios]
        linhas = []
        
        for alg in algoritmos:
            linha = [alg]
            for cenario in cenarios:
                match = [d for d in dados if int(d['Tamanho']) == tam and d['Cenario'] == cenario and d['Algoritmo'] == alg]
                if match:
                    d = match[0]
                    if d.get('Status') == 'SUCESSO':
                        med = float(d['Media_ms'])
                        dp = float(d['DesvioPadrao_ms'])
                        linha.append(f"{med:.4f} ± {dp:.4f}")
                    else:
                        linha.append("FALHA")
                else:
                    linha.append("N/A")
            linhas.append(linha)
        
        print(formatar_tabela_markdown(cabecalhos, linhas))
        print("\n")

def gerar_tabelas_busca(dados):
    if not dados:
        return
    print("\n" + "="*80)
    print("                   TABELAS DE BUSCA - CHAVE -1 (TEMPO EM MS)")
    print("="*80 + "\n")
    
    tamanhos = sorted(list(set(int(d['Tamanho']) for d in dados)))
    algoritmos = sorted(list(set(d['Algoritmo'] for d in dados)))
    
    cabecalhos = ["Algoritmo"] + [f"N = {t:,}" for t in tamanhos]
    linhas = []
    
    for alg in algoritmos:
        linha = [alg]
        for tam in tamanhos:
            match = [d for d in dados if int(d['Tamanho']) == tam and d['Algoritmo'] == alg]
            if match:
                d = match[0]
                if d.get('Status') == 'SUCESSO':
                    med = float(d['Media_ms'])
                    dp = float(d['DesvioPadrao_ms'])
                    linha.append(f"{med:.6f} ± {dp:.6f}")
                else:
                    linha.append("FALHA")
            else:
                linha.append("N/A")
        linhas.append(linha)
    
    print(formatar_tabela_markdown(cabecalhos, linhas))
    print("\n")

def main():
    csv_ord = encontrar_arquivo_csv("resultados_ordenacao_agregado.csv")
    csv_busca = encontrar_arquivo_csv("resultados_busca_agregado.csv")
    
    print("Lendo dados de ordenação:", csv_ord)
    dados_ord = carregar_dados_agregados(csv_ord)
    gerar_tabelas_ordenacao(dados_ord)
    
    print("Lendo dados de busca:", csv_busca)
    dados_busca = carregar_dados_agregados(csv_busca)
    gerar_tabelas_busca(dados_busca)

if __name__ == "__main__":
    main()
