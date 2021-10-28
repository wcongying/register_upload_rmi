package com.nicole.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartServer {
    public static final Logger logger = LoggerFactory.getLogger(StartServer.class);

    public static void main(String[] args) {

        try {
          //  System.setProperty("java.rmi.server.hostname", "8.129.226.29");
//            System.setProperty("java.rmi.server.hostname","127.0.0.1");
           //  System.setProperty("java.rmi.server.hostname","172.21.0.2");
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                    "classpath:spring-mybatis.xml");
//            applicationContext.getBean("UserServiceImpl");
            logger.info("\n" + "Service Two Starts. Waiting for the client");
        } catch (Exception e) {
            logger.error("Error " + e.getMessage());
            e.printStackTrace();
        }
    }
}
