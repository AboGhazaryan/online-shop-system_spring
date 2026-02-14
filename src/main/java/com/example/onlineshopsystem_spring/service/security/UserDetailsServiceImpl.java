package com.example.onlineshopsystem_spring.service.security;



import com.example.onlineshopsystem_spring.model.User;
import com.example.onlineshopsystem_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username)
                .orElseThrow(()->  new UsernameNotFoundException("User not found with Email: " + username));
        return new SpringUser(user);
    }
}
