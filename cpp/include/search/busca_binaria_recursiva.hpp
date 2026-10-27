#ifndef BUSCA_BINARIA_ITERATIVA_HPP
#define BUSCA_BINARIA_ITERATIVA_HPP
#pragma once
#include <vector>


using namespace std;

namespace ed {
    inline int buscaBinaria(vector<int> arr, int n, int i) {
        if (arr[i]  == n) return i;
        buscaBinaria(arr, n, ++i);
    }

    inline int busca_binaria(vector<int> arr, int n) {
        if (arr.empty()) return -1;
        buscaBinaria(arr, n, 0);
    }
}



#endif
