package com.example.story.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.story.contantInfo.CommentsInfo;
import com.example.story.global.MyApplication;
import com.example.story.myUtils.CheckNetWork;
import com.example.story.myUtils.HttpUtils;
import com.example.story.myUtils.MySpUtils;
import com.example.story.myUtils.MyStaticData;
import com.example.story.xiaoyao.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Created by story on 2017/4/17.
 */

public class LoginActivity extends Activity {
    private EditText et_login_username;
    private EditText et_login_password;
    private Button bt_login_login;
    private TextView tv_login_findPassword;
    private Handler mHandler;
    private TextView tv_login_createUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initViewId();
        initData();
    }



    private void initViewId() {
        et_login_username=(EditText)findViewById(R.id.et_login_username);
        et_login_password=(EditText)findViewById(R.id.et_login_password);
        bt_login_login=(Button)findViewById(R.id.bt_login_login);
        tv_login_createUser=(TextView)findViewById(R.id.tv_login_createUser);
        tv_login_findPassword=(TextView)findViewById(R.id.tv_login_forget);
    }
    private void initData() {
        mHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                bt_login_login.setText("登录");
                switch (msg.what)
                {
                    //登录成功的处理操作
                    case 0:
                        bt_login_login.setText("登陆成功");
                        loginSucceed();
                        break;
                    case 1:
                        et_login_username.setError("用户名不存在，请检查后重试");
                        Toast.makeText(LoginActivity.this,"该账号未注册，请先注册",Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        et_login_password.setError("密码错误，请检查后重试");
                        Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(LoginActivity.this,"网络连接错误",Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(LoginActivity.this,"网络未连接，请检查网络",Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(LoginActivity.this,"服务器数据错误，请稍后重试",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        };
        if(!CheckNetWork.checkNetWorkAvailable(getApplicationContext()))
        {
            mHandler.sendEmptyMessage(4);
        }
        bt_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username= et_login_username.getText().toString();
                String password=et_login_password.getText().toString();
                String rexPassword="^(?![0-9]+$)(?![a-zA-Z]+$)[\\dA-Za-z]{6,12}$";
                if(username.isEmpty())
                {
                    et_login_username.setError("用户名不能为空");
                    Toast.makeText(LoginActivity.this,"用户名不能为空,请重新输入",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!password.matches(rexPassword))
                {
                    et_login_password.setError("密码格式为6-12位，字母和数字的组合");
                    Toast.makeText(LoginActivity.this,"密码至少为6位",Toast.LENGTH_LONG).show();
                    return;
                }
                if(CheckNetWork.checkNetWorkAvailable(getApplicationContext()))
                {
                    bt_login_login.setText("正在登录...");
                    handleLoginProcess(username,password);
                }
                else
                {
                    mHandler.sendEmptyMessage(4);
                }
            }
        });
        tv_login_createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        tv_login_findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginSucceed() {
        finish();
    }


    private void handleLoginProcess(final String username, final String password) {

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String loginConnectState = HttpUtils.loginConnect(username, password);
                if(loginConnectState.contains("token"))
                {
                    try {
                        JSONObject jsonObject=new JSONObject(loginConnectState);
                        String username=jsonObject.getString("username");
                        String token=jsonObject.getString("token");
                        MySpUtils.putString(getApplicationContext(),MyStaticData.KEY_USERNAME,username);
                        MySpUtils.putString(getApplicationContext(),MyStaticData.KEY_TOKEN,token);
                        mHandler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(5);
                    }
                }
                else if(loginConnectState.contains("未注册"))
                {
                    Log.e("story","用户不存在.......");
                    mHandler.sendEmptyMessage(1);
                }
                else if(loginConnectState.contains("密码不符"))
                {
                    Log.e("story","密码错误.......");
                    mHandler.sendEmptyMessage(2);
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                    mHandler.sendEmptyMessage(3);
                }
            }
        });
        thread.start();
    }
}
