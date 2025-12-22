package org.example.demo_ssr_v1_1._core.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 컨트롤러 들어가기 전에 조회를 먼저 진행
        // 먼저 로그인이 되어있는지를 판단(로그인 인터셉터 동작)
        HttpSession session = request.getSession();

        User sessionUser = (User) session.getAttribute("sessionUser");

        // 1. 로그인 체크는 loginInterceptor가 이미 했으므로 생략 가능하지만, 안전상 한번 더 체킹
        if (session == null) {
            throw new Exception401("로그인이 필요해요");
        }

        // 2. 관리자 역할 여부 판단
        if (!sessionUser.isAdmin()) {
            throw new Exception403("관리자 권한이 없어요");
        }

        // 이것만 적어도 컨트롤러로 보내짐
        return true;
    }
}
