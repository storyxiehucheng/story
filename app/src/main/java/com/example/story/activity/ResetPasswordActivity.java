package com.example.story.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.story.global.MyApplication;
import com.example.story.myUtils.CheckNetWork;
import com.example.story.myUtils.HttpUtils;
import com.example.story.xiaoyao.R;

/**
 * Created by story on 2017/4/20.
 */

public class ResetPasswordActivity extends Activity {
    private EditText et_forget_email;
    private EditText et_forget_code;
    private EditText et_forget_setPassword;
    private EditText et_forget_confirmPassword;
    private Button bt_forget_sendCode;
    private Button bt_forget_finish;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_layout);
        initViewId();
        initData();
    }

    private void initData() {
        mHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 0:
                        bt_forget_finish.setText("重 置 成 功");
                        enterLoginActivity();
                        break;
                    case 1:
                        Toast.makeText(ResetPasswordActivity.this,"注册失败，请检查网络连接",Toast.LENGTH_LONG).show();
                        bt_forget_finish.setText("重 置 密 码");
                        break;
                    case 2:
                        et_forget_email.setError("该邮箱未注册，请先注册");
                        Toast.makeText(ResetPasswordActivity.this,"该邮箱未注册，请先注册",Toast.LENGTH_LONG).show();
                        bt_forget_finish.setText("重 置 密 码");
                        break;
                    case 3:
                        bt_forget_finish.setText("重 置 密 码");
                        Toast.makeText(ResetPasswordActivity.this,"验证码已经发你的邮箱，请注意查收",Toast.LENGTH_LONG).show();
                        CountDownGetCodeAgain();
                        break;
                    case 4:
                        Toast.makeText(ResetPasswordActivity.this,"网络未连接，请检查网络",Toast.LENGTH_LONG).show();
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
        bt_forget_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=et_forget_email.getText().toString();
                String rexEmailString="^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
                if(email.matches(rexEmailString))
                {
                    sendVerificationCode(email);
                    bt_forget_finish.setClickable(true);
                    bt_forget_finish.setBackground(ContextCompat.getDrawable(ResetPasswordActivity.this,android.R.color.holo_orange_dark));

                }
                else
                {
                    Toast.makeText(ResetPasswordActivity.this,"请输入正确的邮箱格式",Toast.LENGTH_LONG).show();
                }
            }
        });

        bt_forget_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = et_forget_email.getText().toString();
                String strVerification=et_forget_code.getText().toString();
                String strPassword = et_forget_setPassword.getText().toString();
                String strPasswordConfirm = et_forget_confirmPassword.getText().toString();
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
                            if(strPassword.matches(rexPasswordString))
                            {
                                if(strPasswordConfirm.equals(strPassword))
                                {
                                    if(CheckNetWork.checkNetWorkAvailable(getApplicationContext()))
                                    {
                                        resetPasswordProcess(strEmail,strPassword,strVerification);
                                        bt_forget_finish.setText("正在重置...");
                                    }
                                    else
                                    {
                                        mHandler.sendEmptyMessage(4);
                                    }
                                }
                                else
                                {
                                    et_forget_confirmPassword.setError("密码不一致");
                                }
                            }
                            else
                            {
                                et_forget_setPassword.setError("密码格式错误，为6-12位的字母和数字的组合");
                            }
                    }
                }
                else
                {
                    Log.e("story","邮箱格式错误");
                    et_forget_email.setError("邮箱格式错误，请重试");
                }
            }
        });
    }

    private void resetPasswordProcess(final String strEmail, final String strPassword, final String strVerification) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String resetStr = HttpUtils.resetPasswordConnect(strEmail, strPassword, strVerification);
                if(resetStr.contains("密码已重置"))
                {
                    mHandler.sendEmptyMessage(0);
                }
                else if(resetStr.contains("不存在"))
                {
                    mHandler.sendEmptyMessage(2);
                }
                else
                {
                    Log.e("story",resetStr);
                    mHandler.sendEmptyMessage(1);
                }
            }
        });
        thread.start();
    }

    private void CountDownGetCodeAgain() {
        bt_forget_sendCode.setClickable(false);
        bt_forget_sendCode.setBackgroundColor(Color.GRAY);
        CountDownTimer countDowntimer=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bt_forget_sendCode.setText((millisUntilFinished/1000)+"秒后可重发");
            }
            @Override
            public void onFinish() {
                bt_forget_sendCode.setClickable(true);
                bt_forget_sendCode.setText("获取验证码");
                bt_forget_sendCode.setBackground(ContextCompat.getDrawable(ResetPasswordActivity.this,android.R.color.holo_orange_dark));
            }
        };
        countDowntimer.start();
    }

    private void enterLoginActivity() {
        Toast.makeText(ResetPasswordActivity.this,"密码已经重置，请重新登录",Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        },1000);

    }

    private void sendVerificationCode(final String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String verificationCodeResult = HttpUtils.getVerificationCode(false,email);
                if(verificationCodeResult.equals(""))
                {
                    mHandler.sendEmptyMessage(1);
                }
                else if(verificationCodeResult.contains("不存在"))
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

    private void initViewId() {
        et_forget_email=(EditText)findViewById(R.id.et_forget_email);
        et_forget_code=(EditText)findViewById(R.id.et_forget_code);
        et_forget_setPassword=(EditText)findViewById(R.id.et_forget_setPassword);
        et_forget_confirmPassword=(EditText)findViewById(R.id.et_forget_confirmPassword);
        bt_forget_sendCode=(Button)findViewById(R.id.bt_forget_sendCode);
        bt_forget_finish=(Button)findViewById(R.id.bt_forget_finish);
    }
}
