package org.choongang.global;

import jakarta.servlet.http.HttpServletResponse;
import org.choongang.global.exceptions.CommonException;

import java.io.IOException;
import java.io.PrintWriter;

public class MessageUtil {
    public static void alertError(Exception e, HttpServletResponse resp) throws IOException { // 예외 상황 발생 시 클라이언트에게 에러 메시지를 웹 브라우저 알림창(alert)으로 표시하는 역할을 함
        // 매개변수로는 발생한 예외객체 e, HTTP 응답 객체를 받음
        resp.setContentType("text/html; charset=UTF-8"); // 응답 데이터의 형식 설정
        if (e instanceof CommonException commonException) { // 예외 객체 확인
            // 발생한 예외 객체 e가 CommonException 클래스의 인스턴스인지 확인
            // 형변환 해야하기 때문에 짧게 쓸 수 있게 최근에 적용되었음
            resp.setStatus(commonException.getStatus()); // 해당 예외 객체가 가지고 있는 HTTP 응답 코드를 클라이언트에게 전송
        }
        PrintWriter out = resp.getWriter(); // HTTP 응답 본문을 작성하기 위한 출력 스트림 객체를 생성
        out.printf("<script>alert('%s');</script>", e.getMessage());
    } // 스크립트 태그(<script>) 내에 alert 함수를 사용하여 예외 객체의 메시지를 알림창으로 출력

    // 클라이언트를 다른 위치(URL)로 이동시키거나 현재 페이지 내 특정 위치로 이동시키는 역할
    public static void go(String url, String target, HttpServletResponse resp) throws IOException{ // 이동할 대상, 이동할 위치, 응답객체
        target = target == null || target.isBlank() ? "self" : target;
        // target이 null이거나 isBlank() 메서드를 통해 빈 문자열 또는 공백만으로 이루어져 있을 경우 "self" 문자열을 target 변수에 할당
        // 만약 target 이 유효한 값이 들어오면 해당 값을 그대로 유지
        resp.setContentType("text/html; charset=UTF-8"); // 응답 데이터 형식 설정
        PrintWriter out = resp.getWriter(); // / HTTP 응답 본문을 작성하기 위한 출력 스트림 객체
        /**
         * location.href : 주소 이동 시 이동 기록이 남는다. 뒤로가기 버튼을 누른 경우
         * POST 처리가 중복 된다.
         * POST 처리 시 이동할때는 기록을 남기지 않는 이동 방식 location.replace(..)
         */
        // out.printf("<script>%s.location.href='%s';</script>", target, url);
        out.printf("<script>%s.location.replace('%s');</script>", target, url);
    } // 기록을 남기지 않으며 페이지 이동 / url이 있으면 해당 주소로 이동하고, 없으면 부모창으로 이동

    public static void go(String url, HttpServletResponse resp) throws IOException {
        go(url, "self", resp);
    } // 이동할 대상 URL과 응답객체를 매개변수로 가짐
    //  go(url, "self", resp) 와 같이 세 개의 매개변수를 가진 go 메서드를 호출함
    // 이 호출을 통해 target 값이 "self"로 설정되어 이동 방식이 현재 페이지를 다시 로드하는 방식이 됨
    // 이 메서드는 이동 위치를 명시하지 않고 단순히 현재 페이지를 다시 로드하는 역할을 함
}
