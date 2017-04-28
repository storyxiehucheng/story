package com.example.story.myUtils;

import android.content.Context;
import android.util.Log;

import com.example.story.contantInfo.CommentsInfo;
import com.example.story.contantInfo.ContentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * json数据解析工具类
 * Created by story on 2017/4/11.
 */

public class JsonUtil {
    public static Map<String,String> jsonToUpdateInformation(String jsonString)
    {
        Map<String,String> updateMap=new HashMap<>();
        try {
            JSONArray jsonArray=new JSONArray(jsonString);
            JSONObject jsonObject= (JSONObject) jsonArray.get(0);
            String version=jsonObject.getString("version");
            String updateUrl=jsonObject.getString("app");
            String updateInfo=jsonObject.getString("info");
            String qrCode=jsonObject.getString("qrcode");
            updateMap.put("version",version);
            updateMap.put("updateUrl",updateUrl);
            updateMap.put("updateInfo",updateInfo);
            updateMap.put("qrCode",qrCode);
            return updateMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateMap;
    }
    /**
     * 将评论信息的json格式的字符串转化为评论的对象
     * @param jsonString 需要转化的评论信息
     * @return 转化后的评论信息对象
     */
    public static CommentsInfo jsonToCommentsInfo(String jsonString)
    {
        CommentsInfo commentsInfo=null;
        try {
            JSONObject jsonObject=new JSONObject(jsonString);
            String username=jsonObject.getString("whos");
            String createdTime=jsonObject.getString("timeDelta");
            String comments=jsonObject.getString("text");
            commentsInfo=new CommentsInfo(username,comments,createdTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commentsInfo;
    }

    /**
     * 返回评论对象的List集合
     * @param jsonStr
     * @return
     */
    public static ArrayList<CommentsInfo> jsonToCommentsInfoList(String jsonStr)
    {
        ArrayList<CommentsInfo> commentsInfoArrayList=null;
        try {
            JSONObject jsonObjectAll=new JSONObject(jsonStr);
            JSONArray jsonArray=jsonObjectAll.getJSONArray("results");
            commentsInfoArrayList=new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                CommentsInfo commentsInfo = new CommentsInfo(jsonObject.getString("user"), jsonObject.getString("text"), jsonObject.getString("timeDelta"));
                commentsInfoArrayList.add(commentsInfo);
            }
            return commentsInfoArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commentsInfoArrayList;
    }
    /**
     * json的解析函数，
     * @param jsonString 需要解析的json格式的字符串
     * @return 返回一个ContentInfo对象
     */
    public static ContentInfo jsonToContentInfo(String jsonString)
    {
        ContentInfo contentinfo=null;
        try {
            /**
             * 根据json格式的字符串获取json的对象
             */
            JSONObject contentInfoJSONObject=new JSONObject(jsonString);
//            根据获取的json对象，获取制定key的jsonObject对象
//            获取contentInfo里面的内容
            contentinfo=new ContentInfo();
            contentinfo.setId(contentInfoJSONObject.getInt("id"));
            contentinfo.setRumourResult(contentInfoJSONObject.getBoolean("tf"));
            contentinfo.setContentText(contentInfoJSONObject.getString("contentText"));
            contentinfo.setRumourTitle(contentInfoJSONObject.getString("title"));
            contentinfo.setAnalysisText(contentInfoJSONObject.getString("analysisText"));
            contentinfo.setImageUrl(contentInfoJSONObject.getString("img"));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("story","json解析失败");
        }
        return contentinfo;
    }

    public static Map<String,Object> jsonToContentInfoList(String jsonString)
    {
        String nextUrl;
        String previousUrl;
        ArrayList<ContentInfo> contentInfoArrayList=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        try {
            JSONObject jsonObject=new JSONObject(jsonString);
            Log.e("story","jsonObject解析成功");
            nextUrl= jsonObject.getString("next");
            previousUrl=jsonObject.getString("previous");
            JSONArray jsonArray=jsonObject.getJSONArray("results");
            Log.e("story","jsonArray解析成功");
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonArrayObject= (JSONObject) jsonArray.get(i);
                ContentInfo jsonContentInfo=new ContentInfo();
                jsonContentInfo.setId(jsonArrayObject.getInt("id"));
                jsonContentInfo.setRumourResult(jsonArrayObject.getBoolean("tf"));
                jsonContentInfo.setRumourTitle(jsonArrayObject.getString("title"));
                jsonContentInfo.setContentText(jsonArrayObject.getString("contentText"));
                jsonContentInfo.setImageUrl(jsonArrayObject.getString("img"));
                contentInfoArrayList.add(jsonContentInfo);
            }
            map.put("nextUrl",nextUrl);
            map.put("previousUrl",previousUrl);
            map.put("ContentInfoList",contentInfoArrayList);
            return map;
        } catch (JSONException e) {
            Log.e("story","json解析失败");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list数据，转换为JSONArray的形式保存到配置文件
     * @param context 上下文环境
     * @param readIDList 需要保存的int型list
     */
    public static void saveJSONArrayToLocal(Context context,ArrayList<Integer> readIDList,String key)
    {
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<readIDList.size();i++)
        {
            try {
                jsonArray.put(i,(int)readIDList.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("story",key+"需要保存的jsonArrayID为："+jsonArray.toString());
        MySpUtils.putString(context,key,jsonArray.toString());
    }

    /**
     * 从本地文件获取List(类型为int)
     * @param context 上下文环境
     * @return
     */
    public static ArrayList<Integer> getArrayListFromLocal(Context context,String key)
    {
        String jsonString = MySpUtils.getString(context, key, "");
        ArrayList<Integer> idList=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(jsonString);
            for(int i=0;i<jsonArray.length();i++)
            {
                idList.add((int)jsonArray.get(i));
            }
            Log.e("story",key+"本地ID数据为:"+ idList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("story",key+"本地没有ID数据:"+jsonString);
        }
        return idList;
    }
}
