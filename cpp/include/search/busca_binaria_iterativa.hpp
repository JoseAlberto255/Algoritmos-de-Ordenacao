#ifndef BUSCA_BINARIA_ITERATIVA_HPP
#define BUSCA_BINARIA_ITERATIVA_HPP
#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline int busca_binaria_iterativa(vector<int>& arr, int n) {
        int i = 0;
        int f = (int)arr.size() - 1;
        while (i <= f) {
            int m = i + (f - i) / 2;

            if (arr[m] == n) {
                return arr[m];
            }

            if (arr[m] > n) {
                f = m - 1;
            } else {
                i = m + 1;
            }
        }
        return -1;
    }
}


#endif
