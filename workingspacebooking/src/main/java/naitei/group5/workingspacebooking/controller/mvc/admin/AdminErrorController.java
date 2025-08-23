package naitei.group5.workingspacebooking.controller.mvc.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller xử lý các lỗi HTTP cho admin panel
 * Implements ErrorController để xử lý các lỗi một cách tập trung
 */
@Controller
public class AdminErrorController implements ErrorController {

    /**
     * Xử lý tất cả các lỗi HTTP và quyết định hiển thị trang lỗi phù hợp
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        // Thêm thông tin về thời gian lỗi
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Thêm thông tin về URL gây lỗi
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (requestUri != null) {
            model.addAttribute("requestUri", requestUri);
        }
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            // Kiểm tra xem có phải request từ admin panel không
            if (isAdminRequest(request)) {
                return handleAdminError(statusCode, request, model);
            } else {
                // Đối với request không phải admin, xử lý theo cách khác
                return handleGeneralError(statusCode, request, model);
            }
        }
        
        // Mặc định trả về lỗi 500 cho admin
        if (isAdminRequest(request)) {
            return "admin/error/500";
        }
        
        return "error"; // Generic error page cho non-admin
    }

    /**
     * Xử lý lỗi cho admin panel
     */
    private String handleAdminError(int statusCode, HttpServletRequest request, Model model) {
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        
        // Thêm chi tiết lỗi vào model (chỉ trong dev environment)
        if (exception != null && isDevelopmentMode()) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("stackTrace", getStackTrace(exception));
        }
        
        return switch (statusCode) {
            case 400 -> "admin/error/400";
            case 401 -> "admin/error/401";
            case 403 -> "admin/error/403";
            case 404 -> "admin/error/404";
            case 500 -> {
                model.addAttribute("error", errorMessage);
                yield "admin/error/500";
            }
            default -> {
                // Cho các mã lỗi khác, hiển thị trang default với thông tin lỗi
                model.addAttribute("status", statusCode);
                model.addAttribute("error", errorMessage);
                yield "admin/error/default";
            }
        };
    }

    /**
     * Xử lý lỗi cho request không phải admin
     */
    private String handleGeneralError(int statusCode, HttpServletRequest request, Model model) {
        // Đối với API endpoints, có thể trả về JSON
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        if (requestUri != null && requestUri.startsWith("/api/")) {
            // Để GlobalExceptionHandler xử lý
            return null;
        }
        
        // Đối với các request web khác, có thể có trang lỗi chung
        return switch (statusCode) {
            case 404 -> "error/404"; // Generic 404 page
            default -> "error/general"; // Generic error page
        };
    }

    /**
     * Kiểm tra xem request có phải từ admin panel không
     */
    private boolean isAdminRequest(HttpServletRequest request) {
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String referer = request.getHeader("Referer");
        
        // Kiểm tra URL hiện tại
        if (requestUri != null && requestUri.startsWith("/admin")) {
            return true;
        }
        
        // Kiểm tra referer (trang trước đó)
        if (referer != null && referer.contains("/admin")) {
            return true;
        }
        
        // Kiểm tra session hoặc cookie admin
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    // Nếu có token và request lỗi từ admin area
                    if (requestUri != null && (requestUri.startsWith("/admin") || requestUri.equals("/"))) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Kiểm tra xem có phải đang trong môi trường development không
     */
    private boolean isDevelopmentMode() {
        String activeProfile = System.getProperty("spring.profiles.active");
        return "dev".equals(activeProfile) || "development".equals(activeProfile);
    }

    /**
     * Lấy stack trace từ exception
     */
    private String getStackTrace(Exception exception) {
        if (exception == null) return "";
        
        var elements = exception.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(5, elements.length); i++) {
            sb.append(elements[i].toString()).append("\n");
        }
        return sb.toString();
    }
}
