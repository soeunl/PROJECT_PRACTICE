package org.choongang.global.router;

import jakarta.servlet.http.HttpServletRequest;
import org.choongang.global.config.annotations.*;
import org.choongang.global.config.containers.BeanContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HandlerMappingImpl implements HandlerMapping{


    private String controllerUrl;

    @Override
    public List<Object> search(HttpServletRequest request) {

        List<Object> items = getControllers();

        for (Object item : items) {
            /** Type 애노테이션에서 체크 S */
            // @RequestMapping, @GetMapping, @PostMapping, @PatchMapping, @PutMapping, @DeleteMapping // 요청 처리에 관련된 애노테이션들
            if (isMatch(request,item.getClass().getDeclaredAnnotations(), false, null)) {
                // 메서드 체크
                // getDeclaredAnnotations() : 모든 애노테이션을 배열 형태로 반환, 특정 요소에 어떤 애노테이션이 선언되어 있는지 확인하는 데 사용
                for (Method m : item.getClass().getDeclaredMethods()) {
                    if (isMatch(request, m.getDeclaredAnnotations(), true, controllerUrl)) {
                        return List.of(item, m);
                    }
                }
            }
            /** Type 애노테이션에서 체크 E */

            /**
             * Method 애노테이션에서 체크 S
             *  - Type 애노테이션 주소 매핑이 되지 않은 경우, 메서드에서 패턴 체크
             */
            for (Method m : item.getClass().getDeclaredMethods()) {
                if (isMatch(request, m.getDeclaredAnnotations(), true, null)) {
                    return List.of(item, m); // 요청과 메서드 정보가 일치하는지 여부를 판단
                }
            }
            /* Method 애노테이션에서 체크 E */
        }

        return null;
    }


    /**
     *
     * @param request
     * @param annotations : 적용 애노테이션 목록
     * @param isMethod : 메서드의 에노테이션 체크인지
     * @param prefixUrl : 컨트롤러 체크인 경우 타입 애노테이션에서 적용된 경우
     * @return
     */
    private boolean isMatch(HttpServletRequest request, Annotation[] annotations, boolean isMethod, String prefixUrl) {

        String uri = request.getRequestURI(); // request 객체로부터 요청 URI를 추출하여 uri 변수에 저장
        // getRequestURI() : 요청 URL의 경로 부분을 추출
        String method = request.getMethod().toUpperCase(); // 메서드를 추출하고 대문자로 변환하여 method 변수에 저장
        // getMethod() : 사용된 HTTP 메서드를 반환 (예: "GET", "POST", "PUT", "DELETE" 등)
        String[] mappings = null;
        for (Annotation anno : annotations) {

            if (anno instanceof RequestMapping) { // 모든 요청 방식 매핑
                RequestMapping mapping = (RequestMapping) anno;
                mappings = mapping.value();
            } else if (anno instanceof GetMapping && method.equals("GET")) { // GET 방식 매핑
                GetMapping mapping = (GetMapping) anno;
                mappings = mapping.value();
            } else if (anno instanceof PostMapping && method.equals("POST")) {
                PostMapping mapping = (PostMapping) anno;
                mappings = mapping.value();
            } else if (anno instanceof PutMapping && method.equals("PUT")) {
                PutMapping mapping = (PutMapping) anno;
                mappings = mapping.value();
            } else if (anno instanceof PatchMapping && method.equals("PATCH")) {
                PatchMapping mapping = (PatchMapping) anno;
                mappings = mapping.value();
            } else if (anno instanceof DeleteMapping && method.equals("DELETE")) {
                DeleteMapping mapping = (DeleteMapping) anno;
                mappings = mapping.value();
            }

            if (mappings != null && mappings.length > 0) { // 애노테이션으로부터 추출한 매핑 정보가 null이 아니고 길이가 0보다 크면 실행

                String matchUrl = null;
                if (isMethod) {
                    String addUrl = prefixUrl == null ? "" : prefixUrl;
                    // 메서드인 경우 *와 {경로변수} 고려하여 처리
                    for(String mapping : mappings) {
                        String pattern = mapping.replace("/*", "/\\w*") // /* 패턴을 /\\w* 정규 표현식으로 바꿈
                                .replaceAll("/\\{\\w+\\}", "/(\\\\w*)");
                                // /{w+} 패턴을 /(\\[\\\\w*\\]) 정규 표현식으로 바꿈
                                // /{w+}는 경로 변수를 나타내는 패턴
                                // /\\[\\\\w*\\]는 경로 변수에 포함될 수 있는 알파벳 문자 (a-z, A-Z), 숫자 (0-9), 언더스코어 (_) 1개 이상을 나타내는 정규 표현식
                        Pattern p = Pattern.compile("^" + request.getContextPath() + addUrl + pattern + "$");
                        Matcher matcher = p.matcher(uri);
                        return matcher.find();
                        // 각 매핑 문자열을 정규 표현식 기반으로 변환하여 요청 URI와의 매칭 과정에 사용할 준비를 하는 작업을 수행
                    }
                } else {
                    List<String> matches = Arrays.stream(mappings) // mappings 배열을 문자열 스트림으로 변환
                            .filter(s -> uri.startsWith(request.getContextPath() + s)).toList(); // 조건에 따라 URI 패턴 스트림을 필터링
                            // 요청 URI의 시작 부분과 잠재적으로 일치하는 패턴을 필터링
                            // .toList(): 필터링된 스트림을 다시 List로 변환
                    if (!matches.isEmpty()) { // matches 목록이 비어 있는지 (일치하는 패턴이 없는지) 확인
                        matchUrl = matches.get(0); // 일치하는 경우 matches 목록의 첫 번째 요소를 가져와 matchUrl 변수에 할당
                        controllerUrl = matchUrl; // matchUrl의 값을 controllerUrl 변수에도 할당
                    }
                }
                return matchUrl != null && !matchUrl.isBlank(); // matchUrl을 기반으로 boolean 값을 반환
                // matchUrl이 null이 아니고 빈 문자열도 아닌지 확인
                // 두 조건 모두 참인 경우 true를 반환, 그렇지 않으면 false를 반환
            }
        }

        return false;
    }

    /**
     * 모든 컨트롤러 조회
     *
     * @return
     */
    private List<Object> getControllers() { // @Controller 또는 @RestController 애노테이션이 붙은 컨트롤러 객체들만 포함하는 리스트를 반환
       return BeanContainer.getInstance().getBeans().entrySet()
               // entrySet() : Bean 정보를 Map 형태의 데이터 구조 (키-값 쌍) 컬렉션으로 변환
                    .stream() // 스트림으로 변환
                    .map(s -> s.getValue())
                .filter(b -> Arrays.stream(b.getClass().getDeclaredAnnotations()).anyMatch(a -> a instanceof Controller || a instanceof RestController)) // 조건에 맞는 요소만 남기고 나머지는 제외
               // Bean 객체의 클래스에 @Controller 또는 @RestController 애노테이션이 하나 이상 존재하는 경우 필터를 통과
                .toList(); // .toList(): 필터링된 스트림을 다시 List로 변환
    }
}
