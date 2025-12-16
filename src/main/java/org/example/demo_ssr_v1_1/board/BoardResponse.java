package org.example.demo_ssr_v1_1.board;

import lombok.Data;
import org.example.demo_ssr_v1_1._core.utils.MyDateUtil;
import org.example.demo_ssr_v1_1.user.User;

/**
 * 응답 DTO
 */

public class BoardResponse {

    @Data
    public static class ListDto {
        private Long id;
        private String title;

        // 작성자명 (평탄화) => {{board.user.username}} => 원래는 이렇게 접근함 근데 편탄화 하면 {{board.username}} 이렇게 접근 가능
        // SSR 에서는 평탄화 해주는게 좋음
        private String username;
        private String createdAt;

        public ListDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();

            // 쿼리 작업했던걸 --> JOIN FETCH로 가져오면 문제가 없긴함
            if (board.getUser() != null) {
                this.username = board.getUser().getUsername();
            }

            // 날짜 포맷팅
            if (board.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(board.getCreatedAt());
            }
        }

    } // end of static inner class

    /**
     * 게시글 상세 응답 DTO
     */
    @Data
    public static class DetailDto {
       private Long id;
       private String title;
       private String content;
       private Long userId;
       private String username;
       private String createdAt;

        public DetailDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();

            // JOIN FETCH 활용 (한번에 JOIN 해서 Repository 단에서 가져옴)
            if (board.getUser() != null) {
                this.userId = board.getUser().getId();
                this.username = board.getUser().getUsername();
            }

            if (board.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(board.getCreatedAt());
            }
        }
    }

    /**
     * 게시글 수정 화면 응답 DTO
     */
    @Data
    public static class UpdateFormDto {
        private Long id;
        private String title;
        private String content;

        public UpdateFormDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
        }
    }











}
