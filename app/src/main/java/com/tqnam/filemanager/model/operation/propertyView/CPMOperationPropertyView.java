package com.tqnam.filemanager.model.operation.propertyView;

import com.tqnam.filemanager.explorer.dialog.OperationInforDialogFragment;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.CPMOperation;

/**
 * Created by quangnam on 1/8/17.
 * Project FileManager-master
 */
public class CPMOperationPropertyView extends OperationPropertyView<CPMOperation<? extends ItemExplorer>> {

    public CPMOperationPropertyView(CPMOperation<? extends ItemExplorer> operation) {
        super(operation);
    }

    @Override
    public void bindView(ViewHolder rootView) {
        CPMOperation<? extends ItemExplorer> operation = getOperation();
        ItemExplorer[] listItem = new ItemExplorer[operation.getData().size()];
        OperationInforDialogFragment.ViewHolder holder = (OperationInforDialogFragment.ViewHolder) rootView;

        holder.setSource(operation.getSourcePath());
        holder.setListData(operation.getData().toArray(listItem));
    }

    @Override
    public void unBindView(ViewHolder rootView) {

    }
}
