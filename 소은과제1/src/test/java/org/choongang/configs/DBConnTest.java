package org.choongang.configs;

import org.apache.ibatis.session.SqlSession;
import org.choongang.global.configs.DBConn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DBConnTest {

    @Test
    @DisplayName("DB 연결 테스트")
    void dbConnectionTest() {
        assertDoesNotThrow(() -> {
            SqlSession session = DBConn.getSession(); // DBConn 클래스의 getSession() 메서드를 호출하여 DB 연결을 가져옴 
            System.out.println(session);
        });
    }
}
