#ifndef BUSCA_BINARIA_RECURSIVA_HPP
#define BUSCA_BINARIA_RECURSIVA_HPP
#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline int buscaBinariaRecursiva(vector<int>& arr, int n, int i, int f) {
        if (i > f) return -1;
        int m = i + (f - i) / 2;
        if (n == arr[m]) return arr[m];
        if (n > arr[m]) return buscaBinariaRecursiva(arr, n, m + 1, f);
        return buscaBinariaRecursiva(arr, n, i, m - 1);
    }

    inline int busca_binaria_recursiva(vector<int>& arr, int n) {
        if (arr.empty()) return -1;
        return buscaBinariaRecursiva(arr, n, 0, (int)arr.size() - 1);
    }
}



#endif
