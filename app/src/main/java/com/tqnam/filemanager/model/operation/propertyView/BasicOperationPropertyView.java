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
