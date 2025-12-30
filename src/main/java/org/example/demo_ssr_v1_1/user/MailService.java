package org.example.demo_ssr_v1_1.user;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.utils.MailUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {

    // 의존성 주입 받았던 클래스
    // JavaMailSender -> 편지를 쓰게 해주는 클래스
    private final JavaMailSender javaMailSender;

    private final HttpSession session;

    public void 인증번호발송(String email) {
        // email -> 인증번호(6자리) 생성 -> 임시로 세션 메모리에 저장 -> 메일 발송 요청

        // 1. 인증 번호 생성
        String code = MailUtils.generateRandomCode();


        // 2. 이메일 전송 내용 설정
        // MimeMessage => 텍스트 뿐만 아니라 HTML, 첨부파일을 포함할 수 있는 표준 포맷
        // SimpleMailMessage => 순수 텍스트만 보낼 때 사용함
        MimeMessage message = javaMailSender.createMimeMessage();

        // 3. 구글 메일 서버로 전송 -> 본 서버가 아닌 외부 서버로 통신을 요청해야함
        //    외부 통신 코드일 때 기본적으로 try - catch 문 사용 필수
        try {
            // 3-1. 도우미 객체를 사용(헬퍼 클래스)
            // message: 이메일 전송 내용
            // true : multipart 허용 여부
            // UTF-8: encoding 설정 -> 한글 깨짐 방지
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email); // 받는 사람 이메일 주소
            helper.setSubject("[My Blog] 회원가입 이메일 전송"); // 제목
            helper.setText("<h3>인증번호는 [" + code + "] 입니다 </h3>", true); // 본문

            javaMailSender.send(message);

            // 4. 세션에 임시코드 저장
            // sessionUser : User(...)
            // code_xxx@naver.com: yyyyyy => 이런 형태로 찾을 거임
            // 동시에 접속자가 많아도 이메일 주소로 누구의 인증번호인지 구별 가능
            session.setAttribute("code_" + email, code);
            

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public boolean 인증번호확인(String email, String code) {
        // 1. 세션에서 저장된 코드 가져오기
        // key: code_ + "xxx@~~~"
        String savedCode = (String) session.getAttribute("code_" + email);

        // 2. 세션에 가지고 온 code 값과 사용자가 입력한 인증번호 일치 여부 확인
        if (savedCode != null && savedCode.equals(code)) {
            // 세션 메모리에서 제거해줘야함
            session.removeAttribute("code_" + email);
            return true;
        }

        return false;
    }
}
