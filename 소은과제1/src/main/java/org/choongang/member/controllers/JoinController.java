package org.choongang.member.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.choongang.global.exceptions.CommonException;
import org.choongang.member.services.JoinService;
import org.choongang.member.services.MemberServiceProvider;

import java.io.IOException;

import static org.choongang.global.MessageUtil.alertError;
import static org.choongang.global.MessageUtil.go;

// 회원가입 기능의 컨트롤러
// doGet 메서드는 회원 가입 페이지를 보여주고, doPost 메서드는 사용자 입력 정보를 처리하여 회원 가입 로직을 수행함
// 회원 가입 처리 후 로그인 페이지로 이동하거나 에러 상황 시 에러 메시지를 표시함

@WebServlet("/member/join")
public class JoinController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 회원가입 페이지를 보여주는 역할
        RequestDispatcher rd = req.getRequestDispatcher
                ("/WEB-INF/templates/member/join.jsp"); // join.jsp를 가져오기 위한 RequestDispatcher 객체를 생성
        rd.forward(req, resp); // 요청 정보와 응답 정보를 활용해 forward 메서드를 호출하여 페이지를 바꿈 (버퍼 치환)
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       // 서비스를 넣어서 처리 / 회원 가입 form 데이터를 처리하는 역할
        try {JoinService service = MemberServiceProvider.getInstance().joinService(); // MemberServiceProvider 클래스를 통해 JoinService 객체 가져오기
        service.process(req); // 요청데이터가 req에 들어 있다(클라이언트가 입력한 회원 가입 정보가 담김)
            // JoinService 객체의 process(req) 메서드를 호출하여 회원 가입 로직을 수행함
            // req를 service로 처리해서 DTO에 넣음

            go(req.getContextPath() + "/member/login", "parent", resp); // 회원 가입 처리가 성공하면 go 메서드를 호출하여 로그인 페이지로 이동함
            //resp.sendRediret(req.getContextPath() + "/member/login");
        } catch (CommonException e) {
            alertError(e, resp); // 예외가 발생하면 alertError 메서드를 호출하여 에러 메시지를 띄움
        }
    }
}
