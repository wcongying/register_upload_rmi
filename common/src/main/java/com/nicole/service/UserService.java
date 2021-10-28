package com.nicole.service;

import com.nicole.entity.User;
import com.nicole.util.ImgException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    /**
     * 添加用户
     * @param user
     * @return
     */
    long insertUser(User user);

    /**
     * 通过用户名，手机号或者email查询
     * @param username
     * @return
     */
    User selectUserName(String username);

    /**
     * 上传图片
     * @param
     * @return
     */
    String uploadImage(MultipartFile file, String username) throws IOException, ImgException;
}

