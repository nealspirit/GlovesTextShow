package com.glovestextshow.android.utils;

public class MatchTextUtils {

    //手语语句库
    private static String[] LibraryText = {"你/好/","你/电话/多少/","你/很可爱/","你/要加油/","你/很高兴/","你/电话/什么/","你/多少钱/",
                                            "很高兴/见到/你/",
                                            "对不起/",
                                            "这个地址/怎么/走/",
                                            "保持/联系/",
                                            "谢谢/",
                                            "我/爱/你/","我/很高兴/","我/要加油/","我/电话/","我/走/","我/联系/你/",
                                            "再见",
                                            "我很可爱"};

    public static String SearchText(String text){
        String receivedText = "";
        int i = 0;

        for (String receivedSingleText : text.split("/")){
            for (String librarySingleText : LibraryText){
                String[] wordGroup = librarySingleText.split("/");

                if ( (i+1) <= wordGroup.length) {
                    if (receivedSingleText.equals(wordGroup[i])){
                        receivedText += receivedSingleText;
                        i++;
                        break;
                    }
                }

            }
        }

        if (receivedText.equals("")){
            receivedText = "无法识别，请重新输入";
        }
        return receivedText;
    }
}
