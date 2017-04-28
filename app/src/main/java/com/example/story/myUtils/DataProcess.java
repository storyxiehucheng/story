package com.example.story.myUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.story.contantInfo.ContentInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by story on 2017/4/26.
 */

public class DataProcess {
    /**
     * 下拉更新
     * handler 有数据 返回0
     * handler 无数据 返回2
     */
    public static void refreshDatalistPullDown(final ArrayList<ContentInfo> mContentInfoList, final Handler mHandler,final String url)
    {
        //开启一个线程进行网络连接，并解析json数据
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonContent;
                //连接nextUrl网络，并返回具有json格式的字符串
                jsonContent = HttpUtils.getJsonContent(url);
                handlerJsonString(true,jsonContent,mContentInfoList,mHandler);
            }
        });
        //开启线程
        thread.start();
    }

    /**
     * handler 返回数据
     * 有数据  下拉  返回0
     * 有数据  上拉  返回1
     * 无数据  下拉  返回2
     * 无数据  上拉  返回3
     */

    private static void handlerJsonString(boolean isPullDown,String jsonContent,ArrayList<ContentInfo> mContentInfoList,Handler mHandler) {
        String nextUrl="";
        Message obtainMessage;
        Map<String, Object> stringObjectMap = JsonUtil.jsonToContentInfoList(jsonContent);
        if(stringObjectMap!=null)
        {
            nextUrl= (String) stringObjectMap.get("nextUrl");
            ArrayList<ContentInfo> contInfoList= (ArrayList<ContentInfo>) stringObjectMap.get("ContentInfoList");
            if(isPullDown)
            {
                mContentInfoList.clear();
                for(int i=0;i<contInfoList.size();i++)
                {
                    ContentInfo contentInfo=contInfoList.get(contInfoList.size()-1-i);
                    if(MyStaticData.hasReadIdList.contains(contentInfo.getId()))
                    {
                        contentInfo.setRead(true);
                    }
                    if(MyStaticData.collectionIdList.contains(contentInfo.getId()))
                    {
                        contentInfo.setCollection(true);
                    }
                    mContentInfoList.add(0,contentInfo);
                }
            }
            else
            {
                for (ContentInfo contentinfo : contInfoList)
                {
                    if (MyStaticData.hasReadIdList.contains(contentinfo.getId())) {
                        contentinfo.setRead(true);
                    }
                    mContentInfoList.add(contentinfo);
                }
            }
            if(isPullDown)
            {
                obtainMessage = mHandler.obtainMessage(0, nextUrl);
            }
            else
            {
                obtainMessage = mHandler.obtainMessage(1, nextUrl);
            }
            mHandler.sendMessageDelayed(obtainMessage,1000);
        }
        else
        {
            if(isPullDown)
            {
                obtainMessage = mHandler.obtainMessage(2, nextUrl);
            }
            else
            {
                obtainMessage = mHandler.obtainMessage(3, nextUrl);
            }
            mHandler.sendMessageDelayed(obtainMessage,1000);
        }
    }

    /**
     * 如果是上拉加载更多
     * handler 有数据 返回1
     * handler 无数据 返回3
     * @param mContentInfoList
     * @param mHandler
     * @param url
     */
    public static void refreshPullUP(final ArrayList<ContentInfo> mContentInfoList, final Handler mHandler,final String url)
    {
        //开启一个线程进行网络连接，并解析json数据
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonContent;
                //连接nextUrl网络，并返回具有json格式的字符串
                jsonContent = HttpUtils.getJsonContent(url);
                handlerJsonString(false,jsonContent,mContentInfoList,mHandler);
            }
        });
        //开启线程
        thread.start();
    }
}
