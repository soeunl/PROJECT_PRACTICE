package org.choongang.global.router;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.choongang.global.config.containers.BeanContainer;

import java.io.IOException;

// 모든 요청들이 다 여기로 들어옵니다
@WebServlet("/") // 여기서 모든 요청을 처리
public class DispatcherServlet extends HttpServlet  {

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        // ServletRequest req: 클라이언트의 요청 정보를 담고 있는 객체
        // ServletResponse res: 서버의 응답 정보를 설정하는 객체
        // req 는 일반적인 ServletRequest 타입이지만 실제 데이터를 다루기 위해 HttpServletRequest 타입으로 형변환
        // res도 마찬가지
        BeanContainer bc = BeanContainer.getInstance(); // BeanContainer의 인스턴스를 가져옴
        // 매번 다르기 때문에 수동으로 세팅
        bc.addBean(HttpServletRequest.class.getName(), request);
        bc.addBean(HttpServletResponse.class.getName(), response);
        bc.addBean(HttpSession.class.getName(), request.getSession());

        bc.loadBeans(); // 모든 객체를 다 불러오는 것
        // 기준이 있는데 - 특정 애노테이션이 있는 객체만 가지고 와서 거기서 필요한 의존성을 주입해줌

        RouterService service = bc.getBean(RouterService.class); // 이미 생성되어 있는 객체를 불러와서 사용
        service.route(request, response); // RouterService 객체의 route 메서드를 호출
        // 요청 정보를 분석하여 실제적인 요청처리 로직을 담당하는 서비스 객체를 찾아 실행하고, 결과를 응답 객체를 통해 클라이언트에게 전달하는 역할을 수행
    }   // 라우터를 통해 요청 처리
} 
