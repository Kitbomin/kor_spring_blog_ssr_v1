package org.example.demo_ssr_v1_1._api;

import lombok.Data;

// 응답 ToDo Dto 설계 완료
@Data
public class ToDo {
    // { "userId": 1, "id": 1, "title": "delectus aut autem", "completed": false }

    private Integer userId;
    private Integer id;
    private String title;
    private boolean completed;

    // 이렇게 해도 문제는 없음 -> 근데 형변환 생각하면 위에처럼 맞추는게 best
//    private String userId;
//    private String id;
//    private String title;
//    private boolean completed;

}
