#ifndef BUSCA_BINARIA_ITERATIVA_HPP
#define BUSCA_BINARIA_ITERATIVA_HPP
#pragma once
#include <vector>


using namespace std;

namespace ed {
    inline int busca_binaria(vector<int> arr, int n) {
     for (int i : arr) {
         if (n == i) return i;
     }
     return -1;
    }
}



#endif
