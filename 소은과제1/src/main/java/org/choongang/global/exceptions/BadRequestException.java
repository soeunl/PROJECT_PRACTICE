package org.choongang.global.exceptions;

import jakarta.servlet.http.HttpServletResponse;

// 클라이언트가 잘못된 요청을 보낸 경우를 처리하기 위한 예외
// 유효하지 않은 형식의 데이터를 전달하거나 필수 항목이 누락되었을 때 발생

public class BadRequestException extends CommonException {

    public BadRequestException () {
        this("잘못된 요청입니다");
    } // 기본값을 넣어주기 위한 처리임

    public BadRequestException(String message) {
        super(message, HttpServletResponse.SC_BAD_REQUEST);
    }
}

// CommonException 클래스를 상속받음
// 기본 생성자와 메세지를 포함하는 생성자 2가지의 생성자가 있음
// 문자열 message 를 매개 변수로 받는 생성자는 받은 message를 예외 메시지로 설정함
// super 키워드를 통해 상속받은 클래스(CommonException)의 생성자를 호출
// HttpServletResponse.SC_BAD_REQUEST는 HTTP 상태 코드 400 (Bad Request)을 나타냄