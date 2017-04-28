package com.example.story.myUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 从网络加载图片
 * Created by story on 2017/4/12.
 */

public class ImageLoaderFromUrl {
    private static ExecutorService executorService= Executors.newFixedThreadPool(5);
//    public static void onLoadImage(final String bitmapUrl, final OnLoadImageListener onLoadListener)
//    {
////        final Handler handler=new Handler() {
////            @Override
////            public void handleMessage(Message msg) {
////                onLoadListener.OnLoadImage((Bitmap)msg.obj,null);
////            }
////        };
////        executorService.submit(new Runnable() {
////            @Override
////            public void run() {
////                try {
////                    URL imageUrl = new URL(bitmapUrl);
////                    HttpURLConnection connection= (HttpURLConnection) imageUrl.openConnection();
////                    InputStream inputstream=connection.getInputStream();
////                    Bitmap bitmap= BitmapFactory.decodeStream(inputstream);
////                    Message message=new Message();
////                    message.obj=bitmap;
////                    message.what=0;
////                    handler.sendMessage(message);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
////        });
//
//    }
//
//    public interface OnLoadImageListener
//    {
//        void OnLoadImage(Bitmap bitmap,String bitmapPath);
//    }


    public static void ImageFromUrlToFile(final String url, final String pathName, final String fileName)
    {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL imageUrl=new URL(url);
                    HttpURLConnection conn= (HttpURLConnection) imageUrl.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setInstanceFollowRedirects(true);
                    InputStream is=conn.getInputStream();
                    File file=new File(pathName,fileName);
                    FileOutputStream fos=new FileOutputStream(file);
                    copyStream(is,fos);
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * 将输入流写到输出流,即保存在文件中
     * @param is
     * @param os
     */
    private static void copyStream(InputStream is, OutputStream os) {
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
}

