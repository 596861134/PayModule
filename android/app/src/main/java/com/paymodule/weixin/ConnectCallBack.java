package com.paymodule.weixin;

public interface ConnectCallBack {
    public void onResponse(String response);

    public void onNetError();
}
