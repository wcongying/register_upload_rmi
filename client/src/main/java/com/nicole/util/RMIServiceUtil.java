package com.nicole.util;
import com.nicole.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RMIServiceUtil {
    private Logger logger = LoggerFactory.getLogger(RMIServiceUtil.class);
    private UserService userService;

    public UserService getUserServiceRandom(){
        Random random = new Random();
        //产生的随机数为0-100的整数,不包括100
        int randNum = random.nextInt(100);
        String serviceName = null;
        //现在是二选一个service，有一个service关闭，马上看看另一个是否能用
        if( randNum % 2 == 0 ) {
            try{
                //first choice UserServiceOne
                logger.info("getting service one ... ");
                userService = getUserService("UserServiceOne");
                userService.selectUserName("nicole");
            } catch (Exception exception){
                try {
                    logger.info("Service one is off, getting service two ... error : "
                            + exception.getMessage());
                    userService = getUserService("UserServiceTwo");
                }catch ( Exception e) {
                    logger.info("Service one and two are off. error : " + e.getMessage());
                }
            }
        } else {
            try {
                //first choice UserServiceTwo
                logger.info("getting service two ... ");
                userService = getUserService("UserServiceTwo");
                userService.selectUserName("nicole");
            }catch ( Exception exception) {
                try{
                    logger.info("Service two is off . getting service one ... error : "
                            + exception.getMessage());
                    userService = getUserService("UserServiceOne");
                }catch ( Exception e) {
                    logger.info("Service one and two are off. error : "+ e.getMessage());
                }
            }
        }
        return userService;
    }

    private UserService getUserService( String serviceName){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(
                "client.xml");
        return (UserService) applicationContext.getBean(serviceName);
    }

}
