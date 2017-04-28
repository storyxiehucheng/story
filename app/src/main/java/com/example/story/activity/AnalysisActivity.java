package com.example.story.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.story.contantInfo.CommentsInfo;
import com.example.story.contantInfo.ContentInfo;
import com.example.story.global.MyApplication;
import com.example.story.myUtils.CheckNetWork;
import com.example.story.myUtils.HideSoftKeyBoard;
import com.example.story.myUtils.HttpUtils;
import com.example.story.myUtils.JsonUtil;
import com.example.story.myUtils.MySpUtils;
import com.example.story.myUtils.MyStaticData;
import com.example.story.myUtils.ShareUtils;
import com.example.story.userManager.ImageManager;
import com.example.story.xiaoyao.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**分析界面
 * Created by story on 2017/4/9.
 */

public class AnalysisActivity extends Activity{
    private Toolbar mToolBar;
    private TextView tv_analysis_analysis;
    private ImageView iv_analysis_answer;
    private ImageView iv_analysis_comment;
    private ImageView iv_analysis_share;
    private ImageView iv_analysis_collection;
    private ContentInfo mContentInfo;
    private ImageView iv_analysis_picture;
    private TextView tv_analysis_rumour;
    private RecyclerView comments_recyclerView;
    private Handler mHandler;
    private CommentsAdapter mCommentsAdapter;
    private ArrayList<CommentsInfo> mCommentsInfoList;
    private TextView tv_analysis_comments_number;
    private CollapsingToolbarLayout analysis_collapsing;
    private ProgressBar analysis_progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_ativity);
        initView();
        initData();
        initListener();
    }

    /**
     * 初始化监听器，所有的控件监听结果都在这里面
     */
    private void initListener() {
        iv_analysis_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token=MySpUtils.getString(getApplicationContext(),MyStaticData.KEY_TOKEN,"");
                if(token.equals(""))
                {
                    Intent intent=new Intent(AnalysisActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    showCommentsDialog();
                }
            }
        });
        iv_analysis_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b=mContentInfo.isCollection();
                if(mContentInfo.isCollection())
                {
                    iv_analysis_collection.setImageDrawable(ContextCompat.getDrawable(AnalysisActivity.this,R.drawable.like));
                    mContentInfo.setCollection(false);
                    collectionManageFromID(mContentInfo.getId(),false);
                    MyStaticData.collectionIdList.remove((Integer) mContentInfo.getId());
                    JsonUtil.saveJSONArrayToLocal(getApplicationContext(),MyStaticData.collectionIdList,MyStaticData.KEY_COLLECTION_ID);
                    Log.e("story","取消收藏...收藏的个数:"+MyStaticData.contentListCollection.size());
                }
                else
                {
                    iv_analysis_collection.setImageDrawable(ContextCompat.getDrawable(AnalysisActivity.this,R.drawable.like_choose));
                    mContentInfo.setCollection(true);
                    mContentInfo.setRead(true);
                    collectionManageFromID(mContentInfo.getId(),true);

                    if(!MyStaticData.collectionIdList.contains(mContentInfo.getId()))
                    {
                        MyStaticData.collectionIdList.add(mContentInfo.getId());
                        JsonUtil.saveJSONArrayToLocal(getApplicationContext(),MyStaticData.collectionIdList,MyStaticData.KEY_COLLECTION_ID);
                    }
                    Log.e("story","添加收藏...收藏的个数:"+MyStaticData.contentListCollection.size());
                    Toast.makeText(AnalysisActivity.this,"已添加到印迹",Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_analysis_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseShareDialog();
            }
        });
    }

    private void showChooseShareDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.choose_wechat,null);
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
                    String url="http://iecas.win/news/"+mContentInfo.getId()+"/";
                    ShareUtils.shareUrlWithWX(url,mContentInfo.getRumourTitle(),mContentInfo.getContentText(),MyStaticData.shareTitleBitmap
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
                    String url="http://iecas.win/news/"+mContentInfo.getId()+"/";
                    ShareUtils.shareUrlWithWX(url,mContentInfo.getRumourTitle(),mContentInfo.getContentText(),MyStaticData.shareTitleBitmap
                            ,MyStaticData.mWxApi,true);
                }
                chooseDialog.dismiss();
            }
        });
    }

    private void collectionManageFromID(int id, Boolean isCollection) {
        if(!isCollection)
        {
            for(int i=0;i<MyStaticData.contentListCollection.size();i++)
            {
                if(MyStaticData.contentListCollection.get(i).getId()==id)
                {
                    MyStaticData.contentListCollection.remove(MyStaticData.contentListCollection.get(i));
                    break;
                }
            }
        }
        else
        {
            MyStaticData.contentListCollection.add(0,mContentInfo);
        }
        for(int i=0;i<MyStaticData.contentList.size();i++)
        {
            if(MyStaticData.contentList.get(i).getId()==id)
            {
                if(isCollection)
                {
                    MyStaticData.contentList.get(i).setCollection(true);
                    break;
                }
                else
                {
                    MyStaticData.contentList.get(i).setCollection(false);
                    break;
                }
            }
        }
    }

    private void showCommentsDialog() {
        final Dialog commentsDialog=new Dialog(this,R.style.style_dialog_comments);
        commentsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view =View.inflate(this,R.layout.comments_dialog_layout,null);
        commentsDialog.setContentView(view);
        commentsDialog.setCanceledOnTouchOutside(true);
        commentsDialog.show();

        Window window=commentsDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp=window.getAttributes();
        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        Button bt_dialog_comments=(Button)view.findViewById(R.id.bt_dialog_comments);
       final EditText et_dialog_comments=(EditText)view.findViewById(R.id.et_dialog_comments);
        bt_dialog_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strComments = et_dialog_comments.getText().toString();
                Log.e("story","评论的内容为: "+strComments);
                if(!strComments.isEmpty())
                {
                    handleComments(strComments);
                }
                commentsDialog.dismiss();
            }
        });

    }

    private void handleComments(final String strComments) {
        final String token= MySpUtils.getString(getApplicationContext(),MyStaticData.KEY_TOKEN,"");
        if(!token.equals(""))
        {
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    String strResult = HttpUtils.commentsConnect(token, strComments, mContentInfo.getId());
                    if(!strResult.equals(""))
                    {
                        //评论成功
                        getALlComments(mContentInfo.getId());
                        mHandler.sendEmptyMessage(2);
                    }
                    else
                    {
                       //评论失败
                        mHandler.sendEmptyMessage(3);
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        int position=getIntent().getIntExtra("position",2);
        Log.e("story","position= "+position);
        int clickState=getIntent().getIntExtra("clickState",2);
        Log.e("story","clickState= "+clickState);
        int viewPage=getIntent().getIntExtra("page",0);
        Log.e("story","viewPage= "+viewPage);
        switch (viewPage)
        {
            case MyStaticData.PAGE_RECOMMEND:
                if(!MyStaticData.contentList.isEmpty())
                {
                    mContentInfo= MyStaticData.contentList.get(position);
                }
                else
                {
                    return;
                }
                break;
            case MyStaticData.PAGE_FOOD:
                if(!MyStaticData.contentFoodList.isEmpty())
                {
                    mContentInfo= MyStaticData.contentFoodList.get(position);
                }
                else
                {
                    return;
                }
                break;
            case MyStaticData.PAGE_HEALTHY:
                if(!MyStaticData.contentHealthyList.isEmpty())
                {
                    mContentInfo= MyStaticData.contentHealthyList.get(position);
                }
                else
                {
                    return;
                }
                break;
            case MyStaticData.PAGE_TECHNOLOGY:
                if(!MyStaticData.contentTechnologyList.isEmpty())
                {
                    mContentInfo= MyStaticData.contentTechnologyList.get(position);
                }
                else
                {
                    return;
                }
                break;
            case MyStaticData.PAGE_COLLECTION:
                if(!MyStaticData.contentListCollection.isEmpty())
                {
                    mContentInfo= MyStaticData.contentListCollection.get(position);
                }
                else
                {
                    return;
                }
                break;
            default:
                return;
        }
        if(!MyStaticData.contentList.isEmpty())
        {

            mCommentsInfoList=mContentInfo.getCommentsInfoList();
            mContentInfo.setRead(true);
            //读过的ID保存本地
            if(!MyStaticData.hasReadIdList.contains(mContentInfo.getId()))
            {
                MyStaticData.hasReadIdList.add(mContentInfo.getId());
                JsonUtil.saveJSONArrayToLocal(getApplicationContext(),MyStaticData.hasReadIdList,MyStaticData.KEY_HAS_READ_ID);
            }
            if(CheckNetWork.checkNetWorkAvailable(getApplicationContext()))
            {
                getAnalysisDetails(mContentInfo.getId());
                getALlComments(mContentInfo.getId());
            }
            else
            {
                Log.e("story","进入无网处理....");
                if(mContentInfo.getAnalysisText().isEmpty())
                {
                    tv_analysis_analysis.setText("网络连接失败,无法获取解析......");
                    setClickAgain(tv_analysis_analysis);
                }
                analysis_progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this,"网络未连接，请检查网络",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            return;
        }
        //根据Intent传进来的标志位。判断是点击什么进来的。
        switch (clickState)
        {
            //如果是0，则表示点击"背景图片"进来的分析界面
            case MyStaticData.CLICK_PICTURE:
                //不显示回答正确与否
                iv_analysis_answer.setVisibility(View.GONE);
                break;

            //如果是1.则表示点击的“假”进来的分析界面
            case MyStaticData.CLICK_FALSE:
                iv_analysis_answer.setVisibility(View.VISIBLE);
                if(mContentInfo.isRumourResult())
                {
                    iv_analysis_answer.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.answer_wrong));
                    tv_analysis_rumour.setTextColor(ContextCompat.getColor(AnalysisActivity.this,R.color.red_answer_wrong));
                }
                else
                {
                    iv_analysis_answer.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.answer_right));
                    tv_analysis_rumour.setTextColor(ContextCompat.getColor(AnalysisActivity.this,R.color.green_answer_right));
                }

                break;

           //如果是2，则表示点击的是“真”，进来的分析界面
            case MyStaticData.CLICK_TRUE:
                iv_analysis_answer.setVisibility(View.VISIBLE);
                if(mContentInfo.isRumourResult())
                {
                    iv_analysis_answer.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.answer_right));
                    tv_analysis_rumour.setTextColor(ContextCompat.getColor(AnalysisActivity.this,R.color.green_answer_right));
                }
                else
                {
                    iv_analysis_answer.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.answer_wrong));
                    tv_analysis_rumour.setTextColor(ContextCompat.getColor(AnalysisActivity.this,R.color.red_answer_wrong));
                }
                break;
            default:
                break;
        }
        //设置背景图片
        if(mContentInfo.getContentDrawable()==null)
        {
            ImageManager imageManager=new ImageManager(this);
            imageManager.setImage(mContentInfo,iv_analysis_picture);
        }
        else
        {
            iv_analysis_picture.setImageDrawable(mContentInfo.getContentDrawable());
        }
        boolean b=mContentInfo.isCollection();
        if(mContentInfo.isCollection())
        {
            iv_analysis_collection.setImageDrawable(ContextCompat.getDrawable(AnalysisActivity.this,R.drawable.like_choose));
        }
        else
        {
            iv_analysis_collection.setImageDrawable(ContextCompat.getDrawable(AnalysisActivity.this,R.drawable.like));
        }
        //设置大标题
        mToolBar.setTitle(mContentInfo.getRumourTitle());
        //设置标题折叠前后的颜色
        analysis_collapsing.setCollapsedTitleTextColor(Color.BLACK);
        analysis_collapsing.setExpandedTitleColor(Color.WHITE);
        //设置谣言内容
        tv_analysis_rumour.setText(mContentInfo.getContentText());
        if(!mContentInfo.getAnalysisText().isEmpty())
        {
            //设置解析内容
            tv_analysis_analysis.setText(mContentInfo.getAnalysisText());
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        comments_recyclerView.setLayoutManager(linearLayoutManager);
        comments_recyclerView.setHasFixedSize(true);
        comments_recyclerView.setNestedScrollingEnabled(false);

        mCommentsAdapter=new CommentsAdapter(mCommentsInfoList);
        comments_recyclerView.setAdapter(mCommentsAdapter);

        mHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 0:
                        tv_analysis_analysis.setText(mContentInfo.getAnalysisText());
                        analysis_progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        if(mCommentsAdapter!=null)
                        {
                            if((mCommentsInfoList!=null)&&(tv_analysis_comments_number!=null))
                            {
                                tv_analysis_comments_number.setText(mCommentsInfoList.size()+"");
                                Log.e("story","更新List数据"+mCommentsInfoList.size());
                            }
                            mCommentsAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        Toast.makeText(AnalysisActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(AnalysisActivity.this,"评论失败",Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        analysis_progressBar.setVisibility(View.INVISIBLE);
                        if(mContentInfo.getAnalysisText().isEmpty())
                        {
                            tv_analysis_analysis.setText("很抱歉，获取解析失败.....");
                            setClickAgain(tv_analysis_analysis);
                        }
                        break;
                    default:
                        break;
                }

            }
        };
    }

    /**
     * 设置textview的文字中的特殊字发生点击事件
     * @param textView 需要设置的textView
     */
    private void setClickAgain(TextView textView) {
        Log.e("story","进入重试处理....");
        SpannableString spanText=new SpannableString("点击此处重试");
        spanText.setSpan(new ForegroundColorSpan(Color.BLUE),0,spanText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new UnderlineSpan(),0,spanText.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                getAnalysisDetails(mContentInfo.getId());
                getALlComments(mContentInfo.getId());
            }
        },0,spanText.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(spanText);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        Log.e("story","进入显示文字....");
    }

    /**
     * 根据文章的ID获取文章的所有评论
     * @param mContentInfoId 文章的ID
     */
    private void getALlComments(final int mContentInfoId) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonContent = HttpUtils.getJsonCommentsListWithId(mContentInfoId);
                ArrayList<CommentsInfo> commentsInfoArrayList = JsonUtil.jsonToCommentsInfoList(jsonContent);
                if(commentsInfoArrayList!=null)
                {
                    mCommentsInfoList=commentsInfoArrayList;
                    mHandler.sendEmptyMessage(1);
                }
            }
        });
        thread.start();
    }

    /**
     * 根据文章的ID获取文章的详细内容
     * @param mContentInfoId 文章的ID
     */
    private void getAnalysisDetails(final int mContentInfoId) {
        analysis_progressBar.setVisibility(View.VISIBLE);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonContent = HttpUtils.getJsonContentWithId(mContentInfoId);
                ContentInfo contentInfo = JsonUtil.jsonToContentInfo(jsonContent);
                if(contentInfo!=null)
                {
                    mContentInfo.setAnalysisText(contentInfo.getAnalysisText());
                    mHandler.sendEmptyMessageDelayed(0,1000);
                }else
                {
                    mHandler.sendEmptyMessageDelayed(4,2000);
                }
            }
        });
        thread.start();
    }

    /**
     * 获取控件的ID
     */
    private void initView() {
        mToolBar= (Toolbar) findViewById(R.id.analysis_toolbar);

        tv_analysis_analysis=(TextView)findViewById(R.id.tv_analysis_analysis);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/msyhl.ttc");
        tv_analysis_analysis.setTypeface(typeface);
        tv_analysis_rumour=(TextView)findViewById(R.id.tv_analysis_rumour);
        comments_recyclerView=(RecyclerView)findViewById(R.id.comments_recyclerView);

        iv_analysis_picture=(ImageView)findViewById(R.id.iv_analysis_picture);
        Display display=getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        int width=size.x;
        CollapsingToolbarLayout.LayoutParams params=(CollapsingToolbarLayout.LayoutParams)iv_analysis_picture.getLayoutParams();
        params.height=(int)(width*0.618);
        iv_analysis_picture.setLayoutParams(params);

        iv_analysis_answer=(ImageView)findViewById(R.id.iv_analysis_answer);
        iv_analysis_share=(ImageView)findViewById(R.id.iv_analysis_share);
        iv_analysis_collection = (ImageView) findViewById(R.id.iv_analysis_collection);
        iv_analysis_comment=(ImageView)findViewById(R.id.iv_analysis_comment);
        tv_analysis_comments_number=(TextView)findViewById(R.id.tv_analysis_comments_number);
        analysis_progressBar=(ProgressBar)findViewById(R.id.analysis_progressBar);
        analysis_collapsing=(CollapsingToolbarLayout)findViewById(R.id.analysis_collapsing);
    }


    private class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tv_username;
        private final TextView tv_content;
        private final ImageView iv_comments_head;

        CommentsViewHolder(View itemView) {
            super(itemView);
            tv_username=(TextView) itemView.findViewById(R.id.tv_comments_username);
            tv_content=(TextView)itemView.findViewById(R.id.tv_comments_content);
            iv_comments_head=(ImageView)itemView.findViewById(R.id.iv_comments_head);
        }

         void setItem(CommentsInfo commentInfo)
        {
            tv_username.setText(commentInfo.getUserName()+"    "+commentInfo.getTimeDelta());
            tv_content.setText(commentInfo.getUserComments());
        }
    }

    private class CommentsAdapter extends RecyclerView.Adapter<CommentsViewHolder>
    {
        private ArrayList<CommentsInfo> mmCommentsInfoList;

        CommentsAdapter(ArrayList<CommentsInfo> commentsInfoList) {
            this.mmCommentsInfoList = commentsInfoList;
        }

        @Override
        public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item_layout,parent,false);
            return new CommentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentsViewHolder holder, int position) {
            holder.setItem(mCommentsInfoList.get(position));
        }

        @Override
        public int getItemCount() {
            return mCommentsInfoList==null? 0:mCommentsInfoList.size();
        }
    }
}
