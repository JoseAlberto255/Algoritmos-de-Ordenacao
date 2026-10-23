#ifndef SELECTION_SORT_ESTAVEL_HPP
#define SELECTION_SORT_ESTAVEL_HPP
#pragma once
#include <vector>

using namespace std;
namespace ed {
    inline void selection_sort_estavel(vector<int>& arr) {
        int n = arr.size();
        for (int i = 0; i < n; i++) {
            int min_idx = i;
            
            for (int j = i+1; j < n; ++j) {
                if (arr[j] < arr[min_idx]) {
                    min_idx = j;
                }
            }
            int min_val = arr[min_idx]; 
            while (min_idx > i) {
                arr[min_idx] = arr[min_idx - 1];
                min_idx--;
            }
            arr[i] = min_val;
        }
    }
}
#endif
