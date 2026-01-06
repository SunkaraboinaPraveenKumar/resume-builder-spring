package in.praveenkumar.resume_builder.controller;


import in.praveenkumar.resume_builder.service.TemplatesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static in.praveenkumar.resume_builder.util.AppConstants.GET_TEMPLATES;

@RestController
@RequiredArgsConstructor
@RequestMapping(GET_TEMPLATES)
@Slf4j
public class TemplatesController {

    private final TemplatesService templatesService;

    @GetMapping
    public ResponseEntity<?> getTemplates(Authentication authentication){
        //Step 1: Call the service method
        Map<String, Object> response = templatesService.getTemplates(authentication.getPrincipal());
        //Step 2: Return the response
        return ResponseEntity.ok(response);
    }
}
