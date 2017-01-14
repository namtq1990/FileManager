package com.tqnam.filemanager.model.operation.propertyView;

import com.tqnam.filemanager.explorer.dialog.OperationInforDialogFragment;
import com.tqnam.filemanager.model.operation.CopyFileOperation;
import com.tqnam.filemanager.utils.DefaultErrorAction;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by quangnam on 1/8/17.
 * Project FileManager-master
 */
public class CopyPropertyView extends CPMOperationPropertyView {
    Subscription mSubscription;

    public CopyPropertyView(CopyFileOperation operation) {
        super(operation);
    }

    @Override
    public void bindView(ViewHolder rootView) {
        super.bindView(rootView);

        CopyFileOperation operation = (CopyFileOperation) getOperation();
        final CopyFileOperation.CopyFileData data = (CopyFileOperation.CopyFileData) operation.getUpdateData();
        final OperationInforDialogFragment.ViewHolder holder = (OperationInforDialogFragment.ViewHolder) rootView;
        holder.setDestination(operation.getDestinationPath());
        holder.setSize(data.getSizeTotal());

        if (data.isFinished()) {
            holder.setProgress(100);
            holder.setSpeed((int) data.getSpeed());
        } else {
            mSubscription = operation.execute()
                    .subscribe(new Action1<CopyFileOperation.CopyFileData>() {
                        @Override
                        public void call(CopyFileOperation.CopyFileData copyFileData) {
                            holder.setSpeed((int) data.getSpeed());
                            holder.setProgress(data.getProgress());
                        }
                    }, new DefaultErrorAction());
        }
    }

    @Override
    public void unBindView(ViewHolder rootView) {
        super.unBindView(rootView);
        mSubscription.unsubscribe();
    }
}
