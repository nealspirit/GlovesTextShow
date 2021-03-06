package com.glovestextshow.android.utils;

import com.glovestextshow.android.domain.LanguageText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandLanguageUtils {
    //语句模型
    private static List<LanguageText> languageTexts;

    //词汇库
    private static String[] LibraryText = {"你","好","电话","多少","很可爱","要加油","很高兴","多少钱",
            "见到",
            "对不起",
            "这个地址","怎么",
            "保持","联系",
            "谢谢",
            "我","爱",
            "再见","好",
            "中国","计量","大学","欢迎","参观",
            "加","聊天",
            "微信"};
    //语句库
    private static String[] wordsLibrary = {"你好","你电话多少","你很可爱","你要加油","你很高兴","你电话什么","这个东西多少钱",
            "很高兴见到你",
            "对不起",
            "这个地址怎么走",
            "保持联系",
            "谢谢",
            "我爱你","我要加油","我联系你",
            "再见",
            "我很可爱",
            "好的",
            "欢迎来到中国计量大学参观",
            "这是我的联系方式",
            "加我大学",
            "上面有我的微信二维码"};

    public static String identityString(String text){
        int N = wordsLibrary.length;
        float[] matchers = new float[N];
        for (int i=0; i<N; i++){
            matchers[i] = languageTexts.get(i).getMatcher(text);
        }
        float[] matchers_copy = matchers.clone();
        Arrays.sort(matchers);

        int index = 0; //匹配到语句的索引
        float maxMatcher = matchers[N-1];
        for (int i=0; i<N; i++){
           if(matchers_copy[i] == maxMatcher){
               index = i;
               break;
           }
        }
        if (wordsLibrary[index].equals("加我大学")){
            wordsLibrary[index] = "你可以和我聊天";
        }

        return wordsLibrary[index];
    }

    //初始化词向量库
    public static void updateLanguage(){
        languageTexts = new ArrayList<LanguageText>();
        for(String word : wordsLibrary){
            languageTexts.add(new LanguageText(word,LibraryText));
        }
    }
}
