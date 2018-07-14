package com.lu.mbcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.mbcontrol.utils.SocketAddress;
import com.lu.mbcontrol.utils.SoundPlay;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class tiantiantejiaActivity extends AppCompatActivity {
    private long firstTime = 0;
    private TextView tv_page;
    private List<tejiaUser> users = new ArrayList<>();
    private String returnData="nodata";

    private Button bt_2send, bt_2search,bt_2netCode;
    private TextView tv_search;
    private LinearLayout lin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiantiantejia);
        initView();
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHandle();
            }
        });
        bt_2search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                startActivity(new Intent(tiantiantejiaActivity.this, DataSerachActivity.class));
                finish();
            }
        });
        bt_2send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                startActivity(new Intent(tiantiantejiaActivity.this, FunctionActivity.class));
                finish();
            }
        });

        bt_2netCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                startActivity(new Intent(tiantiantejiaActivity.this, NetCodeActivity.class));
                finish();
            }
        });
        tv_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                startActivity(new Intent(tiantiantejiaActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    public void initView(){
        lin = (LinearLayout)findViewById(R.id.Lin_tipAndclick);
        lin.setBackgroundResource(R.drawable.shape2);
        bt_2search = (Button) findViewById( R.id.BT_changePage2serach_shop);
        bt_2send = (Button)findViewById(R.id.BT_changePage2send_shop);
        bt_2netCode =(Button)findViewById(R.id.BT_changePage2netCode_shop);
        tv_search = (TextView)findViewById(R.id.TV_search_click_shop);
        tv_page = (TextView)findViewById(R.id.TV_page_shop);
    }
    /**
     * 开启线程
     * 处理数据
     * 界面更新
     */
    public void dataHandle(){
        new Thread(runnable).start();
       while(true){
            if(!returnData.equals("nodata")){
                Log.e("in while(true)","still no data");
                break;
            }
        }
        try {
            Log.e("JSONObject jbj","new JSONObject(returnData);");
            JSONObject jbj = new JSONObject(returnData);

            returnData = jbj.getString("all");
            Log.e("jbj.getString",returnData);

            String[] all = returnData.split("&");
            for(int i = 0; i<all.length; i++){
                String[] tem = all[i].split(",");
                users.add(new tejiaUser(tem[0],tem[1],tem[2],tem[3]));
            }
            UserAdapter userAdapter = new UserAdapter(this,R.layout.layout_user,users);
            ListView listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(userAdapter);
            Toast.makeText(this,"用户数据查询成功！",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户数据请求线程
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(SocketAddress.SOCKET_ADDRESS, SocketAddress.PORT);
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("ID", "control");
                jsonobj.put("datatype","shopusers");
                String data = jsonobj.toString();
                OutputStream outputStream = socket.getOutputStream();//获取一个输出流，向服务端发送信息
                PrintWriter printWriter = new PrintWriter(outputStream);//将输出流包装成打印流
                printWriter.print(data);
                printWriter.flush();
                socket.shutdownOutput();

                InputStream inputStream = socket.getInputStream();//获取一个输入流，接收服务端的信息
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GB2312");//包装成字符流，提高效率
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//缓冲区
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
            } catch (SocketException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(tiantiantejiaActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
