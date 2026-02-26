package com.example.onlineshopsystem_spring.controler.user;

import com.example.onlineshopsystem_spring.model.Category;
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
import org.springframework.data.domain.Sort;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CommentService commentService;


    @GetMapping("/search/products")
    public String searchProducts(ModelMap modelMap,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer categoryId,
                                 @RequestParam(required = false) Optional<Integer> page,
                                 @RequestParam(required = false) Optional<Integer> size,
                                 @AuthenticationPrincipal SpringUser springUser) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<Product> products;

        if (keyword != null && !keyword.isBlank() && categoryId != null) {
            products = productService.searchProductByCategoryIdAndTitle(categoryId, keyword, pageRequest);

            Category category = categoryService.findById(categoryId);
            modelMap.addAttribute("selectedCategory", category);

        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId, pageRequest);

            Category category = categoryService.findById(categoryId);
            modelMap.addAttribute("selectedCategory", category);
        } else {
            products = productService.findAll(pageRequest);
        }

        int totalPages = products.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().toList();
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }

        if (products.isEmpty()) {
            modelMap.addAttribute("infoMessage", "No products Found");
        }

        log.info("quantity of imported goods: {}, currently logged in user: {}", products.getNumberOfElements(), springUser.getUser().getName());
        modelMap.addAttribute("products", products);
        modelMap.addAttribute("keyword", keyword);
        modelMap.addAttribute("categoryId", categoryId);

        return "products";
    }


    @GetMapping("/product/add")
    public String addProduct(ModelMap modelMap) {
        modelMap.addAttribute("categories", categoryService.findAll());
        return "addProduct";
    }

    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile multipartFile, @AuthenticationPrincipal SpringUser springUser) {
        product.setUser(springUser.getUser());
        productService.save(product, multipartFile, springUser);
        return "redirect:/categories";
    }


    @GetMapping("/product/change")
    public String changeProduct(@RequestParam("productId") Integer id, ModelMap modelMap, Principal principal) {
        Product product = productService.findById(id);

        if (!product.getUser().getEmail().equals(principal.getName())) {
            throw new RuntimeException("You are not allowed to edit this product");
        }

        modelMap.addAttribute("product", product);
        modelMap.addAttribute("categories", categoryService.findAll());

        return "changeProduct";
    }

    @PostMapping("/product/change")
    public String changeProduct(@ModelAttribute Product product,
                                @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                @AuthenticationPrincipal SpringUser springUser) {

        productService.changeProduct(product, multipartFile, springUser);
        return "redirect:/product/details?productId=" + product.getId();
    }


    @GetMapping("/product/details")
    public String productDetails(@RequestParam Integer productId, ModelMap modelMap, @AuthenticationPrincipal SpringUser springUser) {
        Product product = productService.findById(productId);
        List<Comment> comments = commentService.findByProductId(productId);
        modelMap.addAttribute("SpringUser", springUser.getUser());
        modelMap.addAttribute("product", product);
        modelMap.addAttribute("comments", comments);
        return "productsDetails";
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam Integer productId, Principal principal) {
        Product product = productService.findById(productId);
        if (!product.getUser().getEmail().equals(principal.getName())) {
            throw new RuntimeException("You are not allowed to delete this product");
        }
        productService.deleteById(productId);
        return "redirect:/search/products?categoryId=" + product.getCategory().getId();
    }

    @PostMapping("/add/comment")
    public String addComment(@RequestParam String comment,
                             @RequestParam Integer productId, Principal principal) {

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        commentService.addComment(comment, productId, user);

        return "redirect:/product/details?productId=" + productId;
    }

}
