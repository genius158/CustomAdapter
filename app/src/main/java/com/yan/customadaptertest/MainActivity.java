package com.yan.customadaptertest;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yan.adapter.CustomAdapter;
import com.yan.adapter.CustomAdapterItem;
import com.yan.adapter.StateAdapterItem;

import java.util.ArrayList;
import java.util.List;

import static com.yan.adapter.StateAdapterItem.FOOTER;
import static com.yan.adapter.StateAdapterItem.HEADER;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomAdapter adapter;
    private int times;
    List<Object> objects;
    List<Object> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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
        recyclerView.setAdapter(adapter);

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
                                .textView.setText("footer"+times);

                        StateAdapterItem noWifi = adapter.findStateItem("NO_WIFI");
                        if (noWifi.getHolder() != null)
                            ((HolderTest2) noWifi.getHolder()).textView.append(".");
                        StateAdapterItem noData = adapter.findStateItem("NO_DATA");
                        if (noData.getHolder() != null)
                            ((HolderTest2) noData.getHolder()).textView.append("-");

                        if (times % 4 == 0) {
                            adapter.show("DataError")
                                    .hide("NO_WIFI")
                                    .hide("NO_DATA")
                                    .hideHeader(false)
                                    .hideFooter(false)
                                    .commit();
                        } else if (times % 4 == 1) {
                            adapter.show("NO_WIFI")
                                    .hide("DataError")
                                    .hide("NO_DATA")
                                    .hideHeader(false)
                                    .hideFooter(false)
                                    .commit();
                        } else if (times % 4 == 2) {
                            adapter.show("NO_DATA")
                                    .hide("DataError")
                                    .hide("NO_WIFI")
                                    .showHeader(false)
                                    .showFooter(false)
                                    .commit();
                        } else if (times % 4 == 3) {
                            dataList.addAll(objects);
                            // adapter.findStateItem("DataError").hide();
                            // adapter.findStateItem("NO_WIFI").hide();
                            // adapter.findStateItem("NO_DATA").hide();
                            // adapter.showHeader(true);
                            adapter.hide("NO_DATA")
                                    .hide("DataError")
                                    .hide("NO_WIFI")
                                    .showHeader(false)
                                    .showFooter(false)
                                    .commit();
                        }
                        times++;
                    }
                }.execute();
            }
        });
    }

    class HolderTest1 extends RecyclerView.ViewHolder {
        public TextView textView;

        public HolderTest1(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.test1);
        }
    }

    class HolderTest2 extends RecyclerView.ViewHolder {
        public TextView textView;

        public HolderTest2(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.test1);
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

                //状态类型一
                .addAdapterItem(new StateAdapterItem<HolderTest2>("DataError", false,
                        getHolder()) {
                    @Override
                    public HolderTest2 viewHolder(ViewGroup parent) {
                        return null;
                    }
                })

                //状态类型二
                .addAdapterItem(new StateAdapterItem<HolderTest2>("NO_WIFI", false) {
                    @Override
                    public HolderTest2 viewHolder(ViewGroup parent) {
                        HolderTest2 holderTest2 = new HolderTest2(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.state_view, parent, false)
                        );
                        holderTest2.textView.setText("确保网络连接正常");
                        return holderTest2;
                    }
                })

                //状态类型三
                .addAdapterItem(new StateAdapterItem<HolderTest2>("NO_DATA", false) {
                    @Override
                    public HolderTest2 viewHolder(ViewGroup parent) {
                        HolderTest2 holderTest2 = new HolderTest2(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.state_view, parent, false)
                        );
                        holderTest2.textView.setText("没有加载到数据");
                        return holderTest2;
                    }
                })
                //状态类型四
                .addAdapterItem(new StateAdapterItem<HolderTest2>() {
                    @Override
                    public HolderTest2 viewHolder(ViewGroup parent) {
                        HolderTest2 holderTest2 = new HolderTest2(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_3, parent, false)
                        );
                        holderTest2.textView.setText("固定的固定的");
                        return holderTest2;
                    }
                })
                //header
                .addAdapterItem(new StateAdapterItem<HolderTest2>(HEADER) {
                    @Override
                    public HolderTest2 viewHolder(ViewGroup parent) {
                        return new HolderTest2(
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_header, parent, false)
                        );
                    }
                })
                //footer
                .addAdapterItem(new StateAdapterItem<HolderTest2>(FOOTER,getFooter()) {
                    @Override
                    public HolderTest2 viewHolder(ViewGroup parent) {
                        return null;
                    }
                })

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
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics()));
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_type_footer, null);
        view.setLayoutParams(layoutParams);
        HolderTest2 holderTest2 = new HolderTest2(view);
        holderTest2.textView.setText("footer");
        return holderTest2;
    }

}
















