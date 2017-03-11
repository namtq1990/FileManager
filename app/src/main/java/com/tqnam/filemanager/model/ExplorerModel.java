/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tqnam.filemanager.model;

import android.os.Bundle;
import android.os.Parcelable;

import com.quangnam.base.BaseDataFragment;
import com.tqnam.filemanager.model.operation.Operation;
import com.tqnam.filemanager.utils.OperationManager;

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
    public static final String ARG_CLIPBOARD = "list_clipboard";
    public static final String ARG_CLIPBOARD_CATEGORY = "clipboard_category";

    public String mCurLocation;
    public String mParentPath;
    public int mCurCompare;

    private BaseDataFragment mDataFragment;
    private OperationManager mOperatorManager;
     // The List item in current location, all item must be same type to restore value
    private ArrayList<ItemExplorer> mListItem;
    private ArrayList<Operation> mUnvalidatedOperations;

    public ExplorerModel(BaseDataFragment dataFragment) {
        mCurCompare = COMPARE_NAME;
        mListItem = new ArrayList<>();

        mDataFragment = dataFragment;
        mOperatorManager = OperationManager.getInstance();
        mUnvalidatedOperations = new ArrayList<>();
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

    public void setList(List<? extends ItemExplorer> list) {
        mListItem = (ArrayList<ItemExplorer>) list;
    }

    public void addItem(ItemExplorer item) {
        mListItem.add(item);
    }

    public void clearItem() {
        mListItem.clear();
    }

    public OperationManager getOperatorManager() {
        return mOperatorManager;
    }

    public ArrayList<Operation> getUnvalidatedList() {
        return mUnvalidatedOperations;
    }

    public void saveClipboard(List<ItemExplorer> clipboard, int category) {
        mDataFragment.getData()
                .putParcelableArrayList(ARG_CLIPBOARD, (ArrayList<? extends Parcelable>) clipboard);
        if (clipboard != null) {
            mDataFragment.getData()
                    .putInt(ARG_CLIPBOARD_CATEGORY, category);
        } else {
            mDataFragment.getData()
                    .remove(ARG_CLIPBOARD_CATEGORY);
        }
    }

    public ArrayList<ItemExplorer> getClipboard() {
        return mDataFragment.getData()
                .getParcelableArrayList(ARG_CLIPBOARD);
    }

    public int getClipboardCategory() {
        return mDataFragment.getData()
                .getInt(ARG_CLIPBOARD_CATEGORY, OperationManager.CATEGORY_COPY);
    }
}
