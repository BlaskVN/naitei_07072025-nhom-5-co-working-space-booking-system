package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.utils.PasswordUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import naitei.group5.workingspacebooking.dto.request.RegisterRequest;
import naitei.group5.workingspacebooking.dto.response.RegisterResponse;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.EmailService;
import naitei.group5.workingspacebooking.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    @Override
    public RegisterResponse registerRenter(RegisterRequest request) {
        return registerWithRole(request, UserRole.renter);
    }

    @Override
    public RegisterResponse registerOwner(RegisterRequest request) {
        return registerWithRole(request, UserRole.pending_owner);
    }

    private RegisterResponse registerWithRole(RegisterRequest request, UserRole role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String rawPassword = PasswordUtil.generateRandomPassword(8);

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(encodedPassword)
                .role(role) // role được set từ service
                .build();

        // Lưu DB
        userRepository.save(user);

        emailService.sendSimpleMessage(
                user.getEmail(),
                "Your Account Password",
                "Hello " + user.getName() + ",\n\n" +
                        "Your account has been created successfully.\n" +
                        "Your password is: " + rawPassword + "\n\n" +
                        "Please change it after logging in."
        );

        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
