package org.choongang.file.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/upload/*") // uploads 뒤에 파일명이 나오면 그 파일을 찾아간다
public class FileSearchController extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI().replace(req.getContextPath(), ""); // 현재 접속 주소 가져오기
        // /upload/파일경로
        System.out.println(uri);

        Pattern pattern = Pattern.compile("^/upload/(.+)"); // 요청 주소 확인
        Matcher matcher = pattern.matcher(uri); // 위에서 등록한 주소 가져옴
        if (matcher.find()) { // 요청 주소가 정의된 패턴과 일치하는지 확인
            String fileName = matcher.group(1); // (.+)부분을 의미함
            // (.+)부분을 fileName 변수에 저장

            File file = new File("D:/uploads/"+fileName); // 서버 상의 파일 위치를 정의 / fileName 을 연결하여 전체 파일 경로를 만들어 file 객체에 저장

            if(file.exists()) { // 파일이 실제로 서버에 존재하는지 확인
                    Path source = file.toPath(); // Path 객체로 변환
                    String contentType = Files.probeContentType(source); // 파일 타입 알아내기
                    resp.setContentType(contentType); // 클라이언트에게 전송할 응답 헤더에 파일 종류 정보를 설정

                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){ // 자원 자동 해제를 위한 문법 (try-with-resources) 사용 / 파일을 읽기 위한 스트림 객체를 생성
                    // servletOutputStream
                    OutputStream out = resp.getOutputStream(); // 출력스트림 불러오기
                    out.write(bis.readAllBytes()); // 데이터를 읽어 출력 스트림을 사용해 읽어온 데이터를 전송
                }

                return;
            }
        }
        // 파일이 없는 경우 - 404 응답코드
        // 서버에서 클라이언트에게 요청된 리소스를 찾을 수 없다는 것을 알리는 HTTP 404 오류 응답을 전송
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
