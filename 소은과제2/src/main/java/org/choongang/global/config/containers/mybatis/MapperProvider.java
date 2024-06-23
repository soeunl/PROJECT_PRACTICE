package org.choongang.global.config.containers.mybatis;

import org.choongang.global.config.DBConn;
import org.choongang.global.config.annotations.mybatis.MapperScan;

import java.util.Arrays;

// 특정 위치에 있는 Mapper 인터페이스를 가져오는 역할
// 매퍼 조립기인가
@MapperScan({"org.choongang.member.mapper"})
public class MapperProvider {

    public static MapperProvider instance;

    private MapperProvider() {}

    public static MapperProvider getInstance() {
        if (instance == null) {
            instance = new MapperProvider();
        }
        return instance;
    } // 싱글톤 패턴으로 유일한 인스턴스 생성

    public <T> T getMapper(Class clz) {

        MapperScan mapperScan = getClass().getAnnotation(MapperScan.class); // 현재 클래스에 설정된 @MapperScan 애노테이션 정보 가져오기
        boolean isMapper = Arrays.stream(mapperScan.value()).anyMatch(s -> s.startsWith(clz.getPackageName())); // @MapperScan 애노테이션의 속성값을 가지고 와서 스트림으로 변환하고 패키지 이름 중 매칭되는 것이 있는지 검사..?
        // anyMatch() : 스트림의 요소 중 하나라도 특정 조건에 만족하는지 여부를 true 또는 false 값으로 반환
        // startsWith() : 특정 문자열로 시작하는지 확인

        if (isMapper) {
            return (T)DBConn.getSession().getMapper(clz);
        } // 데이터베이스 세션에서 해당 Mapper 인스턴스를 검색하여 반환. 그렇지 않으면 null을 반환

        return null;
    }
}
