package org.example.demo_ssr_v1_1.payment;

import lombok.Data;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception400;

public class PaymentRequest {

    // 결제 요청 생성 DTO
    @Data
    public static class PrepareDTO {
        private Integer amount; // 충전할 금액

        public void validate() {
            if (amount == null || amount < 0)  {
                throw new Exception400("충전할 포인트는 0보다 커야합니다.");
            }

            // 최소 | 최대 금액 제한
            if (amount < 100) {
                throw new Exception400("최소 충전 금액은 100 이상입니다.");
            }
            if (amount > 100000) {
                throw new Exception400("최대 충전 금액은 100000 이하입니다.");
            }
        }
    }
}
