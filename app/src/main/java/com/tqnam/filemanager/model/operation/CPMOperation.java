package com.tqnam.filemanager.model.operation;

import com.tqnam.filemanager.model.ItemExplorer;

import java.util.Iterator;
import java.util.List;

/**
 * Created by quangnam on 11/25/16.
 * Project FileManager-master
 */
public abstract class CPMOperation<T extends ItemExplorer> extends Operation.TraverseFileOperation<T> {

    private boolean mIsOverwrite;
    private Validator mValidator;

    public CPMOperation(List<T> data) {
        super(data);

        mIsOverwrite = false;
        mValidator = new Validator();
    }

    public boolean isOverwrite() {
        return mIsOverwrite;
    }

    public void setOverwrite(boolean overwrite) {
        mIsOverwrite = overwrite;
    }

    public Validator getValidator() {
        return mValidator;
    }

    public void validate() {
    }

    public void setItemValidated(ItemExplorer item) {
        mValidator.setItemSafe(item);
    }

    public ItemExplorer getValidatingItem() {
        Iterator<ItemExplorer> iterator = mValidator.getListViolated().iterator();

        return iterator.hasNext() ? iterator.next() : null;
    }
}
