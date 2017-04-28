package com.example.story.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.story.contantInfo.ContentInfo;
import com.example.story.myUtils.CheckNetWork;
import com.example.story.myUtils.DataProcess;
import com.example.story.myUtils.JsonUtil;
import com.example.story.myUtils.MyStaticData;
import com.example.story.myUtils.ShareUtils;
import com.example.story.userManager.ImageManager;
import com.example.story.xiaoyao.R;
import java.util.ArrayList;

/**
 *
 * Created by story on 2017/4/12.
 */

public class MainFragment extends Fragment {

    /**
     * fragment的参数的key
     */
    private String nextUrl="";
    private RecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<ContentInfo> mContentInfoList=MyStaticData.contentList;
    private Handler mHandler;
    private static Handler parentHandler;
    private Handler mFootHolderHandler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  boolean isFirstTime=true;
    //利用静态方法新建一个fragment实例
    public static MainFragment newInstance(Handler handler)
    {
        parentHandler=handler;
        return new MainFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // TODO: 2017/4/17
    }

    /**
     * fragment页面切换的时候，数据的更新
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        notifyRefreshData();
    }
    /**
     * activity切换的时候的数据更新
     */
    @Override
    public void onResume() {
        super.onResume();
        notifyRefreshData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.e("story","进入onCreateView----"+mPage);
        View view =inflater.inflate(R.layout.content_xiaoyao__main,container,false);
        //获取下拉刷新控件ID
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        //为下拉刷新设置旋转的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark,android.R.color.holo_blue_dark,R.color.colorPrimary);
        //为下拉刷新设置触发距离，即下滑多少距离才能触发下拉刷新
        swipeRefreshLayout.setDistanceToTriggerSync(400);
        //设置刷新动画的背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.my_white);
        //设置下拉刷新动画的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        //为下拉刷新设置监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(CheckNetWork.checkNetWorkAvailable(getActivity().getApplicationContext()))
                {
                        refreshContentInfoList(true);
                }
                else
                {
                    mHandler.sendEmptyMessageDelayed(2,500);
                }
            }
        });

        //获取recycler的控件ID
        mRecyclerView= (RecyclerView) view.findViewById(R.id.main_recyclerView);
        //为recyclerView新建一个布局管理器
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity().getApplicationContext());
        //设置recyclerView的布局管理器
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //如果是推荐列表页，则设置推荐列表的数据适配器
        mRecyclerAdapter=new RecyclerAdapter(mContentInfoList);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        //为RecyclerView添加滑动事件的监听，监听是否已经滑动到底了
        mRecyclerView.addOnScrollListener(new EndRecyclerVIewListener(linearLayoutManager) {
            @Override
            public void loadMore() {
                Log.e("story","进入上拉，更多了.......");
                if(CheckNetWork.checkNetWorkAvailable(getActivity().getApplicationContext()))
                {
                    //滑动到底，则更新数据,并将更新后的数据发送handler处理
                    //只有在推荐界面才显示更新
                    notifyRefreshData();
                    refreshContentInfoList(false);
                }
                else
                {
                    mHandler.sendEmptyMessageDelayed(3,500);
                }
            }
        });
        //处理总的刷新结果
        handleRefresh();
        if(isFirstTime)
        {
            isFirstTime=false;
            swipeRefreshLayout.setRefreshing(true);
            if(CheckNetWork.checkNetWorkAvailable(getActivity().getApplicationContext()))
            {
                refreshContentInfoList(true);
            }
            else
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        }

        return view;
    }


    /**
     * 更新数据适配器
     */
    private void notifyRefreshData() {

        if(mRecyclerAdapter!=null)
        {
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 利用handler处理刷新的事件
     */
    private void handleRefresh()
    {
        if(mHandler==null)
        {
            mHandler=new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what)
                    {
                        //推荐界面的下拉刷新之后，进入的消息处理
                        case 0:
                            //停止刷新
                            if(swipeRefreshLayout!=null)
                            {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            nextUrl=(String)msg.obj;
                            //判断数据适配器是否为空，不为空则更新数据适配器
                            notifyRefreshData();
                            break;
                        case 1:
                            if(mFootHolderHandler!=null)
                            {
                                mFootHolderHandler.sendEmptyMessage(0);
                            }
                            nextUrl=(String)msg.obj;
                            break;
                        case 2:
                            if(swipeRefreshLayout!=null)
                            {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            notifyRefreshData();
                            break;
                        case 3:
                            if(mFootHolderHandler!=null)
                            {
                                mFootHolderHandler.sendEmptyMessage(1);
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    /**
     * 更新数据列表
     * @param isPullDown  判断是上拉刷新，还是下拉刷新
     * @return 返回更新后的数据列表
     */
    private void refreshContentInfoList(final boolean isPullDown) {
       if(isPullDown)
       {
           DataProcess.refreshDatalistPullDown(mContentInfoList,mHandler,"http://iecas.win/news/");
       }
       else
       {
            DataProcess.refreshPullUP(mContentInfoList,mHandler,nextUrl);
       }
    }
    /**
     * 为recyclerView设置数据适配器
     */
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private static final int TYPE_FOOTER=1;
        private static final int TYPE_CONTENT=2;
        private ArrayList<ContentInfo> mmContentInfoList;
        private FooterViewHolder mmFooterHolder;

        public RecyclerAdapter(ArrayList<ContentInfo> contentInfoList) {
            this.mmContentInfoList = contentInfoList;
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
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            //如果是内容的type标志位
            if(viewType==TYPE_CONTENT)
            {
                //则加载view为内容的布局
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_layout,parent,false);
                return new RecyclerItemViewHolder(view);
            }
            //如果是上拉刷新的type标志位
            else if(viewType==TYPE_FOOTER)
            {
               //则加载view为刷新的布局文件
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.pull_up_refresh,parent,false);
                return new FooterViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.e("story","onBindViewHolder");
            //得到该条item对应的viewType
            int type=getItemViewType(position);
            //如果类型是显示内容
            if(type==TYPE_CONTENT&&(holder instanceof RecyclerItemViewHolder))
            {
                if(!mmContentInfoList.isEmpty())
                {
                    ContentInfo contentInfo= mmContentInfoList.get(position);
                    RecyclerItemViewHolder itemContentHolder=(RecyclerItemViewHolder)holder;
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
            else if(type==TYPE_FOOTER&&(holder instanceof FooterViewHolder))
            {
                mmFooterHolder=(FooterViewHolder)holder;
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
            return (mmContentInfoList ==null)||(mmContentInfoList.isEmpty()) ? 0: mmContentInfoList.size()+1;
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

    /**
     * 为最后的刷新界面设置viewHolder
     */
    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_pull_up;
        private final RelativeLayout pull_up_linearLayout;
        private final ProgressBar progress_pull_up;

        private FooterViewHolder(View itemView) {
            super(itemView);
            tv_pull_up= (TextView) itemView.findViewById(R.id.tv_pull_up);
            pull_up_linearLayout=(RelativeLayout)itemView.findViewById(R.id.pull_up_linearLayout);
            progress_pull_up=(ProgressBar)itemView.findViewById(R.id.progress_pull_up);
        }

        private void setFooterText(String text)
        {
            tv_pull_up.setText(text);
        }

        private void setProgressVisible(boolean isVisible)
        {
            if(isVisible)
            {
                progress_pull_up.setVisibility(View.VISIBLE);
            }
            else
                progress_pull_up.setVisibility(View.GONE);
        }
        private void setFootViewVisible(boolean isVisible)
        {
            if(isVisible)
            {
                pull_up_linearLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                pull_up_linearLayout.setVisibility(View.GONE);
            }
        }
    }
    /**
     *
     *创建一个ViewHolder类，显示recyclerView里面的布局
     *@author story
     *@time 2017/4/9 19:53
     */
    private class RecyclerItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LinearLayout mLinearLayout;
        private final ImageView iv_view_false;
        private final ImageView iv_view_true;
        private final ImageView iv_view_share;
        private final ImageView iv_view_collection;
        private final TextView tv_view_Title;
        private final TextView tv_view_content;
        private ImageManager mImageManager=new ImageManager(getContext());
        private final LinearLayout ll_content_item_layout;

        private RecyclerItemViewHolder(View itemView) {
            super(itemView);
            tv_view_content = (TextView) itemView.findViewById(R.id.tv_view_content);
            tv_view_Title = (TextView) itemView.findViewById(R.id.tv_view_Title);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_view_picture);
            Display display=getActivity().getWindowManager().getDefaultDisplay();
            Point size=new Point();
            display.getSize(size);
            int width=size.x;
            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)mLinearLayout.getLayoutParams();
            params.height=(int)(width*0.618);
            mLinearLayout.setLayoutParams(params);
            iv_view_false = (ImageView) itemView.findViewById(R.id.iv_view_false);
            iv_view_true = (ImageView) itemView.findViewById(R.id.iv_view_true);
            iv_view_share = (ImageView) itemView.findViewById(R.id.iv_view_share);
            iv_view_collection = (ImageView) itemView.findViewById(R.id.iv_view_collection);
            ll_content_item_layout=(LinearLayout)itemView.findViewById(R.id.ll_content_item_layout);
            /**
             * 实现监听器
             */
            mLinearLayout.setOnClickListener(this);
            iv_view_false.setOnClickListener(this);
            iv_view_true.setOnClickListener(this);
            iv_view_share.setOnClickListener(this);
            iv_view_collection.setOnClickListener(this);
            ll_content_item_layout.setOnClickListener(this);
        }

        /**
         * 进入分析界面的函数
         */
        private void enterAnalysisActivity(int position, int clickState) {
            Intent intent = new Intent(getActivity(), AnalysisActivity.class);
            //利用intent传递两个参数值，分析界面根据这两个参数来做相应的显示
            intent.putExtra("position", position);
            intent.putExtra("clickState", clickState);
            intent.putExtra("page",0);
            startActivity(intent);
        }

        /**
         * 根据传进来ContentInfo对象，获取里面的内容，并将内容显示在recyclerView里面
         *
         * @param contentInfo
         */
        private void setItemView(ContentInfo contentInfo) {
            Log.e("story","setItemView");
            //判断如果为空，则直接返回
            if (contentInfo == null) return;
            //设置标题
            tv_view_Title.setText(String.valueOf(contentInfo.getRumourTitle()));
            //设置谣言内容
            tv_view_content.setText(contentInfo.getContentText());
            //设置背景图片
            if(contentInfo.getContentDrawable()!=null)
            {
                Log.e("story","直接设置背景图片");
                mLinearLayout.setBackground(contentInfo.getContentDrawable());
            }
            else
            {
                mImageManager.setImage(contentInfo,mLinearLayout);
            }
            //根据是否已经阅读过，判断是否显示真假判断的图片
            if (contentInfo.isRead()) {
                //如果已经阅读过，则将真假判断隐藏
                iv_view_false.setVisibility(View.GONE);
                iv_view_true.setVisibility(View.GONE);
            } else {
                //如果没有阅读显示真假判断图片
                iv_view_false.setVisibility(View.VISIBLE);
                iv_view_true.setVisibility(View.VISIBLE);
            }
            if(contentInfo.isCollection())
            {
                iv_view_collection.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.like_choose));
            }
            else
            {
                iv_view_collection.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.like));
            }
        }

        /**
         * 每一个ItemView的点击事件,
         * 根据不同的点击事件传递不同的参数
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            //获取此时的Item在adapter中的位置
            int position = getAdapterPosition();
            ContentInfo contentInfo;
            contentInfo=mContentInfoList.get(position);
            switch (v.getId()) {
                case R.id.iv_view_false:
                    //如果点击了假，传递此时的位置，并传一个标志位1，表示分析界面是通过点击假传递进来的
                    enterAnalysisActivity(position, MyStaticData.CLICK_FALSE);
                    break;
                case R.id.iv_view_true:
                    //同理点击真，则传标志位2
                    enterAnalysisActivity(position, MyStaticData.CLICK_TRUE);
                    break;
                case R.id.ll_view_picture:
                    //如果是点击背景图片传递进来的，则将标志位设置为0
                    enterAnalysisActivity(position, MyStaticData.CLICK_PICTURE);
                    break;
                case R.id.ll_content_item_layout:
                    //如果是未读文章，则必须点了真假才有效
                    if(contentInfo.isRead())
                    {
                        //如果是点击z整体背景传递进来的，则将标志位设置为0
                        enterAnalysisActivity(position, MyStaticData.CLICK_PICTURE);
                    }
                    break;
                case R.id.iv_view_share:
                    //点击了分享按钮的事件处理
                    chooseShareDialog(contentInfo);
                    break;
                case R.id.iv_view_collection:
                    //点击了收藏按钮的事件处理
                    try {
                        if(contentInfo.isCollection())
                        {
                            contentInfo.setCollection(false);
                            collectionRemoveFromID(contentInfo.getId());
                            MyStaticData.collectionIdList.remove((Integer) contentInfo.getId());
                            JsonUtil.saveJSONArrayToLocal(getActivity().getApplicationContext(),MyStaticData.collectionIdList,MyStaticData.KEY_COLLECTION_ID);
                        }
                        else
                        {
                            contentInfo.setCollection(true);
                            MyStaticData.contentListCollection.add(0,contentInfo);
                            Log.e("story","收藏的个数:"+MyStaticData.contentListCollection.size());
                            if(!MyStaticData.collectionIdList.contains(contentInfo.getId()))
                            {
                                MyStaticData.collectionIdList.add(contentInfo.getId());
                                JsonUtil.saveJSONArrayToLocal(getActivity().getApplicationContext(),MyStaticData.collectionIdList,MyStaticData.KEY_COLLECTION_ID);
                            }
                            parentHandler.sendEmptyMessage(0);
                        }
                        notifyRefreshData();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

        private void chooseShareDialog(final ContentInfo contentInfo) {
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            View view=View.inflate(getContext(),R.layout.choose_wechat,null);
            builder.setView(view);
            final AlertDialog chooseDialog=builder.create();
            Window wm=chooseDialog.getWindow();
            wm.setGravity(Gravity.CENTER);
            wm.setBackgroundDrawableResource(R.color.dialog_background);
            chooseDialog.show();
            ImageView choose_weixin=(ImageView)view.findViewById(R.id.choose_weixin);
            ImageView choose_pengyouquan=(ImageView)view.findViewById(R.id.choose_pengyouquan);
            choose_weixin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((MyStaticData.mWxApi!=null)&&(MyStaticData.shareTitleBitmap!=null))
                    {
                        Log.e("story","点了分享");
                        String url="http://iecas.win/news/"+contentInfo.getId()+"/";
                        ShareUtils.shareUrlWithWX(url,contentInfo.getRumourTitle(),contentInfo.getContentText(),MyStaticData.shareTitleBitmap
                                ,MyStaticData.mWxApi,false);
                    }
                    chooseDialog.dismiss();
                }
            });
            choose_pengyouquan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((MyStaticData.mWxApi!=null)&&(MyStaticData.shareTitleBitmap!=null))
                    {
                        Log.e("story","点了分享");
                        String url="http://iecas.win/news/"+contentInfo.getId()+"/";
                        ShareUtils.shareUrlWithWX(url,contentInfo.getRumourTitle(),contentInfo.getContentText(),MyStaticData.shareTitleBitmap
                                ,MyStaticData.mWxApi,true);
                    }
                    chooseDialog.dismiss();
                }
            });
        }

        /**
         * 根据ID去掉收藏里面的文章
         * @param id 需要去掉的文章的ID
         */
        private void collectionRemoveFromID(int id) {
            for(int i=0;i<MyStaticData.contentListCollection.size();i++)
            {
                if(MyStaticData.contentListCollection.get(i).getId()==id)
                {
                    MyStaticData.contentListCollection.remove(MyStaticData.contentListCollection.get(i));
                    break;
                }
            }
            for(int i=0;i<mContentInfoList.size();i++)
            {
                if(mContentInfoList.get(i).getId()==id)
                {
                    mContentInfoList.get(i).setCollection(false);
                    break;
                }
            }
        }
    }
}
