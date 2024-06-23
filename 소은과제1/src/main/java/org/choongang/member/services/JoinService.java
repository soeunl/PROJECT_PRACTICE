package org.choongang.member.services;

import jakarta.servlet.http.HttpServletRequest;
import org.choongang.global.exceptions.BadRequestException;
import org.choongang.global.validators.Validator;
import org.choongang.member.controllers.RequestJoin;
import org.choongang.member.entities.Member;
import org.choongang.member.mapper.MemberMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

// 회원 가입 기능
public class JoinService {

    private Validator<RequestJoin> validator;
    private MemberMapper mapper;

    public JoinService(Validator<RequestJoin> validator, MemberMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }


    // 생성된 RequestJoin 객체는 process(RequestJoin form) 메서드로 전달되어 유효성 검사, 비밀번호 해시화, 데이터 저장 등의 로직 수행
    public void process(RequestJoin form) {

        // 유효성 검사
        validator.check(form); // validator여도 RequestJoin form으로 인해 JoinValidator의 check 메서드가 호출됨

        // 비밀번호 해시화
        // hashpw 메서드는 비밀번호와 salt를 매개변수로 받아서 비밀번호로 만듬, gensalt는 12바이트로 끊어서 비밀번호를 해시화
        String hash = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt(12));

        // BCrypt = 비밀번호를 해시화하는 알고리즘, 사용하려면 의존성 추가 필요, JBCrypt

        Member member = new Member(); // 사용자 입력값으로 새로운 Member 객체를 만드는 과정 (register 쿼리 실행을 위해 필요)
        member.setEmail(form.getEmail());
        member.setPassword(hash);
        member.setUserName(form.getUserName());

        int result = mapper.register(member); // 위에서 생성된 member 객체로 register 쿼리 실행하여 데이터베이스에 값 넣기
        // 저장된 레코드의 수를 result에 저장

        if(result < 1) { // 저장된 레코드의 수가 1보다 작으면 실패
            throw new BadRequestException("회원가입에 실패했습니다");
        } // 회원가입이 됐으면 통과, 안됐으면 예외처리.
    }

    // HTTP 요청 데이터를 기반으로 RequestJoin 객체를 생성
    public void process(HttpServletRequest request) {

        // Object.requiredNonNullElse(객체, null일때 기본값);
        String _termsAgree = Objects.requireNonNullElse(request.getParameter("termsAgree"), "false"); // 요청 파라미터 중 필수값인 termsAgree를 가지고 오고, 파라미터가 없는 경우 기본값 false를 사용
        boolean termsAgree = Boolean.parseBoolean(_termsAgree); // 얻어온 termsAgree 값을 Boolean.parseBoolean 메서드를 이용하여 boolean으로 변환

        RequestJoin form = RequestJoin.builder()
                // RequestJoin.builder를 이용하여 빌더 패턴으로 RequestJoin 객체를 생성
                // 요청 데이터가 들어오면 DTO로 변환 작업 중
                .email(request.getParameter("email"))
                .password(request.getParameter("password"))
                .confirmPassword(request.getParameter("confirmPassword"))
                .userName(request.getParameter("userName"))
                .termsAgree(termsAgree)
                .build();
                // request.getParameter 메서드를 이용하여 이메일, 비밀번호, 비밀번호 확인, 사용자 이름, 약관 동의 여부 등의 정보를 가져와 RequestJoin 객체의 필드에 설정

        process(form); // 생성된 RequestJoin 객체를 파라미터로 하여 같은 클래스 내의 process(RequestJoin form) 메서드를 호출

    }
}
