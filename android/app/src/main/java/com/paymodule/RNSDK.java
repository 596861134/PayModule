package com.paymodule;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.paymodule.alipay.AliPay;
import com.paymodule.alipay.PayResult;
import com.paymodule.alipay.PayResultCallBack;
import com.paymodule.weixin.ConnectCallBack;
import com.paymodule.weixin.WeixinPay;

import java.io.UnsupportedEncodingException;

public class RNSDK {

    public static ReactContext rnContext;
    private static volatile RNSDK mInstance;
    private Activity mActivity;

    public static RNSDK getInstance() {
        if (null == mInstance) {
            synchronized (RNSDK.class) {
                if (null == mInstance) {
                    mInstance = new RNSDK();
                }
            }
        }
        return mInstance;
    }

    public void init(Activity activity) {
        Log.e("czf", "初始化");
        mActivity = activity;
    }

    public void sendRN(String event, String data) {
        rnContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(event, data);//原生调Rn
    }

    public void aliPay(String payTradeNo) {
        if (mActivity != null) {
            byte[] value = Base64.decode(payTradeNo, Base64.DEFAULT);
            String payInfo = null;
            try {
                payInfo = new String(value, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            new AliPay(mActivity, payInfo, new PayResultCallBack() {
                @Override
                public void onPayResult(PayResult result, int payFlag) {
                    try {
                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                        String resultStatus = result.getResultStatus();
                        Log.e("czf", resultStatus);
                        if (rnContext != null) {
                            sendRN("PAY_RESULT", resultStatus);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void weixinPay(String payTradeNo, String payInfo) {
        if (mActivity != null) {
            WeixinPay weixinPay = new WeixinPay(mActivity);
            weixinPay.weixinPay(payTradeNo, payInfo, new ConnectCallBack() {
                @Override
                public void onResponse(String response) {
                    Log.e("czf", "onResponse:" + response);
                    //这个回调是假的，真正的回调在PayActivity的onActivityResult方法中
                    sendRN("PAY_RESULT", "-1111");
                }

                @Override
                public void onNetError() {

                }
            });
        }
    }

}
