package org.example.demo_ssr_v1_1.board;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    // 객체 지향 개념 --> SOLID 원칙
    /**
     * S - 단일 책임
     * O - 개방 폐쇄
     * L - 리스코프 치환
     * I -
     * D - 의존성 역전 -> 추상화가 높은 녀석을 선언하는 것이 좋음
     */
    private final BoardRepository boardRepository;

    public BoardResponse.PageDto 게시글목록조회(int page, int size) {
        // page는 0부터 시작
        // 상한선 제한
        // size는 기본값 5, 최소 1, 최대 50으로 제한해둘거임
        // 페이지 번호가 음수가 되는 것을 막음
        int validPage = Math.max(0, page); // 양수값 보장
        // 최대값 제한 - Math.max(1, Math.min(50          => 최대값을 50으로 제한함
        // 최소값 제한 - Math.max(1, Math.min(50, size))  => 최고값을 1로 보장하는거임
        int validSize = Math.max(1, Math.min(50, size));

        // 정렬 기준
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        // Page<Board>
        Page<Board> boardPage = boardRepository.findAllWithUserOrderByCreatedAtDesc(pageable);

        return new BoardResponse.PageDto(boardPage);
    }

    /**
     * 게시글 목록 조회
     * 트랜잭션
     *  - 읽기 전용 트랜잭션 사용중
     * @return 게시글 목록(생성일 기준으로 내림차순)
     */
//    public List<BoardResponse.ListDto> 게시글목록조회() {
//        // 자바 문법
//        // 데이터 타입을 변환해서 맞춰줘야함
//        List<Board> boardList = boardRepository.findAllWithUserOrderByCreatedAtDesc();
//
//        // List<Board> ---> List<BoardResponse.ListDto> 로 변환해야함
//        // 1. 반복문
//        List<BoardResponse.ListDto> dtoList = new ArrayList<>();
//
//        for (Board board : boardList) {
//            BoardResponse.ListDto dto = new BoardResponse.ListDto(board);
//            dtoList.add(dto);
//        }
//        return dtoList;
//
//        // 2. 람다 표현식
//        return  boardList.stream()
//                .map(board -> new BoardResponse.ListDto(board))
//                .collect(Collectors.toList());
//
//        // 3. 참조 메서드
//        return  boardList.stream()
//                .map(BoardResponse.ListDto::new)
//                .collect(Collectors.toList());
//    }

    public BoardResponse.DetailDto 게시글상세조회(Long boardId) {
        Board board = boardRepository
                .findByIdWithUser(boardId).orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));

        return new BoardResponse.DetailDto(board);
    }


    // 1. 트랜잭션 처리
    // 2. 레파지토리 저장
    @Transactional
    public Board 게시글작성(BoardRequest.SaveDTO saveDTO, User sessionUser) {

        // DTO에서 직접 new 해서 생성한 Board 객체일 뿐, 아직 영속화된 객체가 아님
        Board board = saveDTO.toEntity(sessionUser);
        boardRepository.save(board);
        return board;
    }

    // 1. 게시글 조회
    // 2. 인가 처리
    public BoardResponse.UpdateFormDto 게시글수정화면(Long boardId, Long sessionUserId) {
        Board boardEntity = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없ㅇ어요");
        }

        return new BoardResponse.UpdateFormDto(boardEntity);
    }

    // 1. 트랜잭션 처리
    // 2. DB에서 조회
    // 3. 인가 처리
    // 4. 조회된 board에 상태값 변경 (더티 체킹)
    @Transactional
    public Board 게시글수정(BoardRequest.UpdateDTO updateDTO, Long boardId, Long sessionUserId) {
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글ㅇ이 없어요오"));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("님꺼아님");
        }

        boardEntity.update(updateDTO);
        return boardEntity;
    }


    @Transactional
    public void 게시글삭제(Long boardId, Long sessionUserId) {
        // 조회부터 해야 DB에 있는 Board에 user_id 값을 확인할 수 있음
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글이 없어요"));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("님꺼 아님");
        }

        boardRepository.deleteById(boardId);
    }



}
