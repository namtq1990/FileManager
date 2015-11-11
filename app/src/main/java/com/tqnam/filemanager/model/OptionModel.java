package com.tqnam.filemanager.model;

import java.util.Comparator;

/**
 * Created by quangnam on 11/7/15.
 */


public class OptionModel {

    public static final Comparator<ItemExplorer> OPTION_SORT_BY_NAME = new Comparator<ItemExplorer>() {
        @Override
        public int compare(ItemExplorer a, ItemExplorer b) {
            if (a.isDirectory() ^ b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }

            return (a.getDisplayName().compareToIgnoreCase(b.getDisplayName()) <= 0) ? -1 : 1;
        }
    };
}
