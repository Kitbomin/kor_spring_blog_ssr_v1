package org.example.demo_ssr_v1_1.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    /**
     * 댓글 작성 기능 요청
     * @param saveDto -> 바디
     * @param session -> 세션 정보
     * @return -> 새로고침
     */
    @PostMapping("/reply/save")
    public String saveProc(ReplyRequest.SaveDto saveDto, HttpSession session) {
        // 1. 인증 검사 => 인터셉터로 해놔서 안해도 괜찮음
        User sessionUser = (User) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            throw new Exception401("로그인 먼저.");
//        }

        // 2. 유효성 검사(형식)
        saveDto.validate();

        // 3. 댓글 작성 서비스단 요청
        replyService.댓글작성(saveDto, sessionUser.getId());

        // 4. 게시글 상세보기 redirect 처리
        return "redirect:/board/" + saveDto.getBoardId();

    }


    @PostMapping("/reply/{id}/delete")
    public String deleteProc(@PathVariable(name = "id") Long replyId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        Long boardId = replyService.댓글삭제(replyId, sessionUser.getId());

        boolean isOwner = false;
        return "redirect:/board/" + boardId;
    }
}
