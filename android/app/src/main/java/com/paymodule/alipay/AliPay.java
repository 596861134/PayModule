package com.paymodule.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;

public class AliPay {

    private PayResultHandler handler;

    public AliPay(final Activity activity, final String payInfo, final PayResultCallBack callBack){
        handler = new PayResultHandler(callBack);
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                System.out.println(payInfo);
                String result = alipay.pay(payInfo,true);

                Message msg = new Message();
                msg.what = PayResultCallBack.SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}

class PayResultHandler extends Handler {

    private PayResultCallBack callBack;
    PayResultHandler(PayResultCallBack callBack){
        this.callBack = callBack;
    }
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case PayResultCallBack.SDK_PAY_FLAG: {
                PayResult payResult = new PayResult((String) msg.obj);
                callBack.onPayResult(payResult, PayResultCallBack.SDK_PAY_FLAG);
                break;
            }
            case PayResultCallBack.SDK_CHECK_FLAG: {
                callBack.onPayResult(null, PayResultCallBack.SDK_CHECK_FLAG);
                break;
            }
            default:
                break;
        }
    }
}
