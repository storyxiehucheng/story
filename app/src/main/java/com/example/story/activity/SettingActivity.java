package com.example.story.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.story.global.MyApplication;
import com.example.story.xiaoyao.R;

/**
 * Created by story on 2017/4/10.
 */

public class SettingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
    }
}
