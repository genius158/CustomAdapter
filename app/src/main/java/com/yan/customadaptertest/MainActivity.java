package com.yan.customadaptertest;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yan.adapter.CustomAdapter;
import com.yan.adapter.CustomAdapterItem;
import com.yan.adapter.OnItemClickListener;
import com.yan.adapter.StateAdapterItem;
import com.yan.customadaptertest.LoadMore.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.yan.adapter.StateAdapterItem.FOOTER;
import static com.yan.adapter.StateAdapterItem.HEADER;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomAdapter adapter;
    private LoadMoreWrapper moreWrapper;
    private int times;
    List<Object> objects;
    List<Object> dataList;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        objects = new ArrayList<>();
        objects.add(new Integer(1));
        objects.add(new CustomAdapter(null));
        objects.add(new Integer(1));
        objects.add(new Float(1.00001));
        objects.add("sdfsdfasdfasdfasdf");
        objects.add(new Float(1.00001));
        objects.add(new Float(1.00001));
        objects.add(new Integer(1));
        objects.add(new CustomAdapter(null));
        objects.add("sdfsdfasdfasdfasdf");
        objects.add(new CustomAdapter(null));
        objects.add(new Integer(1));
        objects.add(new CustomAdapter(null));
        objects.add(new Float(1.00001));
        objects.add(new CustomAdapter(null));
        objects.add("sdfsdfasdfasdfasdf");

        dataList = new ArrayList<>();
        dataList.addAll(objects);

        adapter = initAdapter(dataList);
        adapter.setOnDataItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                ((HolderTest) holder).textView.append(position + "");
                toast.setText("onItemClick: position " + position);
                toast.show();
            }
        });

        moreWrapper = new LoadMoreWrapper(adapter);
        moreWrapper.setLoadMoreView(
                getLoadMoreView()
        );
        moreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        dataList.add("dsfasdff");
                        dataList.add(new Integer(2322323));
                        dataList.add(new Float(0009f));
                        moreWrapper.notifyDataSetChanged();
                    }
                }.execute();
            }
        });

        recyclerView.setAdapter(moreWrapper);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        swipeRefreshLayout.setRefreshing(false);
                        dataList.clear();
                        StateAdapterItem dataError = adapter.findStateItem("DataError");
                        ((HolderTest2) dataError.getHolder()).textView.append("!");

                        ((HolderTest2) adapter.getStateFooter().getHolder())
                                .textView.setText("footer" + times);

                        StateAdapterItem noWifi = adapter.findStateItem("NO_WIFI");
                        if (noWifi.getHolder() != null)
                            ((HolderTest2) noWifi.getHolder()).textView.append(".");
                        StateAdapterItem noData = adapter.findStateItem("NO_DATA");
                        if (noData.getHolder() != null)
                            ((HolderTest2) noData.getHolder()).textView.append("-");

                        moreWrapper.clearLoadMore(true);

                        if (times % 4 == 0) {
                            adapter.show("DataError")
                                    .hide("NO_WIFI")
                                    .hide("NO_DATA")
                                    .hideHeader(false)
                                    .hideFooter(false)
//                            .commit();
                            ;
                            moreWrapper.notifyDataSetChanged();
                        } else if (times % 4 == 1) {
                            adapter.show("NO_WIFI")
                                    .hide("DataError")
                                    .hide("NO_DATA")
                                    .hideHeader(false)
                                    .hideFooter(false)
//                            .commit();
                            ;
                            moreWrapper.notifyDataSetChanged();
                        } else if (times % 4 == 2) {
                            adapter.show("NO_DATA")
                                    .hide("DataError")
                                    .hide("NO_WIFI")
                                    .showHeader(false)
                                    .showFooter(false)
//                            .commit();
                            ;
                            moreWrapper.notifyDataSetChanged();
                        } else if (times % 4 == 3) {
                            dataList.addAll(objects);
                            adapter.hide("NO_DATA")
                                    .hide("DataError")
                                    .hide("NO_WIFI")
                                    .showHeader(false)
                                    .hideFooter(false)
//                            .commit();
                            ;
                            moreWrapper.clearLoadMore(false);
                            moreWrapper.notifyDataSetChanged();
                        }
                        times++;
                    }
                }.execute();
            }
        });
    }

    private View getLoadMoreView() {
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        View loadMore = getLayoutInflater().inflate(R.layout.item_type_loadmore, null);
        loadMore.setLayoutParams(layoutParams);
        return loadMore;
    }

    class HolderTest extends RecyclerView.ViewHolder {
        public TextView textView;

        public HolderTest(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.test1);
        }
    }

    class HolderTest1 extends HolderTest {

        public HolderTest1(View itemView) {
            super(itemView);
        }
    }

    class HolderTest2 extends HolderTest {

        public HolderTest2(View itemView) {
            super(itemView);
        }
    }

    private CustomAdapter initAdapter(List objects) {
        return new CustomAdapter(objects)
                //数据类型一
                .addAdapterItem(new CustomAdapterItem<HolderTest1, Integer>() {
                    @Override
                    public Class dataType() {
                        return Integer.class;
                    }

                    @Override
                    public HolderTest1 viewHolder(ViewGroup parent) {
                        return new HolderTest1(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_1, parent, false)
                        );
                    }

                    @Override
                    public void bindData(HolderTest1 holder, Integer item, int position) {
                        holder.textView.setText(item + "");
                    }
                })

                //数据类型二
                .addAdapterItem(new CustomAdapterItem<HolderTest2, String>() {
                    @Override
                    public Class dataType() {
                        return String.class;
                    }

                    @Override
                    public HolderTest2 viewHolder(ViewGroup parent) {
                        return new HolderTest2(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_2, parent, false)
                        );
                    }

                    @Override
                    public void bindData(HolderTest2 holder, String item, int position) {
                        holder.textView.setText(item);
                    }
                })

                //数据类型三
                .addAdapterItem(new CustomAdapterItem<HolderTest1, Float>() {

                    @Override
                    public Class dataType() {
                        return Float.class;
                    }

                    @Override
                    public HolderTest1 viewHolder(ViewGroup parent) {
                        return new HolderTest1(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_3, parent, false)
                        );
                    }

                    @Override
                    public void bindData(HolderTest1 holder, Float item, int position) {
                        holder.textView.setText(item + "");
                    }
                })

                //数据类型四
                .addAdapterItem(new CustomAdapterItem<HolderTest1, CustomAdapter>() {
                    @Override
                    public Class dataType() {
                        return CustomAdapter.class;
                    }

                    @Override
                    public HolderTest1 viewHolder(ViewGroup parent) {
                        return new HolderTest1(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_2, parent, false)
                        );
                    }

                    @Override
                    public void bindData(HolderTest1 holder, CustomAdapter item, int position) {
                        (holder).textView.setText(item.toString());
                    }
                })

//                //状态类型一
//                .addAdapterItem(new StateAdapterItem<HolderTest2>("DataError", false,
//                        getHolder()) {
//                    @Override
//                    public HolderTest2 viewHolder(ViewGroup parent) {
//                        return null;
//                    }
//                })
//
//                //状态类型二
//                .addAdapterItem(new StateAdapterItem<HolderTest2>("NO_WIFI", false) {
//                    @Override
//                    public HolderTest2 viewHolder(ViewGroup parent) {
//                        HolderTest2 holderTest2 = new HolderTest2(
//                                LayoutInflater.from(MainActivity.this).inflate(R.layout.state_view, parent, false)
//                        );
//                        holderTest2.textView.setText("确保网络连接正常");
//                        return holderTest2;
//                    }
//                })
//
//                //状态类型三
//                .addAdapterItem(new StateAdapterItem<HolderTest2>("NO_DATA", false) {
//                    @Override
//                    public HolderTest2 viewHolder(ViewGroup parent) {
//                        HolderTest2 holderTest2 = new HolderTest2(
//                                LayoutInflater.from(MainActivity.this).inflate(R.layout.state_view, parent, false)
//                        );
//                        holderTest2.textView.setText("没有加载到数据");
//                        return holderTest2;
//                    }
//                })
//
//                //header
//                .addAdapterItem(new StateAdapterItem<HolderTest2>(HEADER, true) {
//                    @Override
//                    public HolderTest2 viewHolder(ViewGroup parent) {
//                        HolderTest2 holderTest2 = new HolderTest2(
//                                LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_header, parent, false)
//                        );
//                        holderTest2.textView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                toast.setText("header");
//                                toast.show();
//                            }
//                        });
//                        return holderTest2;
//                    }
//                })
//
//                //footer
//                .addAdapterItem(new StateAdapterItem<HolderTest2>(FOOTER, getFooter(), false) {
//                    @Override
//                    public HolderTest2 viewHolder(ViewGroup parent) {
//                        return null;
//                    }
//                })

                ;
    }

    private HolderTest2 getHolder() {
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.state_view, null);
        view.setLayoutParams(layoutParams);
        HolderTest2 holderTest2 = new HolderTest2(view);
        holderTest2.textView.setText("获取数据出错");
        return holderTest2;
    }

    private HolderTest2 getFooter() {
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_footer, null);
        view.setLayoutParams(layoutParams);
        HolderTest2 holderTest2 = new HolderTest2(view);
        holderTest2.textView.setText("footer");
        return holderTest2;
    }

}
















