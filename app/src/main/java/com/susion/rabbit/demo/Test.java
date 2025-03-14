package com.susion.rabbit.demo;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * susionwang at 2020-01-02
 */
public class Test {

    public int test() {

        return 0;
    }

    private int a = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            a = 2;
        }
    };


    public static void testStaticFun() {
        int a = 0;
        int b = 1;
        int c = a + b;
    }

    public static void asyncMethod() throws Exception {
        int a = 0;
        int b = 1;
        int c = a + b;
        Thread.sleep(1000);
    }

    public static void syncMethod() throws Exception {
        int a = 0;
        int b = 1;
        int c = a + b;
        Thread.sleep(1000);
    }
}
