package com.example.story.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.story.MyAdapter.FooterViewHolderBase;
import com.example.story.MyAdapter.ViewItemHolder;
import com.example.story.contantInfo.ContentInfo;
import com.example.story.myUtils.CheckNetWork;
import com.example.story.myUtils.DataProcess;
import com.example.story.myUtils.HttpUtils;
import com.example.story.myUtils.JsonUtil;
import com.example.story.myUtils.MyStaticData;
import com.example.story.userManager.ImageManager;
import com.example.story.xiaoyao.R;

import java.util.ArrayList;

/**
 * Created by story on 2017/4/26.
 */

public class CollectionFragment extends Fragment {
    private static Handler mParentHandler;
    private RecyclerCollectionAdapter mRecyclerAdapterCollection;
    private RecyclerView collection_recyclerView;
    private Handler mHandler;
    private SwipeRefreshLayout collection_swipeRefresh;
    private static boolean isFirstTime=true;

    public static CollectionFragment newCollectionFragmentInstance(Handler handler)
    {
        mParentHandler=handler;
        return new CollectionFragment();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        refreshAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    private void refreshAdapter() {
        Log.e("story","收集的个数："+MyStaticData.contentListCollection.size());
        if(mRecyclerAdapterCollection!=null)
        {
            mRecyclerAdapterCollection.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_collectionfragment_layout,container,false);
        collection_swipeRefresh=(SwipeRefreshLayout)view.findViewById(R.id.collection_swipeRefresh);
        //为下拉刷新设置旋转的颜色
        collection_swipeRefresh.setColorSchemeResources(android.R.color.holo_green_dark,android.R.color.holo_blue_dark,R.color.colorPrimary);
        //为下拉刷新设置触发距离，即下滑多少距离才能触发下拉刷新
        collection_swipeRefresh.setDistanceToTriggerSync(400);
        //设置刷新动画的背景颜色
        collection_swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.my_white);
        //设置下拉刷新动画的大小
        collection_swipeRefresh.setSize(SwipeRefreshLayout.LARGE);
        HandlerProcess();
        collection_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(0,1000);
            }
        });
        collection_recyclerView=(RecyclerView)view.findViewById(R.id.collection_recyclerView);
        //为recyclerView新建一个布局管理器
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity().getApplicationContext());
        //设置recyclerView的布局管理器
        collection_recyclerView.setLayoutManager(linearLayoutManager);
        //如果是收藏列表，则设置收藏列表的数据适配器
        mRecyclerAdapterCollection=new RecyclerCollectionAdapter();
        collection_recyclerView.setAdapter(mRecyclerAdapterCollection);
        if(isFirstTime)
        {
            isFirstTime=false;
            collection_swipeRefresh.setRefreshing(true);
            if(CheckNetWork.checkNetWorkAvailable(getActivity().getApplicationContext()))
            {
                refreshCollectionList();
            }
            else
            {
                mHandler.sendEmptyMessageDelayed(0,1000);
            }
        }

        return view;
    }
        /**
     * 刷新整个收藏列表
     */
    private void refreshCollectionList() {
       final ArrayList<Integer> collectionID=MyStaticData.collectionIdList;
        final ArrayList<ContentInfo> tempList=new ArrayList<>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<collectionID.size();i++)
                {
                    Log.e("story","正在下载收藏ID为"+collectionID.get(i));
                    String jsonContent = HttpUtils.getJsonContentWithId(collectionID.get(i));
                    if(!jsonContent.equals(""))
                    {
                        ContentInfo contentInfo = JsonUtil.jsonToContentInfo(jsonContent);
                        if(contentInfo!=null)
                        {
                            tempList.add(contentInfo);
                        }

                    }
                }
                if(!tempList.isEmpty())
                {
                    for(int i=0;i<tempList.size();i++)
                    {
                        boolean isAdd=true;
                        for(int k=0;k<MyStaticData.contentListCollection.size();k++)
                        {
                            if(tempList.get(i).getId()==MyStaticData.contentListCollection.get(k).getId())
                            {
                                isAdd=false;
                                break;
                            }
                        }
                        if(isAdd)
                        {
                            ContentInfo contentInfo=tempList.get(i);
                            tempList.get(i).setCollection(true);
                            if(MyStaticData.hasReadIdList.contains(tempList.get(i).getId()))
                            {
                                tempList.get(i).setRead(true);
                            }
                            MyStaticData.contentListCollection.add(0,tempList.get(i));
                        }
                    }
                }
                if(mHandler!=null)
                {
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
        thread.start();
    }
    private void HandlerProcess() {
        if(mHandler==null)
        {
            mHandler=new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    switch(msg.what)
                    {
                        case 0:
                            if(collection_swipeRefresh!=null)
                            {
                                collection_swipeRefresh.setRefreshing(false);
                            }
                            refreshAdapter();
                            break;
                        case 1:
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    private class RecyclerCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private static final int TYPE_FOOTER=1;
        private static final int TYPE_CONTENT=2;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //如果是内容的type标志位
            if(viewType==TYPE_CONTENT)
            {
                //则加载view为内容的布局
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_layout,parent,false);
                return new CollectionItemViewHolder(view,MyStaticData.contentListCollection,getContext(),getActivity());
            }
            //如果是上拉刷新的type标志位
            else if(viewType==TYPE_FOOTER)
            {
                //则加载view为刷新的布局文件
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.pull_up_refresh,parent,false);
                return new FooterViewHolderBase(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //得到该条item对应的viewType
            int type=getItemViewType(position);
            //如果类型是显示内容
            if(type==TYPE_CONTENT&&(holder instanceof CollectionItemViewHolder))
            {
                if(!MyStaticData.contentListCollection.isEmpty())
                {
                    ContentInfo contentInfo = MyStaticData.contentListCollection.get(position);
                    CollectionItemViewHolder itemContentHolder=(CollectionItemViewHolder)holder;
                    itemContentHolder.setItemView(contentInfo);
                }
            }
            //如果该条是显示最后刷新的
            else if(type==TYPE_FOOTER&&(holder instanceof FooterViewHolderBase))
            {
                FooterViewHolderBase mmFooterHolder=(FooterViewHolderBase)holder;
                if(position==0)
                {
                    mmFooterHolder.setFootViewVisible(true);
                    mmFooterHolder.setProgressVisible(false);
                    mmFooterHolder.setFooterText("还没有添加印迹，赶紧去按手印吧！");
                }
                else
                {
                    mmFooterHolder.setFootViewVisible(false);
                }

            }
        }

        @Override
        public int getItemCount() {
            return MyStaticData.contentListCollection.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position+1==getItemCount())
            {
                return TYPE_FOOTER;
            }
            else
            {
                return  TYPE_CONTENT;
            }
        }
    }

    private class CollectionItemViewHolder extends ViewItemHolder
    {

        public CollectionItemViewHolder(View itemView, ArrayList<ContentInfo> contentList, Context context, Activity activity) {
            super(itemView, contentList, context, activity);
        }

        @Override
        public void enterAnalysisActivity(int position, int clickState) {
            Intent intent = new Intent(getActivity(), AnalysisActivity.class);
            //利用intent传递两个参数值，分析界面根据这两个参数来做相应的显示
            intent.putExtra("position", position);
            intent.putExtra("clickState", clickState);
            intent.putExtra("page",4);
            startActivity(intent);
        }

        @Override
        public void notifyAdapterRefresh(boolean isShowCollection) {
            refreshAdapter();
        }

        @Override
        public void collectionRemoveFromID(int id) {
            for(int i=0;i<MyStaticData.contentListCollection.size();i++)
            {
                if(MyStaticData.contentListCollection.get(i).getId()==id)
                {
                    MyStaticData.contentListCollection.remove(MyStaticData.contentListCollection.get(i));
                    Log.e("story","删除收藏ID"+id);
                    break;
                }
            }
            refreshAdapter();
        }
    }
}
