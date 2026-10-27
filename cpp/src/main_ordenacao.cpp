#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include "sorting/sorting.hpp"

using namespace std;

struct Algoritmo {
    string nome;
    void (*func)(vector<int>&);   // mesma assinatura dos algoritmos
};

int main() {
    vector<int> original = {5, 3, 8, 1, 9, 2};
    stringstream para_ordenar;


    vector<Algoritmo> algoritmos = {
        {"BubbleSort", ed::bubble_sort},
        {"BubbleSortOtimizado", ed::bubble_sort_otimizado},
        {"InserctionSort", ed::inserction_sort},
        {"SelectionSort", ed::selection_sort},
        {"SelectionSortEstavel", ed::selection_sort_estavel},
        {"CountingSort", ed::counting_sort},
        {"MergeSort", ed::merge_sort},
        {"QuickSort", ed::quick_sort},
        {"QuickSortShuffle", ed::quick_sort_shuffle}
        // ... demais algoritmos
    };

    for (int v : original) para_ordenar << v;

    cout << "Original: " + para_ordenar.str() + "\n";


    for (auto& a : algoritmos) {
        vector<int> copia = original;            // cópia fora da medição
        a.func(copia);

        cout << a.nome << ":";
        for (int x : copia) cout << " " << x;
        cout << "\n";
    }
    return 0;
}
