package com.glovestextshow.android.domain;

public class LanguageText {
    //权重
    private float weight = 0.5f;

    //词向量
    private byte[] wordVector;

    //词汇个数
    private int wordsNum;

    //词汇库
    private static String[] LibraryText;

    public LanguageText(String text,String[] Library) {
        LibraryText = Library;
        this.wordVector = text2WordVector(text);
        this.wordsNum = 0;
        for(int i = 0; i < LibraryText.length;i++){
           this.wordsNum += this.wordVector[i];
        }
    }
    //计算匹配度
    public float getMatcher(String text){
        int N = LibraryText.length;
        byte[] textWordVector = text2WordVector(text);
        int matcher = 0;
        for(int i=0;i < N;i++){
            if(textWordVector[i] == 1 && textWordVector[i] == this.wordVector[i] ){
                matcher++;
            }
        }

        int textWordNum = 0; //待识别语句词汇匹配个数
        for(int i=0;i < N;i++){
            if(textWordVector[i] == 1 ){
                textWordNum++;
            }
        }


        return this.weight*matcher/this.wordsNum + (1.0f - this.weight)*matcher/textWordNum;
    }

    //将句子转换为词向量
    public static byte[] text2WordVector(String text){
        int N = LibraryText.length;
        byte[] wordVector = new byte[N];
        //初始化词向量
        for(int i =0;i < N; i++){
            if (text.contains(LibraryText[i])){
                wordVector[i] = 1;
            }else{
                wordVector[i] = 0;
            }
        }
        return wordVector;
    }
}
