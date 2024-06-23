package org.choongang.global.validators;

// 이메일 유효성 검증을 위한 EmailValidator 인터페이스를 정의

public interface EmailValidator {
    default boolean checkEmail(String email) { // 문자열 email을 매개 변수로 받아 이메일 유효성을 검증하고 boolean 값을 반환
        // 계정@도메인.com | co.kr
        String pattern = "[^@]+@[^.]+\\.[a-z]+"; // 이메일 주소 형식을 검증하는 정규 표현식 패턴(pattern)
        // 이메일 주소의 아이디 부분 (첫 번째 @ 기호 이전 부분)에 적어도 하나의 문자가 포함되어 있고 @ 기호로 시작하지 않도록 함
        // 이메일 주소의 도메인 부분 (마지막 @ 기호 이후 부분)에 적어도 하나의 문자가 포함되어 있고 . 기호로 시작하거나 끝나지 않도록 함
        // 이메일 주소의 최상위 도메인 (마지막 . 이후 부분)이 소문자로만 구성되어 있도록 함

        return email.matches(pattern);
        // 이메일 주소 문자열(email)이 정규 표현식 패턴(pattern)과 일치하는지 검사
        // 일치하면 true, 일치하지 않으면 false를 반환
    }
}
