package com.example.recyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.newtest.Mycommon;
import com.example.newtest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/10/31.
 */
public class RecyclerActivity extends AppCompatActivity implements RecyclerAdapter.OnRecyclerViewListener{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private List<RecyclerBean> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initData();
        bindView();
    }

    private void bindView(){
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.scrollToPosition(list.size()/2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerAdapter(list);
        adapter.setOnRecyclerViewListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initData(){
        list = new ArrayList<RecyclerBean>();
        RecyclerBean bean;
        for (int i=0;i<36;i++){
            bean = new RecyclerBean("","编号-"+i);
            list.add(bean);
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.i("click","click--"+position);
        Mycommon.myToast("click--"+position);
        adapter.addData(position,new RecyclerBean(null,"new添加"+position));
    }

    @Override
    public boolean onItemLongClick(int position) {
        Log.i("longClick","longClick--"+position);
        Mycommon.myToast("longClick--"+position);
        adapter.removeData(position);
        return false;
    }
}
