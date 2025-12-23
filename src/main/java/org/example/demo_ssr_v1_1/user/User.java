package org.example.demo_ssr_v1_1.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// 엔티티 화면 보고 설계해 보세요.
@NoArgsConstructor
@Data
@Table(name = "user_tb")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String email;

    @CreationTimestamp
    private Timestamp createdAt;

    //@Column(nullable = false)
    private String profileImage; // 추가


    /**
     * User (1) : UserRole (N)
     * User 가 UserRole 리스트를 관리함 (단방향)
     * 실제 DB의 'user_role_tb' 테이블에 user_id 라는 fk 컬럼이 생김
     *
     * CascadeType.ALL
     * - 운명 공동체 | User를 저장하면 Role도 자동으로 저장되고, User 삭제 시 Role도 같이 삭제 됨
     *
     * orphanRemoval = true
     * - 리스트와 DB의 동기화임
     * - Java의 roles 리스트에서 요소(Role)를 .remove() 하거나 .clear() 하면 DB 에서도 해당 데이터 (DELETE)가 실제로 처리됨
     */
    // 나중에 다른 개발자가 findById(쿼리 메서드) 호출 시 신경 쓸 필요 없이 전부 role 까지 가져와줌
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<UserRole> roles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) @ColumnDefault("'LOCAL'") // 문자열이니까 홑따옴표 필수
    private OAuthProvider provider;

    @Builder
    public User(Long id, String username, String password,
                String email, Timestamp createdAt, String profileImage, OAuthProvider provider) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.profileImage = profileImage;  // 추가
        this.provider = (provider == null) ? OAuthProvider.LOCAL : provider;
    }

    // 회원정보 수정 비즈니스 로직 추가
    // 추후 DTO  설계
    public void update(UserRequest.UpdateDTO updateDTO) {
        // 유효성 검사
        updateDTO.validate();
        this.password = updateDTO.getPassword();
        // 추가
        this.profileImage = updateDTO.getProfileImageFilename();
        // 더티 체킹 (변경 감지)
        // 트랜잭션이 끝나면 자동으로 update 쿼리 진행
    }

    // 회원 정보 소유자 확인 로직
    public boolean isOwner(Long userId) {
        return this.id.equals(userId);
    }

    // 새로운 역할을 추가하는 기능
    public void addRole(Role role) {
        this.roles.add(UserRole.builder()
                        .role(role)
                .build());
    }

    // 해당 역할을 가지고 있는지 확인하는 기능
    public boolean hasRole(Role role) {
        // roles (리스트)에 컬렉션이 없거나, 비어있으면 역할이 없는 것으로 간주
        if (this.roles == null || this.roles.isEmpty()) {
            return false;
        }

        // 즉시 로딩이라서 바로 사용해도 LAZY Exception 안터짐
        // any(어떤 것이든... 매칭된다면?): 리스트 안에 있는 것들 중 단 하나라도 매칭이 된다면 true 반환
        return this.roles.stream()
                .anyMatch(r -> r.getRole() == role);
    }

    // 관리자 여부 반환
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    // 템플릿에서 {{#isAdmin}} ... {{/isAdmin}} 형태로 사용하는 편의 메서드 설계
    public boolean getIsAdmin() {
        return isAdmin();
    }

    // 화면에 표시할 역할 문자열 제공
    // - ADMIN 이면 "ADMIN" 제공
    public String getRoleDisplay() {
        return isAdmin() ? "ADMIN" : "USER";
    }

    /**
     * 분기 처리 - 요구사항
     * mustache 화면에서는 서버에 저장된 이미지든, url 이미지이든
     * 그냥 getProfilePath 변수를 호출하면 알아서 셋팅되게 하고 싶음
     */
    public String getProfilePath() {
        if (this.profileImage == null) {
            return null;
        }
        // http 로 시작하면 소셜 이미지 url 그대로 리턴
        // 아니면 로컬이미지 폴더 경로를 붙여서 리턴 처리 할거임
        if (this.profileImage.startsWith("http")) {
            return this.profileImage;
        }
        return "/images/" + this.profileImage;
    }

    // 이거 필요없음.. local 일때만 드러나게 하면 되니까
//    public boolean isKakao() {
//        return !this.provider.equals(OAuthProvider.KAKAO);
//    }
}
