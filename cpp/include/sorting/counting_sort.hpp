#ifndef COUNTING_SORT_HPP
#define COUNTING_SORT_HPP
#include <algorithm>
#pragma once
#include <vector>

using namespace std;

namespace ed {
    void counting_sort(vector<int>& arr) {
        if (arr.empty()) return;

        int max_val = *max_element(arr.begin(), arr.end());

        vector<int> contagem(max_val + 1, 0);

        for (int i : arr) {
            contagem[i]++;
        }

        int k = 0;
        for (int i = 0; i <= max_val; i++) {
            while (contagem[i] > 0) {
                arr[k++] = i;
                contagem[i]--;
            }
        }
    }
};




#endif