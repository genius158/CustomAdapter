package com.yan.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by yan on 2017/1/13.
 */

public abstract class CustomAdapterItem<T extends RecyclerView.ViewHolder, V> {
    private Integer itemType = -1;
    private T holder;

    public CustomAdapterItem() {
    }

    public CustomAdapterItem(T holder) {
        this.holder = holder;
    }

    public T getHolder() {
        return holder;
    }

    public T setHolder(T holder) {
        return this.holder = holder;
    }

    public final Integer getItemType() {
        return itemType;
    }

    public final int setItemType(Integer itemType) {
        return this.itemType = itemType;
    }

    public abstract Class dataType();

    public abstract T viewHolder(ViewGroup parent);

    public abstract void bindData(T holder, V item, int position);

}
