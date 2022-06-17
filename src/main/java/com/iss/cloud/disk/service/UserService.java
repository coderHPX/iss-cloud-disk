package com.iss.cloud.disk.service;

import com.iss.cloud.disk.model.ResultModel;
import com.iss.cloud.disk.model.Pagination;
import com.iss.cloud.disk.model.User;

import java.util.List;


public interface UserService {

    User login(String username, String password);

    Pagination<User> getUsers(Pagination page);

    List<User> chooseUser(int id);

    User getUser(int id);

    ResultModel insertUser(User user);

    ResultModel register(User user);

    ResultModel updateUser(User user);

    ResultModel delete(int[] ids);

    ResultModel updatePhoto(User user);
}
