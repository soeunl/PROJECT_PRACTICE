package org.choongang.global.configs;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class DBConn {
    // SqlSession을 생성하고 연결
    // SqlSession이란  MyBatis 프레임워크에서 데이터베이스와 상호 작용을 수행하는 데 사용되는 주요 객체
    // SqlSession을 사용하려면 먼저 SqlSessionFactory를 생성해야 함
    private static SqlSessionFactory factory;
    // static 변수 factory를 선언하고, SqlSessionFactory 타입의 객체를 할당
    // SqlSessionFactory는 데이터베이스 연결 정보와 설정을 포함하는 객체
    // SqlSession은 SqlSessionFactory를 사용하여 생성됨

    static {
        try {
            Reader reader = Resources.getResourceAsReader("org/choongang/global/configs/mybatis-config.xml");
            // org.choongang.global.configs 패키지 내의 mybatis-config.xml 파일을 로드함
            // 파일로부터 텍스트 데이터를 읽어옴(리소스 읽어오기)
            // 이를 Reader 객체로 반환함

            factory = new SqlSessionFactoryBuilder().build(reader);
            // Reader 객체로부터 받은 정보를 활용해 SqlSessionFactory 객체 생성
            // SqlSessionFactoryBuilder는 MyBatis 프레임워크에서 SqlSessionFactory 객체를 생성하는 데 사용되는 클래스
            // .build(reader)를 통해 reader 객체 (즉, "mybatis-config.xml" 파일의 내용) 에 포함된 설정 정보를 사용하여 SqlSessionFactory 인스턴스를 구축

        } catch (IOException e) { // 파일을 읽는 데 문제가 발생했을 때 발생하는 오류 IOException
            e.printStackTrace();
        }
    }

    // SqlSession 객체를 생성하고 반환하는 메서드

    public static SqlSession getSession(boolean autoCommit)
    // 매개 변수 boolean autoCommit는 트랜잭션 자동 커밋 여부를 결정함
    // true이면 쿼리 실행 후 자동으로 커밋 / false이면 커밋을 수동으로 수행
    {
        String mode = System.getenv("mode"); // "mode"라는 이름의 환경 변수 값을 가져오기

        if (mode != null && mode.equals("test")) autoCommit = false; // "mode" 환경 변수가 존재하고 값이 "test"이면 autoCommit 변수를 false로 설정
        // 테스트 모드에서는 쿼리 실행 후 자동 커밋을 비활성화하겠다는 뜻

        return factory.openSession(autoCommit); // SqlSessionFactory factory 객체를 사용하여 SqlSession 객체를 생성 (autoCommit 매개 변수로 전달된 값을 사용하여 SqlSession 객체를 생성함)
        // 생성된 SqlSession 객체는 메서드의 반환값으로 전달됨
    }

    public static SqlSession getSession() {
        return getSession(true);
    }
}   // 매개 변수가 없으면 자동 커밋을 기본값으로 설정
    // SqlSession 객체를 생성할 때 트랜잭션 자동 커밋을 활성화
    // 매개 변수를 받지 않으므로 autoCommit 값을 직접 설정할 수 없음