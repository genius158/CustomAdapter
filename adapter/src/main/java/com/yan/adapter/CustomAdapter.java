package com.yan.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by yan on 2017/1/13.
 */
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CustomAdapterItem> customAdapterItems;
    private LinkedList<StateAdapterItem> stateAdapterItems;
    private StateAdapterItem stateHeader;
    private StateAdapterItem stateFooter;
    private List<Integer> stateItemTypes;
    private volatile int stateCurrentSize = 0;
    private List dataList;
    private Integer itemType = 0x101;
    private int headerOffset = 0;
    private GridLayoutManager.SpanSizeLookup spanSizeLookup;

    private View.OnClickListener onClickListener;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListener onDataItemClickListener;

    private synchronized void putItemToEnd(StateAdapterItem item) {
        if (stateAdapterItems != null
                && stateAdapterItems.size() > 1
                && stateAdapterItems.getLast() != item) {
            stateAdapterItems.remove(item);
            stateAdapterItems.addLast(item);
        }
    }

    private synchronized void putItemToTop(StateAdapterItem item) {
        if (stateAdapterItems != null
                && stateAdapterItems.size() > 1
                && stateAdapterItems.getFirst() != item) {
            stateAdapterItems.remove(item);
            stateAdapterItems.addFirst(item);
        }
    }

    public void setSpanSizeLookup(GridLayoutManager.SpanSizeLookup spanSizeLookup) {
        this.spanSizeLookup = spanSizeLookup;
    }

    public CustomAdapter addAdapterItem(CustomAdapterItem item) {
        if (item instanceof StateAdapterItem) {
            StateAdapterItem stateAdapterItem = (StateAdapterItem) item;
            stateAdapterItem.attach(this);
            if (stateAdapterItems == null) {
                stateAdapterItems = new LinkedList<>();
            }
            if (stateItemTypes == null) {
                stateItemTypes = new ArrayList<>();
            }

            if (stateAdapterItem.getStateItemType() == StateAdapterItem.HEADER) {
                if (stateHeader != null) {
                    try {
                        throw new RuntimeException("can only hold one header");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (stateAdapterItem.isShow())
                    headerOffset = 1;

                stateHeader = stateAdapterItem;
                return this;

            } else if (stateAdapterItem.getStateItemType() == StateAdapterItem.FOOTER) {
                if (stateFooter != null) {
                    try {
                        throw new RuntimeException("can only hold one footer");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                stateFooter = stateAdapterItem;
                return this;
            }

            if (stateAdapterItem.isShow()) {
                adjustStateSize(1);
                stateAdapterItems.addFirst(stateAdapterItem);
            } else
                stateAdapterItems.add(stateAdapterItem);
            return this;
        }
        if (customAdapterItems == null) {
            customAdapterItems = new ArrayList<>();
        }
        customAdapterItems.add(item);
        return this;
    }

    public StateAdapterItem findStateItem(String tag) {
        if (stateAdapterItems != null) {
            for (StateAdapterItem item : stateAdapterItems) {
                if (tag.equals(item.getTag())) {
                    return item;
                }
            }
        }
        try {
            throw new RuntimeException("findStateItem Exception : tag " + tag + "not find");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CustomAdapter hideHeader(boolean withCommit) {
        if (stateHeader == null)
            return this;
        stateHeader.setShow(false);
        headerOffset = 0;
        notifyDataSetChangedWithCommit(withCommit);
        return this;
    }

    public CustomAdapter showHeader(boolean withCommit) {
        if (stateHeader == null)
            return this;
        stateHeader.setShow(true);
        headerOffset = 1;
        notifyDataSetChangedWithCommit(withCommit);
        return this;
    }

    public CustomAdapter hideFooter(boolean withCommit) {
        if (stateFooter == null)
            return this;
        stateFooter.setShow(false);
        notifyDataSetChangedWithCommit(withCommit);
        return this;
    }

    public CustomAdapter showFooter(boolean withCommit) {
        if (stateFooter == null)
            return this;
        stateFooter.setShow(true);
        notifyDataSetChangedWithCommit(withCommit);
        return this;
    }

    private void notifyDataSetChangedWithCommit(boolean withCommit) {
        if (withCommit)
            notifyDataSetChanged();
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
            notifyDataSetChangedWithCommit(withCommit);
        }
        return this;
    }

    public CustomAdapter hide(String tag) {
        StateAdapterItem stateAdapterItem = findStateItem(tag);
        return hide(stateAdapterItem, false);
    }

    public CustomAdapter hide(String tag, boolean withCommit) {
        return hide(findStateItem(tag), withCommit);
    }

    public CustomAdapter hide(StateAdapterItem stateItem, boolean withCommit) {
        if (stateItem.isShow()) {
            stateItem.setShow(false);
            adjustStateSize(-1);
            putItemToEnd(stateItem);
            notifyDataSetChangedWithCommit(withCommit);
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
        final RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);

            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (spanSizeLookup != null && (position < dataList.size() + headerOffset && position > headerOffset - 1)) {
                        return spanSizeLookup.getSpanSize(position);
                    }
                    return (stateItemTypes != null && stateItemTypes.contains(getItemViewType(position)))
                            ? gridManager.getSpanCount()
                            : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            if (holder.getLayoutPosition() == 0
                    && stateHeader != null
                    && stateHeader.isShow()) {
                p.setFullSpan(true);
            } else if ((holder.getLayoutPosition() >= dataList.size() + headerOffset)) {
                p.setFullSpan(true);
            } else p.setFullSpan(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (customAdapterItems == null
                || customAdapterItems.isEmpty()) {
            try {
                throw new RuntimeException("must call addAdapterItem to add item type");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (position == 0
                && stateHeader != null
                && stateHeader.isShow()
                ) {
            if (stateHeader.getItemType() == -1) {
                ++itemType;
                stateItemTypes.add(itemType);
                return stateHeader.setItemType(itemType);
            } else {
                return stateHeader.getItemType();
            }
        }

        if (stateFooter != null
                && stateFooter.isShow()
                && position == getItemCount() - 1) {
            if (stateFooter.getItemType() == -1) {
                ++itemType;
                stateItemTypes.add(itemType);
                return stateFooter.setItemType(itemType);
            } else {
                return stateFooter.getItemType();
            }
        }

        for (CustomAdapterItem item : customAdapterItems) {
            if (position - headerOffset < dataList.size()) {
                if (dataList.get(position - headerOffset) != null
                        && dataList.get(position - headerOffset).getClass() == item.dataType()) {
                    if (item.getItemType() == -1) {
                        return item.setItemType(++itemType);
                    } else {
                        return item.getItemType();
                    }
                }
            } else {
                if (stateAdapterItems != null) {
                    if (stateAdapterItems.get(position - headerOffset - dataList.size()).getItemType() == -1) {
                        ++itemType;
                        stateItemTypes.add(itemType);
                        return stateAdapterItems.get(position - headerOffset - dataList.size()).setItemType(itemType);
                    } else {
                        return stateAdapterItems.get(position - headerOffset - dataList.size()).getItemType();
                    }
                }
            }
        }
        try {
            throw new RuntimeException("require customAdapterItem for " + dataList.get(position - headerOffset).getClass()
                    + " please check the customAdapterItem that you had add");
        } catch (Exception e) {
            e.printStackTrace();
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
        if (stateHeader != null
                && stateHeader.getItemType().equals(viewType)) {
            return (stateHeader.getHolder() != null)
                    ? stateHeader.getHolder()
                    : stateHeader.setHolder(stateHeader.viewHolder(parent));
        }
        if (stateFooter != null
                && stateFooter.getItemType().equals(viewType)) {
            return (stateFooter.getHolder() != null)
                    ? stateFooter.getHolder()
                    : stateFooter.setHolder(stateFooter.viewHolder(parent));
        }
        for (CustomAdapterItem item : customAdapterItems) {
            if (item.getItemType().equals(viewType)) {
                return item.setHolder(item.viewHolder(parent));
            }
        }
        for (StateAdapterItem item : stateAdapterItems) {
            if (item.getItemType().equals(viewType)) {
                return (item.getHolder() != null) ? item.getHolder() :
                        item.setHolder(item.viewHolder(parent));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (onClickListener != null) {
            holder.itemView.setTag(R.id.ca_position, position);
            holder.itemView.setTag(R.id.ca_holder, holder);
            holder.itemView.setOnClickListener(onClickListener);
        }
        if ((stateHeader != null
                && stateHeader.isShow()
                && position == 0)
                ||
                (stateFooter != null
                        && stateFooter.isShow()
                        && position == getItemCount() - 1)
                ) {
        } else if (position - headerOffset < dataList.size()) {
            for (CustomAdapterItem item : customAdapterItems) {
                if (dataList.get(position - headerOffset) != null
                        && item.dataType() == dataList.get(position - headerOffset).getClass()) {
                    item.bindData(holder, dataList.get(position - headerOffset), position - headerOffset);
                    break;
                }
            }
        }
    }

    private synchronized void adjustStateSize(int stateCurrentSize) {
        this.stateCurrentSize += stateCurrentSize;
    }

    @Override
    public int getItemCount() {
        return dataList.size()
                + stateCurrentSize
                + ((stateHeader != null && stateHeader.isShow()) ? 1 : 0)
                + ((stateFooter != null && stateFooter.isShow()) ? 1 : 0);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        initOnClickListener();
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnDataItemClickListener(OnItemClickListener onDataItemClickListener) {
        initOnClickListener();
        this.onDataItemClickListener = onDataItemClickListener;
    }

    private void initOnClickListener() {
        if (onClickListener == null) {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.id.ca_position);
                    RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag(R.id.ca_holder);

                    if (onDataItemClickListener != null) {
                        if (position - headerOffset < dataList.size()
                                && position - headerOffset >= 0)
                            onDataItemClickListener.onItemClick(viewHolder, position - headerOffset);
                    }

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(viewHolder, position);
                    }
                }
            };
        }
    }
}
