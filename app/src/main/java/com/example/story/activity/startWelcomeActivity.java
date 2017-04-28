package com.example.story.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import com.example.story.xiaoyao.R;

/**
 * Created by story on 2017/4/10.
 */

public class startWelcomeActivity extends Activity {
    private ImageView iv_startWelcome_activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_welcome_activity);
        if(Build.VERSION.SDK_INT>20)
        {
            iv_startWelcome_activity=(ImageView)findViewById(R.id.iv_startWelcome_activity);
            //为图片设置过度动画
            AlphaAnimation alpha=new AlphaAnimation(0,1);
            alpha.setDuration(2000);
            iv_startWelcome_activity.startAnimation(alpha);
        }
        //开启一个线程，使画面停留两秒进入主界面
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    enterMainHome();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * 进入主界面函数
     */
    private void enterMainHome() {
        Intent intent=new Intent(this,Xiaoyao_MainActivity.class);
        startActivity(intent);
        finish();
    }
}
