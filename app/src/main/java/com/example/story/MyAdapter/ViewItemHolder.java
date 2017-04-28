package com.example.story.MyAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.story.activity.AnalysisActivity;
import com.example.story.contantInfo.ContentInfo;
import com.example.story.myUtils.JsonUtil;
import com.example.story.myUtils.MyStaticData;
//import com.example.story.myUtils.shareUtilWithAndroids;
import com.example.story.myUtils.ShareUtils;
import com.example.story.userManager.ImageManager;
import com.example.story.xiaoyao.R;

import java.util.ArrayList;

/**
 * Created by story on 2017/4/26.
 */

public  abstract class ViewItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private ArrayList<ContentInfo> mContentInfoList;
    private Context mContext;
    private LinearLayout mLinearLayout;
    private ImageView iv_view_false;
    private ImageView iv_view_true;
    private ImageView iv_view_share;
    private ImageView iv_view_collection;
    private TextView tv_view_Title;
    private TextView tv_view_content;
    private ImageManager mImageManager;
    private LinearLayout ll_content_item_layout;
    private final Activity mActivity;

    public ViewItemHolder(View itemView, ArrayList<ContentInfo> contentList,Context context,Activity activity) {
        super(itemView);
        mContentInfoList=contentList;
        mContext=context;
        mActivity=activity;
        mImageManager=new ImageManager(context);
        tv_view_content = (TextView) itemView.findViewById(R.id.tv_view_content);
        tv_view_Title = (TextView) itemView.findViewById(R.id.tv_view_Title);
        mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_view_picture);
        Display display=activity.getWindowManager().getDefaultDisplay();
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
    public abstract void enterAnalysisActivity(int position, int clickState);
    public abstract void notifyAdapterRefresh(boolean isShowCollection);
    /**
     * 根据传进来ContentInfo对象，获取里面的内容，并将内容显示在recyclerView里面
     *
     * @param contentInfo
     */
    public void setItemView(ContentInfo contentInfo) {
        //判断如果为空，则直接返回
        if (contentInfo == null) return;
        //设置标题
        tv_view_Title.setText(String.valueOf(contentInfo.getRumourTitle()));
        //设置谣言内容
        tv_view_content.setText(contentInfo.getContentText());
        //设置背景图片
        if(contentInfo.getContentDrawable()!=null)
        {
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
            iv_view_collection.setBackground(ContextCompat.getDrawable(mContext,R.drawable.like_choose));
        }
        else
        {
            iv_view_collection.setBackground(ContextCompat.getDrawable(mContext,R.drawable.like));
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
        ContentInfo contentInfo=mContentInfoList.get(position);
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
                Log.e("story","你单击了分享"+position);
                //点击了分享按钮的事件处理
                showChooseIsFriendCircleDialog(contentInfo);

                break;
            case R.id.iv_view_collection:
                //点击了收藏按钮的事件处理
                Log.e("story","你单击了收藏"+position);
                try
                {
                    if(contentInfo.isCollection())
                    {
                        contentInfo.setCollection(false);
                        collectionRemoveFromID(contentInfo.getId());
                        MyStaticData.collectionIdList.remove((Integer) contentInfo.getId());
                        JsonUtil.saveJSONArrayToLocal(mActivity.getApplicationContext(),MyStaticData.collectionIdList,MyStaticData.KEY_COLLECTION_ID);
                        notifyAdapterRefresh(false);
                    }
                    else
                    {
                        contentInfo.setCollection(true);
                        Log.e("story","点击的收藏的个数:"+MyStaticData.contentListCollection.size());
                        if(!MyStaticData.collectionIdList.contains(contentInfo.getId()))
                        {
                            MyStaticData.contentListCollection.add(0,contentInfo);
                            MyStaticData.collectionIdList.add(contentInfo.getId());
                            JsonUtil.saveJSONArrayToLocal(mActivity.getApplicationContext(),MyStaticData.collectionIdList,MyStaticData.KEY_COLLECTION_ID);
                        }
                        notifyAdapterRefresh(true);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
                break;
            default:
                break;
        }
    }

    private void showChooseIsFriendCircleDialog(final ContentInfo contentInfo) {
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        View view=View.inflate(mContext,R.layout.choose_wechat,null);
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
    public abstract void collectionRemoveFromID(int id);

}
