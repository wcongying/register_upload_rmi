package com.nicole.service.impl;

import com.nicole.dao.UserDao;
import com.nicole.entity.User;
import com.nicole.service.UserService;
import com.nicole.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class UserServiceImpl implements UserService {
    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserDao userDao;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private OSSUtil ossUtil;

    @Override
    public long insertUser(User user){
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        return userDao.insertUser(user);
    }

    @Override
    public User selectUserName(String name){
        String key = name + "userServiceImpl";
        User user = (User) redisUtil.get(key);
        if( user == null ) {
            user = userDao.selectUserName( name );
            redisUtil.set( key, user, 3600);
            logger.info("----- add user to redis : " + user);
        }
        return user;
    }

    @Override
    public String uploadImage(MultipartFile file, String username) throws IOException, ImgException {
        if (file == null || file.getSize() <= 0) {
            throw new ImgException("The image cannot be empty");
        }
        String ossImageEtag = ossUtil.uploadImageOSS(file);
        //阿里云提供了根据文件名获取地址的方法ossClient.generatePresignedUrl。
        //生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        //上传方法ossClient.putObject()返回类型是PutObjectResult，它提供了get etag的方法
        // etag当中正好又包括带后缀的文件名，上传返回etag比较合适，包含比较多信息，后续再处理拿到取药的信息即可
        //虽然在这个代码逻辑中String Filename = file.getOriginalFilename();，处理去掉如果有“/”前面内容即可
        String imgUrl = ossUtil.getImageUrl(ossImageEtag);
        User user=new User();
        user.setUsername(username);
        user.setImageurl(imgUrl);
        //上传的同时把图片url更新到数据库中
        userDao.uploadImageurl(user);
        //能够根据返回的url在阿里云上查看图片
        return imgUrl;
    }
}

