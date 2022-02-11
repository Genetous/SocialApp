package com.mubo.socialapp.main.ui.search;

import java.util.Comparator;

public class collectionNameSorter implements Comparator<SearchData>
{
    @Override
    public int compare(SearchData o1, SearchData o2) {
        return o2.getType().compareToIgnoreCase(o1.getType());
    }
}