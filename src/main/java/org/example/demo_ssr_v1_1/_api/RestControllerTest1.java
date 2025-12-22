package org.example.demo_ssr_v1_1._api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
public class RestControllerTest1 {

    // 테스트 주소: https://jsonplaceholder.typicode.com/todos/{id}
    // 자바 코드로 다른 서버에 요청을 하여 응답 받아보기

    // 우리 서버에서 받아줄 주소: http://localhost:8080/todos/{id}
    // http://localhost:8080/todos/{id} : id에 1을 넣으면? http://localhost:8080/todos/1 로 날아옴
    @GetMapping("/todos/{id}")
    public ResponseEntity<?> test1(@PathVariable Integer id) {

        // HttpClient 라는 것을 써서 통신도 가능한데 이건 너무 old 함
        // 그래서 RestTemplate restTemplate; 라는걸 쓸거임

        // 1. URI 객체 생성(주소 만들기)
        URI uri = UriComponentsBuilder
                .fromUriString("https://jsonplaceholder.typicode.com")
                .path("/todos")
                .path("/" + id)
                .encode()       // 주소에 한글이나 특수 문자가 있을 경우 안전하게 변환해줌
                .build()
                .toUri();

        System.out.println("생선된 URI 주소: " + uri.toString());

        // 2. RestTemplate 객체 생성 (HTTP 통신 도구)
        RestTemplate restTemplate = new RestTemplate();

        // 3. GET 방식으로 요청 보내기
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        // 4. 결과 확인
        System.out.println("HTTP 상태 코드 확인: " + response.getStatusCode());
        System.out.println("HTTP 헤더 정보 확인: " + response.getHeaders());
        System.out.println("HTTP 바디 정보 확인: " + response.getBody());

        // 브라우저에 결과 함께 출력
        return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
    }



}
