import Api from "../Api";

export default class PayMaker {

    /**
     * 获取订单号  伪代码
     * @param productId 商品id
     * @param productName 商品名称
     * @param price 商品价格
     * @param timestamp 时间戳
     * @param notifyUrl 保留字段
     *  @param payType 支付类型
     */
    thirdPay(productId,productName,price,timestamp,notifyUrl,payType){
        let url = (global.billingUrl + Api.getOrder
            + "productId="+productId+"&productName="+productName+"&productName="+price+"&price="+price+"&timestamp="+timestamp+"&payType="+payType);
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

    /**
     * 生成时间戳 秒级
     * @returns {number}
     */
    getTimeStamp(){
        return Date.parse(new Date())/1000;
    }

    /**
     * 生成UUID
     * @returns {string}
     */
    getUUID() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x3|0x8)).toString(16);
        });
        return uuid;
    }

}