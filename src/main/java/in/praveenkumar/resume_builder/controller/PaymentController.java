package in.praveenkumar.resume_builder.controller;

import com.razorpay.RazorpayException;
import in.praveenkumar.resume_builder.documents.Payment;
import in.praveenkumar.resume_builder.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static in.praveenkumar.resume_builder.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYMENT)
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(CREATE_ORDER)
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> request,
                                         Authentication authentication) throws RazorpayException {
        //Step 1: Validate the request
        String planType = request.get("planType");
        if(!planType.equalsIgnoreCase("premium")){
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Plan Type"));
        }

        //Step 2: Call the service method
        Payment payment = paymentService.createOrder(authentication.getPrincipal(), planType);

        //Step 3: Prepare the response object
        Map<String, Object> response = Map.of(
                "orderId", payment.getRazorpayOrderId(),
                "amount", payment.getAmount(),
                "currency", payment.getCurrency(),
                "receipt", payment.getReceipt()
        );

        //Step 4: Return the response
        return ResponseEntity.ok(response);
    }


    @PostMapping(VERIFY)
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) throws RazorpayException {
        //Step 1: Validate the request
        String razorpayOrderId = request.get("razorpay_order_id");
        String razorpayPaymentId = request.get("razorpay_payment_id");
        String razorpaySignature = request.get("razorpay_signature");

        if(Objects.isNull(razorpayPaymentId)||Objects.isNull(razorpaySignature)||Objects.isNull(razorpayOrderId)){
            return ResponseEntity.badRequest().body(Map.of("message","Missing Required payment parameters"));
        }
        //Step 2: Call the service method
        boolean isValid = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if(isValid){
            return ResponseEntity.ok(Map.of(
                    "message","Payment Verified Successfully",
                    "status","Success"
            ));
        }
        else{
            return ResponseEntity.badRequest().body(Map.of("message","Payment Verification failed!"));
        }
    }

    @GetMapping(HISTORY)
    public ResponseEntity<?> getPaymentHistory(Authentication authentication){
        //Step 1: Call the service method
        List<Payment> payments = paymentService.getUserPayments(authentication.getPrincipal());

        //Step 2: Return the response
        return ResponseEntity.ok(payments);
    }

    @GetMapping(GET_ORDER)
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
        //Step 1: call the service method
        Payment paymentDetails = paymentService.getPaymentDetails(orderId);
        //Step 2: return response
        return ResponseEntity.ok(paymentDetails);
    }
}
