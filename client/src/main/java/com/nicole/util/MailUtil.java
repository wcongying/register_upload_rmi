package com.nicole.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

//qq邮箱发邮件
@Component
@PropertySource("classpath:tencentmail.properties")
public class MailUtil {
    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
    @Autowired
    private RedisUtil redisUtil;

    @Value("${mail.smtp.auth}")
    private String auth;
    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.smtp.port}")
    private String port;
    @Value("${mail.smtp.socketFactory.port}")
    private String socketFactoryPort;
    @Value("${mail.smtp.socketFactory.fallback}")
    private String socketFactoryFallback;
    @Value("${mail.smtp.socketFactory.class}")
    private String socketFactoryClass;
    @Value("${mail.user}")
    private String user;
    @Value("${mail.password}")
    private String password;

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

    /**
     * 邮件发送
     */
    public void sendMail(String mail,String code) {
        try{
            // 创建Properties 类用于记录邮箱的一些属性
            Properties props = new Properties();
            // 表示SMTP发送邮件，必须进行身份验证
            props.put("mail.smtp.auth", auth);
            //此处填写SMTP服务器
            props.put("mail.smtp.host", host);
            //端口号，QQ邮箱端口465
            props.put("mail.smtp.port", port);
            props.setProperty("mail.smtp.socketFactory.port", socketFactoryPort);//设置ssl端口
            props.setProperty("mail.smtp.socketFactory.fallback", socketFactoryFallback);
            props.setProperty("mail.smtp.socketFactory.class", socketFactoryClass);
            // 此处填写，写信人的账号
            props.put("mail.user", user);
            // 此处填写16位STMP口令,这里用的是POP3的
            props.put("mail.password", password);

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = user;
                    String passwordNow = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, passwordNow);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            InternetAddress form = new InternetAddress(user);
            message.setFrom(form);

            // 设置收件人的邮箱
            InternetAddress to = new InternetAddress(mail);
            message.setRecipient(Message.RecipientType.TO, to);

            // 设置邮件标题
            message.setSubject("来自nicole网的验证码邮件");

            // 设置邮件的内容体
            message.setContent("<h2>来自nicole网验证码邮件,请接收你的验证码：</h2><h3>你的验证码是："+code
                    +"，请妥善保管好你的验证码！</h3>", "text/html;charset=UTF-8");

            // 最后当然就是发送邮件啦
            Transport.send(message);
        } catch (Exception e) {
            //捕获错误异常码
            logger.info("Err Message : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 封装发送邮箱验证码的方法
     * @param  email
     * @param map
     */
    public void sendMailCode(final String email, Map<String,Object> map){
        //判断5小时内是不是首次登录
        if (redisUtil.get("count" + email) == null) {
            //5小时内最多能发5封验证邮件。也不是特别严
            redisUtil.set("count" + email, "5", 60 * 60 * 5);
            //生成验证码，加到redis中.设置失效时间为2分钟。
            String verify = getCode();
            //发送*********
            sendMail(email,verify);
            logger.info("验证码是：" + verify);
            redisUtil.set(email, verify, 300);//太短，应该比前端时间长，300s比较合理
            map.put("msg", "验证码发送成功,还可以发送" + redisUtil.get("count"+ email) + "次");
        } else {
            //过期时间
            long expire = redisUtil.getExpire(email);
            logger.info("失效时间：---" + expire);
            if (redisUtil.get(email) == null) {
                int count = Integer.parseInt((String) redisUtil.get("count" + email));
                //判断今天还有没有剩余发送次数和间隔时间
                if (count > 0) {
                    //生成验证码，加到redis中.设置失效时间为5分钟。
                    String verify = getCode();
                    //发送*********
                    sendMail(email,verify);
                    logger.info("验证码是：" + verify);
                    redisUtil.set(email, verify, 300);
                    //可发送次数-1
                    redisUtil.decr("count" + email, 1);
                    map.put("msg", "验证码发送成功,还可以发送" + count + "次");
                } else {
                    logger.info(email + "====今天的发送验证码次数已使用完");
                    map.put("msg", "今天的发送次数已经用完");
                }
            }
            else {
                map.put("msg", "请隔" + expire+ "秒再发送");
            }
        }
    }
}
