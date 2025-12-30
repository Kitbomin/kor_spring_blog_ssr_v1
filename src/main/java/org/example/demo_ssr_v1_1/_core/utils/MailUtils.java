package org.example.demo_ssr_v1_1._core.utils;

import java.util.Random;

public class MailUtils {

    // 정적 메서드로 랜덤 번호 6자리 생성하는 헬프 메서드

    public static String generateRandomCode() {
        Random random = new Random();

        // 0 ~ 6 자리면 0부터 899999 까지의 숫자 생성
        // 문제점 1. 0이 발생할 수 있음
        // 문제점 2. 두자리수도 발생할 수 있음
        // => 반드시 6자리를 만들어야함
        int code = 100000 + random.nextInt(900000);

        return String.valueOf(code);
    }

}
