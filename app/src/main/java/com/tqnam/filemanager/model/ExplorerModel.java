package com.tqnam.filemanager.model;

import android.os.Bundle;
import android.os.Parcelable;

import com.quangnam.baseframework.BaseDataFragment;
import com.tqnam.filemanager.utils.OperatorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExplorerModel {

    public static final int COMPARE_NAME = 1;
    public static final int COMPARE_TYPE = 2;

    public static final String ARG_CUR_LOCATION = "cur_location";
    public static final String ARG_PARENT_PATH = "parent_path";
    public static final String ARG_CUR_COMPARE = "cur_compare";
    public static final String ARG_LIST_ITEM = "list_item";
    public static final String ARG_LIST_DISPLAY_ITEM = "list_display";
    public static final String ARG_CLIPBOARD = "list_clipboard";

    public String mCurLocation;
    public String mParentPath;
    public int mCurCompare;
    // The list that will be display in GUI
    public ArrayList<ItemExplorer> mDisplayedItem;
    private BaseDataFragment mDataFragment;
    private OperatorManager mOperatorManager;
     // The List item in current location, all item must be same type to restore value
    private ArrayList<ItemExplorer> mListItem;
    private ArrayList<Operator>  mUnvalidatedOperators;

    public ExplorerModel(BaseDataFragment dataFragment) {
        mCurCompare = COMPARE_NAME;
        mListItem = new ArrayList<>();
        mDisplayedItem = new ArrayList<>();

        mDataFragment = dataFragment;
        mOperatorManager = OperatorManager.getInstance();
        mUnvalidatedOperators = new ArrayList<>();
    }

    public void onSavedInstanceState(Bundle savedState) {
        savedState.putInt(ARG_CUR_COMPARE, mCurCompare);
        savedState.putString(ARG_CUR_LOCATION, mCurLocation);
        savedState.putString(ARG_PARENT_PATH, mParentPath);
        savedState.putParcelableArrayList(ExplorerModel.ARG_LIST_ITEM, getList());
        savedState.putParcelableArrayList(ARG_LIST_DISPLAY_ITEM, getDisplayedItem());
    }

    public void onRestoreInstanceState(Bundle savedState) {
        mCurCompare = savedState.getInt(ARG_CUR_COMPARE);
        mCurLocation = savedState.getString(ARG_CUR_LOCATION);
        mParentPath = savedState.getString(ARG_PARENT_PATH);
        mListItem = savedState.getParcelableArrayList(ExplorerModel.ARG_LIST_ITEM);
        mDisplayedItem = savedState.getParcelableArrayList(ARG_LIST_DISPLAY_ITEM);
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

    public void setList(List<? extends ItemExplorer> list) {
        mListItem = (ArrayList<ItemExplorer>) list;
    }

    public void addItem(ItemExplorer item) {
        mListItem.add(item);
    }

    public void clearItem() {
        mListItem.clear();
    }

    public ArrayList<ItemExplorer> getDisplayedItem() {
        return mDisplayedItem;
    }

    public ItemExplorer getItemDisplayedAt(int position) {
        return mDisplayedItem.get(position);
    }

    public int getDisplayCount() {
        return mDisplayedItem.size();
    }

    public void addDisplayItem(ItemExplorer item) {
        mDisplayedItem.add(item);
    }

    public void clearDisplayItem() {
        mDisplayedItem.clear();
    }

    public void removeDisplayItem(ItemExplorer item) {
        mDisplayedItem.remove(item);
    }

    public void resetDisplayList() {
        mDisplayedItem = new ArrayList<>(mListItem);
    }

    public OperatorManager getOperatorManager() {
        return mOperatorManager;
    }

    public ArrayList<Operator> getUnvalidatedList() {
        return mUnvalidatedOperators;
    }

    public void saveClipboard(List<ItemExplorer> clipboard) {
        mDataFragment.getData()
                .putParcelableArrayList(ARG_CLIPBOARD, (ArrayList<? extends Parcelable>) clipboard);
    }

    public ArrayList<ItemExplorer> getClipboard() {
        return mDataFragment.getData()
                .getParcelableArrayList(ARG_CLIPBOARD);
    }
}
