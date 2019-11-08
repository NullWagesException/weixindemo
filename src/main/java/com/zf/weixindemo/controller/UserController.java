package com.zf.weixindemo.controller;

import com.alibaba.fastjson.JSON;
import com.zf.weixindemo.dto.ResultMessageDTO;
import com.zf.weixindemo.dto.WeiXinLoginDTO;
import com.zf.weixindemo.dto.WeixinPhoneDTO;
import com.zf.weixindemo.utils.HttpUtil;
import com.zf.weixindemo.utils.UUIDUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.zf.weixindemo.utils.AES.wxDecrypt;

/**
 * @Author: nullWagesException
 * @Date: 2019/11/5 15:14
 * @Description:
 */
@RestController
@CrossOrigin(allowCredentials ="true")
@RequestMapping("user")
public class UserController {


    /**
     * 获取openID和session_key
     * @param code
     * @return
     */
    @GetMapping("getKeyAndId/{code}")
    @ResponseBody
    public ResultMessageDTO getKeyAndId(HttpServletRequest request,@PathVariable String code){
        String targetData = HttpUtil.get("https://api.weixin.qq.com/sns/jscode2session?appid=wxd6bed0e22edb630b&secret=1f4473f43dcf64e70d61007cc6c5684b&js_code=" + code + "&grant_type=authorization_code");
        System.out.println(targetData);
        WeiXinLoginDTO loginDTO = JSON.parseObject(targetData, WeiXinLoginDTO.class);
        if (loginDTO != null && loginDTO.getOpenid() != null) {
            String token = UUIDUtils.getUUID();
            //将用户数据存入
            HttpSession session = request.getSession();
            session.setAttribute(token, loginDTO);
            return new ResultMessageDTO(200,"OK", token);
        }else{
            if (loginDTO != null) {
                return new ResultMessageDTO(loginDTO.getErrcode(),"FAIL", loginDTO.getErrmsg());
            }
            else {
                return new ResultMessageDTO(200,"FAIL", "未知错误");
            }
        }
    }




    /**
     * 解密用户信息，获取手机号后将用户存入数据库
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("decodeUserInfo")
    @ResponseBody
    public ResultMessageDTO decodeUserInfo(HttpServletRequest request,String encrypted,String sessionKey,String iv) {
        //解密数据
        String json = wxDecrypt(encrypted,sessionKey,iv);
        System.out.println(json);
        WeixinPhoneDTO phoneDTO = JSON.parseObject(json, WeixinPhoneDTO.class);



        return new ResultMessageDTO(200,"OK", phoneDTO.getPhoneNumber());
    }

}
