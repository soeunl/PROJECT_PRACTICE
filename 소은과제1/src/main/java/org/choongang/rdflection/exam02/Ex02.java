package org.choongang.rdflection.exam02;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Ex02 {
    public static void main(String[] args) throws Exception {
        Class clazz = Member.class; // Member 클래스에 대한 클래스 정보 clazz 변수에 저장
        Constructor constructor = clazz.getDeclaredConstructors()[0]; // getDeclaredConstructors 메서드를 사용하여 Member 클래스의 모든 생성자 정보를 가져오기
        // [0]을 사용하여 첫 번째 생성자 정보를 가져와 constructor 변수에 저장
        Object obj = constructor.newInstance();
        // 기본 생성자를 이용해 Member 클래스 객체를 생성하고 결과를 obj 변수에 저장
        Field field = clazz.getDeclaredField("str"); // getDeclaredField 메서드를 사용하여 Member 클래스의 모든 필드 정보 중에서 이름이 "str"인 필드 정보를 field 변수에 저장
        field.setAccessible(true); // field에 저장된 값이 private 접근제어자를 가지고 있을 경우 이 코드는 필드에 대한 접근 권한을 강제로 허용
        System.out.println(field);
        field.set(obj, "DEF"); // obj 객체의 "str" 값을 "DEF"로 설정

        Member member = (Member)obj;
        System.out.println(member);
        System.out.println(member.str);
    }
}
