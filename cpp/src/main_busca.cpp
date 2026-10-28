#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include "search/search.hpp"

using namespace std;

struct Algoritmo {
    string nome;
    int (*func)(vector<int>&, int n);   // mesma assinatura dos algoritmos
};

int main() {
    vector<int> original = {39, 68, 252, 335, 457, 529, 544, 610, 834, 969};
    stringstream array;


    vector<Algoritmo> algoritmos = {
        {"BuscaLinearIterativa", ed::busca_linear_iterativa},
        {"BuscaLinearRecursiva", ed::busca_linear_recursiva},
        {"BuscaLinearDuasPontas", ed::busca_linear_de_duas_pontos},
        {"BuscaBinariaIterativa", ed::busca_binaria_iterativa},
        {"BuscaBinariaRecursiva", ed::busca_binaria_recursiva}

        // ... demais algoritmos
    };


    for (int v : original) array << v << " ";

    cout << "Original: " + array.str() + "\n";


    for (auto& a : algoritmos) {
        vector<int> copia = original;            // cópia fora da medição
        int x = a.func(copia, 39);

        cout << a.nome << ":";
        cout << " " << x;
        cout << "\n";
    }
    return 0;
}
