package com.glovestextshow.android;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerThread extends Thread {
    private Socket socket;
    private TextView text;
    private BufferedReader reader = null;
    private Activity activity;
    private String line = null;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    String ip;

    public ServerThread(String ip, TextView text, Activity activity){
        this.ip = ip;
        this.text = text;
        this.activity = activity;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip,12345);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.append("@连接成功\n");
                }
            });

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"GB2312"));
            while ((line = reader.readLine()) != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Date date = new Date(System.currentTimeMillis());
                        text.append(simpleDateFormat.format(date) + " 接收：" + "\n");
                        text.append(line + "\n");
                        SpeechUtils.speekText(line);
                        SpeechUtils.SpeechText = line;
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            text.append("@连接错误\n");
        }
    }
}
