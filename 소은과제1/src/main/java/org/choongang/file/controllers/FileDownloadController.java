package org.choongang.file.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;

@WebServlet("/file/download") // 서블릿을 /file/download URL에 매핑
// 사용자가 브라우저에서 /file/download URL을 입력하면 이 서블릿이 호출됨
// 바로 이 FileDownloadController가 호출됨
// 서블릿은 HTTP 요청을 처리하고 응답을 생성할 수 있으며 여기서는 컨트롤러의 역할을 하고 있음

public class FileDownloadController extends HttpServlet { // 이곳은 HttpServlet을 상속받아 서블릿 클래스이다 하고 선언
    // HttpServlet이 무엇이냐면 HTTP 요청 처리, 동적 HTML 페이지 생성, 쿠키 쿠키 및 세션 관리, 파일 업로드 및 다운로드, 데이터베이스 액세스의 기능을 할 수 있는 추상메서드!

    // doGet과 doPost 재정의 - doGet은 보통 리소스를 검색하거나 정보를 요청하는데 사용되고, doPost는 데이터를 전송하거나 작업을 수행하는데 사용됨
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File file = new File("D:uploads/1.png"); // 파일 객체 생성
        String contentType = Files.probeContentType(file.toPath()); // File 객체를 Path 객체로 변환하고, 콘텐트타입을 확인
        // Path 객체가 무엇인가?? = 파일 시스템의 경로를 나타내는 객체
        // 윈도우즈에서 한글 깨짐 문제 발생하면 = 인코딩 UTF-8 -> CPC949, EUC-KR, ISO8859_1
        String fileName = new String(file.getName().getBytes(), "ISO8859_1"); // 파일 이름을 문자열로 가지고 오기

        resp.setHeader("Content-Disposition", "attachment; filename=" + fileName); // 브라우저에게 파일을 다운로드하도록 지시
        resp.setContentType(contentType); // 콘텐트 타입 설정하여 브라우저에게 알림
        resp.setHeader("Cache-Control", "must-revalidate"); // 브라우저를 캐싱하지 않고 매번 다시 갱신
        resp.setIntHeader("Expires", 0); // 캐싱 만료 시간을 0으로 설정
        resp.setContentLengthLong(file.length()); // 파일의 크기를 바이트 단위로 지정

//        OutputStream out = resp.getOutputStream();
//        PrintWriter out = resp.getWriter();
//        out.print("ABC");
//        out.print("DEF");

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            OutputStream out = resp.getOutputStream(); // 출력 스트림 생성
            out.write(bis.readAllBytes()); // bis로부터 모든 데이터를 읽어서 출력
        } 
        // try-with-resources 사용(자원 자동해제)
            // file 변수는 전송할 파일을 나타냄
            // FileInputStream(file) : 파일을 읽기 위한 스트림 객체 생성
            // BufferedInputStream : 버퍼 스트림 객체 생성
    }
}
