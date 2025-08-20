package naitei.group5.workingspacebooking.controller.admin;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.request.LoginRequest;
import naitei.group5.workingspacebooking.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class AdminAuthController {

    private final AuthService authService;

    @GetMapping("/")
    public String root() {
        return "forward:/admin/login";
    }

    @GetMapping("/admin/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "admin/auth/login";
    }

    @PostMapping("/admin/login")
    public String doLogin(@Valid @ModelAttribute("loginRequest") LoginRequest form,
                          HttpServletResponse response,
                          Model model,
                          HttpServletRequest request) {
        try {
            var jwt = authService.login(form, request);
            if (!"admin".equals(jwt.getRole())) {
                model.addAttribute("loginError", "auth.admin.role.required");
                return "admin/auth/login";
            }
            
            Cookie accessCookie = new Cookie("ACCESS_TOKEN", jwt.getAccessToken());
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            // accessCookie.setSecure(true); // Bật khi dùng HTTPS
            
            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", jwt.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            // refreshCookie.setSecure(true); // Bật khi dùng HTTPS
            
            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
            
            return "redirect:/admin/dashboard";
        } catch (Exception ex) {
            model.addAttribute("loginError", "auth.login.invalid");
            return "admin/auth/login";
        }
    }

    @PostMapping("/admin/logout")
    public String logout(@CookieValue(value = "ACCESS_TOKEN", required = false) String accessToken,
                         HttpServletResponse response) {
        if (accessToken != null && !accessToken.isBlank()) {
            authService.logout(accessToken);
        }
        
        // Xóa cookies
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", "");
        accessCookie.setMaxAge(0);
        accessCookie.setPath("/");
        
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", "");
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        
        return "redirect:/admin/login";
    }
}
