package com.example.onlineshopsystem_spring.service.impl;

import com.example.onlineshopsystem_spring.model.User;
import com.example.onlineshopsystem_spring.repository.UserRepository;
import com.example.onlineshopsystem_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUserById(Integer id) {
       User user = userRepository.findById(id).orElse(null);
       if (user == null) {
           return false;
       }
       if(!user.getProducts().isEmpty()){
           return false;
       }
       userRepository.deleteById(id);
        return true;
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User adminChangeUser(User user) {
        User dbUser = userRepository.findById(user.getId())
                .orElseThrow(()-> new RuntimeException("User not found"));

        dbUser.setName(user.getName());
        dbUser.setSurname(user.getSurname());
        dbUser.setEmail(user.getEmail());
        if(user.getPassword() != null && !user.getPassword().isEmpty()){
            dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        dbUser.setType(user.getType());

        return userRepository.save(dbUser);
    }


}
