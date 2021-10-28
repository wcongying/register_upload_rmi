package com.nicole.util;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
@PropertySource(value= "classpath:tencenttext.properties", encoding="UTF-8")
public class TencentSMSUtil {
    private static Logger logger = LoggerFactory.getLogger(TencentSMSUtil.class);
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 密钥
     */
    @Value("${TencentSMS.secretId}")
    private String secretId;
    @Value("${TencentSMS.secretKey}")
    private String secretKey;

    /**
     * 短信业务
     */
    @Value("${TencentSMS.smsSdkAppId}")
    private String smsSdkAppId;
    @Value("${TencentSMS.TemplateID}")
    private String TemplateID;
    @Value("${TencentSMS.sign}")
    private String sign;

    /**
     * 验证码
     */
    public String getCode() {
        String str="0123456789";
        StringBuilder st=new StringBuilder(4);
        for(int i=0;i<6;i++){
            char ch=str.charAt(new Random().nextInt(str.length()));
            st.append(ch);
        }
        String code=st.toString().toLowerCase();
        return code;
    }

    public void sendSMS(String telephone,String code) {

        logger.info("cell phone number: "+telephone);
        logger.info("Verification code:"+code);
        String signGot = sign;
        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey，见《创建secretId和secretKey》小节
        Credential cred = new Credential(secretId, secretKey);

        // 实例化要请求产品(以cvm为例)的client对象
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod(ClientProfile.SIGN_TC3_256);

        //第二个ap-chongqing 填产品所在的区
        SmsClient smsClient = new SmsClient(cred, "ap-guangzhou");
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        //appId ,见《创建应用》小节
        sendSmsRequest.setSmsSdkAppid(smsSdkAppId);
        //发送短信的目标手机号，可填多个。
        String[] phones={telephone};
        sendSmsRequest.setPhoneNumberSet(phones);
        //模版id,见《创建短信签名和模版》小节
        sendSmsRequest.setTemplateID(TemplateID);
        //模版参数，从前往后对应的是模版的{1}、{2}等,见《创建短信签名和模版》小节。目前我的只有一个验证码
        String [] templateParam={code};
        sendSmsRequest.setTemplateParamSet(templateParam);
        //签名内容，不是填签名id,见《创建短信签名和模版》小节
        sendSmsRequest.setSign(signGot);
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse= smsClient.SendSms(sendSmsRequest); //发送短信
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        if (sendSmsResponse.getSendStatusSet() != null) {
            logger.info("Sent successfully");
            logger.info("Data returned by the SMS interface----------------");
            logger.info("StatusSet=" +SendSmsResponse.toJsonString(sendSmsResponse));
            logger.info("RequestId=" + sendSmsResponse.getRequestId());

        } else {
            logger.error("Sending failure");
            logger.info("Data returned by the SMS interface-----------------");
            logger.info("StatusSet=" + SendSmsResponse.toJsonString(sendSmsResponse));
            logger.info("RequestId=" + sendSmsResponse.getRequestId());
        }
    }

    /**
     * 封装发送手机验证码的方法
     * @param phone
     * @param map
     */
    public void sendPhoneCode(final String phone, Map<String,Object> map){
        //判断今天是不是首次登录
        if (redisUtil.get("count" + phone) == null) {
            //短信稍微严格一点，10小时内最多发5条，因为这个很可能计费
            redisUtil.set("count" + phone, "5", 60 * 60 * 10);
            //生成验证码，加到redis中.设置失效时间为2分钟。

            String verify = getCode();
            //发送********
            sendSMS(phone,verify);
            logger.info("Verification code is：" + verify);
            redisUtil.set(phone, verify, 300);//5分钟，不能太短。不设置10分钟以上，太长
            map.put("msg", "verification code is sent successfully  , " +
                    redisUtil.get("count"+phone) + " more times can be sent");
        } else {
            //过期时间
            long expire = redisUtil.getExpire(phone);
            logger.info("out-of-service time:---" + expire);
            //判断今天还有没有剩余发送次数和间隔时间
            if (redisUtil.get(phone) == null) {
                int count=Integer.parseInt((String)redisUtil.get("count"+phone));
                if(count>0) {
                    //生成验证码，加到redis中.设置失效时间为5分钟。
                    String verify = getCode();
                    //发送********
                    sendSMS(phone,verify);
                    logger.info("Verification code is：" + verify);
                    redisUtil.set(phone, verify,300);
                    //可发送次数-1
                    redisUtil.decr("count" + phone, 1);
                    map.put("msg",  "verification code is sent successfully  , " +
                            redisUtil.get("count"+phone) + " more times can be sent");
                } else {
                    logger.info(phone+ "====今天的发送验证码次数已使用完");
                    map.put("msg", "今天的发送次数已经用完");
                }
            } else {
                map.put("msg", "请隔" + expire + "秒再发送");
            }
        }
    }
}
