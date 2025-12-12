package org.example.demo_ssr_v1_1.board;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1_1.user.User;
import org.hibernate.annotations.CreationTimestamp;

import javax.swing.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "board_tb")
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // pc --> db
    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Board(String title, String content, User user){
        this.title = title;
        this.content = content;
        this.user = user;
    }

    // Board 상태값 수정하는 로직
    public void update(BoardRequest.UpdateDTO updateDTO) {
        // 본인의 유효성 검사 완
        updateDTO.validate();

        this.title = updateDTO.getTitle();
        this.content = updateDTO.getContent();

        // 게시글 수정은 작성자를 변경할 수 없음
//        this.user = updateDTO.getUsername();

    }

    // 게시글 소유자 확인 로직
    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    // 개별 필드 수정 - title
    public void updateTitle(String newTitle) {
        // 방어적 코드
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }

        this.title = newTitle;
    }

    // 개별 필드 수정 - content
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }

        this.content = newContent;
    }

}
