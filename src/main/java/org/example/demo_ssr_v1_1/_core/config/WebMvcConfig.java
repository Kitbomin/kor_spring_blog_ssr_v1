package org.example.demo_ssr_v1_1._core.config;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.intercepter.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정 클래스
 * @Controller / @Service / @Repository / @Component...
 */
// @Component 클래스 내부에서 @Bean 어노테이션을 사용해야 된다면 @Configuration을 사용해야함
@Configuration // 내부도 IOC 대상 여부 확인
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

//    public WebMvcConfigurer(LoginInterceptor loginInterceptor) {
//        this.loginInterceptor = loginInterceptor;
//    }


    // ps. 인터셉터는 당연히 여러개 등록이 가능함 -> 1개만 있을 필요는 없음
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 1. 설정에 LoginInterceptor를 등록하는 코드
        // 2. 인터셉터가 동작할 URL 패턴 지정
        // 3. 어떤 URL 요청이 로그인 여부를 필요할지 확인해야함
        //      /board/*)* => 이거 다 검사시킬거이ㅣ임
        //      /user/**   => 이거 다 검사시킬거임
        //      -> 단, 특정 url은 제외시킬 거임
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/board/**", "/user/**");
    }
}
