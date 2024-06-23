package org.choongang.global.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 사이트 설정 로드 및 조회
 *
 */
public class AppConfig {
    private final static ResourceBundle bundle; // 설정 정보를 담고 있는 ResourceBundle 객체
    private final static Map<String, String> configs; // 설정 정보를 키-값 쌍으로 저장하는 Map 객체
    static {
        // 환경 변수 mode에 따라 설정파일을 분리 예) prod이면 application-prod.properties로 읽어온다.
        String mode = System.getenv("mode"); // mode가 존재한다면 application-<mode>.properties 파일을 읽어옴
        // getenv() : 환경 변수의 값을 가져오는 함수
        mode = mode == null || mode.isBlank() ? "":"-" + mode; // mode가 설정되어 있지 않거나 공백이라면 기본 파일인 application.properties 를 읽어옴

        bundle = ResourceBundle.getBundle("application" + mode); // ResourceBundle 객체를 생성하여 설정 파일 정보를 로딩
        configs = new HashMap<>(); // 키-값 쌍으로 configs 맵에 저장
        Iterator<String> iter = bundle.getKeys().asIterator(); // bundle 에서 설정 정보의 키 목록을 가져옴
        // asIterator() :
        // asIterator() 메서드를 사용하여 Iterator<String> 객체로 변환
        while(iter.hasNext()) {
            // hasNext(): 다음 요소가 있는지 확인하는 메서드
            String key = iter.next();
            // next(): 다음 요소를 가져오는 메서드
            String value = bundle.getString(key); // key값을 사용하여 ResourceBundle 객체 bundle 에서 해당 키에 설정된 값을 가져옴
            configs.put(key, value); // 가져온 키-값 쌍을 configs 맵에 저장
        }
    }

    public static String get(String key) {
        return configs.get(key);
    }
}
