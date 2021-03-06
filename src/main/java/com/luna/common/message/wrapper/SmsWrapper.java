package com.luna.common.message.wrapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.luna.common.config.TencentConfigValue;
import com.luna.common.config.TencentSmsConfigValue;
import com.luna.common.tencent.TencentMessage;
import com.luna.common.utils.CommonUtils;

/**
 * @author Luna@win10
 * @date 2020/5/16 10:12
 */
@Component
public class SmsWrapper {

    @Autowired
    StringRedisTemplate   stringRedisTemplate;

    @Autowired
    TencentSmsConfigValue tencentSmsConfigValue;

    @Autowired
    TencentConfigValue    tencentConfigValue;

    /**
     * +86 国内单个发送验证码短信
     * 
     * @param phone
     */
    public boolean sendAuthCode(String phone, String code) {
        phone = "+86" + phone;
        stringRedisTemplate.delete(phone);
        stringRedisTemplate.opsForValue().append(phone, code);
        stringRedisTemplate.expire(phone, 3000, TimeUnit.SECONDS);
        Map map = null;
        try {
            map = TencentMessage.sendMsg(tencentConfigValue.getSecretid(), tencentConfigValue.getSecretKey(),
                new String[] {phone},
                tencentSmsConfigValue.getAuthCode(),
                new String[] {code},
                tencentSmsConfigValue.getAppId(), tencentSmsConfigValue.getSign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.containsValue("1") == true;
    }

    /**
     * 重置密码
     * 
     * @param phone
     * @return
     */
    public boolean resetPassword(String phone, String password) {
        phone = "+86" + phone;
        Map map = null;
        try {
            map = TencentMessage.sendMsg(tencentConfigValue.getSecretid(), tencentConfigValue.getSecretKey(),
                new String[] {phone},
                tencentSmsConfigValue.getResetPassword(),
                new String[] {password},
                tencentSmsConfigValue.getAppId(), tencentSmsConfigValue.getSign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.containsValue("1") == true;
    }
}
