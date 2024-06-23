package org.choongang.rdflection.exam01;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Ex01 {
    public static void main(String[] args) throws Exception {
        Class clazz = JoinService.class; // JoinService 클래스에 대한 클래스 정보를 clazz 변수에 저장

        Constructor[] constructors = clazz.getDeclaredConstructors(); // clazz가 가지고 있는 생성자 정보를 배열 형태로 가져옴
        // getDeclaredConstructors 메서드는 public, private, protected 접근 지정자를 가진 모든 생성자를 다 가지고 옴
        for (Constructor constructor : constructors) { // 생성자 정보 배열을 반복문을 이용하여 순회함
            System.out.println("매개변수 갯수 : " + constructor.getParameterTypes().length); // 현재 생성자가 가지고 있는 매개변수의 갯수를 출력
            // getParameterTypes 메서드는 생성자의 매개변수 타입 정보를 배열 형태로 반환함

            List<Object> arguments = new ArrayList<>(); // 리스트 객체 arguments 생성

            for (Class clz : constructor.getParameterTypes()) { // 생성자의 매개변수 타입 정보 배열을 반복문을 이용하여 순회
                if (clz == String.class) { // 만약 매개변수 타입이 문자열 클래스와 동일하면 "문자열" 값을 arguments 리스트에 추가
                    arguments.add("문자열");
                } else if (clz == int.class) { // 만약 매개변수 타입이 기본 자료형 int와 동일하면 100 값을 arguments 리스트에 추가
                    arguments.add(100);
                } else if (clz == long.class) { // 만약 매개변수 타입이 기본 자료형 long과 동일하면 200L 값을 arguments 리스트에 추가
                    arguments.add(200L);
                }
            }

            Object obj = arguments.isEmpty() // arguments 리스트가 비어있는지 확인
                    ?constructor.newInstance() // 만약 비어있다면 (생성자가 매개변수를 가지지 않는 경우) constructor.newInstance() 메서드를 호출하여 생성자를 호출하고 결과 객체를 obj 변수에 저장
                    :
                    constructor.newInstance(arguments.toArray()); // 만약 비어있지 않다면 (생성자가 매개변수를 가지는 경우) arguments.toArray() 메서드를 이용하여 리스트를 배열로 변환한 후 constructor.newInstance(arguments.toArray()) 메서드를 호출하여 생성자를 호출하고 결과 객체를 obj 변수에 저장

            JoinService service = (JoinService) obj; // obj 변수에 담긴 객체를 JoinService 타입으로 변환하여 service 변수에 저장
            System.out.println(service); // service 변수에 담긴 객체 정보를 출력
        }


        /*
        Constructor c1 = constructors[0];
        Constructor c2 = constructors[1];
        Constructor c3 = constructors[1];

        JoinService s1 = (JoinService) c1.newInstance();
        JoinService s2 = (JoinService) c2.newInstance("값1", 100);
        JoinService s3 = (JoinService) c3.newInstance(new Object[] {"값2", 200, 300});

        /*
        for (Constructor constructor : constructors) {
            System.out.println(constructor);
        }

         */
    }
}
