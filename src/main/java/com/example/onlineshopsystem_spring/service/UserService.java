package com.example.onlineshopsystem_spring.service;

import com.example.onlineshopsystem_spring.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void save(User user);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    boolean deleteUserById(Integer id);

    User findById(Integer id);

    User adminChangeUser(User user);
}
