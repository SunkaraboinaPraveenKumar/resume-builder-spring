package in.praveenkumar.resume_builder.service;

import in.praveenkumar.resume_builder.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.praveenkumar.resume_builder.util.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplatesService {

    private final AuthService authService;

    public Map<String, Object> getTemplates(Object principal){
        //Step 1: Get the current profile
        AuthResponse response = authService.getProfile(principal);

        //Step 2: Identify the available templates based on subscription type
        List<String> availableTemplates;

        boolean isPremium = PREMIUM.equalsIgnoreCase(response.getSubscriptionPlan());

        if(isPremium){
            availableTemplates = List.of("01","02","03");
        }
        else{
            availableTemplates = List.of("01");
        }

        //Step 3: Add the data into map
        Map<String, Object> restrictions = new HashMap<>();

        restrictions.put("availableTemplates", availableTemplates);

        restrictions.put("allTemplates", List.of("01","02","03"));

        restrictions.put("subscriptionPlan", response.getSubscriptionPlan());

        restrictions.put("isPremium", isPremium);

        //Step 4: Return response
        return restrictions;
    }
}
