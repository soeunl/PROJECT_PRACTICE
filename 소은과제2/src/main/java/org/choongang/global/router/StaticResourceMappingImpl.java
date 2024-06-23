package org.choongang.global.router;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.choongang.global.config.AppConfig;
import org.choongang.global.config.annotations.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StaticResourceMappingImpl implements StaticResourceMapping {

    /**
     * 정적 자원 경로인지 체크
     *
     * @param request
     * @return
     */
    @Override
    public boolean check(HttpServletRequest request) {

        // webapp/static 경로 유무 체크 // 존재 유무 확인..?
        return getStaticPath(request).exists();

    }

    @Override
    public void route(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // webapp/static 경로 및 파일 업로드 경로 조회
        File file = getStaticPath(request);
        if (file.exists()) { // 요청된 파일이 존재하는 경우
            Path source = file.toPath(); // 파일을 Path 객체로 변환
            String contentType = Files.probeContentType(source); // 해당 파일의 contentType을 찾기
            response.setContentType(contentType); // setContentType 메서드를 사용하여 응답 헤더에 "Content-Type" 필드 설정

            OutputStream out = response.getOutputStream(); // 데이터를 전송하는 출력 스트림 (OutputStream) 객체 생성

            InputStream in = new BufferedInputStream(new FileInputStream(file)); // 파일 입력 스트림 (InputStream) 객체 생성
            out.write(in.readAllBytes());
            // in.readAllBytes() 메서드를 사용하여 파일 내용을 모두 바이트 배열로 읽어들임
            // 읽어 들인 바이트 배열을 출력 스트림(out) 의 write 메서드를 사용하여 전송
        }
    }

    // webapp/static 경로 또는 파일 객체 경로 조회 File 객체 조회
    private File getStaticPath(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        // getRequestURI() : 요청 URI 전체 경로를 문자열로 반환
        // getContextPath() : 웹 애플리케이션의 컨텍스트 패스를 반환
        // replace() : 요청 URI에서 컨텍스트 패스를 제거
        // 파일의 상대경로를 나타냄
        String path = request.getServletContext().getRealPath("/static");
        // getServletContext() : 웹 애플리케이션의 서블릿 컨텍스트 객체를 반환
        // getRealPath() : static 경로에 해당하는 실제 파일 시스템 경로를 문자열로 얻어옴
        File file = new File(path + uri);
        // 파일 경로(path)와 요청 URI에서 추출한 상대 경로(uri) 를 연결하여 File 객체를 생성

        // webapp/static 경로에 파일이 없다면 파일 업로드 경로 File 객체 조회
        if (!file.exists()) { // 요청 URI에 해당하는 파일이 존재하지 않는 경우 실행되는 부분
            String uploadPath = AppConfig.get("file.upload.path"); // 애플리케이션 설정 정보에서 "file.upload.path" 속성값을 문자열로 가져옴
            String uploadUrl = AppConfig.get("file.upload.url"); //  웹 애플리케이션 상에서 업로드된 파일에 접근하기 위한 URL 패턴
            if (uploadPath != null && !uploadPath.isBlank() && uploadUrl != null && !uploadUrl.isBlank()) {
                uri = uri.replace(uploadUrl, ""); //요청 URI (uri) 에서 업로드된 파일에 접근하는 URL 패턴 (uploadUrl) 을 제거
                file = new File(uploadPath + uri);
            } // endif
        }
        return file;
    }
}
