package naitei.group5.workingspacebooking.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Global exception handler xử lý các exception toàn cục
 * Phân biệt giữa API requests và admin web requests
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý ApiException - chỉ dành cho API requests
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));
    }

    /**
     * Xử lý AccessDeniedException - phân biệt API và Admin
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(ErrorCode.FORBIDDEN.getStatus())
                    .body(ErrorResponse.from(ErrorCode.FORBIDDEN));
        } else {
            // Cho admin - redirect đến trang 403
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("admin/error/403");
            modelAndView.addObject("error", ex.getMessage());
            return modelAndView;
        }
    }

    /**
     * Xử lý InsufficientAuthenticationException - chưa đăng nhập
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public Object handleAuthenticationException(InsufficientAuthenticationException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(ErrorCode.UNAUTHORIZED.getStatus())
                    .body(ErrorResponse.from(ErrorCode.UNAUTHORIZED));
        } else {
            // Cho admin - redirect đến trang login
            return "redirect:/admin/login";
        }
    }

    /**
     * Xử lý IllegalArgumentException - Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(ErrorCode.BAD_REQUEST.getStatus())
                    .body(ErrorResponse.of(
                            ErrorCode.BAD_REQUEST.getStatus().value(),
                            ErrorCode.BAD_REQUEST.name(),
                            ex.getMessage()
                    ));
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("admin/error/400");
            modelAndView.addObject("error", ex.getMessage());
            return modelAndView;
        }
    }

    /**
     * Xử lý Exception tổng quát
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(ErrorCode.INTERNAL_ERROR.getStatus())
                    .body(ErrorResponse.from(ErrorCode.INTERNAL_ERROR));
        } else {
            // Cho admin - hiển thị trang 500
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("admin/error/500");
            modelAndView.addObject("error", ex.getMessage());
            
            // Thêm thời gian lỗi
            modelAndView.addObject("timestamp", java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return modelAndView;
        }
    }

    /**
     * Kiểm tra xem request có phải là API request không
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        
        // Kiểm tra URL pattern
        if (requestURI.startsWith("/api/")) {
            return true;
        }
        
        // Kiểm tra Accept header
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            return true;
        }
        
        // Kiểm tra Content-Type
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            return true;
        }
        
        return false;
    }
}
