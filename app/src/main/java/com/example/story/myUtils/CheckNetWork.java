package com.example.story.myUtils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by story on 2017/4/21.
 */

public class CheckNetWork {

    public static boolean checkNetWorkAvailable(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo()!=null)
        {
            if(connectivityManager.getActiveNetworkInfo().isAvailable())
            {
                return true;
            }
        }
        return false;
    }
}
