package com.example.onlineshopsystem_spring.controler.admin;

import com.example.onlineshopsystem_spring.service.security.SpringUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("admin/home")
    public String adminHome(@AuthenticationPrincipal SpringUser springUser, ModelMap modelMap) {
        if(springUser != null) {
            modelMap.addAttribute("user", springUser.getUser());
        }
        return "adminPackage/adminHome";
    }


}
