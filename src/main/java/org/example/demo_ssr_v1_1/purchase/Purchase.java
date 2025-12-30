package org.example.demo_ssr_v1_1.purchase;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1_1.board.Board;
import org.example.demo_ssr_v1_1.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Table(
        name = "purchase_tb",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_board",
                        columnNames = {"user_id", "board_id"}
                )
        }
)
@Data
public class Purchase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. User
    //  - 단방향 관계: Purchase : User ( N : 1 )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 2. Board
    //  - 단방향 관계:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 3. A 유저가 1번 게시글을 구매했다 -> 라는 이력을 남겨야함
    //  - 구매 시 지불한 포인트
    private Integer price;

    @CreationTimestamp
    private Timestamp timestamp;

    @Builder
    public Purchase(User user, Board board, Integer price) {
        this.user = user;
        this.board = board;
        this.price = price;
    }

    // 4. A 유저가 또 1번을 구매? -> 중복 구매 방지해야함
    // 5. 구매 시 지불한 포인트
    // 6. 구매 시간
}
