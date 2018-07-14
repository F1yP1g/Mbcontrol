package com.lu.mbcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lu.mbcontrol.utils.*;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static com.lu.mbcontrol.utils.SocketAddress.PORT;
import static com.lu.mbcontrol.utils.SocketAddress.SOCKET_ADDRESS;

public class LoginActivity extends AppCompatActivity {

    private EditText passwdET;
    private Button sendBT, dataBT,netBT,shopBT;
    private String password = "";
    private String returnData = "";
    private long firstTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passwdET  = (EditText)findViewById(R.id.ET_passwd);
        sendBT = (Button)findViewById(R.id.BT_toSend);
        dataBT = (Button)findViewById(R.id.BT_toData);
        netBT = (Button)findViewById(R.id.BT_toNetcode);
        shopBT = (Button)findViewById(R.id.BT_toshop);

        sendBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVerify(0);
            }
        });
        dataBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVerify(1);
            }
        });
        netBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVerify(2);
            }
        });
        shopBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVerify(3);
            }
        });
    }

    /**
     * 密码验证，跳转
     * @param flag
     */
    public void passwordVerify(int flag){
       password = passwdET.getText().toString();
        SoundPlay.initSoundPool(getApplicationContext());
        if(!password.isEmpty()) {
            new Thread(passwordRunnable).start();
            while (true) {
                if (!returnData.isEmpty()) {
                    break;
                }
            }
            if (returnData.equals("Logined")) {
                if (flag == 0) {
                    Intent intent = new Intent(LoginActivity.this, FunctionActivity.class);
                    startActivity(intent);
                    finish();
                } else if (flag == 1) {
                    Intent intent = new Intent(LoginActivity.this, DataSerachActivity.class);
                    startActivity(intent);
                    finish();
                }else if(flag == 2){
                    Intent intent = new Intent(LoginActivity.this, NetCodeActivity.class);
                    startActivity(intent);
                    finish();
                }else if(flag == 3){
                    Intent intent = new Intent(LoginActivity.this, tiantiantejiaActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else if(returnData.equals("noLogin")) {
                Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this, "登录失败。", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "密码不能为空！",Toast.LENGTH_SHORT).show();
        }
    }

    Runnable passwordRunnable = new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
            try {
                Log.e("address",SOCKET_ADDRESS);
                socket = new Socket(SOCKET_ADDRESS, PORT);
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("ID", "control");
                jsonobj.put("datatype","password");
                jsonobj.put("password",Passwd2Md5.getMD5(password));
                String data = jsonobj.toString();
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream);
                printWriter.print(data);
                printWriter.flush();
                socket.shutdownOutput();

                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GB2312");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String info = "";
                String temp = null;
                Log.e("log3", "in socket");
                while ((temp = bufferedReader.readLine()) != null) {
                    info += temp;
                }

                returnData = info;

                Log.e("服务端说：", returnData);
                Log.e("log5", "in socket");
                bufferedReader.close();
                inputStream.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(LoginActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
