package org.example.demo_ssr_v1_1.reply;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1_1.board.Board;
import org.example.demo_ssr_v1_1.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * 단방향, 양방향
 */
@Data
@Entity
@Table(name = "reply_tb")
@NoArgsConstructor
public class Reply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String comment; // 최대 500 자

    // 단방향 설계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Reply(String comment, Board board, User user) {
        this.comment = comment;
        this.board = board;
        this.user = user;
    }

    // 소유자 확인
    public boolean isOwner(Long userId) {
        if (this.user == null || userId == null) {
            return false;
        }
        Long replyUserId = this.user.getId();
        if (replyUserId == null) {
            return false;
        }
        return replyUserId.equals(userId);
    }

    // 댓글 내용 수정
    public void update(String newString) {
        if (newString == null || newString.trim().isEmpty()) {
            throw new Exception400("댓글 내용은 필수 입니다");
        }
        if (newString.length() > 500) {
            throw new Exception400("500자를 넘길 수 없어요");
        }
    }

}
