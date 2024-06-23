package org.choongang.global.filters;

// 모든 페이지에 공통적으로 구현될 수 있는 필터

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter("/*") //모든 요청에 유입되는 필터가 되기 위해 /*를 넣음
// 애플리케이션에 들어오는 모든 요청은 이 필터를 거쳐야한다

public class CommonFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException { // 매개변수 이름 request, response, chain으로 변경
        // 모든 곳에 적용될 수 있는 필터를 만든다
        chain.doFilter(new CommonRequestWrapper(request), response);
        // CommonRequestWrapper는 ServletRequest 인터페이스를 구현하는 클래스로, 원본 요청 객체를 감싸면서 추가 기능을 제공하거나 원본 요청 객체의 일부를 변경할 수 있음
        // CommonRequestWrapper 클래스의 새 인스턴스로 요청 객체를 감싸고 필터 체인 (chain) 에 전달
    }
}

//작동방식
// CommonFilter 필터에서 CommonRequestWrapper 클래스로 요청 객체를 감싸 새 인스턴스를 생성합니다.
// 새로 감싼 요청 객체는 필터 체인 (chain)에 전달됩니다.
// 필터 체인은 실제 요청 처리 로직을 담당하는 서블릿 또는 다른 필터를 호출합니다.
// 서블릿 또는 필터가 처리를 완료한 후 응답을 생성합니다.
// 응답은 CommonFilter 필터로 돌아옵니다.
// 필터는 응답을 그대로 클라이언트에게 전달합니다.
