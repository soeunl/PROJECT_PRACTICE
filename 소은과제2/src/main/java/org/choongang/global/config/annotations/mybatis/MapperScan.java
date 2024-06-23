package org.choongang.global.config.annotations.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 매퍼 스캔 경로 설정
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperScan {
    String[] value();
}
// Mapper 인터페이스를 자동으로 검색하고 등록하는 기능..?
