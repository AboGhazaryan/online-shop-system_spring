package com.example.onlineshopsystem_spring.controler.admin;

import com.example.onlineshopsystem_spring.model.Comment;
import com.example.onlineshopsystem_spring.model.Product;
import com.example.onlineshopsystem_spring.model.User;
import com.example.onlineshopsystem_spring.service.CategoryService;
import com.example.onlineshopsystem_spring.service.CommentService;
import com.example.onlineshopsystem_spring.service.ProductService;
import com.example.onlineshopsystem_spring.service.UserService;
import com.example.onlineshopsystem_spring.service.security.SpringUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping("/admin/products")
    public String adminProducts(ModelMap modelMap, @RequestParam(required = false)String keyword,
                                @RequestParam(required = false)Integer categoryId,
                                @RequestParam(required = false) Optional<Integer> page,
                                @RequestParam(required = false) Optional<Integer> size,
                                @AuthenticationPrincipal SpringUser springUser) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);
        Page<Product> products;


        if(keyword !=null && !keyword.isBlank() && categoryId != null) {
            products = productService.searchProductAndFilter(keyword,categoryId,pageRequest);
        }else if(keyword != null && !keyword.isBlank()) {
            products = productService.searchProductsByTitleAndUsername(keyword,pageRequest);
        }else if(categoryId != null) {
            products = productService.findByCategoryId(categoryId,pageRequest);
        }else {
            products = productService.findAll(pageRequest);
        }

        int totalPages = products.getTotalPages();
        if(totalPages > 0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().toList();
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }

        if (products.isEmpty()) {
            modelMap.addAttribute("infoMessage", "No products Found");
        }

        log.info("quantity of imported goods: {}, currently logged in user: {}",products.getNumberOfElements(),springUser.getUser().getName());
        modelMap.addAttribute("products", products);
        modelMap.addAttribute("selectCategory", categoryId);
        modelMap.addAttribute("keyword", keyword);
        modelMap.addAttribute("categories", categoryService.findAll());


        return "adminPackage/adminProducts";
    }

    @GetMapping("/admin/product/add")
    public String addProduct(ModelMap modelMap) {
        modelMap.addAttribute("categories",categoryService.findAll());
        modelMap.addAttribute("users",userService.findAll());
        return "adminPackage/addProduct";
    }

    @PostMapping("/admin/product/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile multipartFile,
                             @AuthenticationPrincipal SpringUser springUser) {
        productService.save(product,multipartFile,springUser);
        return "redirect:/admin/products";
    }


    @GetMapping("/admin/product/change")
    public String changeProduct(@RequestParam("productId")Integer id, ModelMap modelMap) {

        Product product = productService.findById(id);
        modelMap.addAttribute("product",product);
        modelMap.addAttribute("categories",categoryService.findAll());
        modelMap.addAttribute("users",userService.findAll());
        return "adminPackage/changeProduct";
    }

    @PostMapping("/admin/product/change")
    public String changeProduct(@ModelAttribute Product product,
                                @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                @AuthenticationPrincipal SpringUser springUser) {
        productService.changeProduct(product,multipartFile,springUser);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/product/delete")
    public String deleteProduct(@RequestParam Integer productId) {
        productService.deleteById(productId);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/product/details")
    public String productDetails(@RequestParam Integer productId, ModelMap modelMap) {
        Product product = productService.findById(productId);
        List<Comment> comments = commentService.findByProductId(productId);
        modelMap.addAttribute("product",product);
        modelMap.addAttribute("comments",comments);
        return "adminPackage/adminProductDetails";
    }

    @PostMapping("/admin/add/comment")
    public String addComment(@RequestParam String comment,
                             @RequestParam Integer productId, Principal principal) {

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() ->{
                    log.error("User not found with email: {}", principal.getName());
                    return new RuntimeException("User not found ");
                });

        log.info("this user։ {}, added a comment",user.getName());
        commentService.addComment(comment, productId, user);

        return "redirect:/admin/product/details?productId=" + productId;
    }

    @PostMapping("/admin/comment/delete")
    public String deleteComment(@RequestParam Integer commentId,
                                @RequestParam Integer productId,
                                Principal principal) {

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", principal.getName());
                    return new RuntimeException("User not found");
                });

        commentService.deleteComment(commentId, user);

        return "redirect:/admin/product/details?productId=" + productId;
    }


}
