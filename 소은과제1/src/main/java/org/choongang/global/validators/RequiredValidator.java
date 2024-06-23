package org.choongang.global.validators;

// 필수 입력값의 필수 여부와 논리값 검증을 위한 RequiredValidator 인터페이스를 정의
// 필수 항목이 비어 있거나 조건에 맞지 않는 논리값을 사용하는 상황을 방지하기 위한 검증

public interface RequiredValidator {

    // 디폴트 메서드가 2개 있음

    default void checkRequired(String str, RuntimeException e) { // 문자열str과 예외객체 e를 매개변수로 받음
        if(str == null || str.isBlank()) { // str 값이 null이거나 isBlank() 메서드를 통해 빈 문자열 또는 공백만으로 이루어져 있는지 검사함
            throw e; // 필수 입력값이 비어 있으면 e 객체를 throw 하여 예외를 발생시킴
        }
    }
    // 참인지 체크
    default void checkTrue(boolean checked, RuntimeException e) { // 논리값 checked와 예외 객체 e 를 매개 변수로 받음
        if(! checked) { // checked 값이 false 일 경우 e 객체를 throw 하여 예외를 발생시킴
            throw e;
        }
    }
}