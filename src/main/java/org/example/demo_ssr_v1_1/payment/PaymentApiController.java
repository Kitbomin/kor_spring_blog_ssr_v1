package org.example.demo_ssr_v1_1.payment;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping("/api/payment/prepare")
    public ResponseEntity<?> preparePayment(@RequestBody PaymentRequest.PrepareDTO reqDTO, HttpSession session) {
        reqDTO.validate();

        // 누가 요청한지를 알아야 함
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }
        // 결제 서비스 생성 -> 결제 사전 요청 생성로직 (주문번호표 생성 및 중복 확인)
        PaymentResponse.PrepareDTO prepareDTO = paymentService.결제요청생성(sessionUser.getId(), reqDTO.getAmount());

        // JS -> 성공 응답 반환 처리
        return ResponseEntity.ok().body(Map.of(
                "merchant_uid", prepareDTO.getMerchantUid(),
                "amount", prepareDTO.getAmount(),
                "imp_key", prepareDTO.getImpKey()));
    }
}
