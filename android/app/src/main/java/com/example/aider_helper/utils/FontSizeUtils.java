package com.example.aider_helper.utils;

import android.widget.TextView;

public class FontSizeUtils {

    public int size = 1;
    private static FontSizeUtils instance;

    public static FontSizeUtils getInstance() {
        if(instance==null)
             instance = new FontSizeUtils();
        return instance;
    }

    public void increase(TextView textView){
        float lastValue = textView.getTextSize()*0.5f;
        if(lastValue-1 < 70)
            textView.setTextSize(lastValue+1);
    }

    public void decrease(TextView textView){
        float lastValue = textView.getTextSize()*0.5f;
        if(lastValue-1 > 15)
            textView.setTextSize(lastValue-1);
    }
}
