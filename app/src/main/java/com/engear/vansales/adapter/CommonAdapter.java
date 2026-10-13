package com.engear.vansales.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by cisner-1 on 12/3/18.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonAdapter<T>.CommonViewHolder> {

    private ArrayList<T> data = new ArrayList<>();
    private boolean moreDataAvailable;
    private boolean isLoading;
    private OnLoadMoreListener loadMoreListener;


    public CommonAdapter(ArrayList<T> arrItem) {
        this.data = arrItem;
        setHasStableIds(false);
    }

    public CommonAdapter(ArrayList<T> arrItem, boolean hasStableIds) {
        this.data = arrItem;
        setHasStableIds(hasStableIds);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
            return new CommonViewHolder(binding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        return new CommonViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CommonAdapter.CommonViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && moreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        } else {
            onBind(holder, position);
            onBind(holder, position, getItem(position));
        }
    }

    public boolean isMoreDataAvailable() {
        return moreDataAvailable;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        this.moreDataAvailable = moreDataAvailable;
    }

    public void addItem(T item) {
        this.data.add(item);
        notifyItemInserted(data.size() - 1);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


    public T getItem(int pos) {
        return this.data.get(pos);
    }

    public void removeItem(int pos) {
        data.remove(pos);
        notifyItemRemoved(pos);
    }

    public void removeItem(T item) {
        data.remove(item);
        notifyItemRemoved(data.indexOf(item));
    }

    public void updateData(ArrayList<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }


    public void startLoadMore() {
        data.add(null);
        notifyItemInserted(data.size() - 1);
    }

    public void stopLoadMore(ArrayList<T> trending) {
        data.remove(data.size() - 1);
        if (trending != null)
            data.addAll(trending);
        notifyDataSetChanged();
        isLoading = false;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public class CommonViewHolder extends RecyclerView.ViewHolder {
        public ViewDataBinding binding;

        public CommonViewHolder(ViewDataBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    public void onBind(CommonViewHolder holder, int position, T item) {

    }

//    public abstract int getLayoutId();

    public abstract void onBind(CommonViewHolder holder, int position);

}