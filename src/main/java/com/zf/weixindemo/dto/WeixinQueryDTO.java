package com.zf.weixindemo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: nullWagesException
 * @Date: 2019/9/5 9:16
 * @Description: 查询返回的数据
 */
@Data
public class WeixinQueryDTO {

    /**
     * 交易状态描述
     */
    private String tradeStateDesc;

    /**
     * 订单支付时间，格式为yyyyMMddHHmmss
     */
    private String timeEnd;

    /**
     * 标价金额
     */
    private BigDecimal totalFee;

    /**
     * 货币类型
     */
    private String cashFeeType;

    /**
     * 订单编号
     */
    private String outTradeNo;

}
