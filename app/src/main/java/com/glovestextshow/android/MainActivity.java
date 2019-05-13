package com.glovestextshow.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {
    private EditText ip;
    private TextView text;
    private BufferedReader reader = null;
    public Socket socket = null;
    public String line = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = findViewById(R.id.edit_ip);
        text = findViewById(R.id.show_message);
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=5cd5813b");

        findViewById(R.id.button_ip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });

        findViewById(R.id.button_textclear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
    }

    private void connect() {
        ip.setText(NetWorkUtils.getLocalIpAddress(MainActivity.this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(3000);
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
                        new ServerThread(socket,text,MainActivity.this).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void send() {
        SpeechUtils.speekText(text.getText().toString());
    }

    private void clear(){
        text.setText("");
    }
}
