package com.paymodule;

import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.paymodule.alipay.AliPay;
import com.paymodule.alipay.PayResult;
import com.paymodule.alipay.PayResultCallBack;
import com.paymodule.weixin.ConnectCallBack;
import com.paymodule.weixin.WeixinPay;
import com.paymodule.MainApplication;

import java.io.UnsupportedEncodingException;

public class RNSDK {

    public static ReactContext rnContext;
    private static volatile RNSDK mInstance;

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

    public void sendRN(String event, String data) {
        rnContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(event, data);//原生调Rn
    }

    public void aliPay(String payTradeNo) {
        Activity mActivity = MainApplication.getInstance().getCurrentActivity();
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
        Activity mActivity = MainApplication.getInstance().getCurrentActivity();
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

    public void onPayResult(Intent data){
        Log.e("mtsdk", "payResultIntent:" + data);
        if (data == null) {
            return;
        }
        // 支付部分
        String flag = data.getExtras().getString("flag");
        if (flag != null && flag.equals("WEIXIN_PAY_RESULT")) {
            //微信统计

        } else {
            //银联统计
        }
        String str = data.getExtras().getString("pay_result");
        Log.e("mtsdk", "payResult:" + str);
        if (str.equalsIgnoreCase("success")) {
            Log.e("mtsdk", "payResultsuccess:" + str);
            RNSDK.getInstance().weiyinPay("9000");
        } else if (str.equalsIgnoreCase("fail")) {
            Log.e("mtsdk", "payResultfail:" + str);
            RNSDK.getInstance().weiyinPay("8000");
        } else if (str.equalsIgnoreCase("cancel")) {
            Log.e("mtsdk", "payResultcancel:" + str);
            RNSDK.getInstance().weiyinPay("6000");
        }
    }

    /**
     * 回调RN微信支付结果
     *
     * @param info
     */
    public void weiyinPay(String info) {
        Log.e("weixinPayinfo", info);
        if (rnContext != null) {
            Log.e("info", info);
            if (rnContext != null) {
                sendRN("PAY_RESULT", info);
            }
        }
    }



}
