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
    public static final String ARG_PARENT_PATH = "parent_path";
    public static final String ARG_CUR_COMPARE = "cur_compare";
    public static final String ARG_LIST_ITEM = "list_item";
    public String mCurLocation;
    public String mParentPath;
    public int mCurCompare;
    /**
     * List item in current location, all item must be same type to restore value
     */
    private ArrayList<ItemExplorer> mListItem;
    // The list that will be display in GUI
    private ArrayList<ItemExplorer> mDisplayedItem;

    public ExplorerModel() {
        mCurCompare = COMPARE_NAME;
        mListItem = new ArrayList<>();
        mDisplayedItem = new ArrayList<>();
    }

    public void onSavedInstanceState(Bundle savedState) {
        savedState.putInt(ARG_CUR_COMPARE, mCurCompare);
        savedState.putString(ARG_CUR_LOCATION, mCurLocation);
        savedState.putString(ARG_PARENT_PATH, mParentPath);
        savedState.putParcelableArrayList(ExplorerModel.ARG_LIST_ITEM, getList());
    }

    public void onRestoreInstanceState(Bundle savedState) {
        mCurCompare = savedState.getInt(ARG_CUR_COMPARE);
        mCurLocation = savedState.getString(ARG_CUR_LOCATION);
        mParentPath = savedState.getString(ARG_PARENT_PATH);
        mListItem = savedState.getParcelableArrayList(ExplorerModel.ARG_LIST_ITEM);
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

    public ItemExplorer getItemAt(int position) {
        return mListItem.get(position);
    }

    public int getTotalItem() {
        return mListItem.size();
    }

    public ArrayList<ItemExplorer> getList() {
        return mListItem;
    }
}
