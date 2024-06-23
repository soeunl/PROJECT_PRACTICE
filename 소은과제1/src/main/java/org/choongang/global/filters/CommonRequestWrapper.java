package org.choongang.global.filters;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Member;

public class CommonRequestWrapper extends HttpServletRequestWrapper {
    // 원본 요청 객체를 감싸면서 추가 기능을 제공하거나 원본 요청 객체의 일부를 변경할 수 있음
    // filter에서 넘어오는 값을 바로 사용하기 위해 ServletRequest req를 매개변수로 사용
    public CommonRequestWrapper(ServletRequest req) {
        super((HttpServletRequest) req);
        // 상속받은 HttpServletRequestWrapper 생성자의 매개변수가 HttpServletRequest이므로 형변환을 함
        HttpServletRequest request = (HttpServletRequest) req;

        // 로그인 상태, 로그인 회원 정보 전역 유지
        boolean isLogin = MemberUtil.isLogin(request); // isLogin(request) 메서드를 호출하여 현재 사용자의 로그인 상태를 확인
        Member member = MemberUtil.getMember(request);
        // MemberUtil 클래스의 getMember(request) 메서드를 호출하여 현재 로그인된 사용자의 정보를 가져옴
        request.setAttribute("isLogin", isLogin); // isLogin 변수의 값을 "isLogin"이라는 이름의 속성으로 요청 객체에 설정
        request.setAttribute("loggedMember", member); // member 객체를 "loggedMember"이라는 이름의 속성으로 요청 객체에 설정
    }
}
