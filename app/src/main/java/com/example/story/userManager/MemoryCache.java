package com.example.story.userManager;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 将图片缓存到内存中
 * Created by story on 2017/4/12.
 */

public class MemoryCache {
    private Map<String,SoftReference<Bitmap>> cache=new LinkedHashMap<>();
     public Bitmap get(String value)
     {
         if(!cache.containsKey(value))
         {
             return null;
         }
         SoftReference<Bitmap> ref=cache.get(value);
         return ref.get();
     }

     public void put(String id,Bitmap bitmap)
     {
         cache.put(id,new SoftReference<>(bitmap));
     }

     public void clear()
     {
         cache.clear();
     }
}
