package org.example.demo_ssr_v1_1.board;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

    /**
     * 게시글 목록 조회
     * 트랜잭션
     *  - 읽기 전용 트랜잭션 사용중
     * @return 게시글 목록(생성일 기준으로 내림차순)
     */
    public List<Board> 게시글목록조회() {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }

    public Board 게시글상세조회(Long boardId) {
        return boardRepository
                .findById(boardId).orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));
    }


    // 1. 트랜잭션 처리
    // 2. 레파지토리 저장
    @Transactional
    public Board 게시글작성(BoardRequest.SaveDTO saveDTO, User sessionUser) {

        // DTO에서 직접 new 해서 생성한 Board 객체일 뿐, 아직 영속화된 객체가 아님
        Board board = saveDTO.toEntity(sessionUser);

        return boardRepository.save(board);
    }

    // 1. 게시글 조회
    // 2. 인가 처리
    public Board 게시글수정화면(Long boardId, Long sessionUserId) {
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없ㅇ어요");
        }

        return boardEntity;
    }

    // 1. 트랜잭션 처리
    // 2. DB에서 조회
    // 3. 인가 처리
    // 4. 조회된 board에 상태값 변경 (더티 체킹)
    @Transactional
    public void 게시글수정(BoardRequest.UpdateDTO updateDTO, Long boardId, Long sessionUserId) {
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글ㅇ이 없어요오"));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("님꺼아님");
        }

        boardEntity.update(updateDTO);
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
