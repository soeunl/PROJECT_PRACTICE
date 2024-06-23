package org.choongang.member.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member { // 데이터베이스의 테이블을 자바 객체로 매핑한 클래스
    private long userNo;
    private String email;
    private String password;
    private String userName;
    private LocalDateTime regDt;
    private LocalDateTime modDt;
}
// 데이터베이스 테이블과 변수 매핑하기 위함