package com.tqnam.filemanager.model;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by quangnam on 11/7/15.
 */
public class ExplorerModel {

    public static final int COMPARE_NAME = 1;
    public static final int COMPARE_TYPE = 2;

    public static final String ARG_CUR_LOCATION = "cur_location";
    public static final String ARG_CUR_COMPARE = "cur_compare";
    public static final String ARG_LIST_ITEM = "list_item";

    /**
     * List item in current location, all item must be same type to restore value
     */
    public ArrayList<ItemExplorer> mListItem;
    public String mCurLocation;
    public int mCurCompare;

    public ExplorerModel() {
        mCurCompare = COMPARE_NAME;
    }

    public void onSavedInstanceState(Bundle savedState) {
        savedState.putInt(ARG_CUR_COMPARE, mCurCompare);
        savedState.putString(ARG_CUR_LOCATION, mCurLocation);
    }

    public void onRestoreInstanceState(Bundle savedState) {
        mCurCompare = savedState.getInt(ARG_CUR_COMPARE);
        mCurLocation = savedState.getString(ARG_CUR_LOCATION);
    }

    public void sort() {
        Comparator<ItemExplorer> curComparator = null;

        switch (mCurCompare) {
            case COMPARE_NAME:
                curComparator = OptionModel.OPTION_SORT_BY_NAME;
                break;
        }

        if (curComparator != null)
            Collections.sort(mListItem, curComparator);
    }
}