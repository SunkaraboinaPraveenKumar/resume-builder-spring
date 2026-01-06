package in.praveenkumar.resume_builder.controller;

import in.praveenkumar.resume_builder.dto.AuthResponse;
import in.praveenkumar.resume_builder.dto.LoginRequest;
import in.praveenkumar.resume_builder.dto.RegisterRequest;
import in.praveenkumar.resume_builder.service.AuthService;
import in.praveenkumar.resume_builder.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static in.praveenkumar.resume_builder.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Inside AuthController - register(): {}", request);
        AuthResponse response = authService.register(request);
        log.info("Response from service: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("Inside AuthController - verifyEmail(): {}",token);
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Email Verified successfully..."));
    }

    @PostMapping(IMAGE_UPLOAD)
    public ResponseEntity<?> uploadImage(@RequestPart("image")MultipartFile file) throws IOException {
        log.info("Inside AuthController - uploadImage()");
        Map<String, String> response = fileUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        log.info("Inside AuthController - login()");
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public String testValidationToken(){
        return "Token validation is working";
    }

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> recentVerification(@RequestBody Map<String, String> body){
        //Step 1: Get the email from request
        String email = body.get("email");

        //Step 2: Add the validations
        if(Objects.isNull(email)){
            return ResponseEntity.badRequest().body(Map.of("message","Email is required"));
        }

        //Step 3: Call the service method to resend verification link
        authService.resendVerification(email);

        //Step 4: Return the response
        return ResponseEntity.ok(Map.of("success",true,"message","Verification email sent"));
    }


    @GetMapping(GET_PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication){
        //Step 1: Get the principal object
        Object principalObject =  authentication.getPrincipal();

        //Step 2: call the service method
        AuthResponse currentProfile = authService.getProfile(principalObject);

        //Step 3: return the response
        return ResponseEntity.ok(currentProfile);
    }
}
