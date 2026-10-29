"""
Script para geração automática de gráficos a partir dos resultados dos benchmarks CSV.
Requer: matplotlib (instalar com 'pip install matplotlib' se necessário).
"""
import csv
import os
import sys

def carregar_dados(caminho_csv):
    if not os.path.exists(caminho_csv):
        return []
    dados = []
    with open(caminho_csv, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            dados.append(row)
    return dados

def gerar_graficos():
    try:
        import matplotlib.pyplot as plt
        import numpy as np
    except ImportError:
        print("[!] Biblioteca 'matplotlib' não encontrada.")
        print("    Para gerar os gráficos do relatório, instale com: pip install matplotlib")
        return

    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    output_dir = os.path.join(base_dir, "analysis", "graficos")
    os.makedirs(output_dir, exist_ok=True)

    # 1. Gráficos de Busca
    csv_busca = os.path.join(base_dir, "data", "resultados_busca_agregado.csv")
    dados_busca = carregar_dados(csv_busca)
    if dados_busca:
        tamanhos = sorted(list(set(int(d['Tamanho']) for d in dados_busca)))
        algoritmos = sorted(list(set(d['Algoritmo'] for d in dados_busca)))

        plt.figure(figsize=(10, 6))
        for alg in algoritmos:
            tempos = []
            for t in tamanhos:
                m = [d for d in dados_busca if int(d['Tamanho']) == t and d['Algoritmo'] == alg and d.get('Status') == 'SUCESSO']
                tempos.append(float(m[0]['Media_ms']) if m else None)
            plt.plot(tamanhos, tempos, marker='o', label=alg)

        plt.title("Comparativo de Desempenho dos Algoritmos de Busca (Chave Ausente = -1)")
        plt.xlabel("Tamanho do Vetor (N)")
        plt.ylabel("Tempo Médio de Execução (ms)")
        plt.yscale('log')
        plt.grid(True, which="both", ls="--", alpha=0.5)
        plt.legend()
        plt.tight_layout()
        caminho_img_busca = os.path.join(output_dir, "comparativo_busca_log.png")
        plt.savefig(caminho_img_busca, dpi=300)
        plt.close()
        print(f"[OK] Gráfico de busca gerado: {caminho_img_busca}")

    # 2. Gráficos de Ordenação
    csv_ord = os.path.join(base_dir, "data", "resultados_ordenacao_agregado.csv")
    dados_ord = carregar_dados(csv_ord)
    if dados_ord:
        cenarios = sorted(list(set(d['Cenario'] for d in dados_ord)))
        tamanhos = sorted(list(set(int(d['Tamanho']) for d in dados_ord)))

        for cenario in cenarios:
            plt.figure(figsize=(12, 7))
            dados_cenario = [d for d in dados_ord if d['Cenario'] == cenario and d.get('Status') == 'SUCESSO']
            algoritmos = sorted(list(set(d['Algoritmo'] for d in dados_cenario)))

            for alg in algoritmos:
                tams_alg = []
                tempos = []
                for t in tamanhos:
                    m = [d for d in dados_cenario if int(d['Tamanho']) == t and d['Algoritmo'] == alg]
                    if m:
                        tams_alg.append(t)
                        tempos.append(float(m[0]['Media_ms']))
                if tempos:
                    plt.plot(tams_alg, tempos, marker='s', label=alg)

            plt.title(f"Desempenho de Ordenação - Cenário: {cenario}")
            plt.xlabel("Tamanho do Vetor (N)")
            plt.ylabel("Tempo Médio (ms) - Escala Logarítmica")
            plt.yscale('log')
            plt.grid(True, which="both", ls="--", alpha=0.5)
            plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
            plt.tight_layout()

            nome_arquivo = f"ordenacao_{cenario.lower().replace(' ', '_').replace('~', '').replace('(', '').replace(')', '').replace('%', '')}.png"
            caminho_img = os.path.join(output_dir, nome_arquivo)
            plt.savefig(caminho_img, dpi=300)
            plt.close()
            print(f"[OK] Gráfico gerado para {cenario}: {caminho_img}")

if __name__ == "__main__":
    gerar_graficos()
