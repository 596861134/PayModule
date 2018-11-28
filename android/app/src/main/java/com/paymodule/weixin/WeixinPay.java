package com.paymodule.weixin;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class WeixinPay {

    public static final String wxAppId = "wxdc83676986cb8176";
    public static final String wxPayAppKey = "7bbe0b18bcfc5722c9f5d69817181d5c";
    public static final String wxPayPartnerId = "1447554802";

    private IWXAPI api;
    private String PAY_RESPONSE = "{\"code\":$$$1,\"msg\":\"$$$2\",\"data\":{\"result\":\"success\"}}";

    public WeixinPay(Activity activity){
        api = WXAPIFactory.createWXAPI(activity.getApplication().getApplicationContext(), null);
        api.registerApp(wxAppId);
    }

    public void weixinPay(String payTradeNo, String payInfo, ConnectCallBack callback) {
        Log.e("weixinPay",payTradeNo+":"+payInfo);
        String response = PAY_RESPONSE;
        try {
            JSONObject extraData = new JSONObject(payInfo);
            String wxPackageValue = "Sign=WXPay";
            PayReq request = new PayReq();
            if (extraData.has("wx_appid") && extraData.has("wx_sign")
                    && extraData.has("wx_mchid") && extraData.has("wx_rand")
                    && extraData.has("wx_ts")) {
                try {
                    String respWxAppId = extraData.getString("wx_appid");
                    String respWxPartnerId = extraData.getString("wx_mchid");
                    String respWxNonce = extraData.getString("wx_rand");
                    String respWxTimeStamp = extraData.getString("wx_ts");
                    String respWxSign = extraData.getString("wx_sign");

                    if (!TextUtils.isEmpty(respWxAppId) &&
                            !TextUtils.isEmpty(respWxPartnerId) &&
                            !TextUtils.isEmpty(respWxNonce) &&
                            !TextUtils.isEmpty(respWxTimeStamp) &&
                            !TextUtils.isEmpty(respWxSign)) {
                        // 使用服务器返回参数
                        request.appId = respWxAppId;
                        request.partnerId = respWxPartnerId;
                        request.prepayId = payTradeNo;
                        request.packageValue = wxPackageValue;
                        request.nonceStr = respWxNonce;
                        request.timeStamp = respWxTimeStamp;
                        request.sign = respWxSign;
                    } else {
                        // 使用本地参数
                        if (!useLocalParamsCreateWxReq(request, payTradeNo, wxPackageValue,callback)) {
                            return;
                        }
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                // 使用本地参数
                if (!useLocalParamsCreateWxReq(request, payTradeNo, wxPackageValue,callback)) {
                    return;
                }
            }

            if (!api.sendReq(request)) {
                Log.e("weixinPay","-1");
                response = response.replace("$$$1","-1");
                //判断微信是否安装了
                boolean wxInstalled = api.isWXAppInstalled();
                if (!wxInstalled) {
                    Log.e("weixinPay","请先安装微信");
                    response = response.replace("$$$2","请先安装微信");
                } else {
                    Log.e("weixinPay","签名错误");
                    response = response.replace("$$$2","签名错误");
                }
                //微信支付校验不通过时需要通知
                callback.onResponse(response);
            }else{
                //成功时不时在这里通知
                Log.e("weixinPay","0");
                response = response.replace("$$$1","0");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("weixinPay","系统异常");
            response = response.replace("$$$1","-1");
            response = response.replace("$$$2","系统异常");
            //微信支付校验不通过时需要通知
            callback.onResponse(response);
        }
        Log.e("weixinPay",response);
        //微信支付不会立刻返回结果
        //        callback.onResponse(response);
    }

    private boolean useLocalParamsCreateWxReq(PayReq request, String payTradeNo, String wxPackageValue, ConnectCallBack callback) {
        String response = PAY_RESPONSE;
        if (TextUtils.isEmpty(wxAppId)) {
            response = response.replace("$$$1","-1");
            response = response.replace("$$$2","微信支付未设置app_id");
            callback.onResponse(response);
            return false;
        }
        if (wxPayAppKey == null || wxPayAppKey.equals("")) {
            response = response.replace("$$$1","-1");
            response = response.replace("$$$2","微信支付未设置app_key");
            callback.onResponse(response);
            return false;
        }

        Random random = new Random();
        String nonceStr = MD5(String.valueOf(random.nextInt(10000)));
        request.appId = wxAppId;
        request.partnerId = wxPayPartnerId;
        request.prepayId = payTradeNo;
        request.packageValue = wxPackageValue;
        request.nonceStr = nonceStr;
        request.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("appid", request.appId);
        params.put("noncestr", request.nonceStr);
        params.put("package", request.packageValue);
        params.put("partnerid", request.partnerId);
        params.put("prepayid", request.prepayId);
        params.put("timestamp", request.timeStamp);
        request.sign = genAppSign(params);
        return true;
    }

    private String genAppSign(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();

        if (params != null) {
            if (!params.isEmpty()) {
                for (String key : params.keySet()) {
                    sb.append(key);
                    sb.append('=');
                    sb.append(params.get(key));
                    sb.append('&');
                }
            }
        }

        sb.append("key=");
        sb.append(wxPayAppKey);
        String appSign = MD5(sb.toString()).toUpperCase();
        Log.d("mtsdk","weixinPay,orion->" + sb.toString());
        return appSign;
    }

    public String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
