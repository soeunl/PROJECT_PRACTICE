package org.choongang.member.services;

import org.apache.ibatis.session.SqlSession;
import org.choongang.global.configs.DBConn;
import org.choongang.member.mapper.MemberMapper;
import org.choongang.member.validators.JoinValidator;
import org.choongang.member.validators.LoginValidator;

    // 객체 조립기
    // 회원쪽 기능을 조립해서 제공함
    // 싱글톤 패턴
    public class MemberServiceProvider {
        private static MemberServiceProvider instance; //  MemberServiceProvider 클래스의 유일한 인스턴스 생성

        private MemberServiceProvider() {} // 직접 new 연산자를 이용하여 객체를 생성하는 것을 방지하기 위해 private으로 생성자를 만들었음

        public static MemberServiceProvider getInstance() {
            if (instance == null) { // 처음 호출될 때 instance 변수가 null인지 확인
                instance = new MemberServiceProvider(); // null 이라면 아직 인스턴스가 생성되지 않은 것이므로, 새로운 MemberServiceProvider 객체를 생성하여 instance 변수에 저장
            }

            return instance; // 인스턴스 반환
        }

        public SqlSession getSession() {
            return DBConn.getSession();
        }
        // DBConn 클래스의 getSession 메서드를 호출하여 데이터베이스 연결 세션 객체(SqlSession 객체)를 가져옴

        public MemberMapper memberMapper() {
            return getSession().getMapper(MemberMapper.class);
        } // getSession 메서드를 호출하여 데이터베이스 연결 세션 객체를 가져오고, 가져온 세션 객체의 getMapper 메서드를 이용하여 MemberMapper 객체를 생성하여 반환

        public JoinValidator joinValidator() {
            return new JoinValidator(memberMapper());
        }
        // memberMapper 메서드를 호출하여 MemberMapper 객체를 생성하고, MemberMapper 객체를 인자로 하여 JoinValidator 클래스의 새로운 인스턴스를 생성하여 반환

        public JoinService joinService() {
            return new JoinService(joinValidator(), memberMapper());
        }
        // joinValidator 메서드와 memberMapper 메서드를 각각 호출하여 생성한 객체를 이용하여 새로운 JoinService 객체를 생성하여 반환

        public LoginValidator loginValidator () {
            return new LoginValidator(memberMapper());
        }
        // memberMapper 메서드를 호출하여 MemberMapper 객체를 생성하고, MemberMapper 객체를 인자로 하여 LoginValidator 클래스의 새로운 인스턴스를 생성하여 반환

        public LoginService loginService () {
            return new LoginService(loginValidator(), memberMapper());
        }
        // LoginValidator 메서드와 memberMapper 메서드를 각각 호출하여 생성한 객체를 이용하여 새로운 LoginService 객체를 생성하여 반환
    }
