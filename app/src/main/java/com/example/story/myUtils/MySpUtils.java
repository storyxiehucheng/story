package com.example.story.myUtils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 储存配置文件，以及一些必要的文件在本地
 * Created by story on 2017/4/17.
 */

public class MySpUtils {
    public static SharedPreferences sp;

    /**
     * 存储String数据
     * @param context 上下文环境
     * @param key     需要储存的键
     * @param value   需要储存的值
     */
    public static void putString(Context context,String key,String value)
    {
        if(sp==null)
        {
            sp=context.getSharedPreferences("myconfig",Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,value).apply();
    }

    /**
     * 读取存储的String数据
     * @param context 上下文环境
     * @param key 读取的key
     * @param defValue 读取失败返回的默认值
     * @return 返回读取的值，失败将返回默认值
     */
    public static String getString(Context context,String key,String defValue)
    {
        if(sp==null)
        {
            sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValue);
    }

    /**
     * 保存一个Boolean型数据
     * @param context 上下文环境
     * @param key 需要保存的key
     * @param booleanValue  需要保存的Boolean值
     */
    public static void putBoolean(Context context,String key,boolean booleanValue)
    {
        if(sp==null)
        {
            sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,booleanValue).apply();
    }

    /**
     * 读取一个Boolean值
     * @param context
     * @param key 读取值的key
     * @param defValue 读取失败默认的返回值
     * @return
     */
    public static boolean getBoolean(Context context,String key,boolean defValue)
    {
        if(sp==null)
        {
            sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValue);
    }
}
