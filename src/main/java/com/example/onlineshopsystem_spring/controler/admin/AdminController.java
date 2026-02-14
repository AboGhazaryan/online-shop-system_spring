package com.example.onlineshopsystem_spring.controler.admin;

import com.example.onlineshopsystem_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("admin/home")
    public String adminHome(){
        return "adminPackage/adminHome";
    }


}
