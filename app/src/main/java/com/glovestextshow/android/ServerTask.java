package com.glovestextshow.android;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerTask extends AsyncTask<Void,String,Void> {
    private Socket socket;
    private TextView text;
    private String showText = null;
    public ServerTask(Socket s,TextView text){
        this.socket = s;
        this.text = text;
    }

    BufferedReader reader = null;
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"GB2312"));
            String line = null;
            while ((line = reader.readLine()) != null){
                publishProgress(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (showText == null || !showText.equals(values[0])) {
            text.setText(values[0]);
            showText = values[0];
        }
        super.onProgressUpdate(values);
    }
}
