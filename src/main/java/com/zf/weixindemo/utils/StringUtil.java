package com.zf.weixindemo.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: nullWagesException
 * @Date: 2019/9/3 15:04
 * @Description:
 */
public class StringUtil {

    /**
     * 数值类型前面补零（共13位）
     * @param num
     * @return
     */
    public static String supplementZeroGenerateThirteen(int num){
        String str = String.format("%013d", num);

        return str;
    }

    /**
     * 数值类型前面补零（共16位）
     * @param num
     * @return
     */
    public static String supplementZeroGenerateSixteen(int num){
        String str = String.format("%016d", num);

        return str;
    }
    /**
     * 数值类型前面补零（共3位）
     * @param num
     * @return
     */
    public static String supplementZeroGenerateThree(int num){
        String str = String.format("%03d", num);

        return str;
    }

    /**
     * 判断字符串是不是double型
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static String trim(String str, boolean nullFlag){
        String tempStr = null;

        if (str != null)
        {
            tempStr = str.trim();
        }

        if (nullFlag)
        {
            if ("".equals(tempStr) || "null".equals(tempStr))
            {
                tempStr = null;
            }
        }
        else
        {
            if (tempStr == null)
            {
                tempStr = "";
            }
        }

        return tempStr;
    }
    public static String replace(String strSource, String strFrom, String strTo) {
        if(strSource==null){
            return null;
        }
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0) {
            char[] cSrc = strSource.toCharArray();
            char[] cTo = strTo.toCharArray();
            int len = strFrom.length();
            StringBuffer buf = new StringBuffer(cSrc.length);
            buf.append(cSrc, 0, i).append(cTo);
            i += len;
            int j = i;
            while ((i = strSource.indexOf(strFrom, i)) > 0) {
                buf.append(cSrc, j, i - j).append(cTo);
                i += len;
                j = i;
            }
            buf.append(cSrc, j, cSrc.length - j);
            return buf.toString();
        }
        return strSource;
    }


    public static String deal(String str) {
        str = replace(str, "\\", "\\\\");
        str = replace(str, "'", "\\'");
        str = replace(str, "\r", "\\r");
        str = replace(str, "\n", "\\n");
        str = replace(str, "\"", "\\\"");
        return str;
    }

    public static String GetMapToXML(Map<String,String> param){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        for (Map.Entry<String,String> entry : param.entrySet()) {
            sb.append("<"+ entry.getKey() +">");
            sb.append(entry.getValue());
            sb.append("</"+ entry.getKey() +">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

}
