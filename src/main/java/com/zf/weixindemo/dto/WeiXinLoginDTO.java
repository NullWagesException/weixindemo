package com.zf.weixindemo.dto;

import lombok.Data;

/**
 * @Author: nullWagesException
 * @Date: 2019/11/5 15:19
 * @Description:
 */
@Data
public class WeiXinLoginDTO {

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 会话密钥
     */
    private String sessionKey;

    /**
     * 用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回
     */
    private String unionid;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 手机号码
     */
    private String phone;

    private String name;

}
