package com.glovestextshow.android;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class TextDisplay {
    public  static void displayOnUi(TextView textView,String text){

        textView.append(text);
    }
}
