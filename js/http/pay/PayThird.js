import Api from "../Api";
import HttpUtils from "../HttpUtils";
import SignMaker from "../SingMaker";

var encodeExtra = '';
export default class PayThird {

    thirdPayImpl(serverName,productId,productName,price,
                        tradeNo,notifyUrl,para,payItem,
                        finalAmount,cid,gameRegionId,gameRoleId){
        console.log('thirdPayImpl:');
        encodeExtra='';
        var extra = "";
        var pName = "";
        if (productName===null || productName===''){
            pName = finalAmount + '点点券';
        } else {
            pName = productName;
        }
        if (payItem) {
            extra = this.payByCoupon(serverName,productId,productName,price,tradeNo,notifyUrl,para,false);
        }
        let data = {};
        data.lk = global.LK;
        data.method = 'get';
        data.rid = global.UUID;
        data.cid = cid;
        data.tid = 1;
        data.pid = -1;
        data.pname = pName;
        data.eid = 1;
        data.amount = finalAmount/10;
        data.ramount = finalAmount/10;
        data.ip = global.IP;
        data.fromsite = global.APPID;
        data.extend_p = extra;
        data.buyaccount = '1';
        data.serverid = serverName;
        data.regionid = gameRegionId;
        data.roleid = gameRoleId;
        data.loan_appid = global.loan_appid;
        data.loan_bundle_id = global.loans_api_key;
        data.sheng_request_from = 'ANDROID_APP';
        data.sheng_bundle_id = global.PACKAGE_NAME;

        let url = (global.billingUrl + Api.CHARGE_RECHARGE + SingMaker.getSign(data,2)).replace(extra,encodeExtra);
        console.log('url:'+url);
        return new Promise((resolve, reject) => {
            HttpUtils.get(url)
                .then(result => {
                    if (!result) {
                        reject(new Error('responseData is null'));
                        return;
                    }
                    console.log('thirdPayImpl:'+result);
                    resolve(result);
                })
                .catch(err => {
                    reject(err);
                }).done();
        });

    }

    payByCoupon(serverName, productId, productName, price, tradeNo, notifyUrl, para, construction){
        var url ='';
        var obj = {};
        obj.lk = global.IP;
        obj.rid = global.UUID;
        obj.appid = global.APPID ;
        obj.channel_name = global.CHANNEL_NAME;
        obj.region = serverName;
        obj.product_id = productId;
        obj.product_name = productName;
        obj.count = price;
        obj.out_trade_no = tradeNo;
        obj.notify_url = notifyUrl;
        obj.para = para;
        obj.ip = global.IP;
        if (construction) {
            url = SignMaker.getSign(obj, 2);
        } else {
            url = "function_name=internalchargenotify&" + SignMaker.getSign(obj, 2);
            // 坑，extra这里加密的时候不要encode，否则直接用encode后的extra拼接sign签名会报错，应该加密后拼接url时候用encode过的extra替换原extra
            encodeExtra = encodeURI(url);
        }
        console.log('payByCoupon:'+url);
        return url;

    }
}