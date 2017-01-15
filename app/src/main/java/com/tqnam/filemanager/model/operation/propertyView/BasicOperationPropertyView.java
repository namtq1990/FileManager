package com.tqnam.filemanager.model.operation.propertyView;

import com.tqnam.filemanager.explorer.dialog.OperationInforDialogFragment;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.BasicOperation;
import com.tqnam.filemanager.utils.DefaultErrorAction;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by quangnam on 1/8/17.
 * Project FileManager-master
 */
public class BasicOperationPropertyView extends OperationPropertyView<BasicOperation<? extends ItemExplorer>> {
    Subscription mSubscription;

    public BasicOperationPropertyView(BasicOperation<? extends ItemExplorer> operation) {
        super(operation);
    }

    @Override
    public void bindView(ViewHolder rootView) {
        BasicOperation<? extends ItemExplorer> operation = getOperation();
        ItemExplorer[] listItem = new ItemExplorer[operation.getData().size()];
        final OperationInforDialogFragment.ViewHolder holder = (OperationInforDialogFragment.ViewHolder) rootView;

        holder.setSource(operation.getSourcePath());
        holder.setListData(operation.getData().toArray(listItem));


        final BasicOperation.BasicUpdatableData data = (BasicOperation.BasicUpdatableData) operation.getUpdateData();
        holder.setDestination(operation.getDestinationPath());
        holder.setSize(data.getSizeTotal());

        if (data.isFinished()) {
            holder.setProgress(100);
            holder.setSpeed((int) data.getSpeed());
        } else {
            mSubscription = operation.execute()
                    .subscribe(new Action1<BasicOperation.BasicUpdatableData>() {
                        @Override
                        public void call(BasicOperation.BasicUpdatableData copyFileData) {
                            holder.setSpeed((int) data.getSpeed());
                            holder.setProgress(data.getProgress());
                        }
                    }, new DefaultErrorAction());
        }

    }

    @Override
    public void unBindView(ViewHolder rootView) {
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }
}
