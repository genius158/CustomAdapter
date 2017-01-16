package com.yan.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by yan on 2017/1/13.
 */

public abstract class StateAdapterItem<T extends RecyclerView.ViewHolder>
        extends CustomAdapterItem<T, Object> {

    private boolean isShow = false;
    private CustomAdapter adapter;
    private String tag;

    private int stateItemType = 0;
    public static final int NORMAL = 0;
    public static final int HEADER = 1;
    public static final int FOOTER = 2;

    public StateAdapterItem(String tag, boolean isShow, Integer stateItemType, T t) {
        super(t);
        this.tag = tag;
        this.isShow = isShow;
        this.stateItemType = stateItemType;
    }

    public StateAdapterItem(String tag, boolean isShow, T t) {
        this(tag, isShow, 0, t);
    }

    public StateAdapterItem(String tag, boolean isShow) {
        this(tag, isShow, null);
    }

    public StateAdapterItem(boolean isShow) {
        this(null, isShow, null);
    }

    public StateAdapterItem(boolean isShow, T t) {
        this(null, isShow, t);
    }

    public StateAdapterItem() {
        this(true);
    }

    public StateAdapterItem(Integer stateItemType) {
        this(null, true, stateItemType, null);
    }

    public StateAdapterItem(Integer stateItemType, boolean isShow) {
        this(null, isShow, stateItemType, null);
    }

    public StateAdapterItem(Integer stateItemType, T holder) {
        this(null, true, stateItemType, holder);
    }

    public StateAdapterItem(Integer stateItemType, T holder, boolean isShow) {
        this(null, isShow, stateItemType, holder);
    }

    public StateAdapterItem(String tag) {
        this(tag, false);
    }

    public final String getTag() {
        return tag;
    }

    public final void attach(CustomAdapter recyclerView) {
        this.adapter = recyclerView;
    }

    public StateAdapterItem show() {
        adapter.show(tag).commit();
        return this;
    }

    public StateAdapterItem hide() {
        adapter.hide(tag).commit();
        return this;
    }

    @Override
    public final Class dataType() {
        return null;
    }

    @Override
    public final void bindData(T holder, Object item, int position) {

    }

    public final int getStateItemType() {
        return stateItemType;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public final boolean isShow() {
        return isShow;
    }

}
