package com.example.story.userManager;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 储存图片
 * Created by story on 2017/4/12.
 */

public class FileCache {
    //缓存文件的目录名
    private static final String DIR_NAME=File.separatorChar+"Android"
            +File.separatorChar+"data"
            +File.separatorChar+"com.example.story.xiaoyao"
            +File.separatorChar+"imageCache";
    private File cacheDir;
    public FileCache(Context context)
    {
       //判断是否存在外部存储器
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
           //如果存在则将缓存目录存在外部存储中
            cacheDir=new File(Environment.getExternalStorageDirectory(),DIR_NAME);
        }
        else
        {
            //如果不存在，则将图片缓存在内部存储中
            cacheDir=context.getCacheDir();
        }
        if(!cacheDir.exists())
        {
            //文件目录如果不存在，则创建新的目录
            cacheDir.mkdirs();
        }
    }

    /**
     * 根据URL来创建唯一文件名的文件
     * @param url
     * @return 返回创建的文件
     */
    public File getFile(String url)
    {
        String filename=String.valueOf(url.hashCode());
        return new File(cacheDir,filename);
    }

    /**
     * 清楚缓存文件
     */
    public void clear()
    {
        File[] files=cacheDir.listFiles();
        if(files!=null)
        {
            for (File f:files)
            {
                f.delete();
            }
        }
    }
}
