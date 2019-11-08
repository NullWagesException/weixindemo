package com.zf.weixindemo.controller;

import com.zf.weixindemo.dto.ResultMessageDTO;
import com.zf.weixindemo.dto.WeixinOrderDTO;
import com.zf.weixindemo.dto.WeixinQueryDTO;
import com.zf.weixindemo.dto.WenxinBillDTO;
import com.zf.weixindemo.utils.PayCommonUtil;
import com.zf.weixindemo.utils.QRCodeUtil;
import com.zf.weixindemo.utils.WeChatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Author: nullWagesException
 * @Date: 2019/9/1 15:38
 * @Description: 微信支付业务
 */
@RestController
@RequestMapping("/weixinPay")
public class OrderController {

    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    /**
     * 创建订单
     * @param productId 商品id
     */
    @PostMapping("/creatOrder")
    public ResultMessageDTO creatOrder(Long productId, HttpServletRequest request) throws Exception {

        //根据商品id查询商品详细信息(假数据)
        //(0.01元)
        double price = 0.01;
        String productName = "VIP";
        //生成订单编号 随机数
        int number = (int)((Math.random()*9)*1000);
        //时间
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String orderNumber = dateFormat.format(new Date()) + number;
        Map<String, String> payMap = PayCommonUtil.weixinPrePay(orderNumber, new BigDecimal(price), productName, request, WeChatConfig.APP_ID,WeChatConfig.TRADE_TYPE);
        //微信预支付单成功创建
        WeixinOrderDTO result = new WeixinOrderDTO();
        if(WeChatConfig.SUCCESS.equals(payMap.get("return_code")) && WeChatConfig.SUCCESS.equals(payMap.get("result_code"))){
            //使用二维码生成工具，把微信返回的codeUrl转为二维码图片，保存到磁盘
            String codeUrl = payMap.get("code_url");
            //使用订单号来作为二维码的图片名称
            //TODO 测试目录
            File file = new File("/var/www/html",orderNumber+".jpg");
            QRCodeUtil.createImage(codeUrl,new FileOutputStream(file));
            result.setOrderNum(orderNumber);
            result.setQRCodePath("http://shiptux.cn/" + orderNumber+".jpg");
        }else{
            //给前端返回错误码和描述
            return new ResultMessageDTO(Integer.valueOf(payMap.get("err_code")), payMap.get("err_code_des"),result);
        }
        //给前端返回订单号以及二维码地址
        return new ResultMessageDTO(200, "OK",result);
    }

    /**
     * 订单查询
     * @param orderNumber 订单编号
     */
    @PostMapping("/refundOrder")
    public ResultMessageDTO refundOrder(String orderNumber,HttpServletRequest request) throws Exception {

        //根据订单编号查询
        Map<String, String> payMap = PayCommonUtil.Order(orderNumber, WeChatConfig.QUERY_URL);
        WeixinQueryDTO result = new WeixinQueryDTO();
        if(WeChatConfig.SUCCESS.equals(payMap.get("return_code")) && WeChatConfig.SUCCESS.equals(payMap.get("result_code"))){
            result.setCashFeeType(payMap.get("cash_fee_type"));
            result.setOutTradeNo(payMap.get("out_trade_no"));
            result.setTimeEnd(payMap.get("time_end"));
            result.setTotalFee(new BigDecimal(payMap.get("cash_fee")).divide(new BigDecimal(100)));
            result.setTradeStateDesc(payMap.get("trade_state_desc"));
        }else{
            //给前端返回错误码和描述
            return new ResultMessageDTO(Integer.valueOf(payMap.get("err_code")), payMap.get("err_code_des"),result);
        }
        //给前端返回订单号以及二维码地址
        return new ResultMessageDTO(200, "OK",result);
    }


    /**
     * 下载对账单
     * @param billDate 查询日期
     */
    @PostMapping("/downloadBill")
    public ResultMessageDTO downloadBill(String billDate,HttpServletRequest request) throws Exception {

        //根据日期查询
        Map<String, Object> payMap = PayCommonUtil.downloadBill(billDate);
        WenxinBillDTO result = new WenxinBillDTO();
        if (payMap.get("return_code")!=null) {
            if (WeChatConfig.SUCCESS.equals(payMap.get("return_code")) && WeChatConfig.SUCCESS.equals(payMap.get("result_code"))) {
                for (Map.Entry<String, Object> entry : payMap.entrySet()) {
                    System.out.println("key:" + entry.getKey() + "");
                }
            } else {
                //给前端返回错误码和描述
                return new ResultMessageDTO(Integer.valueOf(payMap.get("error_code") + ""), payMap.get("return_msg") + "", result);
            }
        }else{
            List<WenxinBillDTO> list = new ArrayList<>();
            for (Map.Entry<String, Object> entry : payMap.entrySet()) {
                list.add((WenxinBillDTO) entry.getValue());
            }
            return new ResultMessageDTO(200, "OK",list);
        }
        return null;
    }

    /***
     * APP创建订单
     * @param productId 商品id
     */
    @GetMapping("/creatOrderByApp/{productId}")
    public ResultMessageDTO creatOrderByApp(@PathVariable Long productId, HttpServletRequest request) throws Exception {

        //----需要根据订单编号查询真实数据

        //订单号
        String orderNumber = "";
        //----创建订单

        Map<String, String> payMap = PayCommonUtil.weixinPrePay(orderNumber, new BigDecimal(1), "商品名称", request,WeChatConfig.APP_ID,WeChatConfig.TRADE_TYPE);
        //微信预支付单成功创建
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        if(WeChatConfig.SUCCESS.equals(payMap.get("return_code")) && WeChatConfig.SUCCESS.equals(payMap.get("result_code"))){
            /*
               将APP需要的数据封装并重新签名
               参与签名的字段包括
               appid，partnerid，prepayid，noncestr，timestamp，package
             */
            parameterMap.put("appid",WeChatConfig.APP_ID);
            parameterMap.put("partnerid",WeChatConfig.MCH_ID);
            parameterMap.put("prepayid",payMap.get("prepay_id"));
            parameterMap.put("noncestr",payMap.get("nonce_str"));
            parameterMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
            parameterMap.put("package","Sign=WXPay");
            //获取签名
            String sign = PayCommonUtil.createSign("UTF-8", parameterMap);
            parameterMap.put("sign", sign);
            parameterMap.put("orderNumber",orderNumber);
        }else{
            //给app返回错误码和描述
            return new ResultMessageDTO(Integer.valueOf(payMap.get("err_code")), payMap.get("err_code_des"),null);
        }
        //给app返回封装的数据
        return new ResultMessageDTO(200, "OK",parameterMap);
    }

    /**
     * APP支付回调
     * @param orderNumber 订单编号
     */
    @GetMapping("/payNotifyByApp/{orderNumber}")
    public ResultMessageDTO payNotifyByApp(@PathVariable String orderNumber, HttpServletRequest request) throws Exception {

        //根据订单编号查询
        Map<String, String> payMap = PayCommonUtil.Order(orderNumber, WeChatConfig.QUERY_URL);
        if(WeChatConfig.SUCCESS.equals(payMap.get("return_code")) && WeChatConfig.SUCCESS.equals(payMap.get("result_code"))){
            //获取支付状态
            String tradeState = payMap.get("trade_state");
            //如果支付完成,以下为支付状态
            /*
            SUCCESS—支付成功

            REFUND—转入退款

            NOTPAY—未支付

            CLOSED—已关闭

            REVOKED—已撤销（刷卡支付）

            USERPAYING--用户支付中

            PAYERROR--支付失败(其他原因，如银行返回失败)
             */
            if (WeChatConfig.SUCCESS.equals(tradeState)){
                return new ResultMessageDTO(200, "OK",tradeState);
            }else{
                return new ResultMessageDTO(200, "OK",tradeState);
            }
        }else{
            //给app返回错误码和描述
            return new ResultMessageDTO(Integer.valueOf(payMap.get("err_code")), payMap.get("err_code_des"),null);
        }
    }


}
