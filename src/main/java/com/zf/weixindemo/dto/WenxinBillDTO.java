package com.zf.weixindemo.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: nullWagesException
 * @Date: 2019/9/5 21:14
 * @Description: 当日成功支付的订单DTO
 */
@Data
public class WenxinBillDTO {

    //交易时间,
    private String transDate;
    // 公众账号ID,
    private String subscriptionNum;
    // 商户号,
    private String mchID;
    // 设备号,
    private String equipmentNum;
    // 微信订单号,
    private String transactionId;
    // 商户订单号,
    private String outTradeNo;
    // 用户标识,
    private String userNum;
    // 交易类型,
    private String orderType;
    // 交易状态,
    private String tradeStateDesc;
    // 付款银行,
    private String payBank;
    // 货币种类,
    private String cashFeeType;
    // 总金额,
    private String totalFee;
    // 商品名称,
    private String commodityName;
    // 手续费,
    private String serviceCharge;
    // 费率
    private String Rate;

}
