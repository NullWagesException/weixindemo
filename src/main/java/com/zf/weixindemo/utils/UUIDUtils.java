package com.zf.weixindemo.utils;

import java.util.UUID;

/**
 * @Date: 2019/5/25 12:34
 * @Description: UUID生成工具类
 */
public class UUIDUtils {

    /**
     * @Date: 2019/5/25 14:37
     * @Description: 随机生成UUID，移除了其中的“-”字符
     * @Param []
     * @Return java.lang.String
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


}

