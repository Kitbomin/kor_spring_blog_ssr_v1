package org.example.demo_ssr_v1_1.payment;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1_1.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Table(name = "payment_tb")
@Data
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 포트원 결제 고유 번호 저장
    @Column(unique = true, nullable = false)
    private String impUid;

    // 본 서버에서 사용할 고유 주문 번호(가맹점 주문 번호)
    @Column(unique = true, nullable = false)
    private String merchantUid;

    // 결제한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer amount;

    // 결제 상태 (paid: 결제 완료 | canceled: 결제 취소)
    @Column(nullable = false)
    private String staus;

    // 결제 시간
    @CreationTimestamp
    private Timestamp timestamp;

    @Builder
    public Payment(Long id, String impUid, String merchantUid, User user, Integer amount, String staus, Timestamp timestamp) {
        this.id = id;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.user = user;
        this.amount = amount;
        this.staus = staus;
        this.timestamp = timestamp;
    }


}
