package org.choongang.rdflection.exam02;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class Ex01 {
    public static void main(String[] args) throws Exception {
        Class clazz = Member.class; // Member 클래스에 대한 클래스 정보를 clazz 변수에 저장

        Constructor constructor = clazz.getDeclaredConstructors()[0];
        // getDeclaredConstructors 메서드를 사용하여 Member 클래스의 모든 생성자 정보를 가져옴
        // [0] 를 사용하여 첫 번째 생성자 정보(인덱스 0)를 가져와 constructor 변수에 저장함
        Object obj = constructor.newInstance();
        // 기본 생성자를 활용해 Member 클래스 객체를 생성하고 결과를 obj 변수에 저장

        Method[] methods = clazz.getDeclaredMethods(); // Member 클래스의 모든 메서드 정보를 배열 형태로 가져옴
        for (Method method : methods) {
            String name = method.getName(); // 현재 메서드의 이름을 가져와서 name 변수에 저장
            if (!name.startsWith("set")) { // 메서드 이름이 "set"으로 시작하지 않으면 continue 문을 통해 다음 메서드로 넘어감
                continue;
            }

            Class clz = method.getParameterTypes()[0]; // setter 메서드의 파라미터 타입 정보를 clz 변수에 저장함
            Object arg = null; // arg 초기화
            if (clz == String.class) { // setter 메서드의 파라미터 타입이 문자열 클래스와 동일하면 "문자열" 값을 arg 변수에 저장
                arg = "문자열";
            } else if (clz == LocalDateTime.class) { // setter 메서드의 파라미터 타입이 날짜 시간 클래스와 동일하면 LocalDateTime.now() 메서드를 사용하여 현재 날짜 시간 값을 arg 변수에 저장
                arg = LocalDateTime.now();
            }
//            obj.setUserId("문자열");

//          obj.method(arg..);
            method.invoke(obj, arg); // setter 메서드 호출
            // invoke는 일반적으로 메서드 호출을 의미하며, 주로 객체의 메서드를 호출하는 것을 뜻하지만, 리플렉션을 사용하여 동적으로 메서드를 호출할때도 사용
        }

        System.out.println(obj);
    }
}
