package com.glovestextshow.android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText send_msg;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView NavHeaderIP;

    public Socket socket = null;
    private RecognizerDialog iatDialog;

    private BufferedReader reader = null;
    private String line = null;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private List<Msg> msgList = new ArrayList<>();

    private MsgAdapter adapter;


    private static final String TAG = "MainActivity";

    //IP地址
    String ipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        send_msg = findViewById(R.id.edit_msg);
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=5cd67bda");

        //初始化点击事件
        findViewById(R.id.button_speak).setOnClickListener(this);
        findViewById(R.id.button_send).setOnClickListener(this);

        //初始化缓存
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        ipText = pref.getString("ip","");
        if (ipText.equals("")){
            ipText = "无连接";
        }

        //初始化RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(adapter);

        //设置Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //初始化DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        //设置NavigationView
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        NavHeaderIP = headerView.findViewById(R.id.nav_header_IP);
        NavHeaderIP.setText(NetWorkUtils.getLocalIpAddress(this));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_reconnect:
                        connect();
                        break;
                    case R.id.change_ip:
                        final EditText et = new EditText(MainActivity.this);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("切换IP")
                                .setView(et)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ipText = et.getText().toString();
                                        connect();
                                        Toast.makeText(MainActivity.this,"切换成功",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        connect();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_speak:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                }else {
                    startSpeak();
                }
                break;
            case R.id.button_send:
                String content = send_msg.getText().toString();
                if (!"".equals(content)){
                    Date date = new Date(System.currentTimeMillis());
                    Msg msg = new Msg(content,Msg.TYPE_SENT,date);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    recyclerView.scrollToPosition(msgList.size() - 1);
                    send_msg.setText("");
                }
                break;
            default:
                break;
        }
    }

    private void connect() {
        toolbar.setTitle(ipText);
        toolbar.setSubtitle("正在连接...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ipText,3000);

                    editor = pref.edit();
                    editor.putString("ip",ipText);
                    editor.apply();

                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                    while ((line = reader.readLine()) != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (line.equals("@200")){
                                    toolbar.setSubtitle("已连接");
                                }else {
                                    Date date = new Date(System.currentTimeMillis());
                                    Msg msg = new Msg(line, Msg.TYPE_RECEIVED, date);
                                    msgList.add(msg);
                                    adapter.notifyItemInserted(msgList.size() - 1);
                                    recyclerView.scrollToPosition(msgList.size() - 1);
                                }
                            }
                        });
                    }
                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toolbar.setSubtitle("连接断开");
                        }
                    });
                }
            }
        }).start();

    }

    private void startSpeak() {
        iatDialog = new RecognizerDialog(MainActivity.this,minitListener);
        iatDialog.setListener(new RecognizerDialogListener() {
            String resultJson = "[";

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    resultJson += recognizerResult.getResultString() + ",";
                }else{
                    resultJson += recognizerResult.getResultString() + "]";
                }
                if (isLast){
                    Gson gson = new Gson();
                    List<DictationResult> resultList = gson.fromJson(resultJson,new TypeToken<List<DictationResult>>(){}.getType());
                    String words = "";
                    for(DictationResult result : resultList){
                        words += result.toString();
                    }
                    Date date = new Date(System.currentTimeMillis());
                    Msg msg = new Msg(words,Msg.TYPE_SENT,date);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    recyclerView.scrollToPosition(msgList.size() - 1);
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                speechError.getPlainDescription(true);
            }
        });
        iatDialog.show();
    }

    private InitListener minitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            Log.d(TAG, "SpeechRecognizer init() code = " + i);
            if (i != ErrorCode.SUCCESS){
                Toast.makeText(MainActivity.this,"初始化失败，错误码:" + i,Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startSpeak();
                }else {
                    Toast.makeText(this,"权限被禁止",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clearText:
                msgList.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.reconnect:
                connect();
                break;
            case R.id.setting:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }
}
