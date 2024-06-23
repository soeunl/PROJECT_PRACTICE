package org.choongang.global.config.containers;

import org.junit.jupiter.api.Test;

public class BeanContainerTest {
    @Test
    void beanLoadTest() {
        BeanContainer bc = BeanContainer.getInstance(); // BeanContainer 클래스의 인스턴스를 bc 변수에 저장
        bc.loadBeans(); // BeanContainer 인스턴스의 loadBeans() 메서드를 호출



    }
}
