package com.nate.baselibrary.netstatus;

/**
 * 网络状况改变回调接口
 */
public interface NetChangeObserver
{
    
    /**
     * 当网络连接起来之后的回调接口
     */
    void onNetConnected(NetUtils.NetType type);
    
    /**
     * 当网络断开的时候的回调接口
     */
    void onNetDisConnect();
}
