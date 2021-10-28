package com.nicole.dao;

import com.nicole.entity.User;

public interface UserDao {
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
     * @param user
     * @return
     */
    boolean uploadImageurl(User user);
}
