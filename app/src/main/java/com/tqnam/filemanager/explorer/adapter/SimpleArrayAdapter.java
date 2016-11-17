package com.tqnam.filemanager.explorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by quangnam on 11/12/16.
 */
public class SimpleArrayAdapter<T> extends RecyclerView.Adapter<SimpleArrayAdapter.ViewHolder>{

    private T[] mData;

    public SimpleArrayAdapter(T[] data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView label = holder.label;
        formatLabel(label, getLabelFromData(mData[position]));
    }

    public void formatLabel(TextView tv, String label) {
        tv.setText(label);
    }

    public String getLabelFromData(T data) {
        return data.toString();
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView label;

        public ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView;
        }
    }
}
