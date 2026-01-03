package in.praveenkumar.resume_builder.service;

import in.praveenkumar.resume_builder.documents.User;
import in.praveenkumar.resume_builder.dto.AuthResponse;
import in.praveenkumar.resume_builder.dto.RegisterRequest;
import in.praveenkumar.resume_builder.exception.ResourceExistsException;
import in.praveenkumar.resume_builder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.base.url:http://localhost:8000}")
    private String appBaseUrl;



    public AuthResponse register(RegisterRequest request){
        log.info("Inside AuthService: register() {}", request);

        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceExistsException("User Already Exists with this email");
        }

        User newUser = toDocument(request);

        userRepository.save(newUser);

        //TODO: send verification email
        sendVerificationEmail(newUser);

        return toResponse(newUser);
    }

    private void sendVerificationEmail(User newUser) {
        try{
            String link = appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html = "<div style='font-family:sans-serif'>" +
                    "<h2>Hey " + newUser.getName() + ", please confirm your email to activate your account</h2>" +
                    "<p><a href='" + link +
                    "' style='display:inline-block;padding:10px 16px;background:#6366f1;color:#fff;text-decoration:none;border-radius:4px'>Confirm Email</a></p>" +
                    "<p>Or copy this link: " + link + "</p>" +
                    "<p>This link expires in 24 hours.</p>" +
                    "</div>";

            emailService.sendHtmlEmail(newUser.getEmail(), "Verify your Email", html);
        }catch(Exception e){
            throw new RuntimeException("Failed to send verification email: "+ e.getMessage());
        }
    }

    private AuthResponse toResponse(User newUser){
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .emailVerified(newUser.isEmailVerified())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    private User toDocument(RegisterRequest request){
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .profileImageUrl(request.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }
}
