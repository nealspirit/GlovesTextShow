package com.glovestextshow.android;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ip;
    private Button button_connect;
    private RecyclerView recyclerView;

    public Socket socket = null;
    private RecognizerDialog iatDialog;

    private BufferedReader reader = null;
    private String line = null;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private List<Msg> msgList = new ArrayList<>();

    private MsgAdapter adapter;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        ip = findViewById(R.id.edit_ip);
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=5cd67bda");

        //初始化点击事件
        button_connect = findViewById(R.id.button_ip);
        button_connect.setOnClickListener(this);
        findViewById(R.id.button_speak).setOnClickListener(this);

        //初始化缓存
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddress = pref.getString("ip","");
        if (!ipAddress.equals("")){
            ip.setText(ipAddress);
        }

        //初始化RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_ip:
                connect();
                break;
            case R.id.button_speak:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                }else {
                    startSpeak();
                }
                break;
            default:
                break;
        }
    }

    private void connect() {
        final String ipText = ip.getText().toString();

        if (ipText != null && !ipText.equals("")){
            editor = pref.edit();
            editor.putString("ip",ipText);
            editor.apply();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(ipText,3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                            }
                        });

                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                        while ((line = reader.readLine()) != null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Date date = new Date(System.currentTimeMillis());
                                    Msg msg = new Msg(line,Msg.TYPE_RECEIVED,date);
                                    msgList.add(msg);
                                    adapter.notifyItemInserted(msgList.size() - 1);
                                    recyclerView.scrollToPosition(msgList.size() - 1);
                                }
                            });
                        }
                        reader.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"连接出错",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();

        }else {
            Toast.makeText(this,"请输入IP地址",Toast.LENGTH_SHORT).show();
        }

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
}
