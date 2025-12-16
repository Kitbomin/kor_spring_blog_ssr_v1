package org.example.demo_ssr_v1_1.user;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service --> 응답 DTO 설계 해서 전달 --> Controller

/**
 * 사용자 서비스 레이어
 *
 * 1. 역할
 *  - 비즈니스 로직을 처리하는 계층
 *  - Controller 와 Repository 사이의 중간 계층
 *  - 트랜잭션 관리
 *  - 여러 Repository를 조홥해 복잡한 비즈니스 로직을 처리함
 *
 *
 */

@Service //IoC 대상 @Component의 특수한 형태
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    // 객체 지향 개념 --> SOLID 원칙
    /**
     * S - 단일 책임
     * O - 개방 폐쇄
     * L - 리스코프 치환
     * I -
     * D - 의존성 역전 -> 추상화가 높은 녀석을 선언하는 것이 좋음
     */
    private final UserRepository userRepository;

    // 회원 가입
    @Transactional
    public User 회원가입(UserRequest.JoinDTO joinDTO) {

        // 1. 회원가입 시 사용자명 중복 체크
        if (userRepository.findByUsername(joinDTO.getUsername()).isPresent()) {
            // isPresent -> 있으면 true, 없으면 false 반환
            throw new Exception400("이미 있는 이름잉요오");
        }
        User user = joinDTO.toEntity();
        return userRepository.save(user);
    }

    // 로그인 처리
    public User 로그인(UserRequest.LoginDTO loginDTO) {
        // 사용자가 던진 값과 DB에 있는 사용자 이름과 비밀번호를 확인해줘야함
        User userEntity = userRepository
                .findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
                .orElse(null);

        if (userEntity == null) {
            throw new Exception400("사용자 명 또는 비밀번호가 올바르지 않슷빈다.");
        }

        return userEntity;
    }

    // 회원정보 수정
    public User 회원정보수정화면(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없어요"));

        if (!userEntity.isOwner(userId)) {
            throw new Exception403("님이 쓴 글이 아니잖아요");
        }

        return userEntity;
    }

    // 데이터의 수정은 (더티 체킹 - 반드시 먼저 조회 -> 조회된 객체의 상태값을 변경 시 자동 반영되게끔)
    // 1. 회원정보 조회
    // 2. 인가 검사
    // 3. 엔티티 상태 변경(더티 체킹)
    // 4. 트랜잭션이 일어나고 변경된 UserEntity 반환
    @Transactional
    public User 회원정보수정(UserRequest.UpdateDTO updateDTO, Long userId) {
        User userEntity = userRepository
                .findById(userId).orElseThrow(() -> new Exception404("사용자 못찾음"));

        if (!userEntity.isOwner(userId)) {
            throw new Exception403("회원 정보 수정 권한이 없으어요");
        }

        // 객체 상태값 변경(트랜잭션이 끝나면 자동으로 commit 및 반영함)
        userEntity.update(updateDTO);

        return userEntity;
    }

    //

}
