package com.example.story.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 *recyclerViwe的滚动监听器，主要是监听是否滚动到底
 *@author story
 *@time 2017/4/15 10:59
 */
public abstract class EndRecyclerVIewListener extends RecyclerView.OnScrollListener {
    private int lastVisibleItemNumber=0;
    private LinearLayoutManager mLinearLayoutManager;

    public EndRecyclerVIewListener(LinearLayoutManager mLinearLayoutManager) {
        this.mLinearLayoutManager = mLinearLayoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //如果滚动状态停止，并且最后一条可见Item的位置+1=总的item数，则表示滚动到底了
        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemNumber + 1 ==
                recyclerView.getAdapter().getItemCount()) {
            Log.e("story","正在加载更多啦.....");
            //利用抽象函数，将滚动结果的处理，交由调用者来处理
            loadMore();
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //设置最后一条可见的item的位置
        lastVisibleItemNumber=mLinearLayoutManager.findLastVisibleItemPosition();

    }

    /**
     * 定义一个抽象函数，让调用者实现，加载更多的具体处理事件
     */
    public abstract void loadMore();
}
