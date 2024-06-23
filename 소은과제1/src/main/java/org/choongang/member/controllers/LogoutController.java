package org.choongang.member.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/member/logout")
public class LogoutController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(); // 현재 사용자 세션 가져오기
        session.invalidate(); // 세션 비우기(세션 무효화) - 로그아웃
        // 세션에 저장된 사용자 정보 모두 삭제

        resp.sendRedirect(req.getContextPath() + "/member/login"); // 로그아웃 로직을 수행하고 로그인 페이지로 이동함
    }
}
