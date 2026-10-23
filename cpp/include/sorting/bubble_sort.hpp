#ifndef BUBBLE_SORT_HPP
#define BUBBLE_SORT_HPP
#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline void bubble_sort(vector<int>& arr) {
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n-1-i; j++) {
                if (arr[j] > arr[j+1]) swap(arr[j], arr[j+1]);
            }
        }
    }
}
#endif
