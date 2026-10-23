#ifndef SELECTION_SORT_HPP
#define SELECTION_SORT_HPP
#pragma once
#include <vector>

using namespace std;
namespace ed {
    inline void selection_sort(vector<int>& arr) {
        int n = arr.size();
        for (int i = 0; i < n; i++) {
            int min_idx = i;
            
            for (int j = i+1; j < n; ++j) {
                if (arr[j] < arr[min_idx]) min_idx = j;
            }

            if (min_idx != i) swap(arr[i], arr[min_idx]);   
        }
    }
}
#endif
