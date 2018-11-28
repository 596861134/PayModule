/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {DeviceEventEmitter, StyleSheet, Text, TouchableOpacity, View} from 'react-native';
import PayMaker from "./js/http/pay/PayMaker";
import NativeUtil from "./js/NativeUtil";
import JsonUtil from "./js/JsonUtil";


type Props = {};
export default class App extends Component<Props> {

    constructor(props) {
        super(props);
        this.payMaker = new PayMaker();
        this.state = {
            payType: 0,//支付类型
            trade_no:'',//发起支付的订单号
        };
    }

    componentWillMount() {
        //接收原生传过来的数据
        DeviceEventEmitter.addListener("PAY_RESULT", (msg) => {
            console.log("PAY_RESULT:" + msg);
            this.payResult(msg);
        });

    }


    render() {
        return (
            <View style={styles.container}>
                <TouchableOpacity
                    style={{margin: 10, width: 100, height: 40, backgroundColor: "#B3B3B3"}}
                    onPress={() => this.aliPay()}>
                    <View>
                        <Text>支付宝</Text>
                    </View>
                </TouchableOpacity>

                <TouchableOpacity
                    style={{margin: 10, width: 100, height: 40, backgroundColor: "#B3B3B3"}}
                    onPress={() => this.weixinPay()}>
                    <View>
                        <Text>微信</Text>
                    </View>
                </TouchableOpacity>

            </View>
        );
    }


    /**
     * 支付宝
     */
    aliPay() {
        this.setState({
            payType:0,
        });
        // 生成订单
        // this.payMaker.thirdPay("productId","productName","price","timestamp","notifyUrl","payType")
        //     .then(result => {
        //         console.log('pay:' + result);
        //         let data = JsonUtil.strToJson(result);
        //         if (data.code === 0) {
        //             this.callPay(data);
        //         } else {
        //             console.log(data.code + "," + result);
        //             // this.showToast(data.msg);
        //         }
        //
        //     })
        //     .catch(error => {
        //         this.showToast(error);
        //         console.log(error);
        //     });

        NativeUtil.aliPay("234243424232324");//测试

    }

    /**
     * 微信支付
     */
    weixinPay() {
        this.setState({
            payType:1,
        })
        // 生成订单
        this.payMaker.thirdPay("productId","productName","price","timestamp","notifyUrl","payType")
            .then(result => {
                console.log('pay:' + result);
                let data = JsonUtil.strToJson(result);
                if (data.code === 0) {
                    this.callPay(data);
                } else {
                    console.log(data.code + "," + result);
                    // this.showToast(data.msg);
                }

            })
            .catch(error => {
                this.showToast(error);
                console.log(error);
            });
    }

    /**
     * 发起支付
     * @param payInfo
     */
    callPay(payInfo){
        console.log('callPay:' + JSON.stringify(payInfo));
        this.setState({
            trade_no:payInfo.data.out_trade_no,
        });

        if (this.state.payType === 0) {
            //支付宝
            // NativeUtil.aliPay(payInfo.data.pay_trade_no);
            NativeUtil.aliPay("234243424232324");
        } else if (this.state.payType === 1) {
            //微信
            NativeUtil.weixinPay(payInfo.data.pay_trade_no, payInfo.data);
        }

    }


    /**
     * 处理返回的支付结果
     * @param msg
     */
    payResult(msg) {
        console.log('payResult:' + msg);
        let result = {};
        if (msg === '9000') {
            result.code = 0;
            result.data = "支付成功";
        } else {
            if (msg === '-1111') {
                result.code = -3;
                result.data = "请先安装微信";
            } else {
                if (msg === '8000') {
                    result.code = -1;
                    result.data = "请先安装微信";
                } else {
                    result.code = -2;
                    result.data = "你已取消了本次订单的支付";
                }
            }
        }
        // this.handleResult(result);//根据不同的结果跳转到对应的页面
    }


}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});
