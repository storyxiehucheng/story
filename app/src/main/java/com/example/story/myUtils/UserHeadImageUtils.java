package com.example.story.myUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.example.story.activity.Xiaoyao_MainActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 *
 * Created by story on 2017/4/25.
 */

public class UserHeadImageUtils {
    public final static int ACTIVITY_RESULT_CAMERA=0001;//选择拍照的返回码
    public final static int ACTIVITY_RESULT_ALBUM=0002;//选择相册的返回码
    public final static String pathName=File.separatorChar+"Android"
            +File.separatorChar+"data"
            +File.separatorChar+"com.example.story.xiaoyao"
            +File.separatorChar+"imageIcon";
    public Uri photoUri;//图片的uri路径
    private Uri tempUri;
    public File picFile;//图片文件
    private Context mContext;

    public UserHeadImageUtils(Context context) {
        this.mContext = context;

    }
private File chooseFile()
{
    File file;
    try {
        File uploadFileDir=new File(Environment.getExternalStorageDirectory(),pathName);
        if(!uploadFileDir.exists())
        {
            if(!uploadFileDir.mkdirs())
            {
                return null;
            }
        }

        file=new File(uploadFileDir, "userHead.png");
        if(!file.exists())
        {
            if(!file.createNewFile())
            {
                return null;
            }
        }
        return file;
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
    return null;
}
    /**
     * 通过拍照来选取照片
     */
    public void byCamera()
    {
        File chooseFile = chooseFile();
        if(chooseFile==null)
        {
            return;
        }
        picFile=chooseFile;
        //获取图片的uri
        tempUri=Uri.fromFile(picFile);
        //启动相机的intent，传入图片的路径作为存储路径
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues contentValues=new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA,picFile.getAbsolutePath());
        tempUri=mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempUri);
        //启动intent
        ((Xiaoyao_MainActivity)mContext).startActivityForResult(cameraIntent,ACTIVITY_RESULT_CAMERA);
    }
    /**
     * 通过相册来选取图片
     */
    public void byAlbum()
    {
        File chooseFile = chooseFile();
        if(chooseFile==null)
        {
            return;
        }
        picFile=chooseFile;
        tempUri=Uri.fromFile(picFile);
        ContentValues contentValues=new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA,picFile.getAbsolutePath());
        tempUri=mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        //获取剪切图片的Intent
        final Intent intent=cutIntentByAlbumIntent();
        ((Xiaoyao_MainActivity)mContext).startActivityForResult(intent,ACTIVITY_RESULT_ALBUM);
    }

    private Intent cutIntentByAlbumIntent() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT,null);
        intent.setType("image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    public void cutImageByCamera()
    {
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(tempUri, "image/*");
        intent.putExtra("crop", "true");
        //设定宽高比
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设定剪裁图片宽高
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ((Xiaoyao_MainActivity)mContext).startActivityForResult(intent,ACTIVITY_RESULT_ALBUM);
    }

    public Bitmap decodeBitmap()
    {
        Bitmap bitmap=null;
        try
        {
            if(tempUri!=null)
            {
                photoUri=tempUri;
                bitmap= BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(photoUri));
            }
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
