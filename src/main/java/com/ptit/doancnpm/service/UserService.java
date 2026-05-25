package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.dao.UserDao;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this(new UserDao());
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public Optional<User> getUserById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("User id must be positive");
        }
        return userDao.findById(id);
    }

    public void createUser(User user) {
        if (isBlank(user.getUsername()) || isBlank(user.getFullName())) {
            throw new IllegalArgumentException("Username and full name are required");
        }
        userDao.create(user);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
