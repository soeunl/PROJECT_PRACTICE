package org.choongang.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.choongang.member.entities.Member;

public class MemberUtil {
    // 로그인 여부 조회
    public static boolean isLogin(HttpServletRequest request) {
        // 로그인 한 상태인지 체크
        // 로그인 정보가 존재하는지 확인하여 로그인 여부를 반환
        return getMember(request) != null; // null이 아니면 로그인 상태로 판단하고 true를 반환
        // null인 경우는 로그인되지 않은 상태로 판단하고 false를 반환
    }

    // 로그인 된 사용자 정보 조회
    public static Member getMember(HttpServletRequest request) {
        // 세션을 가지고 오고 회원 정보도 가지고 온다
        HttpSession session = request.getSession(); // HttpSession 객체를 가져옴
        Member member = (Member) session.getAttribute("member"); // "member"라는 이름의 세션 속성 값을 Member 객체로 변환하여 가져옴
        return member;
        // 로그인한 정보를 조회할 수 있는 편의 메서드 정의
    }
}
