package com.example.story.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by story on 2017/4/17.
 */

public class RegisterActivity extends Activity {
    private EditText et_register_username;
    private EditText et_register_email;
    private EditText et_register_password;
    private EditText et_confirm_password;
    private Handler mHandler;
    private EditText et_register_code;
    private Button bt_register_finish;
    private Button bt_register_sendCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initViewId();
        initData();
    }
    private void initViewId() {
        et_register_email=(EditText)findViewById(R.id.et_register_email);
        et_register_code=(EditText)findViewById(R.id.et_register_code);
        bt_register_finish=(Button)findViewById(R.id.bt_register_finish);
        bt_register_sendCode=(Button)findViewById(R.id.bt_register_sendCode);
        et_register_username=(EditText)findViewById(R.id.et_register_userName);
        et_register_password=(EditText)findViewById(R.id.et_register_setPassword);
        et_confirm_password=(EditText)findViewById(R.id.et_register_confirmPassword);

    }
    private void initData() {
        mHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 0:
                        bt_register_finish.setText("注册成功，即将返回主页");
                        enterHomeActivity();
                        break;
                    case 1:
                        Toast.makeText(RegisterActivity.this,"注册失败，请检查网络连接",Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        et_register_email.setError("该邮箱已被注册，请直接登录");
                        Toast.makeText(RegisterActivity.this,"该邮箱已被注册，请直接登录",Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(RegisterActivity.this,"验证码已发你的邮箱，请注意查收",Toast.LENGTH_LONG).show();
                        bt_register_finish.setClickable(true);
                        bt_register_finish.setBackground(ContextCompat.getDrawable(RegisterActivity.this,android.R.color.holo_orange_dark));
                        CountDownGetCodeAgain();
                        break;
                    case 4:
                        Toast.makeText(RegisterActivity.this,"网络未连接，请检查网络",Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(RegisterActivity.this,"服务器数据错误，请稍后重试",Toast.LENGTH_LONG).show();
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
        bt_register_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1、获取邮箱地址
                String email = et_register_email.getText().toString();
                String rexEmailString="^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
                if(email.matches(rexEmailString))
                {
                    if(CheckNetWork.checkNetWorkAvailable(getApplicationContext()))
                    {
                        sendVerificationCode(email);
                    }
                    else
                    {
                        mHandler.sendEmptyMessage(4);
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"请输入正确的邮箱格式",Toast.LENGTH_LONG).show();
                    et_register_email.setError("邮箱格式错误，请重新输入");
                }
            }
        });
        bt_register_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = et_register_email.getText().toString();
                String strUserName = et_register_username.getText().toString();
                String strVerification=et_register_code.getText().toString();
                String strPassword = et_register_password.getText().toString();
                String strPasswordConfirm = et_confirm_password.getText().toString();
                //邮箱的正则表达式匹配式子
                String rexEmailString="^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
                //匹配密码的正则表达式，为6-12位，必须包含数字和字母
                String rexPasswordString="^(?![0-9]+$)(?![a-zA-Z]+$)[\\dA-Za-z]{6,12}$";

                if(strEmail.matches(rexEmailString))
                {
                    Log.e("story","邮箱正确");
                    if(strVerification.length()==4)
                    {
                        Log.e("story","验证码正确");
                        if(!strUserName.isEmpty())
                        {
                            Log.e("story","用户名不为空");
                            if(strPassword.matches(rexPasswordString))
                            {
                                if(strPasswordConfirm.equals(strPassword))
                                {
                                    registerProcess(strEmail,strUserName,strPassword,strVerification);
                                    bt_register_finish.setText("正在注册...");
                                }
                                else
                                {
                                    et_confirm_password.setError("密码不一致");
                                }
                            }
                            else
                            {
                                et_register_password.setError("密码格式错误，为6-12位的字母和数字的组合");
                            }
                        }
                        else
                        {
                            et_register_username.setError("用户名不能为空");
                        }
                    }
                }
                else
                {
                    Log.e("story","邮箱格式错误");
                    et_register_email.setError("邮箱格式错误，请重试");
                }
            }
        });
    }

    private void enterHomeActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(RegisterActivity.this,Xiaoyao_MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }

    private void CountDownGetCodeAgain() {
        bt_register_sendCode.setClickable(false);
        bt_register_sendCode.setBackgroundColor(Color.GRAY);
        CountDownTimer countDowntimer=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bt_register_sendCode.setText((millisUntilFinished/1000)+"秒后可重发");
            }

            @Override
            public void onFinish() {
                bt_register_sendCode.setClickable(true);
                bt_register_sendCode.setText("获取验证码");
                bt_register_sendCode.setBackground(ContextCompat.getDrawable(RegisterActivity.this,android.R.color.holo_orange_dark));
            }
        };
        countDowntimer.start();
    }

    /**
     * 注册
     * @param verificationCode 邮箱验证码
     * @param strUserName 注册的用户名
     * @param strPassword 注册的密码
     */
    private void registerProcess(final String email,final String strUserName, final String strPassword, final String verificationCode) {
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    String registerStr = HttpUtils.registerConnect(email,strUserName, strPassword, verificationCode);
                    if(registerStr.contains("token"))
                    {
                        try {
                            JSONObject jsonObject=new JSONObject(registerStr);
                            String strToken=jsonObject.getString("token");
                            saveUserInformation(email,strUserName,strToken);
                            mHandler.sendEmptyMessage(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(5);
                        }

                    }
                    else
                    {
                        Log.e("story",registerStr);
                        mHandler.sendEmptyMessage(1);
                    }
                }
            });
            thread.start();
    }

    /**
     * 保存用户信息
     * @param email 需要保存的邮箱
     * @param strUserName 需要保存的用户名
     * @param token 需要保存的token
     */
    private void saveUserInformation(String email, String strUserName,String token) {
        MySpUtils.putString(getApplicationContext(), MyStaticData.KEY_USERNAME,strUserName);
        MySpUtils.putString(getApplicationContext(),MyStaticData.KEY_EMAIL,email);
        MySpUtils.putString(getApplicationContext(),MyStaticData.KEY_TOKEN,token);
    }

    /**
     * 通过邮箱，发送验证码
     * @param email 需要发送验证码的邮箱
     */
    private void sendVerificationCode(final String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String verificationCodeResult = HttpUtils.getVerificationCode(true,email);
                if(verificationCodeResult.equals(""))
                {
                    mHandler.sendEmptyMessage(1);
                }
                else if(verificationCodeResult.contains("已被注册"))
                {
                    mHandler.sendEmptyMessage(2);
                }
                else
                {
                    mHandler.sendEmptyMessage(3);
                }
            }
        }).start();
    }
}
