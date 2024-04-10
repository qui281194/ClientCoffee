/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package aptech.project.coffeeClient.controller.admin;

import aptech.project.coffeeClient.dto.PageDto;
import aptech.project.coffeeClient.dto.UserDto;
import aptech.project.coffeeClient.models.Category;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ASUS
 */
@Controller
@SessionAttributes("user")
@RequestMapping("/admin/category")
public class CategoryController {
    RestTemplate rest = new RestTemplate();
    private final String apiUrl = "http://localhost:9999/api/category/";
    
    @ModelAttribute("user")
    public UserDto userDto(){
        return new UserDto();
    }
   @GetMapping("/all")
    public String function_allCategory(Model model, @RequestParam(defaultValue = "1") Integer pageNo) {
        ResponseEntity<PageDto<Category>> response = rest.exchange(
                apiUrl + "all?pageNo=" + pageNo,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageDto<Category>>() {
        }
        );

        if (response.getStatusCode() == HttpStatus.OK) {

            PageDto<Category> pageDto = response.getBody();

            model.addAttribute("category", pageDto.getContent());
            model.addAttribute("totalPages", pageDto.getTotalPages());
            model.addAttribute("totalElements", pageDto.getTotalElements());
            model.addAttribute("currentPage", pageNo);
            //lấy ngày giờ hiện tại
            model.addAttribute("msg","Today's : "+ new Date());
            // Calculate page numbers
            List<Integer> pageNumbers = IntStream.rangeClosed(1, pageDto.getTotalPages()).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            return "admin/category/index";
        } else {
            // Handle error here
            return "error";
        }
    }
    @GetMapping("/create")
    public String showCreateCategoryForm(Model model) {
       
        model.addAttribute("category", new Category() );
        
        return "admin/category/create"; // Trả về view để hiển thị form tạo người dùng
    }
    
    // Phương thức createCategory
@PostMapping("/create")
public String createCategory(@ModelAttribute("category") Category category, Model model) {
    // Gửi yêu cầu POST tới API
    ResponseEntity<Category> response = rest.postForEntity(
            apiUrl + "create",
            category,
            Category.class
    );

    // Kiểm tra phản hồi từ API
    if (response.getStatusCode() == HttpStatus.CREATED) {
        // Nếu tạo thành công, redirect hoặc hiển thị thông báo thành công
        return "redirect:/admin/category/all"; // Hoặc trả về một trang thông báo thành công phù hợp
    } else {
        // Nếu có lỗi, xử lý lỗi phù hợp
        return "error"; // Hoặc trả về một trang lỗi phù hợp
    }
}

    
}
