package com.example.story.myUtils;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 网络连接工具类
 * Created by story on 2017/4/11.
 */
public class HttpUtils {
    /**
     * 评论网络的连接
     * @param token 从服务器获取的token
     * @param comments 评论的内容
     * @param rumourId 需要评论的谣言的ID
     * @return 返回服务器的返回结果
     */
    public static String commentsConnect(String token,String comments,int rumourId)
    {
        String urlStr="http://iecas.win/news/"+rumourId+"/comments/";
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("text",comments);
            URL url=new URL(urlStr);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            //设置输入流为true
            connection.setDoInput(true);
            //设置输出流为true
            connection.setDoOutput(true);
            //设置请求方式为post方式请求
            connection.setRequestMethod("POST");
            //设置超时连接时间
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            //关闭缓存，使用post方式请求的，必须关闭缓存
            connection.setUseCaches(false);
            //设置连接遵循重定向
            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length",String.valueOf(jsonObject.toString().getBytes().length));
            connection.setRequestProperty("Authorization","Token "+token);
            connection.connect();

            DataOutputStream dop=new DataOutputStream(connection.getOutputStream());
            dop.write(jsonObject.toString().getBytes());
            dop.flush();
            dop.close();

            int code=connection.getResponseCode();
            if(code==HttpURLConnection.HTTP_CREATED)
            {
                InputStream inputStream=connection.getInputStream();
                String streamToString = inputStreamToString(inputStream);
                Log.e("story","评论成功:"+streamToString);
                return "评论成功";
            }
            else
            {
                //错误代码的时候，利用getErrorStream来获取流数据
                InputStream inputStream=connection.getErrorStream();
                String streamToString = inputStreamToString(inputStream);
                Log.e("story","评论失败,错误为："+streamToString);
                return "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  "";
    }

    /**
     * 登录的网络连接
     * @param username 登录的用户名
     * @param password 登录的密码
     * @return 返回服务器的数据
     */
    public static String loginConnect(String username,String password)
    {
        String resultStr = UserManagerConnect(null,username, password, null, "http://iecas.win/login/");
        return resultStr;
    }

    public static String resetPasswordConnect(String email, String password, String verification)
    {
        String resultStr=UserManagerConnect(email,null,password,verification,"http://iecas.win/reset/");
        return resultStr;
    }
    /**
     * 通过邮箱发送验证码
     * @param email 需要发送验证码的邮箱
     * @return 返回验证码
     */
    public static String getVerificationCode(boolean isRegister,String email)
    {
        if(isRegister)
        {
            String jsonContent = getJsonContent("http://iecas.win/register/?email=" + email);
            return jsonContent;
        }
        else
        {
            String jsonContent = getJsonContent("http://iecas.win/reset/?email=" + email);
            return jsonContent;
        }

    }
    /**
     * 注册连接网络
     * @param email 注册的邮箱
     * @param username 注册的用户名
     * @param password 注册的密码
     * @param verificationCode 验证码
     * @return
     */
    public static String registerConnect(String email,String username,String password,String verificationCode)
    {
        String resultStr = UserManagerConnect(email,username,password,verificationCode,"http://iecas.win/register/");
        return resultStr;
    }
    /**
     * 用户管理的网络连接
     * @param email 注册的邮箱（登录时不需要）
     * @param username 注册的用户名(或者登录名)
     * @param password 注册的密码(或者登录密码)
     * @param verificationCode    邮箱验证码(登录时不需要验证码)
     * @param loginUrl 需要使用的的网址（登录或者注册的网址）
     * @return 返回服务器返回的信息
     */
    private static String UserManagerConnect(String email,String username,String password,String verificationCode,String loginUrl)
    {
        Log.e("story","开始登录");
        try {
            //将数据封装成json对象
            JSONObject jsonObject=new JSONObject();
            if(username!=null)
            {
                jsonObject.put("username",username);
            }
            jsonObject.put("password",password);
            if(verificationCode!=null)
            {
                jsonObject.put("last_name",verificationCode);
            }
            if(email!=null)
            {
                jsonObject.put("email",email);
            }

            Log.e("story","封装的json结果为："+jsonObject);
            //创建URL网址
            URL url=new URL(loginUrl);
            //对网络进行参数设置
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            //设置输入流为true
            connection.setDoInput(true);
            //设置输出流为true
            connection.setDoOutput(true);
            //设置请求方式为post方式请求
            connection.setRequestMethod("POST");
            //设置超时连接时间
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            //关闭缓存，使用post方式请求的，必须关闭缓存
            connection.setUseCaches(false);

            connection.setInstanceFollowRedirects(true);
            //设置请求的参数
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type",
                    "application/json");
            connection.setRequestProperty("Content-Length",String.valueOf(jsonObject.toString().getBytes().length));
            connection.connect();
            //将数据输出
            OutputStream dop=connection.getOutputStream();
            dop.write(jsonObject.toString().getBytes());
            dop.flush();
            dop.close();
            //得到状态码
            int code=connection.getResponseCode();
            //如果状态码返回创建成功(201),则返回服务器数据
            if(code==HttpURLConnection.HTTP_CREATED)
            {
                Log.e("story","注册成功");
                InputStream inputStream=connection.getInputStream();
                String streamToString = inputStreamToString(inputStream);
                Log.e("story","接收到的注册信息为："+streamToString);
                return streamToString;
            }
            //如果返回的状态码是OK（200），则表示登录成功
            else if(code==HttpURLConnection.HTTP_OK)
            {
                InputStream inputStream=connection.getInputStream();
                String streamToString = inputStreamToString(inputStream);
                Log.e("story","登录成功-----接收到的网络数据为："+streamToString);
                return streamToString;
            }
            //如果返回的状态码是错误请求(400),则表示登录失败
           else
            {
                //错误代码的时候，利用getErrorStream来获取流数据
                InputStream inputStream=connection.getErrorStream();
                String streamToString = inputStreamToString(inputStream);
                Log.e("story","登录或者注册失败：错误为："+streamToString);
                return streamToString;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("story","登录失败： URL解析错误");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("story","读取输入流失败:"+e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //错误则返回空
        return "";
    }

    public static String getJsonCommentsListWithId(int id)
    {
        String url="http://iecas.win/news/"+id+"/comments.json";
        String s = getJsonContent(url);
        return s;
    }
    /**
     * 根据ID去访问网址，获取指定ID的jsonContent内容
     * @param id 需要访问的文章ID
     * @return 返回文章的JSON内容
     */
    public static String getJsonContentWithId(int id)
    {
        String url="http://iecas.win/news/" + id + "/detail.json";
        String jsonContent = getJsonContent(url);
        return jsonContent;

    }

    /**
     * 访问整个文章的列表
     * @return 返回这个文章列表的json内容
     */
    public static String getJsonContentWithAll()
    {
        String url="http://iecas.win/news.json";
        return getJsonContent(url);
    }
    /**
     * 连接网络，获取网络的数据，并以字符串的形式返回
     * @param url_path 需要连接的URL地址
     * @return 返回字符串网络数据,返回空字符串，这表示连接失败。
     */
    public static String getJsonContent(String url_path)
    {
        try {
            URL url=new URL(url_path);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            //设置超时连接时间为2秒
            connection.setConnectTimeout(2000);
            //设置转换为inputStream
            connection.setDoInput(true);
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setReadTimeout(4000);
            //获取连接状态码
            int code=connection.getResponseCode();
            //判断状态码是否连接正常
            if(code==HttpURLConnection.HTTP_OK)
            {
                String toString = inputStreamToString(connection.getInputStream());
                return toString;
            }
            else
            {
                //错误代码的时候，利用getErrorStream来获取流数据
                InputStream inputStream=connection.getErrorStream();
                String streamToString = inputStreamToString(inputStream);
                Log.e("story","登录或者注册失败：错误为："+streamToString);
                return streamToString;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("story","网络连接失败.....URL不对");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("story","网络连接失败");
        }
        //连接失败者返回空字符串
        return "";
    }

    /**
     * 将输入流转换为字符串
     * @param inputStream  需要转换的输入流
     * @return 返回字符串
     */
    private static String inputStreamToString(InputStream inputStream) {
        StringBuilder outBuffer=new StringBuilder();
        InputStreamReader inputStreamReader;
        BufferedReader bufferReader;
        String newLine;
        try {
            inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
            bufferReader=new BufferedReader(inputStreamReader);
            while((newLine=bufferReader.readLine())!=null)
            {
                outBuffer.append(newLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.e("story","网络JSON内容:"+outBuffer.toString());
        return outBuffer.toString();
    }

}
