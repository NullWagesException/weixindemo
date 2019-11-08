package com.zf.weixindemo.dto;

import lombok.Data;

/**
 * @Author: nullWagesException
 * @Date: 2019/9/4 17:19
 * @Description: 统一下单返回的数据
 */
@Data
public class WeixinOrderDTO {

    /**
     * 订单号
     */
    private String orderNum;

    /**
     * 二维码地址
     */
    private String QRCodePath;

}
