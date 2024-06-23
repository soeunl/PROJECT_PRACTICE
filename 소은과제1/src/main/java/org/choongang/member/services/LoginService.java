package org.choongang.member.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.choongang.global.validators.Validator;
import org.choongang.member.entities.Member;
import org.choongang.member.mapper.MemberMapper;

// 로그인 기능
// 로그인 기능을 처리하는 서비스를 만든다

public class LoginService {

    private Validator<HttpServletRequest> validator;
    private MemberMapper mapper;

    public LoginService(Validator<HttpServletRequest> validator, MemberMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }

    public void process(HttpServletRequest request) {
        // 로그인 유효성 검사
        validator.check(request); // validator여도 HttpServletRequest request으로 인해 LoginValidator의 check 메서드가 호출됨

        // 로그인 처리 - 회원 정보 조회, 세션에 유지

        String email = request.getParameter("email"); // request.getParameter("email") 메서드를 이용하여 요청 파라미터에서 이메일 정보를 가져옴
        Member member = mapper.get(email); // mapper의 get메서드를 호출하여 email 값을 기준으로 회원 정보를 조회
        // email 값을 파라미터로 받아 해당 이메일을 가진 회원 정보를 데이터베이스에서 검색하고, 검색된 정보를 Member 객체로 변환하여 반환

        HttpSession session = request.getSession(); // 세션 객체 가지고 오기
        // 현재 null값이므로 NullPointerException 발생
        session.setAttribute("member", member); //세션을 통해 이 값을 유지
        // member 객체를 "member"이라는 키값으로 session 객체에 저장
        // setAttribute 메서드는 키-값 쌍을 세션 객체에 저장하는 데 사용됨
    }
}
