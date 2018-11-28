import NativeInteraction from './NativeInteraction';


export default class NativeUtil {

    /**
     * 支付宝
     * @param obj
     */
    static aliPay(obj){
        NativeInteraction.aliPay(obj);
    }

    /**
     * 微信
     * @param pay_trade_no
     * @param obj
     */
    static weixinPay(pay_trade_no,obj){
        NativeInteraction.weixinPay(pay_trade_no,JSON.stringify(obj));
    }

}
