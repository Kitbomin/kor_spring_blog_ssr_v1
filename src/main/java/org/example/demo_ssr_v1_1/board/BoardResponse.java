package org.example.demo_ssr_v1_1.board;

import lombok.Data;
import org.example.demo_ssr_v1_1._core.utils.MyDateUtil;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

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

    @Data
    public static class PageDto {
        private List<ListDto> content;
        private int number;             // 현재 페이지 번호(0번부터 시작) (사용자에게만 1번을 보여줌)
        private int size;               // 한 페이지의 크기

        private int totalPages;         // 전체 페이지 수
        private Long totalElements;     // 전체 게시글 수

        private boolean first;          // 첫번째 페이지 여부
        private boolean last;           // 마지막 페이지 여부

        private boolean hasNext;        // 다음 페이지 존재 여부
        private boolean hasPrevious;    // 이전 페이지 존재여부

        private Integer previousPageNumber; // 이전 페이지 번호(없으면 null 들어감)
        private Integer nextPageNumber;     // 다음 페이지 번호(없으면 null 들어감)

        private List<PageLink> pageLinks;   // 페이지별 링크 목록

        public PageDto(Page<Board> page) {
            // List<Board> -> Page.content 호출 필요
            // page.getContent()
            // page.content -> List<Board> 임
            // 게시글 리스트 DTO --> BoardResponse.ListDto로 설계함
            this.content = page.getContent().stream()
                    .map(ListDto::new)
                    .toList();

            this.number = page.getNumber();
            this.size = page.getSize();
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.first = page.isFirst();
            this.last = page.isLast();
            this.hasNext = page.hasNext();
            this.hasPrevious = page.hasPrevious();

            // 현재 페이지 인덱스 0부터 시작함
            // 화면에서는 N + 1 으로 세팅됨
            // 이전 페이지를 선택하게 될 시 -1 을 해줄 필요는 없음
            this.previousPageNumber = page.hasPrevious() ? page.getNumber() : null;
            this.nextPageNumber = page.hasNext() ? page.getNumber() + 2 : null;

            // 페이지 링크 생성 (앞뒤로 2페이지씩 링크 생성)
//            this.pageLinks = ....;
            this.pageLinks = generatedPageLinks(page);
        }

        private List<PageLink> generatedPageLinks(Page<Board> page) {
            // 코드 구현
            List<PageLink> links = new ArrayList<>();

            int currentPage = page.getNumber() + 1;
            int totalPages = page.getTotalPages();

            // 현재 페이지 번호가 5인 상태라면?
            //  3 4 [5] 6 7
            int startPage = Math.max(1, currentPage - 2);

            // 토탈 페이지가 2 이면
            int endPage = Math.min(totalPages, currentPage + 2);

            for (int i = startPage; i <= endPage; i++) {
                PageLink link = new PageLink();
                link.setDisplayNumber(i);
                link.setActive(i == currentPage);
                links.add(link);
            }

            return links;
        }
    }


    @Data
    public static class PageLink {
        private int displayNumber; // 표시할 페이지 번호
        private boolean active;

    }









}
