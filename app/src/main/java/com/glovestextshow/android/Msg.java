package com.glovestextshow.android;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;
    private Date date;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    public Msg(String content, int type, Date date) {
        this.content = content;
        this.type = type;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getDate() {
        return simpleDateFormat.format(date);
    }
}
