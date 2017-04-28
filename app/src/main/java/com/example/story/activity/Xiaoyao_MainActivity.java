
package com.example.story.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.story.MyAdapter.MyFragmentPageAdapter;
import com.example.story.contantInfo.ContentInfo;
import com.example.story.myUtils.CheckNetWork;
import com.example.story.myUtils.HttpUtils;
import com.example.story.myUtils.JsonUtil;
import com.example.story.myUtils.MySpUtils;
import com.example.story.myUtils.MyStaticData;
import com.example.story.myUtils.ShareUtils;
import com.example.story.myUtils.UserHeadImageUtils;
import com.example.story.xiaoyao.R;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 *主界面
 *@author story
 *@time 2017/4/12 9:27
 */
public class Xiaoyao_MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private ArrayList<ContentInfo> mContentList;
    private ImageView mLogin_imageView;
    private boolean isLoginSucceed=false;
    private TabLayout mTabLayout;
    private Button bt_setting_exitLogin;
    private Button bt_head_login;
    private Handler mHandler;
    private IWXAPI mWxApi;
    private TextView tv_login_username;
    private UserHeadImageUtils userHeadImageUtils;
    private final int REQUEST_WRITE=1;
    private long firstTimeClickBackTime=0;
    private Bitmap shareImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaoyao__main);
        userHeadImageUtils=new UserHeadImageUtils(this);
        //微信注册管理
        weiChatManager();
        //初始化list的数据
        initListData();
        //初始化view
        initView();
        //初始化数据
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {
            case UserHeadImageUtils.ACTIVITY_RESULT_ALBUM:
                try{
                    if(resultCode==-1)
                    {
                        Bitmap bm_icon=userHeadImageUtils.decodeBitmap();
                        if(bm_icon!=null)
                        {
                            if(mLogin_imageView!=null)
                            {
                                Bitmap circleBitmap = setCircleBitmap(bm_icon);
                                if(mHandler!=null)
                                {
                                    mHandler.sendMessage(mHandler.obtainMessage(2,circleBitmap));
                                }
                            }
                        }
                    }
                    else
                    {
                        if(userHeadImageUtils.picFile!=null)
                        {
                            userHeadImageUtils.picFile.delete();
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case UserHeadImageUtils.ACTIVITY_RESULT_CAMERA:
                try{
                    if(resultCode==-1)
                    {
                        userHeadImageUtils.cutImageByCamera();
                    }
                    else
                    {
                        if(userHeadImageUtils.picFile!=null)
                        {
                            userHeadImageUtils.picFile.delete();
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    /**
     * 微信分享管理
     */
    private void weiChatManager() {
        //注册到微信
        mWxApi = WXAPIFactory.createWXAPI(this, MyStaticData.weiChatAPP_ID, true);
        mWxApi.registerApp(MyStaticData.weiChatAPP_ID);
        MyStaticData.mWxApi=mWxApi;
        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.xy_ic_launcher_share);
        MyStaticData.shareTitleBitmap=bitmap;
    }
    /**
     * 初始化list数据，主要将内容添加到list中去
     */
    private void initListData() {
        MyStaticData.contentList.clear();
        MyStaticData.contentListCollection.clear();
        mContentList = MyStaticData.contentList;
        ContentInfo contentInformation1 = new ContentInfo();
        contentInformation1.setCollection(false);
        contentInformation1.setId(1);
        contentInformation1.setRumourTitle("宿便是健康杀手，排宿便保健康");
        contentInformation1.setContentText("宿便有多种危害，会压迫小肠绒毛的活力和弹性，发酵、胀气导致酸毒症，还会改变腹部和脊柱形状，让身材变形");
        contentInformation1.setContentDrawable(ContextCompat.getDrawable(this,R.drawable.p1));
        contentInformation1.setAnalysisText("真相： “宿便”并非一个医学概念，它是被商家创造出来的概念，各种宣传中“宿便”的危害皆为夸大其辞的商业宣传。\n" +
                "简述： 任何一本医学教科书中都没有“宿便”这个概念，从字面上理解，应该是积存在体内的粪便，从食物的消化过程来看，应该就是堆积在结肠末端准备排出体外的粪便。错误一：宿便会压迫小肠绒毛的活力和弹性。食物在运行到结肠被吸收水分之前，都是粥样的物质，和小肠绒毛充分接触，吸收养分，而不会压迫小肠绒毛。\n" +
                "错误二：粪便发酵、胀气导致酸毒症。 事实上小肠中的肠液是碱性的，即使食物发酵产酸，也会被肠液迅速中和，不可能堆积酸性物质，更不可能进入血液。\n" +
                "错误三：宿便会改变腹部和脊柱形状，会让身材变形。粪便是柔软的食物残渣集合体，即使碰到了脊柱，也会被脊柱挤压变形。腹围增大，主要是脂肪堆积在腹部所致，和宿便一点关系都没有。\n" +
                "如果相信此说，而采用泻药来“排宿便”，会对肠道的功能造成影响，错误二：粪便发酵、胀气导致酸毒症。 事实上小肠中的肠液是碱性的，即使食物发酵产酸，也会被肠液迅速中和，不可能堆积酸性物质，更不可能进入血液。\n" +
                "错误三：宿便会改变腹部和脊柱形状，会让身材变形。粪便是柔软的食物残渣集合体，即使碰到了脊柱，也会被脊柱挤压变形。腹围增大，主要是脂肪堆积在腹部所致，和宿便一点关系都没有。\n" +
                "如果相信此说，而采用泻药来“排宿便”，会对肠道的功能造成影响，严重者可能造成水电酸碱紊乱、昏迷甚至死亡。\n严重者可能造成水电酸碱紊乱、昏迷甚至死亡。\n");
        mContentList.add(contentInformation1);

        ContentInfo contentInformation2 = new ContentInfo();
        contentInformation2.setCollection(false);
        contentInformation2.setId(2);
        contentInformation2.setRumourTitle("酸性体质是百病之源");
        contentInformation2.setContentText("人体体液的pH值处于7.35～7.45 的弱碱状态是最健康的，但大多数人由于生活习惯及环境的影响，体液pH值都在7.35 以下，他们的身体处于健康和疾病之间的亚健康状态，这些人就是酸性体质者");
        contentInformation2.setContentDrawable(ContextCompat.getDrawable(this,R.drawable.p2));
        contentInformation2.setAnalysisText("真相： “酸性体质”只是一个“伪概念”。人体的不同体液pH值也有所不同，比如胃液就是强酸性的。正常情况下血液的pH值在7.35～7.45之间。酸中毒和碱中毒都是必须接受专业治疗的严重疾病。短期内的特定食物不可能改变正常人血液的酸碱度。\n" +
                "简述：人体是个复杂而具有自我调节能力的系统。以血液为例，其中含有碳酸氢盐、磷酸盐、血浆蛋白、血红蛋白和氧合血红蛋白等几大缓冲系统，这些化合物保证了在不停有物质释放入血液又排除出血液的动态平衡中，只要不超过这些化合物的缓冲能力，血液的pH值会始终在一个极小的范围内波动。\n" +
                "事实上，一旦人体血液pH值低于7.35，会发生酸中毒，而pH值高于7.45则是碱中毒。无论酸中毒或者碱中毒，实际上都代表身体器官出了严重的问题，甚至会有生命危险，必须立刻接受正规的医疗照护。绝不可能靠吃些普通食物就能力挽“酸性体质”的狂澜。\n");
        mContentList.add(contentInformation2);

        ContentInfo contentInformation3 = new ContentInfo();
        contentInformation3.setCollection(false);
        contentInformation3.setId(3);
        contentInformation3.setRumourTitle("食物相克：果汁与海鲜不能同吃");
        contentInformation3.setContentText("一些常见的食物，比如柿子和蟹、虾和水果、豆浆和鸡蛋混在一起吃会相克，轻则导致身体不适，重则使人中毒。");
        contentInformation3.setContentDrawable(ContextCompat.getDrawable(this,R.drawable.p3));
        contentInformation3.setAnalysisText("真相：这些传说要么来自于人们对日常生活的不恰当总结，要么来自于对科学研究的夸大和误读。有科学家验证了坊间流传的数百种食物相克传说，无一成立。" +
                "简述： 1935年我国生物化学家郑集曾经搜集了184对“相克”的食物，从中选出14对在日常生活中比较容易遇到的组合，用老鼠、狗和猴子做实验。他本人和一名同事也试验了其中的7种组合。在食用24小时内观察实验动物和人的表情、行为、体温及粪便颜色与次数等，都很正常，没有中毒的迹象。在郑集试验的“相克”食物中，就包括螃蟹与柿子、大葱与蜂蜜。近来中国营养学会分别与兰州大学公共卫生学院、哈尔滨医科大学合作，做了更严格一些的“食物相克”实验，均未发现异常。\n" +
                "以柿子和螃蟹不能同食为例。柿子因含有鞣酸，大量食用易引发胃石症导致腹痛。而螃蟹如果未能充分烹煮杀菌，也容易引发消化道感染。这些症状与“一起食用”没有关系。\n" +
                "虾中的五价砷和水果中的维生素C混合可产生砒霜则是夸大了科学研究。即便绝对理想状况下，一个人也要一次食用远超出日常食量的虾和维生素C才可能中毒。\n");
        mContentList.add(contentInformation3);

        ContentInfo contentInformation4 = new ContentInfo();
        contentInformation4.setCollection(false);
        contentInformation4.setId(4);
        contentInformation4.setRumourTitle("水知道答案：人的态度会影响食物");
        contentInformation4.setContentText("根据日本人江本胜的畅销书《水知道答案》称，同样的一杯水，分为两份，赞美其中的一份，咒骂另外一份，之后在显微镜下观察水分子的形状，发现前面的特别规整漂亮，后面的杂乱无章。");
        contentInformation4.setContentDrawable(ContextCompat.getDrawable(this,R.drawable.rumours_4));
        contentInformation4.setAnalysisText("真相：水不可能对人类情绪进行感知和反应。“水知道答案”这项研究并不符合相关的技术流程，其结论自然也不足为信。" +
                "简述：什么原因导致了水结晶形状的区别呢？加州理工学院物理系主任Kenneth Lebbrecht发现，温度和湿度是决定雪花形状的最重要的两个因素。如果结晶温度在-5℃到-10℃之间，晶体更容易形成柱状或是针状的结构。而在-15℃左右的情况下，水气倾向于结成片状的雪花。至于雪花的复杂程度，则和湿度有关。湿度越小，雪花的形状就越简单。根据这些发现，Kenneth Lebbrecht甚至可以在实验室中通过人为设定的条件来设计不同形状的雪花。\n" +
                "江本胜宣传自己的“研究结果”有着明确的商业目的。他的公司正在出售一种“高能水”，这种水号称有着最完美的晶体结构，还可以延缓衰老，治愈疾病。这样的水自然价格不菲，一瓶227克（8盎司）的“高能水”的价格是35美元。\n");
        mContentList.add(contentInformation4);

        ContentInfo contentInformation5 = new ContentInfo();
        contentInformation5.setCollection(false);
        contentInformation5.setId(5);
        contentInformation5.setRumourTitle("孕妇需要穿防辐射服保护胎儿");
        contentInformation5.setContentText("日常生活中手机、微波炉、电脑等各种电器产生大量有害的辐射，有研究表明这些辐射会增加孕妇的流产率，因此孕妇最好穿戴防辐射服。");
        contentInformation5.setContentDrawable(ContextCompat.getDrawable(this,R.drawable.p5));
        contentInformation5.setAnalysisText("真相：这条谣言故意歪曲了一些二十多年前的研究结论。目前没有证据表明日常非电离辐射会导致孕妇流产率、胎儿畸形率的提高，也不会导致新生儿出生体重过低。孕妇无需特别防护。\n" +
                "简述：在1988年6月出版的《美国工业医学杂志》（AmericanJournal of Industrial Medicine）上，来自一家非营利医疗组织的三位医生发表了一篇名为《孕期女性使用视屏终端与流产和新生儿出生缺陷发生概率的关系》的文章。这三位医生在1981-1982年间统计了1583名来自美国北加州的孕妇的情况，" +
                "发现在怀孕头三个月每周使用电脑显示器超过20小时的孕妇的流产率比不接触的孕妇高。这个研究结果出现在各种“防辐射服”的产品宣传里。但是这些宣传材料无一例外的自动忽略了原文作者的一句话：“我们的研究结果并不能证明非电离辐射与流产率提高之间有直接联系，较差的工作条件和较高的工作压力也是可能的因素。”\n" +
                "稍后更大规模的研究证实日常接触的各种电磁辐射并不会影响孕妇和胎儿的健康。目前也没有这些辐射会对人体健康产生危害的任何因果证据。\n");
        mContentList.add(contentInformation5);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取文件的存储权限
        requiresFilePermission();
        //Todo
        MyStaticData.hasReadIdList.clear();
        MyStaticData.hasReadIdList= JsonUtil.getArrayListFromLocal(getApplicationContext(),MyStaticData.KEY_HAS_READ_ID);
        Log.e("story","最开始获得的hasReadIdList: "+MyStaticData.hasReadIdList);
        MyStaticData.collectionIdList.clear();
        MyStaticData.collectionIdList=JsonUtil.getArrayListFromLocal(getApplicationContext(),MyStaticData.KEY_COLLECTION_ID);
        //获取登录信息
        getLoginInformation();
        //网络未连接
        if(!CheckNetWork.checkNetWorkAvailable(getApplicationContext()))
        {
            Toast.makeText(this,"网络未连接，请检查网络",Toast.LENGTH_LONG).show();
        }
        handlerDownLoadAppInfo();
    }

    private void requiresFilePermission() {
       int version=Build.VERSION.SDK_INT;
        if(version>=23)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    !=PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(Xiaoyao_MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    new AlertDialog.Builder(this).setTitle("权限说明")
                            .setMessage("【消谣】需要获取你的SD卡权限")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(Xiaoyao_MainActivity.this,new String[]
                                            {Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE);
                }
            }

        }

    }

    private void getLoginInformation() {
        String token=MySpUtils.getString(getApplicationContext(),MyStaticData.KEY_TOKEN,"");
        if(!token.equals(""))
        {
            isLoginSucceed=true;
            String username=MySpUtils.getString(getApplicationContext(),MyStaticData.KEY_USERNAME,"");
            tv_login_username.setVisibility(View.VISIBLE);
            tv_login_username.setText(username);
//            Log.e("story","设置头像...");
//            if(MyStaticData.userHeadIcon==null)
//            {
                mLogin_imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.head));
//            }
//            else
//            {
//                mLogin_imageView.setImageBitmap(MyStaticData.userHeadIcon);
//            }
//            setUserHeadIcon();
            bt_setting_exitLogin.setVisibility(View.VISIBLE);
            bt_head_login.setVisibility(View.GONE);
        }
    }

    private void setUserHeadIcon() {
        final Handler handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 0:
                        Bitmap bitmap= (Bitmap) msg.obj;
                        Bitmap circleBitmap = setCircleBitmap(bitmap);
                        mLogin_imageView.setImageBitmap(circleBitmap);
                        MyStaticData.userHeadIcon=circleBitmap;
                        Toast.makeText(Xiaoyao_MainActivity.this,"头像设置成功",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath=UserHeadImageUtils.pathName;
                File imageFile=new File(Environment.getExternalStorageDirectory()+filePath,"userHead.png");
                if(imageFile.exists())
                {
                    try {
                        FileInputStream fileInputStream=new FileInputStream(imageFile);
                        Bitmap bitmap= BitmapFactory.decodeStream(fileInputStream);
                        handler.sendMessage(handler.obtainMessage(0,bitmap));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("story","文件转化为bitmap失败");
                    }
                }
                Log.e("story","头像文件不存在");
            }
        });
        thread.start();
    }

    /**
     * 将图像设置为圆形头像
     * @param bitmap
     * @return
     */
    private Bitmap setCircleBitmap(Bitmap bitmap) {
        if(bitmap==null)
        {
            return null;
        }
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int r=0;
        if(width>height)
        {
            r=height;
        }
        else
        {
            r=width;
        }
        //构建一个bitmap
        Bitmap userHeadIconBm=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //new一个canvas，在上面画图
        Canvas canvas=new Canvas(userHeadIconBm);
        Paint p=new Paint();
        //设置边缘光滑
        p.setAntiAlias(true);
        RectF rect=new RectF(0,0,r,r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return userHeadIconBm;
    }

    /**
     * 初始化界面，获取界面ID并设置值
     */
    private void initView() {
      Handler  popupWindowHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if(mTabLayout==null)
                    return;
                showPopupWindow(mTabLayout);
                Toast.makeText(Xiaoyao_MainActivity.this,"已添加到印迹",Toast.LENGTH_SHORT).show();
            }
        };
        //获取顶部toolbar的ID值
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //获取选项卡顿的ID值
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //获取viewpager的ID值
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        //创建viewpager的内容适配器的对象,此处是利用的fragment作为内容填充
        MyFragmentPageAdapter myFragmentPageAdapter = new MyFragmentPageAdapter(getSupportFragmentManager(), popupWindowHandler);
        //为viewpager设置内容适配器
        viewPager.setAdapter(myFragmentPageAdapter);
        //将viewpager与tab选项卡绑定
        mTabLayout.setupWithViewPager(viewPager);

        //点击头像登录，监听事件
        mLogin_imageView= (ImageView)findViewById(R.id.iv_login_imageView);
        tv_login_username=(TextView)findViewById(R.id.tv_login_username);

        mLogin_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoginSucceed)
                {
//                    chooseDialog();
                    Toast.makeText(Xiaoyao_MainActivity.this,"欢迎您的使用",Toast.LENGTH_SHORT).show();
                    return;
                }
                enterLoginActivity();
            }
        });

        TextView tv_setting_checkUpdate=(TextView)findViewById(R.id.tv_setting_checkUpdate);
        shareImage=getImageFromAssetsFile("appQRcode.png");
        tv_setting_checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppDownLoadUrl(false);
            }
        });

        bt_head_login=(Button)findViewById(R.id.bt_head_login);
        bt_head_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterLoginActivity();
            }
        });
        bt_setting_exitLogin=(Button)findViewById(R.id.bt_setting_exitLogin);

        bt_setting_exitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginSucceed=false;
                mLogin_imageView.setImageDrawable(ContextCompat.getDrawable(Xiaoyao_MainActivity.this,R.drawable.head_no_login));
                MySpUtils.putString(getApplicationContext(),MyStaticData.KEY_TOKEN,"");
                bt_setting_exitLogin.setVisibility(View.INVISIBLE);
                tv_login_username.setVisibility(View.GONE);
                bt_head_login.setVisibility(View.VISIBLE);
            }
        });
        TextView tv_setting_recommend=(TextView)findViewById(R.id.tv_setting_recommend);
        tv_setting_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downLoadUrl=MySpUtils.getString(getApplicationContext(),MyStaticData.KEY_DOWNLOAD_URL,"");
                if(downLoadUrl.equals(""))
                {
                    getAppDownLoadUrl(true);
                }
                else
                {
                    if(mHandler!=null)
                    {
                        mHandler.sendMessage(mHandler.obtainMessage(1,downLoadUrl));
                    }
                }
            }
        });

        TextView tv_setting_about=(TextView)findViewById(R.id.tv_setting_about);
        tv_setting_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });



        //以下是系统创建的，主要用于配置侧边导航栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //设置侧边导航栏的监听器
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 从asset资源文件中获取图片文件
     * @param fileName
     * @return
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image=null;
        AssetManager am=getResources().getAssets();
        try{
            InputStream is=am.open(fileName);
            image=BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void chooseDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.choose_head_icon_layout,null);
        builder.setView(view);
        final AlertDialog chooseHeadDialog=builder.create();
        chooseHeadDialog.show();
        Button bt_choose_head_camera=(Button)view.findViewById(R.id.bt_choose_head_camera);
        Button bt_choose_head_album=(Button)view.findViewById(R.id.bt_choose_head_album);
        bt_choose_head_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseHeadDialog.dismiss();
                userHeadImageUtils.byCamera();
            }
        });
        bt_choose_head_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseHeadDialog.dismiss();
                userHeadImageUtils.byAlbum();
            }
        });
    }

    private void showAboutDialog() {
        AlertDialog.Builder aboutDialogBuilder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.about_app_layout,null);
        aboutDialogBuilder.setView(view);
        final AlertDialog aboutAppDialog = aboutDialogBuilder.create();
        aboutAppDialog.show();
        final TextView tv_about_app=(TextView)view.findViewById(R.id.tv_about_app);
        String appVersionName = getAppVersionName();
        if(!appVersionName.isEmpty())
        {
            tv_about_app.setText("版本号："+appVersionName);
        }
        Button bt_about_app=(Button)view.findViewById(R.id.bt_about_app);
        bt_about_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutAppDialog.dismiss();
            }
        });
    }

    /**
     * 检查更新
     */
    private void handlerDownLoadAppInfo()
    {
        if(mHandler==null)
        {
            mHandler=new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    switch(msg.what)
                    {
                        case 0:
                            Bundle bundleUpdate=(Bundle)msg.obj;
                            String version=bundleUpdate.getString("version");
                            String updateUrl=bundleUpdate.getString("updateUrl");
                            String updateInfo=bundleUpdate.getString("updateInfo");
                            int appVersion = getAPPVersion();
                            if(!version.equals(String.valueOf(appVersion)))
                            {
                                Toast.makeText(Xiaoyao_MainActivity.this,"有新版本更新",Toast.LENGTH_LONG).show();
                                showUpdateDialog(updateUrl,updateInfo);
                            }
                            else
                            {
                                Toast.makeText(Xiaoyao_MainActivity.this,"该版本目前已经是最新版",Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 1:
                                String downLoadUrl=(String)msg.obj;
                                showShareAppDialog(downLoadUrl);
                            break;
                        case 2:
                                Bitmap bitmap= (Bitmap) msg.obj;
                                if(bitmap!=null)
                                {
                                    mLogin_imageView.setImageBitmap(bitmap);
                                    MyStaticData.userHeadIcon=bitmap;
                                }
                            break;
                        case 3:
                            Toast.makeText(Xiaoyao_MainActivity.this,"网络连接失败，请检查后重试",Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    private void showShareAppDialog(final String downLoadUrl)
    {
        AlertDialog.Builder shareAppDialogBuilder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.share_app_layout,null);
        shareAppDialogBuilder.setView(view);
        final AlertDialog shareAppDialog = shareAppDialogBuilder.create();
        shareAppDialog.show();
        Button shareButton=(Button)view.findViewById(R.id.bt_share_app);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editString="消谣致力于打破生活中广为流传的谣言，流言止于智者，让我们做真理的追寻者！";
                chooseShareFriendCircle(editString,downLoadUrl);
                shareAppDialog.dismiss();
            }
        });
    }

    private void chooseShareFriendCircle(final String messageStr, final String downLoadUrl) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(getApplicationContext(),R.layout.choose_wechat,null);
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
                    ShareUtils.shareUrlWithWX(downLoadUrl,"【消谣】APP下载链接",messageStr,MyStaticData.shareTitleBitmap
                            ,MyStaticData.mWxApi,false);
                    Toast.makeText(Xiaoyao_MainActivity.this,"感谢您的推荐",Toast.LENGTH_SHORT).show();
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
                    ShareUtils.shareUrlWithWX(downLoadUrl,"【消谣】APP下载链接",messageStr,MyStaticData.shareTitleBitmap
                            ,MyStaticData.mWxApi,true);
                    Toast.makeText(Xiaoyao_MainActivity.this,"感谢您的推荐",Toast.LENGTH_SHORT).show();
                }
                chooseDialog.dismiss();
            }
        });
    }

    private void getAppDownLoadUrl(final boolean isShareApp) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonContent = HttpUtils.getJsonContent("http://iecas.win/update.json");
                Map<String, String> stringStringMap = JsonUtil.jsonToUpdateInformation(jsonContent);
                if(!stringStringMap.isEmpty())
                {
                    String version=stringStringMap.get("version");
                    String updateUrl=stringStringMap.get("updateUrl");
                    MySpUtils.putString(getApplicationContext(),MyStaticData.KEY_DOWNLOAD_URL,updateUrl);
                    String updateInfo=stringStringMap.get("updateInfo");
                    if(isShareApp)
                    {
                        Message messageUrl=new Message();
                        messageUrl.what=1;
                        messageUrl.obj=updateUrl;
                        mHandler.sendMessage(messageUrl);
                    }
                    else
                    {
                        Bundle bundle=new Bundle();
                        bundle.putString("version",version);
                        bundle.putString("updateUrl",updateUrl);
                        bundle.putString("updateInfo",updateInfo);
                        Message message=new Message();
                        message.what=0;
                        message.obj=bundle;
                        mHandler.sendMessage(message);
                    }
                }
                else
                {
                    mHandler.sendEmptyMessage(3);
                }
            }
        });
        thread.start();
    }

    private void showUpdateDialog(final String updateUrl, final String updateInfo) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.app_update_layout,null);
        builder.setView(view);
        final AlertDialog updateDialog=builder.create();
        updateDialog.show();
        TextView tv_updateInfo_app=(TextView)view.findViewById(R.id.tv_updateInfo_app);
        tv_updateInfo_app.append("\n"+updateInfo);
        Button bt_update_cancel=(Button)view.findViewById(R.id.bt_update_cancel);
        Button bt_update_ok=(Button)view.findViewById(R.id.bt_update_ok);
        bt_update_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(updateUrl));
                startActivity(intent);
                updateDialog.dismiss();
            }
        });
        bt_update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
            }
        });
    }

    /**
     * 将文件保存在本地
     * @param bitmap
     */
    private void putImageToFile(final Bitmap bitmap)
    {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                File filepath=new File(Environment.getDataDirectory(),File.separator+"ORcode/");
                if(!filepath.exists())
                {
                    if(!filepath.mkdirs())
                    {
                        Log.e("story","创建文件夹保存二维码失败");
                    }
                }
                File file=new File(filepath,"ORcode.png");
                try {
                    FileOutputStream out=new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * 获取系统的版本号
     */
    private int getAPPVersion() {
        PackageInfo packageInfo;
        try {
            PackageManager pm=getPackageManager();
            packageInfo=pm.getPackageInfo(this.getPackageName(),0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名称
     * @return
     */
    private String getAppVersionName()
    {
        PackageInfo packageInfo;
        try {
            PackageManager pm=getPackageManager();
            packageInfo=pm.getPackageInfo(this.getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 显示popupWindow，来显示+1
     * @param v
     */
    private void showPopupWindow(View v) {
        ImageView imageView=new ImageView(Xiaoyao_MainActivity.this);
        imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.puls1));
        final PopupWindow popupWindow=new PopupWindow(imageView,120,120,false);
        popupWindow.setAnimationStyle(R.style.popup_window);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this,android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v,-90,-210,Gravity.BOTTOM|Gravity.END);
        popupWindow.update();
        CountDownTimer timer=new CountDownTimer(1000,10) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                popupWindow.dismiss();
            }
        };
        timer.start();
    }

    /**
     * 处理登录事件
     */
    private void enterLoginActivity() {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLoginInformation();
    }

    /**
     * 按下返回键的处理结果
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            long currentTime=System.currentTimeMillis();
            if(currentTime-firstTimeClickBackTime==0||currentTime-firstTimeClickBackTime>1500)
            {
                firstTimeClickBackTime=System.currentTimeMillis();
                Toast.makeText(Xiaoyao_MainActivity.this,"再按一次返回键退出",Toast.LENGTH_SHORT).show();
            }
            else
            {
                finish();
            }

        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_collection) {
            //点击了侧边菜单栏的收藏，后的处理事件
        } else if (id == R.id.menu_setting) {

        }
        //关闭侧面导航栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
