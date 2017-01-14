package com.yan.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.yan.adapter.StateAdapterItem.FOOTER;
import static com.yan.adapter.StateAdapterItem.HEADER;
import static com.yan.adapter.StateAdapterItem.NORMAL;

/**
 * Created by yan on 2017/1/13.
 */
@SuppressWarnings("unchecked")
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CustomAdapterItem> customAdapterItems;
    private LinkedList<StateAdapterItem> stateAdapterItems;
    private StateAdapterItem stateHeader;
    private StateAdapterItem stateFooter;
    private List<Integer> stateItemTypes;
    private volatile int stateCurrentSize = 0;
    private List dataList;
    private Integer itemType = 0x101;
    private boolean[] headerAndFooter = new boolean[2];

    private int headerOffset = 0;

    private synchronized void putItemToEnd(StateAdapterItem item) {
        if (stateAdapterItems.size() > 1 &&
                stateAdapterItems.getLast() != item) {
            stateAdapterItems.remove(item);
            stateAdapterItems.addLast(item);

            adjustFooterPosition();
        }
    }

    private void adjustFooterPosition() {
        if (stateFooter != null &&
                stateFooter.isShow() &&
                stateAdapterItems.get(stateCurrentSize - 1) != stateFooter) {
            stateAdapterItems.remove(stateFooter);
            stateAdapterItems.add(stateCurrentSize - 1, stateFooter);
        }
    }

    private synchronized void putItemToTop(StateAdapterItem item) {
        if (stateAdapterItems.size() > 1 &&
                stateAdapterItems.getFirst() != item) {
            stateAdapterItems.remove(item);
            stateAdapterItems.addFirst(item);

            adjustFooterPosition();
        }
    }

    public synchronized CustomAdapter addAdapterItem(CustomAdapterItem item) {
        if (item instanceof StateAdapterItem) {
            StateAdapterItem stateAdapterItem =
                    (StateAdapterItem) item;
            stateAdapterItem.attach(this);

            if (stateAdapterItems == null) {
                stateAdapterItems = new LinkedList<>();
            }
            if (stateItemTypes == null) {
                stateItemTypes = new ArrayList<>();
            }
            if (stateAdapterItem.getStateItemType() != NORMAL) {
                if (stateAdapterItem.getStateItemType() == HEADER) {
                    headerOffset = 1;
                    if (headerAndFooter[0]) {
                        try {
                            throw new Error("can only hold one header");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    headerAndFooter[0] = true;
                    stateHeader = stateAdapterItem;
                    return this;
                }
                if (headerAndFooter[1]) {
                    try {
                        throw new Error("can only hold one footer");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                headerAndFooter[1] = (stateAdapterItem.getStateItemType() == FOOTER);
                stateFooter = stateAdapterItem;
                stateAdapterItems.add(stateFooter);
                if (stateAdapterItem.isShow()) {
                    adjustStateSize(1);
                }
                adjustFooterPosition();
                return this;
            }

            if (stateAdapterItem.isShow()) {
                adjustStateSize(1);
                stateAdapterItems.addFirst(stateAdapterItem);
            } else
                stateAdapterItems.add(stateAdapterItem);

            adjustFooterPosition();
            return this;
        }
        if (customAdapterItems == null) {
            customAdapterItems = new ArrayList<>();
        }
        customAdapterItems.add(item);

        return this;
    }

    public StateAdapterItem findStateItem(String tag) {
        for (StateAdapterItem item : stateAdapterItems) {
            if (tag.equals(item.getTag())) {
                return item;
            }
        }
        try {
            throw new Exception("findStateItem Exception : tag " + tag + "not find");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CustomAdapter hideHeader(boolean withCommit) {
        headerAndFooter[0] = false;
        headerOffset = 0;
        if (withCommit)
            notifyDataSetChanged();
        return this;
    }

    public CustomAdapter showHeader(boolean withCommit) {
        headerAndFooter[0] = true;
        headerOffset = 1;
        if (withCommit)
            notifyDataSetChanged();
        return this;
    }

    public CustomAdapter hideFooter(boolean withCommit) {
        return hide(stateFooter, withCommit);
    }

    public CustomAdapter showFooter(boolean withCommit) {
        return show(stateFooter, withCommit);
    }

    public CustomAdapter show(String tag) {
        StateAdapterItem stateAdapterItem = findStateItem(tag);
        return show(stateAdapterItem, false);
    }

    public CustomAdapter show(String tag, boolean withCommit) {
        StateAdapterItem stateAdapterItem = findStateItem(tag);
        return show(stateAdapterItem, withCommit);
    }

    public CustomAdapter show(StateAdapterItem stateItem, boolean withCommit) {
        if (!stateItem.isShow()) {
            stateItem.setShow(true);
            adjustStateSize(1);
            putItemToTop(stateItem);
            if (withCommit) notifyDataSetChanged();
        }
        return this;
    }

    public CustomAdapter hide(String tag) {
        StateAdapterItem stateAdapterItem = findStateItem(tag);
        return hide(stateAdapterItem, false);
    }

    public CustomAdapter hide(String tag, boolean withCommit) {
        StateAdapterItem stateAdapterItem = findStateItem(tag);
        return hide(stateAdapterItem, withCommit);
    }

    public CustomAdapter hide(StateAdapterItem stateItem, boolean withCommit) {
        if (stateItem.isShow()) {
            stateItem.setShow(false);
            adjustStateSize(-1);
            putItemToEnd(stateItem);
            if (withCommit) notifyDataSetChanged();
        }
        return this;
    }

    public void commit() {
        notifyDataSetChanged();
    }

    public CustomAdapter(List dataList) {
        this.dataList = dataList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return stateItemTypes.contains(getItemViewType(position))
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            if (holder.getLayoutPosition() == 0 &&
                    headerAndFooter[0]) {
                p.setFullSpan(true);
            } else if ((holder.getLayoutPosition() >= dataList.size() + headerOffset)) {
                p.setFullSpan(true);

            } else p.setFullSpan(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headerAndFooter[0] && position == 0) {
            if (stateHeader.getItemType() == -1) {
                ++itemType;
                stateItemTypes.add(itemType);
                return stateHeader.setItemType(itemType);
            } else {
                return stateHeader.getItemType();
            }
        }

        for (CustomAdapterItem item : customAdapterItems) {
            if (position - headerOffset < dataList.size()) {
                if (dataList.get(position - headerOffset) != null &&
                        dataList.get(position - headerOffset).getClass() == item.dataType()) {
                    if (item.getItemType() == -1) {
                        return item.setItemType(++itemType);
                    } else {
                        return item.getItemType();
                    }
                }
            } else {
                if (stateAdapterItems.get(position - headerOffset - dataList.size()).getItemType() == -1) {
                    ++itemType;
                    stateItemTypes.add(itemType);
                    return stateAdapterItems.get(position - headerOffset - dataList.size())
                            .setItemType(itemType);
                } else {
                    return stateAdapterItems.get(position - headerOffset - dataList.size()).getItemType();
                }
            }
        }
        return 0;
    }

    public StateAdapterItem getStateHeader() {
        return stateHeader;
    }

    public StateAdapterItem getStateFooter() {
        return stateFooter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (stateHeader != null && stateHeader.getItemType() == viewType) {
            return (stateHeader.getHolder() != null) ? stateHeader.getHolder() :
                    stateHeader.setHolder(stateHeader.viewHolder(parent));
        }
        for (CustomAdapterItem item : customAdapterItems) {
            if (item.getItemType() == viewType) {
                return item.setHolder(item.viewHolder(parent));
            }
        }
        for (StateAdapterItem item : stateAdapterItems) {
            if (item.getItemType() == viewType) {
                return (item.getHolder() != null) ? item.getHolder() :
                        item.setHolder(item.viewHolder(parent));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (headerAndFooter[0] && position == 0) {
        } else if (position - headerOffset < dataList.size())
            for (CustomAdapterItem item : customAdapterItems) {
                if (dataList.get(position - headerOffset) != null &&
                        item.dataType() == dataList.get(position - headerOffset).getClass()) {
                    item.bindData(holder, dataList.get(position - headerOffset), position - headerOffset);
                    break;
                }
            }
    }

    private synchronized void adjustStateSize(int stateCurrentSize) {
        this.stateCurrentSize += stateCurrentSize;
    }

    @Override
    public int getItemCount() {
        return dataList.size() + stateCurrentSize +
                ((headerAndFooter[0]) ? 1 : 0);
    }
}
