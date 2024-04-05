/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package aptech.project.coffeeClient.controller.admin;

import aptech.project.coffeeClient.dto.PageDto;
import aptech.project.coffeeClient.dto.RoleDto;
import aptech.project.coffeeClient.dto.UserDto;
import jakarta.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author ASUS
 */
@Controller
@SessionAttributes("user")
@RequestMapping("/admin/user")

public class AdminController {

    @Value("${upload.path}")
    private String filterUpload;

    RestTemplate rest = new RestTemplate();
    private final String apiUrl = "http://localhost:9999/api/users/";

    @ModelAttribute("user")
    public UserDto userDto() {
        return new UserDto();
    }

    @GetMapping("/all")
    public String function_allUser(Model model, @RequestParam(defaultValue = "1") Integer pageNo) {
        ResponseEntity<PageDto<UserDto>> response = rest.exchange(
                apiUrl + "all?pageNo=" + pageNo,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageDto<UserDto>>() {
        }
        );

        if (response.getStatusCode() == HttpStatus.OK) {

            PageDto<UserDto> pageDto = response.getBody();

            model.addAttribute("users", pageDto.getContent());
            model.addAttribute("totalPages", pageDto.getTotalPages());
            model.addAttribute("totalElements", pageDto.getTotalElements());
            model.addAttribute("currentPage", pageNo);
            // Calculate page numbers
            List<Integer> pageNumbers = IntStream.rangeClosed(1, pageDto.getTotalPages()).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            return "admin/user/index";
        } else {
            // Handle error here
            return "error";
        }
    }

    @GetMapping("/details/{email}")
    public String function_details(@PathVariable("email") String email, Model model) {
        ResponseEntity<UserDto> response = rest.getForEntity(apiUrl + "details/" + email, UserDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            UserDto userDetails = response.getBody();
            model.addAttribute("userDetails", userDetails);
            return "admin/user/details"; // Trả về view để hiển thị thông tin chi tiết người dùng
        } else {
            // Xử lý lỗi ở đây nếu cần
            return "error";
        }
    }

    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        // Khởi tạo một đối tượng UserDto để lưu trữ dữ liệu từ form
        UserDto newUserDto = new UserDto();
        model.addAttribute("userDto", newUserDto);
        model.addAttribute("newRoleDto", new RoleDto());
        return "admin/user/create"; // Trả về view để hiển thị form tạo người dùng
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("userDto") UserDto newUserDto, RedirectAttributes redirectAttributes) {
        try {
            
            // Gọi API tạo người dùng
            ResponseEntity<UserDto> response = rest.postForEntity(apiUrl + "create", newUserDto, UserDto.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                UserDto createdUser = response.getBody();
                // Redirect về trang chi tiết người dùng sau khi tạo thành công
                return "redirect:/admin/user/all";
            } else {
                // Xử lý lỗi nếu có
                
                return "redirect:/admin/user/create?email";
            }
        } catch (Exception ex) {
            // Xử lý lỗi nếu có
            
            return "redirect:/admin/user/create?email";
        }
    }

    @GetMapping("/detailsById/{id}")
    public String function_detailsById(@PathVariable("id") int id, Model model) {
        ResponseEntity<UserDto> response = rest.getForEntity(apiUrl + "detailsById/" + id, UserDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            UserDto userDetails = response.getBody();
            model.addAttribute("userDetails", userDetails);
            return "admin/user/detailsindex"; // Trả về view để hiển thị thông tin chi tiết người dùng
        } else {
            // Xử lý lỗi ở đây nếu cần
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") int id, Model model) {
        try {
            // Gọi API để lấy thông tin người dùng cần chỉnh sửa
            ResponseEntity<UserDto> response = rest.getForEntity(apiUrl + "edit/" + id, UserDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                UserDto userDto = response.getBody();
                model.addAttribute("userDto", userDto);
                return "admin/user/edit"; // Trả về view để hiển thị form chỉnh sửa thông tin người dùng
            } else {
                // Xử lý lỗi nếu không tìm thấy người dùng
                return "error";
            }
        } catch (Exception ex) {
            // Xử lý lỗi nếu có
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable("id") int id, @ModelAttribute("userDto") @Valid UserDto updatedUserDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            return "admin/user/edit";
        }

        try {
            ResponseEntity<UserDto> response = rest.exchange(
                    apiUrl + "edit/" + id, HttpMethod.PUT, new HttpEntity<>(updatedUserDto), UserDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/admin/user/all";
            } else {
                redirectAttributes.addFlashAttribute("error", "Error updating user");
                return "redirect:/admin/user/edit/" + id;
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + ex.getMessage());
            return "redirect:/admin/user/edit/" + id;
        }
    }

}
