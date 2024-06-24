package org.choongang.member.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.choongang.global.config.annotations.*;
import org.choongang.member.services.JoinService;

@Controller // 타입에 명시된 주소를 가지고 옴
@RequestMapping("/member") // 여러개도 가능하게 처리 해놓음
@RequiredArgsConstructor
public class MemberController {
    private final JoinService joinService;

    @GetMapping // /member
    public String index(HttpServletRequest request) {
        String attr = (String) request.getAttribute("commonValue");
        // System.out.println(attr);
        /*
        boolean bool = true;
        if (bool) {
            throw new RuntimeException("테스트1212121212");
        }

         */
        return "member/index"; // 해당 파일을 찾아서 응답
    }

    @GetMapping("/{mode}/test/{num}") // 요청하면 메서드에 주입
    public String join(@PathVariable("mode") String mode, @RequestParam("seq") int seq, RequestJoin form,  HttpServletResponse response, @PathVariable("num") int num) {
        System.out.printf("mode=%s, seq=%d, num=%d%n", mode, seq, num);
        System.out.println(form);
        joinService.process();
        return "member/join";
    }

   /*
    @ExceptionHandler({RuntimeException.class}) // 던져진 예외를 보고 다른 예외처리
    public String errorHandler(RuntimeException e1, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        System.out.println(e1); // 발생한대로 메서드에 주입되어 호출됨

        System.out.println(request);
        System.out.println(response);
        System.out.println(session);

        return "errors/error";
    } */
}
