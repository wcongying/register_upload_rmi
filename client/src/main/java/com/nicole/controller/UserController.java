package com.nicole.controller;

import com.nicole.entity.User;
import com.nicole.service.UserService;
import com.nicole.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    RMIServiceUtil rmiServiceUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    MailUtil mailUtil;
    @Autowired
    TencentSMSUtil tencentSMSUtil;

    private UserService userService;

    private static final String KEY="3456";

    @RequestMapping(value = "/loginSuccessful", method = RequestMethod.GET)
    public String LoginSuccessful() {
        return "loginSuccessful";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String Login(HttpServletRequest request, Model model, HttpServletResponse response)
            throws UnsupportedEncodingException {
        //获取用户传入的账号密码
        String username = request.getParameter("username");
        String out_password = request.getParameter("password");

        userService = rmiServiceUtil.getUserServiceRandom();
        User user = userService.selectUserName(username);

        //验证该用户是否存在，如果不存在用户名，无法验证
        if (user != null) {

            //数据库中存储的已经加密的密码
            String md5Str = user.getPassword();

            //用DigestUtils.md5DigestAsHex看是否正确
            String out_passwordMd5 =DigestUtils.md5DigestAsHex(out_password.getBytes("UTF-8"));
            boolean flag = out_passwordMd5.equals( md5Str);

            if (flag) {
                //生成token
                String token = JWTUtil.getJWT(String.valueOf(user.getId()), user.getUsername(),
                        new Date(), KEY);
                //把token装到cookie中发送到客户端
                CookieUtil.setCookie(response, "token", token, 60 * 10);
                return "redirect:loginSuccessful";
            } else {
                model.addAttribute("msg", "wrong password");
                return "login";
            }
        } else {
            model.addAttribute("msg", "no username");
            return "login";
        }
    }

    //相对邮箱注册，更倾向于用手机号注册的客户
    @RequestMapping(value = "/login/regist", method = RequestMethod.GET)
    public String loginRegist() {
        return "phoneRegist";
    }

    //get方法来到上传前端页面
    @RequestMapping(value = "/u/upload/image", method = RequestMethod.GET)
    public String uploadImg() {
        return "uploadFile";
    }

    /**
     * 邮箱注册页面导航，get的时候显示页面
     *
     */
    @RequestMapping(value = "/email/regist", method = RequestMethod.GET)
    public String MailRegist() {
        return "emailRegist";
    }


    /**
     * 邮箱注册接口
     *
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "/email/regist", method = RequestMethod.POST)
    public String domailRegist(HttpServletRequest request, Model model) throws
            UnsupportedEncodingException {
        //获取表单信息
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");
        String email = request.getParameter("email");
        String code = request.getParameter("code");
        userService = rmiServiceUtil.getUserServiceRandom();
        User user1 = userService.selectUserName(username);
        User user2 = userService.selectUserName(email);
        if (redisUtil.get(email) != null) {//本来以为错，后来发现正确，因为mail这个key过期时间很短
            if (code.equals(redisUtil.get(email))) {
                /**
                 * 邮箱注册
                 * 判断用户名是否已经存在和比对验证码是否正确
                 */
                if (user1 == null && user2 == null) {
                    User userMail = new User();
                    userMail.setEmail(email);
                    //md5加密密码
                    String md5Password= DigestUtils.md5DigestAsHex(password.
                            getBytes("UTF-8"));
                    userMail.setUsername(username);
                    userMail.setPassword(md5Password);
                    userService.insertUser(userMail);
                    return "login";
                } else {
                    model.addAttribute("msg", "username or email address already exists");
                    return "emailRegist";
                }
            } else {
                model.addAttribute("msgCode", "incorrect verification code");
                return "emailRegist";
            }
        } else {
            model.addAttribute("magCode", "verification code has expired");
            return "emailRegist";
        }
    }

    /**
     * 发送邮箱验证码接口
     *
     * @param email 邮箱号码
     * @return
     */
    @RequestMapping(value = "/email/code", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sentMailCode(@RequestParam(value = "email", required = false)
                                                    String email) {
        Map<String, Object> map = new HashMap<>(100);
        //验证邮箱
        String mailRegex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)" +
                "*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern p1 = Pattern.compile(mailRegex);
        Matcher m1 = p1.matcher(email);
        boolean isMail = m1.matches();
        //判断邮箱号格式，如果不对给页面显示错误信息
        if (!isMail) {
            logger.info("invalid email address");
            map.put("msg", "invalid email address");
        } else {
            mailUtil.sendMailCode(email, map);
        }
        return map;
    }

    @RequestMapping(value = "/phone/regist", method = RequestMethod.GET)
    public String regist() {
        return "phoneRegist";
    }
    /**
     * 手机注册接口
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/phone/regist", method = RequestMethod.POST)
    public String doPhoneRegist(HttpServletRequest request, Model model)
            throws UnsupportedEncodingException {
        //获取表单信息
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");
        String phone = request.getParameter("phone");
        String code = request.getParameter("code");
        userService = rmiServiceUtil.getUserServiceRandom();
        User user1 = userService.selectUserName(username);
        User user2 = userService.selectUserName(phone);
        if (redisUtil.get(phone) != null) {
            if (code.equals(redisUtil.get(phone))) {
                /**
                 * 手机注册
                 * 判断用户名是否已经存在和比对验证码是否正确
                 */
                if (user1 == null && user2 == null) {
                    User userPhone = new User();
                    userPhone.setCellphone(phone);
                    userPhone.setUsername(username);
                    //md5加密密码
                    String md5Password= DigestUtils.
                            md5DigestAsHex(password.getBytes("UTF-8"));
                    userPhone.setPassword(md5Password);
                    userPhone.setCreatedAt(System.currentTimeMillis());
                    userPhone.setUpdatedAt(System.currentTimeMillis());
                    userService.insertUser(userPhone);
                    return "login";
                } else {
                    model.addAttribute("msg", "user name or mobile phone number " +
                            "has already been registered");
                    return "phoneRegist";
                }
            } else {
                model.addAttribute("msgCode", "incorrect verification code");
                return "phoneRegist";
            }
        } else {
            model.addAttribute("magCode", "verification code has expired");
            return "phoneRegist";
        }

    }


    /**
     * 发送手机验证码接口
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/phone/code", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sentPhoneCode(@RequestParam("phone") String phone) {
        Map<String, Object> map = new HashMap<>(100);
        //验证手机号码格式
        String phoneRegex = "^((\\+86)|(86))?1[3|4|5|7|8][0-9]\\d{4,8}$";
        Pattern p = Pattern.compile(phoneRegex);
        Matcher m = p.matcher(phone);
        boolean isPhone = m.matches();
        //判断手机号格式，如果不对给页面显示错误信息
        if (!isPhone) {
            logger.info("invalid cell phone number");
            map.put("msg", "invalid cell phone number");
        } else {
            tencentSMSUtil.sendPhoneCode(phone, map);
        }
        return map;
    }


    /**
     * 上传图片/头像接口
     * @param file
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/u/upload/image",method = RequestMethod.POST)
    public String uploadImage(@RequestParam MultipartFile file, Model model, HttpServletRequest request){
        try {
            userService = rmiServiceUtil.getUserServiceRandom();
            String userImageUrl = userService.uploadImage(file, request.getParameter("username"));
            model.addAttribute("data", userImageUrl);
        } catch (ImgException | IOException e) {
            e.printStackTrace();
        }
        return "uploadFile";
    }
}
