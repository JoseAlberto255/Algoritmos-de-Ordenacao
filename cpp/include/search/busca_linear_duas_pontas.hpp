#ifndef BUSCA_LINEAR_DUAS_PONTAS_HPP
#define BUSCA_LINEAR_DUAS_PONTAS_HPP
#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline int busca_linear_de_duas_pontos(vector<int>& arr, int n) {
        int t = arr.size();
        for (int i = 0; i < t; i++) {
            if (arr[i] == n) return arr[i];
            if (arr[t - 1 - i] == n) return arr[t - 1 - i];
        }
        return -1;
    }
}



#endif
