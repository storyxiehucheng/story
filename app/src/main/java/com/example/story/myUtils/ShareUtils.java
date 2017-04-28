package com.example.story.myUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * Created by story on 2017/4/24.
 */

public class ShareUtils {
    public static void shareUtilWithAndroid(Activity activity, String extra_subject, String extra_text)
    {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,extra_subject);
        intent.putExtra(Intent.EXTRA_TEXT,extra_text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent,"消谣"));
    }
    public static void shareImageWithWX(Bitmap bitmap,IWXAPI mWxApi,boolean isFriendCircle)
    {
        WXImageObject imageObject=new WXImageObject(bitmap);
        WXMediaMessage msg=new WXMediaMessage();
        msg.mediaObject=imageObject;

        Bitmap thumbBmp=Bitmap.createScaledBitmap(bitmap,30,30,true);
        bitmap.recycle();
        msg.setThumbImage(thumbBmp);

        SendMessageToWX.Req req=new SendMessageToWX.Req();
        req.transaction=String.valueOf((System.currentTimeMillis()));
        req.message=msg;
        if(isFriendCircle)
        {
            req.scene=SendMessageToWX.Req.WXSceneTimeline;
        }
        else
        {
            req.scene=SendMessageToWX.Req.WXSceneSession;
        }
        mWxApi.sendReq(req);
    }

    public static void shareUrlWithWX(String url,String Title,String description,Bitmap bitmap,IWXAPI iwxapi,boolean isFriendCircle)
    {
        WXWebpageObject wxWebpageObject=new WXWebpageObject();
        wxWebpageObject.webpageUrl=url;
        WXMediaMessage msg=new WXMediaMessage(wxWebpageObject);
        msg.title=Title;
        msg.description=description;
        msg.setThumbImage(bitmap);

        SendMessageToWX.Req req=new SendMessageToWX.Req();
        req.transaction=String.valueOf(System.currentTimeMillis());
        req.message=msg;

        if(isFriendCircle)
        {
            req.scene=SendMessageToWX.Req.WXSceneTimeline;
        }
        else
        {
            req.scene=SendMessageToWX.Req.WXSceneSession;
        }
        iwxapi.sendReq(req);
    }

}
