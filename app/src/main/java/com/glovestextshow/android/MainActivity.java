package com.glovestextshow.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ip;
    private TextView text;
    public Socket socket = null;
    private boolean isConnected = false;
    private ServerSocket serverSocket = null;
    private Button button_connect;

    Thread connectThread = null;
    Thread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = findViewById(R.id.edit_ip);
        text = findViewById(R.id.show_message);
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=5cd5813b");

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
                SpeechUtils.speekText(text.getText().toString());
                break;
            case R.id.button_textclear:
                clear();
                break;
            case R.id.button_speak:

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

}
