package org.example.demo_ssr_v1_1.reply;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1.board.Board;
import org.example.demo_ssr_v1_1.board.BoardRepository;
import org.example.demo_ssr_v1_1.user.User;
import org.example.demo_ssr_v1_1.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 댓글 목록 조회

    /**
     * OSIV 에 대응하기 위해 DTO 설계, 계층간 결합도를 줄이기 위해 설계
     * - JOIN FETCH로 한번에 User를 들고옴
     * @param boardId -> 해당 게시글 불러오기
     * @param sessionUserId -> 소유 확인
     * @return List<ReplyResponse.ListDto>
     */
    public List<ReplyResponse.ListDto> 댓글목록조회(Long boardId, Long sessionUserId) {
        // 1. 조회 -- List<>로 반환됨
        // 2. 인가 처리 (필요없음)
        // 3. List로 반환된거 제대로 된 형식으로 반환하게 함 --> 데이터 변환

        // 조회를 했기 때문에 1차 캐시에 들어가있으니까 영속화 되어있음
        List<Reply> replyList = replyRepository.findByBoardIdWithUser(boardId);

        return replyList.stream()
                .map(reply -> new ReplyResponse.ListDto(reply, sessionUserId))
                .collect(Collectors.toList());

    }

    // 댓글 작성
    @Transactional
    public Reply 댓글작성(ReplyRequest.SaveDto saveDto, Long sessionUserId) {
        // 1. 게시글 존재 여부 확인
        // 2. 현재 로그인 여부 확인
        // 3. 인가 처리 할 필요는 없음
        // 4. 요청 DTO를 엔티티로 변환처리 해야함
        // 5. 레파지토리에 저장 요청

        // 조회를 하였으니 board는 영속화 된 상태임
        Board boardEntity = boardRepository.findById(saveDto.getBoardId())
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));

        User userEntity = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없어요"));

        // 비영속 상태 (개발자가 직접 new를 때려서 객체를 생성함)
        Reply reply = saveDto.toEntity(boardEntity, userEntity);
        return replyRepository.save(reply);
    }

    // 댓글 삭제
    @Transactional
    public Long 댓글삭제(Long replyId, Long sessionUserId) {
        // 댓글 조회 (findById(replyId) --> LAZY 때문에 댓글 작성자 정보는 없음)
        // -> 하지만 소유자 확인을 해야하기에 댓글 작성자 정보가 함께 필요함
        Reply replyEntity = replyRepository.findByIdWithUser(replyId)
                .orElseThrow(() -> new Exception404("댓글이 없어요"));

        if (!replyEntity.isOwner(sessionUserId)) {
            throw new Exception403("해당 댓글에 대한 권한이 없으십니다.");
        }

        Long boardId = replyEntity.getBoard().getId();

        replyRepository.delete(replyEntity);

        // 컨트롤러 단에서 redirect 처리를 해 다시 게시글 상세보기 페이지를 호출하기 위해 boardId값을 보냄
        return boardId;
    }
}
