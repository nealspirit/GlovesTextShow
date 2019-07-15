package com.glovestextshow.android;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class CoverActivity extends AppCompatActivity {
    private ImageView imageView_ni;
    private ImageView imageView_hao;
    private TextView textView_nihao;
    private TextView textView_hello;
    private TextView textView_konnichiha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        imageView_ni = findViewById(R.id.iv_ni);
        imageView_hao = findViewById(R.id.iv_hao);
        textView_nihao = findViewById(R.id.tv_nihao);
        textView_hello = findViewById(R.id.tv_hello);
        textView_konnichiha = findViewById(R.id.tv_konnichiha);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏系统状态栏

        imageView_ni.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator.ofFloat(imageView_ni,"translationY",-imageView_ni.getHeight(),0).setDuration(1000).start();
            }
        });

        imageView_hao.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(imageView_hao,"translationY",imageView_hao.getHeight(),0).setDuration(1000);
                animator1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        textView_nihao.setVisibility(View.VISIBLE);
                        textView_hello.setVisibility(View.VISIBLE);
                        textView_konnichiha.setVisibility(View.VISIBLE);
                        ObjectAnimator.ofFloat(textView_nihao,"translationX",textView_nihao.getWidth(),0).setDuration(1000).start();
                        ObjectAnimator.ofFloat(textView_hello,"translationX",textView_hello.getWidth(),0).setDuration(1000).start();
                        ObjectAnimator animator2 = ObjectAnimator.ofFloat(textView_konnichiha,"translationX",textView_konnichiha.getWidth(),0).setDuration(1000);
                        animator2.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                Intent intent = new Intent(CoverActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        animator2.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animator1.start();
            }
        });

    }
}
