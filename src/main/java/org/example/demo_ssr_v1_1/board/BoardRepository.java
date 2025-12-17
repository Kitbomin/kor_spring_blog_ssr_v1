package org.example.demo_ssr_v1_1.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 자동 제공 메서드 (별도 구현 없이 사용 가능)
    // - save(T entity): (Insert 또는 Update)
    // - findById(ID id): ID로 Entity 조회 (Optional<T> 반환)
    // - findAll()
    // - deleteById(Id id): ID로 Entity 삭제
    // - count() : 전체 개수 조회
    // - existsById(ID id): ID 존재여부 확인


    // 전체 조회
    // SELECT * FROM board_tb ORDER BY created_at DESC
    // => findAllByOrderByCreatedAtDESC

    // LAZY 로딩이라 한번에 username을 가져와야함
//    @Query("SELECT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC")
//    List<Board> findAllByOrderByCreatedAtDesc();

    // 게시글 전체 조회
//    @Query("SELECT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC")
//    List<Board> findAllWithUserOrderByCreatedAtDesc();

    // 게시글 전체 조회 (페이징 처리)
    // - 인수값은 개발자가 생성한 Pageable 객체를 삽입해주면 됨
    // - 리턴 타입은 Page 객체로 반환

    /**
     *
     * @param pageable 페이징 정보(페이지 번호, 크기, 정렬)를 가지고 있음
     * @return 페이징된 BoardList 를 가지고 있음 (단, 작성자 정보가 포함되어있음)
     * select 절에 DISTINCT를 사용하면 정확한 count를 가져올 수 있음
     * -> 이건 JOIN FETCH 때문에 하는 일임 -> 하이버 네이트가 쿼리를 이상하게 작성하는 것을 막는 처리
     *
     * countQuery - 전체 게시글에 개수를 빠르게 가져오기 위해 사용함 -> 성능문제임
     */
    @Query(value = "SELECT DISTINCT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC",
            countQuery = "SELECT  COUNT (DISTINCT b) FROM Board b")
    Page<Board> findAllWithUserOrderByCreatedAtDesc(Pageable pageable);



    // 게시글 ID로 조회 (작성자 본인정보 포함 - JOIN FETCH 에서 사용해야함)
    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.id = :id")
    Optional<Board> findByIdWithUser(@Param("id") Long id);


//    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.id = :id")
//    Optional<Board> findByIdWithUser(@Param("id") Long aLong);

}
