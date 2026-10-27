#ifndef QUICK_SORT_HPP
#define QUICK_SORT_HPP
#pragma once
#include <iostream>
#include <vector>

using namespace std;

namespace ed {

    inline int partition(vector<int> &arr, int low, int high)
    {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++)
        {
            if (arr[j] < pivot)
            {
                i++;
                swap(arr[i], arr[j]);
            }
        }

        swap(arr[i + 1], arr[high]);
        return i + 1;
    }

    inline void quickSort(vector<int> &arr, int low, int high)
    {
        if (low < high)
        {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    // Função pública: recebe apenas o vector
    inline void quick_sort(vector<int>& arr)
    {
        if (!arr.empty())
            quickSort(arr, 0, arr.size() - 1);
    }
}


#endif
