package com.example.story.userManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.story.contantInfo.ContentInfo;
import com.example.story.xiaoyao.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *创建一个图片管理类
 *@author story
 *@time 2017/4/12 11:10
 */
public class ImageManager {
    private Context mContext;
    private View mView;
    //设置网络加载时间
    private static final int TIME_OUT=10000;
    //定义图片的内存缓存对象
    MemoryCache mMemoryCache;
    //定义图片的文件缓存对象
    FileCache mFileCache;
    //定义一个handler去更新主线程
    Handler handler;
    //定义线程数
    ExecutorService executorService;

    public ImageManager(Context mContext) {
        this.mContext = mContext;
        //设置最大线程数为5
        executorService=Executors.newFixedThreadPool(5);
        //创建文件缓存对象
        mFileCache=new FileCache(mContext);
        //创建缓存对象
        mMemoryCache=new MemoryCache();
    }

    public void setImage(ContentInfo contentInfo, View imageView)
    {
        this.mView=imageView;
       //首先判断内存中是否有图片缓存
        Bitmap bitmap=mMemoryCache.get(contentInfo.getImageUrl());
        if(bitmap!=null)
        {
            Drawable drawable= new BitmapDrawable(mContext.getResources(),bitmap);
            //如果有则直接显示
            mView.setBackground(drawable);
//            contentInfo.setContentDrawable(drawable);
            Log.e("story","直接从内存中读取图片来显示");
        }
        else
        {
            //如果没有则从网络或者本地文件获取
            Log.e("story","直接从从网络或者本地文件获取中读取图片来显示");
            obtainImage(contentInfo,mView);
        }
    }
    public void clearCache()
    {
        mMemoryCache.clear();
        mFileCache.clear();
    }
    /**
     * 从网络或者本地去获取图片文件，
     * @param contentInfo 需要获取的图片url
     * @param mImageView 需要设置的控件
     */
    private void obtainImage(final ContentInfo contentInfo, final View mImageView) {
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                Bitmap bitmap= (Bitmap) msg.obj;
                if(bitmap!=null)
                {
                    mMemoryCache.put(contentInfo.getImageUrl(),bitmap);
                    Drawable drawable= new BitmapDrawable(mContext.getResources(),bitmap);
                    mImageView.setBackground(drawable);
//                    contentInfo.setContentDrawable(drawable);
                }
                else
                {
                    mImageView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.listview_picture5));
                }
            }
        };
        executorService.submit(new PhotosLoader(contentInfo.getImageUrl()));
    }


    private Bitmap getBitmap(String UrlPath)
    {
        //图片不存在内存当中，则判断图片是否存在文件缓存当中
        //1、获取图片文件名
        File imageFile=mFileCache.getFile(UrlPath);
        //2、将图片文件，转化为bitmap
        Bitmap bmp=decodeFile(imageFile);
        if(bmp!=null)
        {
            Log.e("story","从文件读取来显示");
            return bmp;
        }
        //如果内存和文件都不存在，则从网络下载图片
        try {
            URL imageUrl=new URL(UrlPath);
            HttpURLConnection conn= (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            //将图片输入流写入文件输出流，即将图片保存在文件系统
            OutputStream os=new FileOutputStream(imageFile);
            copyStream(is,os);
            conn.disconnect();
            Bitmap bitmap=decodeFile(imageFile);
            Log.e("story","从网络下载图片来显示");
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("story","网络获取图片失败");
            return null;
        }
    }

    /**
     * 将输入流写到输出流,即保存在文件中
     * @param is
     * @param os
     */
    private void copyStream(InputStream is, OutputStream os) {
        byte[] inPutByte=new byte[4096];
        int len;
        try {
            while((len=is.read(inPutByte))!=-1)
            {
                os.write(inPutByte,0,len);
                os.flush();
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("story","输入流转换为输出流失败,文件保存失败");
        }
    }

    /**
     * 将文件转化为bitmap
     * @param imageFile 需要转换的图片文件
     * @return 返回转换后的bitmap对象
     */
    private Bitmap decodeFile(File imageFile) {
        try {
            FileInputStream fileInputStream=new FileInputStream(imageFile);
            Bitmap bitmap= BitmapFactory.decodeStream(fileInputStream);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("story","文件转化为bitmap失败");
            return null;
        }
    }

    /**
     * 定义一个类来开启多线程，获取图片
     */
    private class PhotosLoader implements  Runnable {
        private String mUrlPath;
        PhotosLoader(String urlPath) {
            mUrlPath=urlPath;
        }
        @Override
        public void run() {
            Bitmap bitmap=getBitmap(mUrlPath);
            Message msg=new Message();
            msg.what=0;
            msg.obj=bitmap;
            handler.sendMessage(msg);
        }
    }
}
