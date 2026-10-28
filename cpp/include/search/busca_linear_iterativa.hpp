#ifndef BUSCA_LINEAR_ITERATIVA_HPP
#define BUSCA_LINEAR_ITERATIVA_HPP
#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline int busca_linear_iterativa(vector<int>& arr, int n) {
        int t = arr.size();
        for (int i = 0; i < t; i++) {
             if (n == arr[i]) return arr[i];
        }
        return -1;
    }
}



#endif
