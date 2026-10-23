#ifndef INSERCTION_SORT_HPP
#define INSERCTION_SORT_HPP

#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline void inserction_sort(vector<int>& arr) {
        int n = arr.size();
        for (int i = 0; i < n; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                arr[j+1] = arr[j];
                j = j-1;
            }
            arr[j+1] = key;
        }
    }

}


#endif
