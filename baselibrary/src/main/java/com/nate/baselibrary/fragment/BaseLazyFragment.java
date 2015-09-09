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
package com.nate.baselibrary.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nate.baselibrary.loading.ShapeLoadingDialog;
import com.nate.baselibrary.utils.CommonUtils;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

/**
 * 基本的Fragment
 */
public abstract class BaseLazyFragment extends Fragment
{
    
    /**
     * Log tag
     */
    protected static String TAG_LOG = null;
    
    /**
     * 屏幕信息
     */
    protected int mScreenWidth = 0;
    
    protected int mScreenHeight = 0;
    
    protected float mScreenDensity = 0.0f;
    
    /**
     * 上下文
     */
    protected Context mContext = null;
    
    private boolean isFirstResume = true;
    
    private boolean isFirstVisible = true;
    
    private boolean isFirstInvisible = true;
    
    private boolean isPrepared;
    
    private ShapeLoadingDialog shapeLoadingDialog = null;
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mContext = activity;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TAG_LOG = this.getClass().getSimpleName();
        if (isBindEventBusHere())
        {
            EventBus.getDefault().register(this);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (getContentViewLayoutID() != 0)
        {
            return inflater.inflate(getContentViewLayoutID(), null);
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        
        // 初始化屏幕相关数据
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        initViewsAndEvents();
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (isBindEventBusHere())
        {
            EventBus.getDefault().unregister(this);
        }
    }
    
    @Override
    public void onDetach()
    {
        super.onDetach();
        // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
        try
        {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        if (isFirstResume)
        {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint())
        {
            onUserVisible();
        }
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        if (getUserVisibleHint())
        {
            onUserInvisible();
        }
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if (isFirstVisible)
            {
                isFirstVisible = false;
                initPrepare();
            }
            else
            {
                onUserVisible();
            }
        }
        else
        {
            if (isFirstInvisible)
            {
                isFirstInvisible = false;
                onFirstUserInvisible();
            }
            else
            {
                onUserInvisible();
            }
        }
    }
    
    private synchronized void initPrepare()
    {
        if (isPrepared)
        {
            onFirstUserVisible();
        }
        else
        {
            isPrepared = true;
        }
    }
    
    /**
     * when fragment is visible for the first time, here we can do some initialized work or refresh data only once
     */
    protected abstract void onFirstUserVisible();
    
    /**
     * this method like the fragment's lifecycle method onResume()
     */
    protected abstract void onUserVisible();
    
    /**
     * when fragment is invisible for the first time
     */
    private void onFirstUserInvisible()
    {
        // here we do not recommend do something
    }
    
    /**
     * this method like the fragment's lifecycle method onPause()
     */
    protected abstract void onUserInvisible();
    
    /**
     * init all views and add events
     */
    protected abstract void initViewsAndEvents();
    
    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();
    
    /**
     * is bind eventBus
     *
     * @return
     */
    protected abstract boolean isBindEventBusHere();
    
    /**
     * get the support fragment manager
     *
     * @return
     */
    protected FragmentManager getSupportFragmentManager()
    {
        return getActivity().getSupportFragmentManager();
    }
    
    /**
     * startActivity
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz)
    {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }
    
    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle)
    {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    
    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode)
    {
        Intent intent = new Intent(getActivity(), clazz);
        startActivityForResult(intent, requestCode);
    }
    
    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle)
    {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    
    /**
     * show toast
     *
     * @param msg
     */
    protected void showToast(String msg)
    {
        if (null != msg && !CommonUtils.isEmpty(msg))
        {
            Snackbar.make(((Activity)mContext).getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 展示一个等待框
     */
    protected void showShapeLoadingDialog()
    {
        if (null == shapeLoadingDialog)
        {
            shapeLoadingDialog = new ShapeLoadingDialog(mContext);
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
        SweetAlertDialog sd = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
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
        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE).setTitleText(title)
            .setContentText(content)
            .setConfirmText("好的")
            .show();
    }
    
}
