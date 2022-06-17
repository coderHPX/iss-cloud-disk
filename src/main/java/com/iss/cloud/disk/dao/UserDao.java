package com.iss.cloud.disk.dao;

import com.iss.cloud.disk.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {

    User login(@Param("username") String username, @Param("password") String password);

    int getCount();

    User getUser(int id);

    List<User> getUsers(@Param("start") Integer start, @Param("pageSize") Integer pageSize);

    List<User> chooseUser(int id);

    int insertUser(User user);

    int updatePath(User user);

    int updateUser(User user);

    int delete(int[] ids);

    int updatePhoto(User user);
}
