package com.zf.weixindemo.controller;

import com.zf.weixindemo.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @Author: nullWagesException
 * @Date: 2019/9/2 16:06
 * @Description:
 */
@RestController
public class NotifyController {

    private static Logger log = LoggerFactory.getLogger(NotifyController.class);
    
    @RequestMapping(value = "payNotifyUrl")
    @ResponseBody
    public String payNotifyUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("微信支付回调");
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        String resultxml = new String(outSteam.toByteArray(), "utf-8");
        Map<String, String> params = PayCommonUtil.doXMLParse(resultxml);
        outSteam.close();
        inStream.close();
        String total_fee = params.get("total_fee");
        double v = Double.valueOf(total_fee) / 100;
        //订单号
        String outTradeNo = String.valueOf(Long.parseLong(params.get("out_trade_no").split("O")[0]));
        Date accountTime = DateUtil.stringtoDate(params.get("time_end"), "yyyyMMddHHmmss");
        String ordertime = DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        //金额
        String totalAmount = String.valueOf(v);
        //微信订单号
        String tradeNo = params.get("transaction_id");
        log.info("付款金额为：" + totalAmount);
        log.info("订单号为：" + outTradeNo);

        Map<String,String> return_data = new HashMap<String,String>();
        if (!PayCommonUtil.isTenpaySign(params)) {
            // 支付失败
            //通知页面订单支付失败
            PayWebSocket.sendMessage(outTradeNo, WeChatConfig.FAIL);
            return_data.put("return_code", "FAIL");
            return_data.put("return_msg", "return_code不正确");
            return StringUtil.GetMapToXML(return_data);
        } else {
            log.info("===============付款成功==============");
            // ------------------------------
            // 先查询订单状态是否完成

            // ------------------------------
            // 处理业务开始
            // ------------------------------
            // 此处处理订单状态，首先对订单状态进行查询以免产生重复通知造成资金损失
            // 结合自己的订单数据完成订单状态的更新
            // ------------------------------
            log.info("开始处理业务...");

            //通知页面订单支付完成
            PayWebSocket.sendMessage(outTradeNo, WeChatConfig.SUCCESS);
            return_data.put("return_code", "SUCCESS");
            return_data.put("return_msg", "OK");
            return StringUtil.GetMapToXML(return_data);
        }
    }


}
