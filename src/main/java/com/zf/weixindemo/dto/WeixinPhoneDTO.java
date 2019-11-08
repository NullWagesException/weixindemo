package com.zf.weixindemo.dto;

import lombok.Data;

/**
 * @Author: nullWagesException
 * @Date: 2019/11/5 15:48
 * @Description:
 */
@Data
public class WeixinPhoneDTO {

    /**
     * 用户绑定的手机号（国外手机号会有区号）
     */
    private String phoneNumber;

    /**
     * 没有区号的手机号
     */
    private String purePhoneNumber;

    /**
     * 区号
     */
    private String countryCode;

}
