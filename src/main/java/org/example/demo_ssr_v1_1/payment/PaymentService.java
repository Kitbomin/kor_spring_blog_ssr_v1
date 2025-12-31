package org.example.demo_ssr_v1_1.payment;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${portone.imp-key}")
    private String impKey;

    @Value("${portone.imp-secret}")
    private String impSecret;

    // 1. 사전 결제 요청
    //  - 프론트엔드가 결제창을 띄우기 전, 서버에서 먼저 고유한 '주문번호(merchantUid)' 를 생성해 내려주기 위함
    //  - 중복 결제 방지 목적 | 금액 위변조 방지 목적
    @Transactional
    public PaymentResponse.PrepareDTO 결제요청생성(Long userId, Integer amount) {
        if (!userRepository.existsById(userId)) {
            throw new Exception404("유저를 못찾았어요");
        }
        
        // 주문 번호 생성 (UUID 사용, if 중복 시 재 생성 로직 추가)
        String merchantUid = generateMerchantUid(userId);
        while (paymentRepository.existsByMerchantUid(merchantUid)) {
            merchantUid = generateMerchantUid(userId);
        }

        return new PaymentResponse.PrepareDTO(merchantUid, amount, impKey);

    }

    // 주문번호 생성 유틸 키
    // 형식: point_{userId}+{timeStamp}_{uuid}
    private static String generateMerchantUid(Long userId) {
        return "point_" + userId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }


}
