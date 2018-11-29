package com.paymodule;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class NativeInteraction extends ReactContextBaseJavaModule {


    public NativeInteraction(ReactApplicationContext reactContext) {
        super(reactContext);
        RNSDK.rnContext = reactContext;
    }

    @Override
    public String getName() {
        return "NativeInteraction";
    }

    @Override
    public boolean canOverrideExistingModule() {
        //这里需要返回true
        return true;
    }

    /**
     * 该方法就是给js使用
     * Java方法需要使用注解@ReactMethod。
     * 方法的返回类型必须为void。
     * React Native的跨语言访问是异步进行的，所以想要给JavaScript返回一个值的唯一办法是使用回调函数或者发送事件
     * */
    @ReactMethod
    public void show(String mag){

    }

    @ReactMethod
    public void aliPay(String payTradeNo){
        Log.e("czf","被调用"+payTradeNo);
        RNSDK.getInstance().aliPay(payTradeNo);
    }

    @ReactMethod
    public void weixinPay(String payTradeNo,String payInfo){
        Log.e("czf","被调用"+payInfo);
        RNSDK.getInstance().weixinPay(payTradeNo,payInfo);
    }

}
