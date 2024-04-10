/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package aptech.project.coffeeClient.controller.admin;

import aptech.project.coffeeClient.dto.LoginDto;
import aptech.project.coffeeClient.dto.PageDto;
import aptech.project.coffeeClient.dto.UserDto;
import aptech.project.coffeeClient.models.User;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ASUS
 */
@Controller
@SessionAttributes("user")
public class LoginController {

    RestTemplate rest = new RestTemplate();
    private final String apiUrl = "http://localhost:9999/api/users/";
    
    @ModelAttribute("user")
    public UserDto userDto(){
        return new UserDto();
    }
    
    
    @GetMapping("/home")
    public String home(Model model) {
        return "/admin/dashboard";
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        return "/admin/login/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginDto") LoginDto loginDto, Model model) {
        try {
            // Kiểm tra xem các trường thông tin đăng nhập có được điền đầy đủ không
            
            if (loginDto.getPassword().isEmpty()) {
                return "redirect:/login?password";
            }

            // Gọi API login
            ResponseEntity<UserDto> response = rest.postForEntity(apiUrl + "login", loginDto, UserDto.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                UserDto loggedInUser = response.getBody();
                
                if (loggedInUser.getRole_id() == 1 || loggedInUser.getRole_id() == 2) {
                    model.addAttribute("user", loggedInUser);
                    return "admin/dashboard";
                } else if (loggedInUser.getRole_id() == 3) {
                    return "user/layout";
                }
            }
        } catch (Exception ex) {
            // Xử lý lỗi chung
            model.addAttribute("error", "An error occurred");
            return "/admin/login/login";
        }
        // Đăng nhập không thành công, chuyển hướng tới trang đăng nhập với thông báo lỗi
        
        return "redirect:/login?error";
    }

    @GetMapping("/register")
    public String registerUser(Model model) {
        model.addAttribute("newUser", new UserDto());
        return "/admin/login/register";
    }

    @PostMapping("/register")
    public String registerNewCustomer(@ModelAttribute UserDto newUser, Model model) {
        try {
            // Kiểm tra xem các trường thông tin bắt buộc có được điền đầy đủ không

            if (newUser.getUsername().isEmpty()) {
                return "redirect:/register?name";
            }
            if (newUser.getEmail().isEmpty()) {
                return "redirect:/register?email";
            }
            if (newUser.getPassword().isEmpty()) {
                return "redirect:/register?password";
            }
            // Kiểm tra xem mật khẩu và mật khẩu lặp lại có khớp nhau không
            if (!newUser.getPassword().equals(newUser.getRepeatpassword())) {
                return "redirect:/register?repeatpassword";
            }

            // Gọi API đăng ký
            ResponseEntity<UserDto> responseEntity = rest.postForEntity(
                    apiUrl + "register",
                    newUser,
                    UserDto.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // Đăng ký thành công
                return "redirect:/register?success";
            } 
             else {
                // Xử lý lỗi chung
               
                return "redirect:/register?emailexist";
            }
        } catch (Exception ex) {
            // Xử lý lỗi từ API
           
            return "redirect:/register?emailexist";
        }
    }
     @GetMapping("/change-password")
    public String changepassword(Model model) {
        model.addAttribute("user", new UserDto());
        return "/admin/user/change-pasword";
    }
    @PostMapping("/change-password")
public String changePassword(@ModelAttribute("user") UserDto userDto,
        @RequestParam String currentPassword,
        @RequestParam String newPassword,
        Model model) {
    try {
        // Tạo Map chứa thông tin cần thiết
       HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("email", userDto.getEmail());
        requestMap.put("currentPassword", currentPassword);
        requestMap.put("newPassword", newPassword);

        // Gọi API change-password
        ResponseEntity<String> responseEntity = rest.postForEntity(
                apiUrl + "change-password",
                requestMap,
                String.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Thay đổi mật khẩu thành công
            return "redirect:/change-password?success";
        } else {
            // Xử lý lỗi thất bại khi thay đổi mật khẩu
            model.addAttribute("error", "Failed to change password");
            return "/admin/user/change-pasword";
        }
    } catch (HttpClientErrorException ex) {
        // Xử lý lỗi từ API
        model.addAttribute("error", ex.getMessage());
        return "/admin/user/change-pasword";
    } catch (Exception ex) {
        // Xử lý lỗi chung
        model.addAttribute("error", "An error occurred");
        return "/admin/user/change-pasword";
    }
}

    

}
