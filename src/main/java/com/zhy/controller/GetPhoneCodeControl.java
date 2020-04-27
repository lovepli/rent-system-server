package com.zhy.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.zhy.component.PhoneRandomBuilder;
import com.zhy.service.RedisService;
import com.zhy.utils.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2018/6/4 15:03
 * Describe: 注册获得手机验证码
 */
@RestController
@Slf4j
public class GetPhoneCodeControl {

    @Autowired
    RedisService redisService;

    private static final String REGISTER = "register";

    /**
     * 阿里云 accessKeyId
     */
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    /**
     * 阿里云 secret
     */
    @Value("${aliyun.secret}")
    private String secret;

    /**
     * 阿里云短信发送模板
     */
    private static final String SIGN_NAME = "张海洋ocean";

    @PostMapping(value = "/getAuthCode", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAuthCode(@RequestBody HashMap hashMap){

        String phone = (String) hashMap.get("phone");

        String trueMsgCode = PhoneRandomBuilder.randomBuilder();
        System.out.println(trueMsgCode);

       //在redis中保存手机号验证码并设置过期时间
        redisService.saveMsgCode(phone, trueMsgCode);

        String msgCode = "SMS_136394413";

//        try {
//            sendSmsResponse(phone, trueMsgCode, msgCode);
//        } catch (ClientException e) {
//            log.error("[{}] send phone message exception", phone, e);
//            return JsonResult.fail().toJSON();
//        }

        return JsonResult.success().toJSON();
    }

    private void sendSmsResponse(String phoneNumber, String code, String msgCode) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //"***"分别填写自己的AccessKey ID和Secret
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        //填写接收方的手机号码
        request.setPhoneNumbers(phoneNumber);
        //此处填写已申请的短信签名
        request.setSignName(SIGN_NAME);
        //此处填写获得的短信模版CODE
        request.setTemplateCode(msgCode);
        //笔者的短信模版中有${code}, 因此此处对应填写验证码
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        acsClient.getAcsResponse(request);

    }



}
