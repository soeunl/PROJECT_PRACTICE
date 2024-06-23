package org.choongang.member.validators;

import jakarta.servlet.http.HttpServletRequest;
import org.choongang.global.exceptions.BadRequestException;
import org.choongang.global.validators.RequiredValidator;
import org.choongang.global.validators.Validator;
import org.choongang.member.entities.Member;
import org.choongang.member.mapper.MemberMapper;
import org.mindrot.jbcrypt.BCrypt;

public class LoginValidator implements Validator<HttpServletRequest>, RequiredValidator {
    // Validator<HttpServletRequest> 인터페이스를 구현하여 HttpServletRequest 객체를 파라미터로 받는 check() 메서드를 제공
    private MemberMapper mapper;

    public LoginValidator(MemberMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void check(HttpServletRequest form) { // HttpServletRequest 객체를 파라미터로 받는 check() 메서드
        String email = form.getParameter("email");
        String password = form.getParameter("password");
        // form 객체에서 이메일과 비밀번호 추출
        
        /* 필수 항목 검증 */
        checkRequired(email, new BadRequestException("이메일을 입력하세요"));
        checkRequired(password, new BadRequestException("비밀번호를 입력하세요"));
        // checkRequired 메서드를 사용하여 이메일과 비밀번호가 필수 항목인지 검증
        // 만약 필수 항목이 누락되면 BadRequestException 예외를 발생시켜 오류 메시지를 전달함
        
        // 이메일로 회원이 조회되는지 검증
        String message = "이메일 또는 비밀번호가 일치하지 않습니다";
        Member member = mapper.get(email); // 해당 이메일로 가입된 회원 정보를 조회
        checkTrue(member != null, new BadRequestException(message));
        // null이 아니어야 True 통과기 떄문에 !=로 했다
        // checkTrue 메서드 확인
        // 조회된 회원 정보가 null인 경우, 즉 해당 이메일로 가입된 회원이 없는 경우 BadRequestException 예외를 발생시켜 오류 메시지를 전달
        
        // 비밀번호 일치 여부 체크
        boolean isMatchd = BCrypt.checkpw(password, member.getPassword());
        // BCrypt 해시 알고리즘을 사용하여 두 비밀번호를 비교
        checkTrue(isMatchd, new BadRequestException(message)); // 검증을 하기위한 메서드
        // 만약 isMatchd가 false인 경우, 즉 입력된 비밀번호와 회원 정보의 비밀번호가 일치하지 않으면 BadRequestException 예외를 발생
    }
}
