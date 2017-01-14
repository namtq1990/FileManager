package com.tqnam.filemanager.model.operation.propertyView;

import com.tqnam.filemanager.model.operation.Operation;

/**
 * Created by quangnam on 1/8/17.
 * Project FileManager-master
 */
public abstract class OperationPropertyView<T extends Operation> {

    private T mOperation;

    public OperationPropertyView(T operation) {
        mOperation = operation;
    }

    public T getOperation() {
        return mOperation;
    }

    public abstract void bindView(ViewHolder rootView);
    public abstract void unBindView(ViewHolder rootView);

    public interface ViewHolder {

    }
}