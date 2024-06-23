package org.choongang.file.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@WebServlet("/file/upload")
public class FileUploadController extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 양식
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/file/upload.jsp");
        rd.forward(req, resp);
    } // 클라이언트의 요청을 다른 서블릿이나 JSP 페이지로 전달하는 RequestDispatcher 객체를 생성
    // 클라이언트가 GET 요청을 보내면 /WEB-INF/file/upload.jsp 라는 JSP 페이지로 이동시켜 파일 업로드 양식을 표시하도록 함

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 처리
        JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload();
        //  파일 업로드를 처리하기 위한 Jakarta Servlet Disk File Upload 라이브러리의 JakartaServletDiskFileUpload 객체를 생성

        List<DiskFileItem> items = upload.parseRequest(req);
        // multipart 형식 body 데이터를 일반 파일 데이터로 분리해서 조회 가능한 List 형태로 변환
        // 파트가 여러개이다보니까 분리해서 분석
        for (DiskFileItem item : items) {

            if (item.isFormField()) { // 일반 텍스트 형태의 양식 데이터
                // subject, content / 일반 텍스트 형식의 양식 데이터인지 확인
                String name = item.getFieldName();
                String value = item.getString(Charset.forName("UTF-8"));
                System.out.printf("name=%s, value=%s%n", name, value);
            } else {
                // 파일 데이터 - file
                String fileName = item.getName(); // 올렸던 파일의 이름을 알 수 있음
                String contentType = item.getContentType(); // 파일의 형식을 알 수 있음
                long size = item.getSize(); // 파일의 용량을 알 수 있음 (바이트 수)

                System.out.printf("파일이름 : %s, 파일 형식 : %s, 파일용량 : %s%n", fileName, contentType, size);

                File file = new File("D:/uploads/" + fileName); // 업로드된 파일을 저장할 경로와 이름으로 File 객체를 생성
                item.write(file.toPath()); // 메모리에 있는 파일 데이터가 file쪽 경로로 이동 -> 업로드된 파일을 실제 서버에 영구 저장
            }
        }
    }
}
