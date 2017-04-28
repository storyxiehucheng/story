package com.example.story.myUtils;

import android.graphics.Bitmap;

import com.example.story.contantInfo.ContentInfo;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.ArrayList;

/**
 * 静态变量的保存
 * Created by story on 2017/4/10.
 */

public class MyStaticData {
    public static  Bitmap userHeadIcon=null;
    /**
     * 点击"假"的标志位
     */
    public static final int CLICK_FALSE = 1;

    /**
     * 点击“真”的标志位
     */
    public static final int CLICK_TRUE = 2;

    /**
     * 点击背景图片的标志位
     */
    public static final int CLICK_PICTURE = 0;
    /**
     * sp保存已经读过的ID的key
     */
    public static final String KEY_HAS_READ_ID ="has_read_ID" ;
    /**
     * sp保存用户名的key
     */
    public static final String KEY_USERNAME ="username" ;
    /**
     * sp保存base64编码的key
     */
    public static final String KEY_USERNAME_PASSWORD_BASE64 = "username_password_base64";
    /**
     * sp保存已经收藏的ID的key
     */
    public static final String KEY_COLLECTION_ID = "key_collection_id";
    /**
     * sp保存的密码的key
     */
    public static final String KEY_PASSWORD = "key_password";
    /**
     * sp保存的邮箱的key
     */
    public static final String KEY_EMAIL = "key_email";
    /**
     * sp保存token的key
     */
    public static final String KEY_TOKEN = "token";
    /**
     * 创建一个全局静态类，来管理数据列表
     */
    public static ArrayList<ContentInfo> contentList=new ArrayList<>();

    /**
     * 创建一个全局静态类，来管理收藏的列表
     */
    public static ArrayList<ContentInfo> contentListCollection=new ArrayList<>();/**
     * 创建一个全局静态类，来管理食物类数据列表
     */
    public static ArrayList<ContentInfo> contentFoodList=new ArrayList<>();

    /**
     * 创建一个全局静态类，来管理科技的列表
     */
    public static ArrayList<ContentInfo> contentTechnologyList=new ArrayList<>();
    /**
     * 创建一个全局静态类，来管理健康类数据列表
     */
    public static ArrayList<ContentInfo> contentHealthyList=new ArrayList<>();

    /**
     * 表示进入的是推荐页
     */
    public static final int PAGE_RECOMMEND=0;
    /**
     * 表示进入的是印记页
     */
    public static final int PAGE_COLLECTION=4;
    /**
     * 表示进入的是食物页
     */
    public static final int PAGE_FOOD=1;
    /**
     * 表示进入的是健康页
     */
    public static final int PAGE_HEALTHY=2;
    /**
     * 表示进入的是科技页
     */
    public static final int PAGE_TECHNOLOGY=3;


    /**
     * 保存已经阅读过后的ID
     */
    public static ArrayList<Integer> hasReadIdList=new ArrayList<>();
    /**
     * 保存收藏的文章的ID
     */
    public static ArrayList<Integer> collectionIdList=new ArrayList<>();
    /**
     * 保存网络状态的值
     */
    public static boolean isNetWorkConnect=false;
    /**
     * 微信的APP_ID
     */
    public static final String weiChatAPP_ID="wx90525fc6768978d7";
    /**
     * 微信api
     */
    public static IWXAPI mWxApi;
    public static Bitmap shareTitleBitmap;
    /**
     * 下载链接的key
     */
    public static final  String KEY_DOWNLOAD_URL="key_download_url";
}
