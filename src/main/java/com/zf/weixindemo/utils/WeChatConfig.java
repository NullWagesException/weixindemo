package com.zf.weixindemo.utils;

import org.jdom2.JDOMException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public interface WeChatConfig {
    /**
     * APPID
     */
    String APP_ID = "";


    /**
     * 商户ID
     */
    String MCH_ID = "";

    /**
     * 密钥
     */
    String KEY = "";

    /**
     * 交易类型
     */
    String TRADE_TYPE = "NATIVE";

    /**
     * 收取支付结果的地址
     */
    String NOTIFY_URL = "";

    /**
     * 统一下单
     */
    String UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 查询订单
     */
    String QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 关闭订单
     */
    String CLOSEURL = "https://api.mch.weixin.qq.com/pay/closeorder";

    /**
     * 申请退款
     */
    String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * 下载对账单
     */
    String DOWNLOADBILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";

    /**
     * 返回状态
     */
    String SUCCESS = "SUCCESS";

    /**
     * 返回状态
     */
    String FAIL = "FAIL";


}
