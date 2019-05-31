package com.glovestextshow.android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ip;
    private TextView text;
    public Socket socket = null;
    private boolean isConnected = false;
    private ServerSocket serverSocket = null;
    private Button button_connect;
    private RecognizerDialog iatDialog;

    Thread connectThread = null;
    Thread serverThread = null;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = findViewById(R.id.edit_ip);
        text = findViewById(R.id.show_message);
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=5cd67bda");

        button_connect = findViewById(R.id.button_ip);
        button_connect.setOnClickListener(this);

        findViewById(R.id.button_play).setOnClickListener(this);
        findViewById(R.id.button_textclear).setOnClickListener(this);
        findViewById(R.id.button_speak).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_ip:
                if (isConnected){
                    if (connectThread != null && serverThread != null) {
                        serverThread.interrupt();
                        connectThread.interrupt();
                        try {
                            socket.close();
                            Log.d("提示信息", "Socket关闭");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("提示信息", "Socket关闭出错");
                        }
                        try {
                            serverSocket.close();
                            Log.d("提示信息", "ServerSocket关闭");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("提示信息", "ServerSocket关闭出错");
                        } finally {
                            serverSocket = null;
                            isConnected = false;
                            button_connect.setText("连接");
                        }
                    }else {
                        isConnected = false;
                        button_connect.setText("连接");
                    }
                }else {
                    connect();
                    isConnected = true;
                    button_connect.setText("断开");
                }
                break;
            case R.id.button_play:
                SpeechUtils.speekText(SpeechUtils.SpeechText);
                break;
            case R.id.button_textclear:
                clear();
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
        ip.setText(NetWorkUtils.getLocalIpAddress(MainActivity.this));

        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"端口打开",Toast.LENGTH_SHORT).show();
                        }
                    });
                    while (true){
                        socket = serverSocket.accept();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"有客户端连接到了本机",Toast.LENGTH_SHORT).show();
                            }
                        });
                        serverThread = new ServerThread(socket,text,MainActivity.this);
                        serverThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("提示信息", "connectThread线程错误");
                }
            }
        });
        connectThread.start();
    }

    private void clear(){
        text.setText("");
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
                    text.append(words + "\n");
                    SpeechUtils.SpeechText = words;
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
