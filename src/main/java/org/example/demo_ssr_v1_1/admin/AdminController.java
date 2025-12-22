package org.example.demo_ssr_v1_1.admin;

import jakarta.servlet.http.HttpSession;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // http://localhost:8080/admin/dashboard
    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User sessionUser = (User) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            throw new Exception401("로그인 필요");
        }

        if (!sessionUser.isAdmin()) {
            throw new Exception403("접근 권한 없음");
        }

        return "admin/dashboard";
    }

}
