package com.paymodule.alipay;

public interface PayResultCallBack {
    int SDK_PAY_FLAG = 1;
    int SDK_CHECK_FLAG = 2;

    void onPayResult(PayResult result, int payFlag);
}

