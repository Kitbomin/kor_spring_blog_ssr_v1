package org.example.demo_ssr_v1_1.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 기본 CRUD가 만들어진 상태~~~~

    // imp_uid 로 결제 내역 조회
    // 포트원 결제 번호로 Payment 정보 조회쿼리 자동생성
    Optional<Payment> findByImpUid(String impUid);

    // merchant_uid
    Optional<Payment> findByMerchantUid(String impUid);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.merchantUid = :merchantUid")
    boolean existsByMerchantUid(@Param("merchantUid") String merchantUid);


}
