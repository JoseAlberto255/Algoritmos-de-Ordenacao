#ifndef MERGE_SORT_HPP
#define MERGE_SORT_HPP
#include <algorithm>
#pragma once
#include <vector>

using namespace std;

namespace ed {
    inline void merge(vector<int>& arr, int left, int mid, int right) {
        vector<int> temp;

        int i = left;
        int j = mid + 1;

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp.push_back(arr[i++]);
            } else {
                temp.push_back(arr[j++]);
            }
        }

        while (i <= mid) {
            temp.push_back(arr[i++]);
        }

        while (j <= right) {
            temp.push_back(arr[j++]);
        }

        for (int k = left; k <= right; k++) {
            arr[k] = temp[k - left];
        }
    }

    inline void merge_sort(vector<int>& arr) {
        auto sort = [&](auto&& self, int left, int right) -> void {
            if (left >= right) {
                return;
            }

            int mid = left + (right - left) / 2;

            self(self, left, mid);
            self(self, mid + 1, right);

            merge(arr, left, mid, right);
        };

        if (!arr.empty()) {
            sort(sort, 0, static_cast<int>(arr.size()) - 1);
        }
    }
}

#endif