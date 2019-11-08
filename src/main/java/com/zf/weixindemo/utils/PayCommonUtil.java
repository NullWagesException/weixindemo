package com.zf.weixindemo.utils;

import com.zf.weixindemo.dto.WenxinBillDTO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author: nullWagesException
 * @Date: 2019/9/3 11:26
 * @Description:
 */
public class PayCommonUtil {


    /**
     * 订单查询/关闭订单
     * @param outTradeNo 订单号
     * @param URL 微信统一请求地址
     * @return
     */
    public static Map<String,String> Order(String outTradeNo,String URL){
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        parameterMap.put("appid",WeChatConfig.APP_ID);
        parameterMap.put("mch_id",WeChatConfig.MCH_ID);
        parameterMap.put("out_trade_no",outTradeNo);
        parameterMap.put("nonce_str", getRandomString());
        String sign = createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);
        String requestXml = PayCommonUtil.getRequestXml(parameterMap);
        System.out.println(requestXml);
        String result = HttpUtil.post(
                URL,
                requestXml);
        Map<String, String> map = null;
        try {
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 申请退款
     * @param outTradeNo 订单编号
     * @param outRefundNo 退款订单编号
     * @return
     */
    public static Map<String,String> Refund(String outTradeNo,String outRefundNo, BigDecimal totalAmount){
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        parameterMap.put("appid",WeChatConfig.APP_ID);
        parameterMap.put("mch_id",WeChatConfig.MCH_ID);
        parameterMap.put("out_trade_no",outTradeNo);
        parameterMap.put("out_refund_no",outRefundNo);
        //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        parameterMap.put("total_fee", df.format(total));
        parameterMap.put("refund_fee", df.format(total));
        parameterMap.put("nonce_str", getRandomString());
        String sign = createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);
        String requestXml = PayCommonUtil.getRequestXml(parameterMap);
        System.out.println(requestXml);
        String result = HttpUtil.post(
                WeChatConfig.REFUND_URL,
                requestXml);
        Map<String, String> map = null;
        try {
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 下载对账单
     * @param billDate 查询日期
     * @return
     */
    public static Map<String,Object> downloadBill(String billDate){
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        parameterMap.put("appid",WeChatConfig.APP_ID);
        parameterMap.put("mch_id",WeChatConfig.MCH_ID);
        parameterMap.put("nonce_str", getRandomString());
        parameterMap.put("bill_date", billDate);
        //返回当日成功支付的订单（不含充值退款订单）
        parameterMap.put("bill_type", "SUCCESS");
        String sign = createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);
        String requestXml = PayCommonUtil.getRequestXml(parameterMap);
        System.out.println(requestXml);
        String result = HttpUtil.post(
                WeChatConfig.DOWNLOADBILL_URL,
                requestXml);
        Map<String, Object> map = new HashMap<>();
        //如果包含错误描述，则说明未获取到账单
        if (result.contains("error")) {
            try {
                map = PayCommonUtil.doXMLParse(result);
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            String tradeMsg = result.substring(result.indexOf("`"));
            tradeMsg = tradeMsg.replaceAll("%,`0.01,`", "%,");
            //总汇信息
            String sumMag = tradeMsg.substring(tradeMsg.lastIndexOf("%"), tradeMsg.length());
            tradeMsg = tradeMsg.substring(0, tradeMsg.lastIndexOf("%") + 1);
            tradeMsg = tradeMsg.replace("`", "");
            String[] tradeArray = tradeMsg.split("%");  // 根据%来区分
            int index = 0;
            for (String tradeDetailInfo : tradeArray) {
                if (tradeDetailInfo.substring(0, 1).equals(",")) {
                    tradeDetailInfo = tradeDetailInfo.substring(1);
                }
                String[] tradeDetailArray = tradeDetailInfo.split(",");
                WenxinBillDTO entity = new WenxinBillDTO();
                entity.setTransDate(tradeDetailArray[0]);// 交易时间
                entity.setSubscriptionNum(tradeDetailArray[1]);// 公众账号ID
                entity.setMchID(tradeDetailArray[2]);// 商户号
                entity.setEquipmentNum(tradeDetailArray[4]);// 设备号
                entity.setTransactionId(tradeDetailArray[5]);// 微信订单号
                entity.setOutTradeNo(tradeDetailArray[6]);// 商户订单号
                entity.setUserNum(tradeDetailArray[7]);// 用户标识
                entity.setOrderType(tradeDetailArray[8]);// 交易类型
                entity.setTradeStateDesc(tradeDetailArray[9]);// 交易状态
                entity.setPayBank(tradeDetailArray[10]);// 付款银行
                entity.setCashFeeType(tradeDetailArray[11]);// 货币种类
                entity.setTotalFee(tradeDetailArray[12]);// 总金额
                entity.setCommodityName(tradeDetailArray[14]);// 商品名称
                entity.setServiceCharge(tradeDetailArray[16]);// 手续费
                entity.setRate(tradeDetailArray[17] + "%");// 费率
                map.put(index++ + "",entity);
            }
        }
        return map;
    }

    /**
     * 随机字符串生成
     * @return
     */
    public static String getRandomString() { //length表示生成字符串的长度
        //32位随机数(UUID去掉-就是32位的)
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 请求xml组装
     * @param parameters
     * @return
     */
    public static String getRequestXml(SortedMap<String,Object> parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if ("attach".equalsIgnoreCase(key)||"body".equalsIgnoreCase(key)||"sign".equalsIgnoreCase(key)) {
                sb.append("<"+key+">"+"<![CDATA["+value+"]]></"+key+">");
            }else {
                sb.append("<"+key+">"+value+"</"+key+">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 统一下单
     */
    public static Map<String, String> weixinPrePay(String tradeNo, BigDecimal totalAmount,
                                                   String description, HttpServletRequest request,
                                                   String appid,String type) throws IOException {
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        //应用appid
        parameterMap.put("appid", appid);
        //商户号
        parameterMap.put("mch_id", WeChatConfig.MCH_ID);
        //32位随机数
        parameterMap.put("nonce_str", getRandomString());
        //商品描述
        parameterMap.put("body", description);
        //商户订单号
        parameterMap.put("out_trade_no", tradeNo);
        //货币类型
        parameterMap.put("fee_type", "CNY");
        //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        parameterMap.put("total_fee", df.format(total));
        parameterMap.put("spbill_create_ip", RequestUtil.getIpAddress(request));
        parameterMap.put("notify_url", WeChatConfig.NOTIFY_URL);
        parameterMap.put("trade_type", type);
        String sign = createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);
        String requestXml = PayCommonUtil.getRequestXml(parameterMap);
        System.out.println(requestXml);
        String result = HttpUtil.post(
                WeChatConfig.UNIFIEDORDER_URL,
                requestXml);
        System.out.println(result);
        Map<String, String> map = null;
        try {
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 生成签名
      * @param characterEncoding
     * @param parameters
     * @return
     */
    public static String createSign(String characterEncoding,SortedMap<String,Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WeChatConfig.KEY);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    /**
     * xml解析
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Map doXMLParse(String strxml) throws JDOMException, IOException {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

        if(null == strxml || "".equals(strxml)) {
            return null;
        }

        Map m = new HashMap();

        InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if(children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        //关闭流
        in.close();

        return m;
    }

    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    /**
     * 验证回调签名
     * @return
     */
    public static boolean isTenpaySign(Map<String, String> map) {
        String characterEncoding="utf-8";
        String charset = "utf-8";
        String signFromAPIResponse = map.get("sign");
        if (signFromAPIResponse == null || signFromAPIResponse.equals("")) {
            System.out.println("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
            return false;
        }
        System.out.println("服务器回包里面的签名是:" + signFromAPIResponse);
        //过滤空 设置 TreeMap
        SortedMap<String,String> packageParams = new TreeMap();

        for (String parameter : map.keySet()) {
            String parameterValue = map.get(parameter);
            String v = "";
            if (null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }

        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();

        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if(!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WeChatConfig.KEY);

        //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        //算出签名
        String resultSign = "";
        String tobesign = sb.toString();

        if (null == charset || "".equals(charset)) {
            resultSign = MD5Util.MD5Encode(tobesign, characterEncoding).toUpperCase();
        }else{
            try{
                resultSign = MD5Util.MD5Encode(tobesign, characterEncoding).toUpperCase();
            }catch (Exception e) {
                resultSign = MD5Util.MD5Encode(tobesign, characterEncoding).toUpperCase();
            }
        }

        String tenpaySign = ((String)packageParams.get("sign")).toUpperCase();
        return tenpaySign.equals(resultSign);
    }



}
