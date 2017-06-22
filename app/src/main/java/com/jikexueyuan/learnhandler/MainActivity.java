package com.jikexueyuan.learnhandler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

/**
 * 这是一个学习Handler的过程
 * 其中有主线程中的Handler，还有新线程中的Handler
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnClickListener {

    private Button button;
    private Button button2;
    private TextView textView;
    private int count;
    private Handler mHandler;

    private class ShowThread extends Thread {

        Handler threadHandler;

        @Override
        public void run() {
            Looper.prepare();
            threadHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x123) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(getString(R.string.button_text) + count);
                            }
                        });
                    }
                }
            };
            Looper.loop();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView);
        count = 0;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x123:
                        button.setText(getString(R.string.button_text) + count);
                        break;
                    case 0x234:
                        button2.setText(msg.obj.toString());
                        break;
                }
            }
        };
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                count++;
                //改变按钮的text
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(0x123);
                    }
                }).start();
                //改变TextView的text
                ShowThread showThread = new ShowThread();
                showThread.start();
                try {
                    //因为创建线程和初始化需要时间，所以主线程休眠100毫秒以后再向新线程发消息
                    Thread.sleep(100);
                    showThread.threadHandler.sendEmptyMessage(0x123);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button2:
                //获取Message实例
                final Message msg = Message.obtain();
                msg.obj = "我是通过Message向myHandler发送的信息";
                msg.what = 0x234;

                //两种向myHandler发送消息的方式都可以用
//                msg.setTarget(mHandler);
//                msg.sendToTarget();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendMessage(msg);
                    }
                }).start();
                break;
        }
    }
}
