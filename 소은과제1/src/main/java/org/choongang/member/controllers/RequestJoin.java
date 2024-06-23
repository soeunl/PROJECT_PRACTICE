package org.choongang.member.controllers;

import lombok.Builder;
import lombok.Data;

@Data // (getter, setter, toString, equals, hashCode 등)자동으로 생성해줌
@Builder // 클래스의 생성자를 간편하게 작성할 수 있는 Builder 패턴을 사용할 수 있도록 해줌

public class RequestJoin { // 회원 가입 요청 시 전달되는 데이터를 담기 위한 DTO 클래스
    private String email;
    private String password;
    private String confirmPassword;
    private String userName;
    private boolean termsAgree;
}
