package org.choongang.member.validators;

import org.choongang.global.exceptions.BadRequestException;
import org.choongang.global.validators.EmailValidator;
import org.choongang.global.validators.RequiredValidator;
import org.choongang.global.validators.Validator;
import org.choongang.member.controllers.RequestJoin;
import org.choongang.member.exceptions.DuplicatedMemberException;
import org.choongang.member.mapper.MemberMapper;

public class JoinValidator implements Validator<RequestJoin>, RequiredValidator, EmailValidator {
    // Validator<RequestJoin>, RequiredValidator, EmailValidator 인터페이스를 구현하여 필수 항목 검증, 이메일 형식 검증, 비밀번호 일치 검증 등을 수행

    private MemberMapper mapper;

    public JoinValidator(MemberMapper mapper) {
        this.mapper = mapper;
    } // MemberMapper 객체를 파라미터로 받는 생성자

    // Validator를 JoinService에 사용할 예정
    @Override
    // 회원가입 요청 데이터 검증
    public void check(RequestJoin form) { // form에서 데이터 추출
        String email = form.getEmail();
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();
        String userName = form.getUserName();
        boolean termsAgree = form.isTermsAgree();
        
        /* 필수 항목 검증 - 이메일, 비밀번호, 비밀번호 확인, 회원명, 약관 동의 */
        // 필수 항목이 누락되면 BadRequestException 예외를 발생시켜 오류 메시지를 전달
        checkRequired(email, new BadRequestException("이메일을 입력하세요"));
        checkRequired(password, new BadRequestException("비밀번호를 입력하세요"));
        checkRequired(confirmPassword, new BadRequestException("비밀번호를 확인하세요"));
        checkRequired(userName, new BadRequestException("회원명을 입력하세요"));
        checkTrue(termsAgree, new BadRequestException("약관에 동의하세요"));

        // 비밀번호, 비밀번호 확인 일치 여부
        // checkTrue 메서드를 사용하여 비밀번호와 비밀번호 확인이 일치하는지 검증
        // 일치하지 않으면 BadRequestException 예외를 발생시켜 오류 메시지를 전달
        checkTrue(password.equals(confirmPassword), new BadRequestException("비밀번호가 일치하지 않습니다."));

        // 이메일 형식 체크
        // checkEmail 메서드를 사용하여 이메일 형식이 유효한지 검증
        // 유효하지 않으면 BadRequestException 예외를 발생시켜 오류 메시지를 전달
        if (!checkEmail(email)) {
            throw new BadRequestException("이메일 형식이 아닙니다.");
        }
        // 비밀번호 자리수 체크
        // checkTrue 메서드를 사용하여 비밀번호 길이가 8자리 이상인지 검증
        checkTrue(password.length() >= 8, new BadRequestException("비밀번호는 8자리 이상 입력하세요"));
        // checked 값이 false 일 경우 e 객체를 throw 하여 예외를 발생

        // 이미 가입된 회원인지 체크
        // 해당 이메일로 이미 가입된 회원이 있는지 확인
        checkTrue(mapper.exist(email) == 0L, new
                DuplicatedMemberException()); // 반환된 회원수가 0인지 확인. 반환값이 0이 아니라면 예외가 발생함
    }
}
