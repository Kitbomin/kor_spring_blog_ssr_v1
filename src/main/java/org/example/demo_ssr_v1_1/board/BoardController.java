package org.example.demo_ssr_v1_1.board;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller // IoC
public class BoardController {

//    @Autowired
    private final BoardPersistRepository repository;

//    public BoardController(BoardPersistRepository boardPersistRepository) {
//        this.boardPersistRepository = boardPersistRepository;
//    }

    // 게시글 수정 폼 페이지 요청
    // GET - http://localhost:8080/board/1/update
    @GetMapping("/board/{id}/update")
    public String updateForm(
            @PathVariable Long id,
            Model model
//            HttpServletRequest request
    ) {


        Board board = repository.findById(id);
        if (board == null) {
            throw new RuntimeException("수정할 게시글을 찾을 수 없어요");
        }

        // view에 밀어넣는 방법
        model.addAttribute("board", board);
//        request.setAttribute("board", board);

        return "board/update-form";
    }

    // 게시글 수정 요청 (기능요청)
    // POST - http://localhost:8080/board/1/update-form
    @PostMapping("/board/{id}/update")
    public String updateProc(@PathVariable Long id,
                             BoardRequest.UpdateDTO updateDTO) {

        try {
            // 더티체킹 활용
            repository.updateById(id, updateDTO);
        }catch (Exception e) {
            throw new RuntimeException("게시글 수정 실패");
        }

        return "redirect:/board/list";
    }

    @GetMapping("/board/list")
    public String boardList(Model model) {
        List<Board> boardList = repository.findAll();
        model.addAttribute("boardList", boardList);

        return "/board/list";
    }

    // 게시글 저장화면 요청
    // POST - http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String saveForm() {

        return "board/save-form";
    }

    // 게시글 저장요청 (기능 요청)
    // POST - http://localhost:8080/board/save
    @PostMapping("/board/save")
    public String saveProc(BoardRequest.SaveDTO saveDTO) {
        // HTTP 요청: username=value&title=value&content=&value
        // 스프링이 처리: new SaveDto(), setter 메서드 호출하여 값을 넣어줌

        Board board = saveDTO.toEntity();

        repository.save(board);

//        Board board = new Board(saveDTO);
//        repository.save(board);

        return "redirect:/";
    }

    // 삭제 @DeleteMapping 이지만 form 태그 활용이기에 없음 -> get, post (fetch 함수 활용)
//    @GetMapping("/board/{id}/delete")
//    public String delete(@PathVariable Long id) {
//        repository.deleteById(id);
//
//        return "redirect:/";
//    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);

        return "redirect:/";
    }

    // 상세 보기
    // http://localhost:8080/board/1
    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Board board = repository.findById(id);
        if (board == null) {
            // 404
            throw new RuntimeException("게시글이 없어요" + id);
        }

        model.addAttribute("board", board);

        return "board/detail";
    }


//    @Autowired
//    private BoardPersistRepository boardPersistRepository;
//
//    // 게시글 화면 요청 - 자원요청 get
//    // http://localhost:8080/board/save-form
//    @GetMapping("/board/save-form")
//    public String saveFrom() {
//        return "board/save-form";
//    }
//
//    // 게시글 작성 기능
//    //post - http://localhost:8080/board/save-form
//    @PostMapping("/board/save-form")
//    public String saveFormProc(@RequestParam("username") String username,
//                               @RequestParam("title") String title,
//                               @RequestParam("content") String content) {
//
//        System.out.println("username : " + username);
//        System.out.println("title : " + title);
//        System.out.println("content : " + content);
//
//        Board board = new Board();
//        board.setUsername(username);
//        board.setTitle(title);
//        board.setContent(content);
//
//        boardPersistRepository.save(board);
//
//        return "redirect:/board/list";
//    }
//
//    // 게시글 목록 보기
//    // http://localhost:8080/board/list
//    @GetMapping("/board/list")
//    public String list(Model model) {
//
//        List<Board> boardList = boardPersistRepository.findAll();
//        model.addAttribute("boardList", boardList);
//
//        System.out.println(boardList.stream().toList());
//        return "board/list";
//    }


}
