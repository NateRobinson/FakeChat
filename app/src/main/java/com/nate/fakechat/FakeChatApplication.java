package com.nate.fakechat;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Nate on 2015/9/6.
 */
public class FakeChatApplication extends Application
{
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        // 初始化Fresco加载框架
        Fresco.initialize(this);
    }
}
