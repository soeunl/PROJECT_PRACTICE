package org.choongang.global.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class DBConn {
    private static SqlSessionFactory factory;

    static {
        try {
            String mode = System.getenv("mode"); // getenv() : 환경 변수의 값을 가져오는 함수
            // 시스템 환경 변수 mode 값을 가져옴
            mode = mode == null || !mode.equals("prod") ? "dev":"prod"; // mode값이 "prod"가 아니면 "dev" 값을 사용하도록 설정

            Reader reader = Resources.getResourceAsReader("org/choongang/global/config/mybatis-config.xml");
            factory = new SqlSessionFactoryBuilder().build(reader, mode); // SqlSessionFactoryBuilder 객체를 생성하여 build 메서드를 호출
            // 첫 번째 매개변수인 reader는 설정 파일 리더 객체, 두 번째 매개변수인 mode 는 앞서 결정한 환경 모드 값
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSession getSession(boolean autoCommit) {
        return factory.openSession(autoCommit);
    }
    // 데이터베이스 트랜잭션 자동 커밋 여부를 설정
    /**
     * 기본 getSession() 메서드를 통해서 SqlSession 객체를 생성하는 경우는
     * 하나의 SQL 쿼리 실행마다 COMMIT을 하도록 autoCommit을 true로 설정합니다.
     *
     * @return
     */
    public static SqlSession getSession() {
        return getSession(true);
    }
    // 기본값인 true 를 매개변수로 하여 getSession(boolean autoCommit) 메서드를 호출
    // getSession(true) 호출을 통해 데이터베이스 세션을 얻을 때 자동 커밋이 활성화됨
}
