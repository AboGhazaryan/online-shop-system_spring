package com.example.onlineshopsystem_spring.controler;


import com.example.onlineshopsystem_spring.model.User;
import com.example.onlineshopsystem_spring.model.UserType;
import com.example.onlineshopsystem_spring.service.UserService;
import com.example.onlineshopsystem_spring.service.security.SpringUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller()
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;

    @Value("${product.image.directory.path}")
    private String imageDirectoryPath;

    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal SpringUser userDetails, ModelMap modelMap) {
        if(userDetails != null) {
            modelMap.addAttribute("user", userDetails.getUser());
        }
        return "home";
    }

    @GetMapping("/successLogin")
    public String successLogin(@AuthenticationPrincipal SpringUser springUser) {
        if(springUser != null && springUser.getUser().getType() == UserType.ADMIN) {
            return "redirect:/admin/home";
        }else{
            return "redirect:/";
        }
    }


    @GetMapping("/loginPage")
    public String login(@RequestParam(required = false) String msg, ModelMap modelMap){
        modelMap.addAttribute("msg", msg);
        return "loginPage";
    }
    @GetMapping("/registerPage")
    public String registerPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "registerPage";
    }

    @PostMapping("/register")
    public String  register(@ModelAttribute User user) {
        if(userService.findByEmail(user.getEmail()).isPresent()) {
            return "redirect:/register?msg=Email already exists!";
        }
        userService.save(user);
        return "redirect:/login?msg=Registered Successfully! Please login again!";
    }

    @GetMapping("/image/get")
    public ResponseEntity <byte[]> getImage(@RequestParam("image") String picName) {
        File file = new File(imageDirectoryPath + picName);

        if(!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] imageBytes = FileUtils.readFileToByteArray(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file.toPath()))
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
