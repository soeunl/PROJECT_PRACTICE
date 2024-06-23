package org.choongang.global.exceptions;

import jakarta.servlet.http.HttpServletResponse;


public class CommonException extends RuntimeException{

    private int status; // 응답코드도 같이 더해짐
    // 예외 발생 시 함께 전달할 HTTP 응답 코드를 저장하는데 사용됨

    public CommonException(String message) {
        this(message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public CommonException(String message, int status) {
        super(message);
        this.status = status; // 응답코드
    }

    public int getStatus() {
        return status;
    } // private int status에 지정된 응답 코드 반환
}

// RuntimeException 클래스를 상속받음
// 두가지의 생성자가 존재
// 문자열 message를 매개 변수로 받는 생성자는 예외 메시지를 담는 message 파라미터만을 받으며, 응답 코드는 기본적으로 설정되지 않음
// 문자열 message와 정수 status를 매개변수로 받는 생성자는 예외 메시지를 담는 message 파라미터와 함께 HTTP 응답 코드를 나타내는 status 파라미터를 받음
// 상속받은 RuntimeException 클래스의 생성자를 호출하여 message를 예외 메시지로 설정할 수 있음(runtimeException이 기본으로 전달해주는 메세지)
