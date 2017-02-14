package com.tqnam.filemanager.explorer.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tqnam.filemanager.R;

import java.util.ArrayList;

/**
 * Created by quangnam on 11/8/16.
 */
public class DrawerMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    private static final int TYPE_HEADER = 0;
    //    private static final int TYPE_LIST = 1;

    private ArrayList<String> mPath;
    private DrawerMenuListener mListener;

    public DrawerMenuAdapter(ArrayList<String> pathList) {
        mPath = pathList;
    }

    public void setListener(DrawerMenuListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.drawer_menu_item, parent, false);
        final ItemHolder holder = new ItemHolder(rootView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String path = mPath.get(holder.getAdapterPosition());
                    mListener.onOpenDirectory(path);
                }
            }
        });

        return holder;
    }

    private String getItem(int position) {
        return mPath.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            String path = getItem(position);
            ((ItemHolder) holder).mLabel.setText(formatLabel(path));
        }
    }

    @Override
    public int getItemCount() {
        return mPath.size();
    }

    @Nullable
    private String getFileNameFromPath(String path) {
        String[] splitedStr = path.split("/");
        if (splitedStr.length > 0) {
            return splitedStr[splitedStr.length - 1];
        }

        return null;
    }

    private String formatLabel(String path) {
        String filename = getFileNameFromPath(path);

        if (path.equals(Environment.getExternalStorageDirectory().getPath())) {
            filename = "Storage";
        }

        return filename;
    }

    public interface DrawerMenuListener {
        void onOpenDirectory(String path);
    }

    //    public static class HeaderHolder extends RecyclerView.ViewHolder {
    //
    //        public HeaderHolder(View itemView) {
    //            super(itemView);
    //        }
    //    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView mLabel;

        public ItemHolder(View itemView) {
            super(itemView);
            mLabel = (TextView) itemView.findViewById(R.id.label);
        }
    }
}
