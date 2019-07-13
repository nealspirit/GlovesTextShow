package com.glovestextshow.android.utils;

import android.os.Bundle;
import android.widget.Toast;

import com.glovestextshow.android.MyApplication;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

public class SpeechUtils {

    public static void speekText(String textSpeech) {
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(MyApplication.getContext(),null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant. TYPE_CLOUD); //设置云端
        mTts.startSpeaking(textSpeech,new MySynthesizerListener());
    }

    public static class MySynthesizerListener implements SynthesizerListener {
        @Override
        public void onSpeakBegin() {
            Toast.makeText(MyApplication.getContext(),"开始播放",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {
            Toast.makeText(MyApplication.getContext(),"暂停播放",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSpeakResumed() {
            Toast.makeText(MyApplication.getContext(),"继续播放",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null){
                Toast.makeText(MyApplication.getContext(),"播放完毕",Toast.LENGTH_SHORT).show();
            }else if (speechError != null){
                Toast.makeText(MyApplication.getContext(),speechError.getPlainDescription(true),Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    }
}
