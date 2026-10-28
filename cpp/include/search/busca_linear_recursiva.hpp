#ifndef BUSCA_LINEAR_RECURSIVA_HPP
#define BUSCA_LINEAR_RECURSIVA_HPP
#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline int buscaLinearRecursiva(vector<int>& arr, int n, int i) {
        if (i >= (int)arr.size()) return -1;
        if (n == arr[i]) return arr[i];
        return buscaLinearRecursiva(arr, n, ++i);
    }

    inline int busca_linear_recursiva(vector<int>& arr, int n) {
        if (arr.empty()) return -1;

        return buscaLinearRecursiva(arr, n, 0);
    }
}



#endif
