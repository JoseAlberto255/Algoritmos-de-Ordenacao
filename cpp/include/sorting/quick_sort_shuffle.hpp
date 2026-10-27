#ifndef QUICK_SORT_SHUFFLE_HPP
#define QUICK_SORT_SHUFFLE_HPP

#include <algorithm>
#include <random>
#include <vector>

namespace ed {
    inline int partition_shuffle(std::vector<int>& arr, int low, int high)
    {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; ++j)
        {
            if (arr[j] < pivot)
            {
                ++i;
                std::swap(arr[i], arr[j]);
            }
        }

        std::swap(arr[i + 1], arr[high]);
        return i + 1;
    }

    inline void quickSort_shuffle(std::vector<int>& arr, int low, int high)
    {
        if (low < high)
        {
            int pi = partition_shuffle(arr, low, high);

            quickSort_shuffle(arr, low, pi - 1);
            quickSort_shuffle(arr, pi + 1, high);
        }
    }

    inline void quick_sort_shuffle(std::vector<int>& arr)
    {
        if (arr.size() < 2)
            return;

        static std::mt19937 rng(std::random_device{}());

        std::shuffle(arr.begin(), arr.end(), rng);

        quickSort_shuffle(arr, 0, static_cast<int>(arr.size()) - 1);
    }
}

#endif
