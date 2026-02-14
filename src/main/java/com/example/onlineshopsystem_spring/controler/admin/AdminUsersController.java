package com.example.onlineshopsystem_spring.controler.admin;

import com.example.onlineshopsystem_spring.model.User;
import com.example.onlineshopsystem_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminUsersController {
    private final UserService userService;

    @GetMapping("/admin/users")
    public String users(ModelMap modelMap) {
        List<User> users = userService.findAll();
        modelMap.addAttribute("users", users);
        return "adminPackage/users";
    }

    @GetMapping("/admin/user/add")
    public String addUserPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "adminPackage/addUser";
    }

    @PostMapping("/admin/user/add")
    public String  register(@ModelAttribute User user) {
        if(userService.findByEmail(user.getEmail()).isPresent()) {
            return "redirect:/admin/user/add?msg=Email already exists!";
        }
        userService.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/user/change")
    public String changeUser(@RequestParam("userId")Integer id, ModelMap modelMap) {

        User user = userService.findById(id);
        modelMap.addAttribute("user", user);
        return "adminPackage/changeUser";

    }

    @PostMapping("/admin/user/change")
    public String changeUser(@ModelAttribute User user) {
        userService.adminChangeUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/user/delete")
    public String deleteUser(@RequestParam Integer userId, RedirectAttributes redirectAttributes) {
        boolean deleted = userService.deleteUserById(userId);
        if(!deleted) {
            redirectAttributes.addFlashAttribute("errorMessage","The user cannot be deleted because it is already owned by a product");
        }else{
            redirectAttributes.addFlashAttribute("infoMessage","User has been deleted successfully.");
        }
        return "redirect:/admin/users";
    }
}
