package com.itlize.project.service;

import com.itlize.project.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User findByUsername(String username);

    List<User> findAllUsers();

    public boolean create(User user);
    public boolean delete(User user);
    public User get(String userName);
    public boolean update(String userName, User user);
    public void clear();

}
