/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nate.baselibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.nate.baselibary.R;
import com.nate.baselibrary.eventbus.EventCenter;
import com.nate.baselibrary.loading.ShapeLoadingDialog;
import com.nate.baselibrary.netstatus.NetChangeObserver;
import com.nate.baselibrary.netstatus.NetStateReceiver;
import com.nate.baselibrary.netstatus.NetUtils;
import com.nate.baselibrary.utils.CommonUtils;
import com.nate.baselibrary.utils.SmartBarUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import de.greenrobot.event.EventBus;

/**
 * App基本AppCompatActivity，所有页面继承自此类
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity
{
    
    /**
     * Log tag
     */
    protected static String TAG_LOG = null;
    
    /**
     * 设备屏幕信息参数
     */
    protected int mScreenWidth = 0;
    
    protected int mScreenHeight = 0;
    
    protected float mScreenDensity = 0.0f;
    
    /**
     * 上下文全局变量
     */
    protected Context mContext = null;
    
    /**
     * 网络状态监听
     */
    protected NetChangeObserver mNetChangeObserver = null;
    
    /**
     * 等待框
     */
    private ShapeLoadingDialog shapeLoadingDialog = null;
    
    /**
     * 几种页面切换动画的枚举类
     */
    public enum TransitionMode
    {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if (toggleOverridePendingTransition())
        {
            switch (getOverridePendingTransitionMode())
            {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        // 如果有extras，则在getBundleExtras（）进行处理
        if (null != extras)
        {
            getBundleExtras(extras);
        }
        // 绑定eventbus与否
        if (isBindEventBusHere())
        {
            EventBus.getDefault().register(this);
        }
        
        // 专门为魅族手机准备的隐藏其smartbar的工具类
        SmartBarUtils.hide(getWindow().getDecorView());
        // 是否全屏应用
        setTranslucentStatus(isApplyStatusBarTranslucency());
        mContext = this;
        TAG_LOG = this.getClass().getSimpleName();
        // 页面堆栈管理
        BaseAppManager.getInstance().addActivity(this);
        // 初始化屏幕相关信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        // 给activity绑定布局文件
        if (getContentViewLayoutID() != 0)
        {
            setContentView(getContentViewLayoutID());
        }
        else
        {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        
        // 网络监听器
        mNetChangeObserver = new NetChangeObserver()
        {
            @Override
            public void onNetConnected(NetUtils.NetType type)
            {
                onNetworkConnected(type);
            }
            
            @Override
            public void onNetDisConnect()
            {
                onNetworkDisConnected();
            }
        };
        
        NetStateReceiver.registerObserver(mNetChangeObserver);
        initViewsAndEvents();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
    }
    
    @Override
    public void finish()
    {
        super.finish();
        BaseAppManager.getInstance().removeActivity(this);
        if (toggleOverridePendingTransition())
        {
            switch (getOverridePendingTransitionMode())
            {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ButterKnife.reset(this);
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
        if (isBindEventBusHere())
        {
            EventBus.getDefault().unregister(this);
        }
    }
    
    /**
     * 获取bundle闯过来的参数
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);
    
    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();
    
    /**
     * 事件过来的时候
     *
     * @param eventCenter
     */
    protected abstract void onEventComming(EventCenter eventCenter);
    
    /**
     * 初始化所有布局和event事件
     */
    protected abstract void initViewsAndEvents();
    
    /**
     * 网络连接连起来了
     */
    protected abstract void onNetworkConnected(NetUtils.NetType type);
    
    /**
     * 网络连接断开
     */
    protected abstract void onNetworkDisConnected();
    
    /**
     * 是否开启应用的全屏展示
     *
     * @return
     */
    protected abstract boolean isApplyStatusBarTranslucency();
    
    /**
     * 是否绑定eventbus
     *
     * @return
     */
    protected abstract boolean isBindEventBusHere();
    
    /**
     * 是否为页面切换增加动画
     *
     * @return
     */
    protected abstract boolean toggleOverridePendingTransition();
    
    /**
     * 获取到页面切换的动画为哪种
     */
    protected abstract TransitionMode getOverridePendingTransitionMode();
    
    /**
     * 跳转另一个活动
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz)
    {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
    
    /**
     * 跳转另一个活动并传递参数
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle)
    {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    
    /**
     * 跳转另一个活动并结束当前
     *
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz)
    {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }
    
    /**
     * 跳转另一个活动并结束，并传递参数
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle)
    {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }
    
    /**
     * 开始一个活动，并等待返回结果
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode)
    {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }
    
    /**
     * 开始一个活动，并等待返回结果，并传递参数
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle)
    {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    
    /**
     * 展示一个Snackbar toast提示
     *
     * @param msg
     */
    protected void showToast(String msg)
    {
        if (null != msg && !CommonUtils.isEmpty(msg))
        {
            Snackbar.make(getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 展示一个等待框
     */
    protected void showShapeLoadingDialog()
    {
        if (null == shapeLoadingDialog)
        {
            shapeLoadingDialog = new ShapeLoadingDialog(this);
        }
        shapeLoadingDialog.show();
    }
    
    /**
     * 成功类型的弹出框
     * 
     * @param title
     * @param content
     */
    protected void showSweetDialogSuccess(String title, String content)
    {
        SweetAlertDialog sd = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        sd.setTitleText(title);
        sd.setConfirmText("好的");
        sd.setContentText(content);
        // 可以按返回取消
        sd.setCancelable(true);
        // 可以点击外部取消
        sd.setCanceledOnTouchOutside(true);
        sd.show();
    }
    
    /**
     * 错误提示类型的弹出框
     * 
     * @param title
     * @param content
     */
    protected void showSweetDialogFail(String title, String content)
    {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText(title)
            .setContentText(content)
            .setConfirmText("好的")
            .show();
    }
    
    public void onEventMainThread(EventCenter eventCenter)
    {
        if (null != eventCenter)
        {
            onEventComming(eventCenter);
        }
    }
    
    /**
     * use SytemBarTintManager 使用rTintManager，使顶部的导航栏颜射发生改变
     * 
     * @param tintDrawable
     */
    protected void setSystemBarTintDrawable(Drawable tintDrawable)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            if (tintDrawable != null)
            {
                mTintManager.setStatusBarTintEnabled(true);
                mTintManager.setTintDrawable(tintDrawable);
            }
            else
            {
                mTintManager.setStatusBarTintEnabled(false);
                mTintManager.setTintDrawable(null);
            }
        }
        
    }
    
    /**
     * set status bar translucency 安卓4.4以上可以开启APP全屏模式 windowTranslucentStatus Flag indicating whether this window
     * requests a translucent status bar. 大意就是说状态栏是否半透明，如果是true的话，你会发现你的Toolbar陷入到状态栏里面了，
     * 所以为了预留空间，需要下面的属性：android:fitsSystemWindows
     * 
     * @param on
     */
    protected void setTranslucentStatus(boolean on)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on)
            {
                winParams.flags |= bits;
            }
            else
            {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }
}