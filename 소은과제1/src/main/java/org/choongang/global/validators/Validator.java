package org.choongang.global.validators;

public interface Validator<T> {
    void check(T form); // check 메서드는 T 타입의 객체 form을 매개 변수로 받음
}
// Validator를 할 때 다형성을 가지고 작업할 예정 (다양한 데이터 유형 <T>을 검증하는 데 사용될 수 있음)
// 제네릭 타입을 사용하여 데이터 검증을 위한 Validator<T> 인터페이스를 정의
// 이 메서드는 form 객체에 대한 검증 로직을 구현해야 함
// 검증 로직은 객체의 유효성, 필수값 여부, 데이터 형식 등을 검사하는 내용을 포함함
// 인터페이스 구현체에서 필요에 따라 검증 로직을 자유롭게 구현 가능
// 사용 시에는 Validator<T> 인터페이스를 구현하는 클래스를 생성하고, check 메서드를 구현하여 검증 로직을 작성해야 함
// 검증 로직은 검증 대상 데이터의 특성에 맞게 작성
