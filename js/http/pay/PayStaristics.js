import {addTask, createNetTask} from '../../util/task/model/NetTask';
import SingMaker from "../SingMaker";
import NativeUtil from "../../NativeUtil";
import Api from "../Api";

export default class PayStaristics {

    //常规支付流程
    static sdk_client_user_pay = "sdk_client_user_pay";//用户支付中
    static sdk_client_user_pay_success = "sdk_client_user_pay_success";//用户支付成功
    static sdk_client_user_pay_cancel = "sdk_client_user_pay_cancel";//用户支付取消
    static sdk_client_user_pay_fail = "sdk_client_user_pay_fail";//用户支付失败

    //盛付通支付流程
    static sdk_client_user_shengpay = "sdk_client_user_shengpay";//用户支付中
    static sdk_client_user_shengpay_success = "sdk_client_user_shengpay_success";//用户支付成功
    static sdk_client_user_shengpay_cancel = "sdk_client_user_shengpay_cancel";//用户支付取消
    static sdk_client_user_shengpay_fail = "sdk_client_user_shengpay_fail";//用户支付失败

    /**
     * 支付数据
     * @param orderId
     * @param status_info
     * @param status
     */
    static createPayEventInfo(orderId, status_info, status) {
        let PayEventInfo = {};
        PayEventInfo.order_id = orderId;
        PayEventInfo.status = status;
        if (status_info && status_info!=="") {
            PayEventInfo.status_info = status_info;
        }
        PayEventInfo.event_time = (new Date()).getTime();
        PayEventInfo.ip = global.IP;
        this.onPayEvent(PayEventInfo);
    }

    /**
     * 支付事件统计
     * @param data
     */
    static onPayEvent(data){
        // let signedData = SingMaker.getSign(data, 2);
        //拼接成map
        // var map = new Map();
        // var strs = signedData.split("&"); //字符分割
        // for (var i = 0; i < strs.length; i++) {
        //     var str = strs[i];
        //     map.set(str.split("=")[0], (str.split("=").length > 1 ? str.split("=")[1] : ""));
        // }

        let url = global.billingUrl + Api.statistics + SingMaker.getStatisSign(data, 2);
        console.log("url:"+url);
        addTask(createNetTask(this.getDateId(), url, "get", new Date(), "", ""));
    }

    /**
     * 支付埋点
     * @param account
     * @param orderId
     * @param amount
     * @param currencyType
     * @param payType
     */
    static onPay(account, orderId, amount, currencyType, payType){
        NativeUtil.onPay(account, orderId,amount, currencyType, payType);
    }

    /**
     * 获取当前的日期时间 格式“yyyy-MM-dd HH:MM:SS”
     * @returns {string}
     */
    static getNowFormatDate() {
        var date = new Date();
        var seperator1 = "-";
        var seperator2 = ":";
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        return date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();
    }

    /**
     * 生成时间ID
     * @returns {number}
     */
    static getDateId() {
        var date = new Date();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        return date.getFullYear() + month +  strDate +  date.getHours() + date.getMinutes() + date.getSeconds() + date.getMilliseconds();
    }
}