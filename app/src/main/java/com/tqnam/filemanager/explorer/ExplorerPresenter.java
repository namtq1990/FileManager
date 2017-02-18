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

package com.tqnam.filemanager.explorer;

import com.quangnam.baseframework.AutoUnsubscribe;
import com.quangnam.baseframework.SaveBundleListener;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.Operation;

import java.util.ArrayList;
import java.util.List;

public interface ExplorerPresenter extends SaveBundleListener {

    void bind(View view);
    void unbind(View view);
    void onBackPressed();
    List<ItemExplorer> getListData();

    void openItem(ItemExplorer item);
    void openDirectory(ItemExplorer path);
    void renameItem(ItemExplorer item, String newLabel);
    void reload();
    void createFile(String filename);
    void createFolder(String filename);

    void queryFile(String path, String query);
    Operation<?> deleteOperation(List<ItemExplorer> list);
    Operation<?> copyCurFolderOperation(List<ItemExplorer> listSelected);
    Operation<?> moveCurFolderOperation(List<ItemExplorer> listSelected);
    Operation<?> doPasteAction();
    void trySetValidated(Operation operation);

    /**
     * Save to clipboard with category (Copy or Move)
     */
    void saveClipboard(List<ItemExplorer> clipboard, int category);
    ArrayList<ItemExplorer> getClipboard();

    Operation getValidatingOperation();

    void setValidatingOperation(Operation operation);

    String getCurLocation();
    void setCurLocation(String path);
    ItemExplorer getCurFolder();

    OpenType getOpenType();

    void setOpenType(OpenType openType);

    OpenOption getOpenOption();

    void setOpenOption(OpenOption openOption);

    public enum OpenType {
        LOCAL
    }

    public enum OpenOption {
        EXPLORER,
        SEARCH
    }

    interface View extends AutoUnsubscribe {
        ExplorerPresenter getPresenter();

        void replaceExplorerAtItem(ItemExplorer root);

        void openPreview(ItemExplorer item);

        void showLoading(boolean isLoading);

        String getQuery();

        void setQuery(String query);

        String getRootPath();

        void setRootPath(String path);

        void refreshView();

        void showError(String message);

        void showError(int message);

        void showMessage(String message);

        void showMessage(int message);

        void onQueryFile(final String query);

        void showValidate(Operation operation);
    }
}
