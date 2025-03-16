package com.example.just.Config.DataSoruce;

import java.util.*;

public class ReadOnlyDataSourceCycle<T> {
    private List<T> readOnlyDataSourceLookupKeys;
    private int index = 0;

    public void setReadOnlyDataSourceLookupKeys(List<T> readOnlyDataSourceLookupKeys) {
        this.readOnlyDataSourceLookupKeys = readOnlyDataSourceLookupKeys;
    }

    public T getReadOnlyDataSourceLookupKey() {
        if (readOnlyDataSourceLookupKeys != null && !readOnlyDataSourceLookupKeys.isEmpty()) {
            T key = readOnlyDataSourceLookupKeys.get(index);
            index = (index + 1) % readOnlyDataSourceLookupKeys.size();  // 순환하도록 수정
            return key;
        }
        return null;  // 빈 리스트일 경우 null 반환
    }
}