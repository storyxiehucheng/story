package com.example.story.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.story.MyAdapter.FooterViewHolderBase;
import com.example.story.MyAdapter.ViewItemHolder;
import com.example.story.contantInfo.ContentInfo;
import com.example.story.myUtils.CheckNetWork;
import com.example.story.myUtils.DataProcess;
import com.example.story.myUtils.MyStaticData;
import com.example.story.xiaoyao.R;

import java.util.ArrayList;

/**
 *
 * Created by story on 2017/4/26.
 */

public class TechnologyFragment extends Fragment {

    private static Handler mParentHandler;
    private RecyclerView main_recyclerView;
    private ArrayList<ContentInfo> mContentInfoTechnologyList= MyStaticData.contentTechnologyList;
    private RecyclerTechnologyAdapter recyclerTechnologyAdapter;
    private Handler mHandler;
    private SwipeRefreshLayout technology_swipeRefresh;
    private Handler mFootHolderHandler;
    private String nextUrl="";
    private boolean isFirstTime=true;
    public static TechnologyFragment newTechnologyFragmentInstance(Handler handler)
    {
        mParentHandler=handler;
        return new TechnologyFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        refreshAdapter();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_techlonogy_fragment_layout,container,false);
        technology_swipeRefresh=(SwipeRefreshLayout)view.findViewById(R.id.technology_swipeRefresh);
        //为下拉刷新设置旋转的颜色
        technology_swipeRefresh.setColorSchemeResources(android.R.color.holo_green_dark,android.R.color.holo_blue_dark,R.color.colorPrimary);
        //为下拉刷新设置触发距离，即下滑多少距离才能触发下拉刷新
        technology_swipeRefresh.setDistanceToTriggerSync(400);
        //设置刷新动画的背景颜色
        technology_swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.my_white);
        //设置下拉刷新动画的大小
        technology_swipeRefresh.setSize(SwipeRefreshLayout.LARGE);
        HandlerProcess();
        technology_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(CheckNetWork.checkNetWorkAvailable(getActivity().getApplicationContext()))
                {
                    //更新数据列表
                    refreshTechnologyContentInfoList(true);
                }
                else
                {
                    mHandler.sendEmptyMessageDelayed(2,500);
                }
            }
        });
        main_recyclerView=(RecyclerView)view.findViewById(R.id.technology_recyclerView);
        //为recyclerView新建一个布局管理器
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity().getApplicationContext());
        //设置recyclerView的布局管理器
        main_recyclerView.setLayoutManager(linearLayoutManager);
        recyclerTechnologyAdapter = new RecyclerTechnologyAdapter();
        main_recyclerView.setAdapter(recyclerTechnologyAdapter);
        main_recyclerView.addOnScrollListener(new EndRecyclerVIewListener(linearLayoutManager) {
            @Override
            public void loadMore() {
                if(CheckNetWork.checkNetWorkAvailable(getActivity().getApplicationContext()))
                {
                    //滑动到底，则更新数据,并将更新后的数据发送handler处理
                    //只有在推荐界面才显示更新
                    refreshAdapter();
                    refreshTechnologyContentInfoList(false);
                }
                else
                {
                    if(mHandler!=null)
                    {
                        mHandler.sendEmptyMessageDelayed(3,500);
                    }
                }
            }
        });
        if(isFirstTime)
        {
            isFirstTime=false;
            technology_swipeRefresh.setRefreshing(true);
            if(CheckNetWork.checkNetWorkAvailable(getActivity().getApplicationContext()))
            {
                refreshTechnologyContentInfoList(true);
            }
            else
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        technology_swipeRefresh.setRefreshing(false);
                    }
                },1000);
            }
        }

        return view;
    }

    private void refreshTechnologyContentInfoList(boolean isPullDown) {
        if(isPullDown)
        {
            DataProcess.refreshDatalistPullDown(mContentInfoTechnologyList,mHandler,"http://iecas.win/news/?newstype=tech");
        }
        else
        {
            DataProcess.refreshPullUP(mContentInfoTechnologyList,mHandler,nextUrl);
        }

    }

    private void HandlerProcess() {
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            if (technology_swipeRefresh != null) {
                                technology_swipeRefresh.setRefreshing(false);
                            }
                            nextUrl=(String)msg.obj;
                            refreshAdapter();
                            break;
                        case 1:
                            if(mFootHolderHandler!=null)
                            {
                                mFootHolderHandler.sendEmptyMessage(0);
                            }
                            nextUrl=(String)msg.obj;
                            break;
                        case 2:
                            if (technology_swipeRefresh != null) {
                                technology_swipeRefresh.setRefreshing(false);
                            }
                            refreshAdapter();
                            break;
                        case 3:
                              if(mFootHolderHandler!=null)
                              {
                                  mFootHolderHandler.sendEmptyMessage(1);
                              }
                            break;
                        case 4:

                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }
    private void refreshAdapter()
    {
        if(recyclerTechnologyAdapter!=null)
        {
            recyclerTechnologyAdapter.notifyDataSetChanged();
        }
    }
    private class RecyclerTechnologyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private static final int TYPE_FOOTER=1;
        private static final int TYPE_CONTENT=2;
        private FooterViewHolderBase mmFooterHolder;

        public RecyclerTechnologyAdapter() {
            mFootHolderHandler=new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what)
                    {
                        case 0:
                            if(mmFooterHolder!=null)
                            {
                                mmFooterHolder.setFootViewVisible(false);
                            }
                            break;
                        case 1:
                            if(mmFooterHolder!=null)
                            {
                                mmFooterHolder.setProgressVisible(false);
                                mmFooterHolder.setFooterText("没有更多了");
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //如果是内容的type标志位
            if(viewType==TYPE_CONTENT)
            {
                //则加载view为内容的布局
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_layout,parent,false);
                return new TechnologyItemViewHolder(view,mContentInfoTechnologyList,getContext(),getActivity());
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
            if(type==TYPE_CONTENT&&(holder instanceof TechnologyItemViewHolder))
            {
                if(!mContentInfoTechnologyList.isEmpty())
                {
                    ContentInfo contentInfo= mContentInfoTechnologyList.get(position);
                    TechnologyItemViewHolder itemContentHolder=(TechnologyItemViewHolder)holder;
                    if(MyStaticData.collectionIdList.contains(contentInfo.getId()))
                    {
                        contentInfo.setCollection(true);
                    }
                    else
                    {
                        contentInfo.setCollection(false);
                    }
                    if(MyStaticData.hasReadIdList.contains(contentInfo.getId()))
                    {
                        contentInfo.setRead(true);
                    }
                    else
                    {
                        contentInfo.setRead(false);
                    }
                    itemContentHolder.setItemView(contentInfo);
                }

            }
            //如果该条是显示最后刷新的
            else if(type==TYPE_FOOTER&&(holder instanceof FooterViewHolderBase))
            {
                mmFooterHolder=(FooterViewHolderBase)holder;
                if(position>3)
                {
                    mmFooterHolder.setFootViewVisible(true);
                    mmFooterHolder.setProgressVisible(true);
                    mmFooterHolder.setFooterText("");
                    mFootHolderHandler.sendEmptyMessageDelayed(1,5000);
                }
                else
                {
                    mmFooterHolder.setFootViewVisible(false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mContentInfoTechnologyList==null? 0:mContentInfoTechnologyList.size()+1;
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

    private class TechnologyItemViewHolder extends ViewItemHolder
    {

        public TechnologyItemViewHolder(View itemView, ArrayList<ContentInfo> contentList, Context context, Activity activity) {
            super(itemView, contentList, context, activity);
        }

        @Override
        public void enterAnalysisActivity(int position, int clickState) {
            Intent intent = new Intent(getActivity(), AnalysisActivity.class);
            //利用intent传递两个参数值，分析界面根据这两个参数来做相应的显示
            intent.putExtra("position", position);
            intent.putExtra("clickState", clickState);
            intent.putExtra("page",3);
            startActivity(intent);
        }

        @Override
        public void notifyAdapterRefresh(boolean isShowCollection) {
            refreshAdapter();
            if(isShowCollection)
            {
                mParentHandler.sendEmptyMessage(0);
            }

        }

        @Override
        public void collectionRemoveFromID(int id) {
            for(int i=0;i<MyStaticData.contentListCollection.size();i++)
            {
                if(MyStaticData.contentListCollection.get(i).getId()==id)
                {
                    MyStaticData.contentListCollection.remove(MyStaticData.contentListCollection.get(i));
                    break;
                }
            }
            for(int i=0;i<mContentInfoTechnologyList.size();i++)
            {
                if(mContentInfoTechnologyList.get(i).getId()==id)
                {
                    mContentInfoTechnologyList.get(i).setCollection(false);
                    break;
                }
            }
            refreshAdapter();
        }
    }


}
