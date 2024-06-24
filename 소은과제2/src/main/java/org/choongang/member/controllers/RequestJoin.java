package org.choongang.member.controllers;

import lombok.Data;
import org.choongang.global.config.annotations.DateTimeFormat;

import java.time.LocalDateTime;

@Data // 찾아서 값을 넣어준다
public class RequestJoin {
    private String email;
    private String password;
    private String confirmPassword;
    private boolean termsAgree;
    private byte num1;
    private short num2;
    private int num3;
    private long num4;
    private float num5;
    private double num6;
    private Byte num11;
    private Short num22;
    private Integer num33;
    private Long num44;
    private Float num55;
    private Double num66;
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss") // 문자열 데이터를 날짜 형식으로 바꾸어줌
    private LocalDateTime regDt;
}
