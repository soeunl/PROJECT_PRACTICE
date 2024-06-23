package org.choongang.global.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.choongang.global.config.annotations.*;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HandlerAdapterImpl implements HandlerAdapter {

    private final ObjectMapper om;

    public HandlerAdapterImpl() {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, List<Object> data) {

        Object controller = data.get(0); // 컨트롤러 객체
        Method method = (Method)data.get(1); // 찾은 요청 메서드

        String m = request.getMethod().toUpperCase(); // 요청 메서드
        Annotation[] annotations = method.getDeclaredAnnotations();

        /* 컨트롤러 애노테이션 처리 S */
        String[] rootUrls = {""};
        for (Annotation anno : controller.getClass().getDeclaredAnnotations()) {
            rootUrls = getMappingUrl(m, anno);
        }
        /* 컨트롤러 애노테이션 처리 E */

        /* PathVariable : 경로 변수 패턴 값 추출  S */
        String[] pathUrls = {""};
        Map<String, String> pathVariables = new HashMap<>();
        for (Annotation anno : annotations) {
            pathUrls = getMappingUrl(m, anno);
        }

        if (pathUrls != null) {
            Pattern p = Pattern.compile("\\{(\\w+)\\}");
            for (String url : pathUrls) {
                Matcher matcher = p.matcher(url);

                List<String> matched = new ArrayList<>();
                while (matcher.find()) {
                    matched.add(matcher.group(1));
                }

                if (matched.isEmpty()) continue;;

                for (String rUrl : rootUrls) {
                    String _url = request.getContextPath() + rUrl + url;
                    for (String s : matched) {
                        _url = _url.replace("{" + s + "}", "(\\w*)");
                    }

                    Pattern p2 = Pattern.compile("^" + _url+"$");
                    Matcher matcher2 = p2.matcher(request.getRequestURI());
                    while (matcher2.find()) {
                        for (int i = 0; i < matched.size(); i++) {
                            pathVariables.put(matched.get(i), matcher2.group(i + 1));
                        }
                    }
                }
            }
        }

        /* PathVariable : 경로 변수 패턴 값 추출 E */

        /* 메서드 매개변수 의존성 주입 처리 S */
        List<Object> args = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            try {
                Class cls = param.getType();
                String paramValue = null;
                for (Annotation pa : param.getDeclaredAnnotations()) {
                    if (pa instanceof RequestParam requestParam) { // 요청 데이터 매칭
                        String paramName = requestParam.value();
                        paramValue = request.getParameter(paramName);
                        break;
                    } else if (pa instanceof PathVariable pathVariable) { // 경로 변수 매칭
                        String pathName = pathVariable.value();
                        paramValue = pathVariables.get(pathName);
                        break;
                    }
                }

                if (cls == int.class || cls == Integer.class || cls == long.class || cls == Long.class || cls == double.class || cls == Double.class ||  cls == float.class || cls == Float.class) {
                    paramValue = paramValue == null || paramValue.isBlank()?"0":paramValue;
                } // 변수 cls의 유형을 확인하고 조건을 충족하면 다른 변수 paramValue에 기본값("0")을 할당

                if (cls == HttpServletRequest.class) {
                    args.add(request);
                } else if (cls == HttpServletResponse.class) {
                    args.add(response);
                } else if (cls == int.class) {
                    args.add(Integer.parseInt(paramValue));
                } else if (cls == Integer.class) {
                    args.add(Integer.valueOf(paramValue));
                } else if (cls == long.class) {
                    args.add(Long.parseLong(paramValue));
                } else if (cls == Long.class) {
                    args.add(Long.valueOf(paramValue));
                } else if (cls == float.class) {
                    args.add(Float.parseFloat(paramValue));
                } else if (cls == Float.class) {
                    args.add(Float.valueOf(paramValue));
                } else if (cls == double.class) {
                    args.add(Double.parseDouble(paramValue));
                } else if (cls == Double.class) {
                    args.add(Double.valueOf(paramValue));
                } else if (cls == String.class) {
                    // 문자열인 경우
                    args.add(paramValue);
                } else {
                    // 기타는 setter를 체크해 보고 요청 데이터를 주입
                    // 동적 객체 생성
                    Object paramObj = cls.getDeclaredConstructors()[0].newInstance();
                    for (Method _method : cls.getDeclaredMethods()) {
                        String name = _method.getName();
                        if (!name.startsWith("set")) continue;

                        char[] chars = name.replace("set", "").toCharArray();
                        chars[0] = Character.toLowerCase(chars[0]);
                        name = String.valueOf(chars);
                        String value = request.getParameter(name);
                        if (value == null) continue;


                        Class clz = _method.getParameterTypes()[0];
                        // 자료형 변환 후 메서드 호출 처리
                        invokeMethod(paramObj,_method, value, clz, name);
                    }
                    args.add(paramObj);
                } // endif
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        /* 메서드 매개변수 의존성 주입 처리 E */

        /* 요청 메서드 호출 S */
        try {
            Object result = args.isEmpty() ? method.invoke(controller) : method.invoke(controller, args.toArray()); // args 객체가 비어 있는지 확인
            // 인자가 없으면 method.invoke(controller) 를 실행. 이 코드는 controller 객체에서 method를 인자 없이 호출
            // 인자가 있으면 method.invoke(controller, args.toArray())를 실행. controller 객체에서 method를 args 객체를 배열로 변환한 인자와 함께 호출
            // args.toArray() : args 객체를 배열로 변환

            /**
             *  컨트롤러 타입이 @Controller이면 템플릿 출력,
             * @RestController이면 JSON 문자열로 출력, 응답 헤더를 application/json; charset=UTF-8로 고정
             */
           boolean isRest = Arrays.stream(controller.getClass().getDeclaredAnnotations()).anyMatch(a -> a instanceof RestController); // controller 객체가 애노테이션을 가지고 있는지 여부를 확인하고, 논리값 (boolean) 변수 isRest 에 저장

           // Rest 컨트롤러인 경우
           if (isRest) { // 이 코드는 컨트롤러가 REST 컨트롤러인지 (isRest) 확인하고 조건에 따라 응답 형식을 설정하고 응답 데이터를 JSON 형식으로 작성
               response.setContentType("application/json; charset=UTF-8");
               String json = om.writeValueAsString(result);
               PrintWriter out = response.getWriter(); // 응답 본문에 데이터를 쓰기 위한 PrintWriter 객체 가져오기
               out.print(json); // PrintWriter를 사용하여 JSON 문자열 (json)을 응답 본문에 씀
               return;
           }

            // 일반 컨트롤러인 경우 문자열 반환값을 템플릿 경로로 사용
            String tpl = "/WEB-INF/templates/" + result + ".jsp"; // 문자열 변수 tpl에 JSP 페이지의 경로를 만듬
            RequestDispatcher rd = request.getRequestDispatcher(tpl); // request 객체에서 getRequestDispatcher 메서드를 이용하여 RequestDispatcher 객체를 생성
            // RequestDispatcher : 서버 내부에서 JSP 페이지를 호출하는 역할
            // 매개변수로 앞서 만든 JSP 페이지 경로 (tpl)를 넘겨줌
            rd.forward(request, response);
            // RequestDispatcher 객체의 forward 메서드를 호출
            // 현재 요청과 응답 객체를 이용하여 제어 흐름을 JSP 페이지로 넘겨줌
            
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        /* 요청 메서드 호출 E */
    }

    /**
     * 자료형 변환 후 메서드 호출 처리
     *
     * @param paramObj
     * @param method
     * @param value
     * @param clz
     * @param fieldNm - 멤버변수명
     */
    private void invokeMethod(Object paramObj, Method method, String value, Class clz, String fieldNm) {
        try {
            if (clz == String.class) { // 문자열 처리
                // clz 변수가 String.class 객체와 같은지 확인
                method.invoke(paramObj, value);
                // Invoke() : 객체에 정의된 메서드를 직접적으로 호출하는 데 사용되는 메서드

                /* 기본 자료형 및 Wrapper 클래스 자료형 처리  S */
            } else if (clz == boolean.class) {
                method.invoke(paramObj, Boolean.parseBoolean(value));
            } else if (clz == Boolean.class) {
                method.invoke(paramObj, Boolean.valueOf(value));
            } else if (clz == byte.class) {
                method.invoke(paramObj, Byte.parseByte(value));
            } else if (clz == Byte.class) {
                method.invoke(paramObj, Byte.valueOf(value));
            } else if (clz == short.class) {
                method.invoke(paramObj, Short.parseShort(value));
            } else if (clz == Short.class) {
                method.invoke(paramObj, Short.valueOf(value));
            } else if (clz == int.class) {
                method.invoke(paramObj, Integer.parseInt(value));
            } else if (clz == Integer.class) {
                method.invoke(paramObj, Integer.valueOf(value));
            } else if (clz == long.class) {
                method.invoke(paramObj, Long.parseLong(value));
            } else if (clz == Long.class) {
                method.invoke(paramObj, Long.valueOf(value));
            } else if (clz == float.class) {
                method.invoke(paramObj, Float.parseFloat(value));
            } else if (clz == Float.class) {
                method.invoke(paramObj, Float.valueOf(value));
            } else if (clz == double.class) {
                method.invoke(paramObj, Double.parseDouble(value));
            } else if (clz == Double.class) {
                method.invoke(paramObj, Double.valueOf(value));
                /* 기본 자료형 및 Wrapper 클래스 자료형 처리 E */
                // LocalDate, LocalTime, LocalDateTime 자료형 처리 S
            } else if (clz == LocalDateTime.class || clz == LocalDate.class || clz == LocalTime.class) {
                // clz 변수가 LocalDateTime.class, LocalDate.class, LocalTime.class 중 하나와 같은지 확인
               Field field = paramObj.getClass().getDeclaredField(fieldNm); // paramObj 객체의 클래스 정보에서 fieldNm이라는 이름의 필드를 찾아 Field 객체로 가져옴
               for (Annotation a : field.getDeclaredAnnotations()) { // field 필드에 선언된 모든 애노테이션을 순회
                   if (a instanceof DateTimeFormat dateTimeFormat) { // 순회하는 애노테이션 중 @DateTimeFormat 애노테이션이 있는지 확인
                       String pattern = dateTimeFormat.value();
                       DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                       if (clz == LocalTime.class) {
                           method.invoke(paramObj, LocalTime.parse(value, formatter));
                           // value 데이터를 formatter를 이용하여 LocalTime 객체 형태로 변환
                       } else if (clz == LocalDate.class) {
                           method.invoke(paramObj, LocalDate.parse(value, formatter));
                       } else {
                           method.invoke(paramObj, LocalDateTime.parse(value, formatter));
                       }
                       break;
                   } // endif
               } // endfor
                // LocalDate, LocalTime, LocalDateTime 자료형 처리 E
            }
            
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 요청 메서드 & 애노테이션으로 설정된 mapping Url 조회
     *
     * @param method
     * @param anno
     * @return
     */
    private String[] getMappingUrl(String method, Annotation anno) {

        // RequestMapping은 모든 요청에 해당하므로 정의되어 있다면 이 설정으로 교체하고 반환한다.
        if (anno instanceof  RequestMapping) { // 애노테이션이 RequestMapping 타입인지 확인하고, 해당 애노테이션과 관련된 값을 추출
            RequestMapping mapping = (RequestMapping) anno;
            return mapping.value();
        }

        if (method.equals("GET") && anno instanceof GetMapping) {
            GetMapping mapping = (GetMapping) anno;
            return mapping.value();
        } else if (method.equals("POST") && anno instanceof PostMapping) {
            PostMapping mapping = (PostMapping) anno;
            return mapping.value();
        } else if (method.equals("PATCH") && anno instanceof PatchMapping) {
            PatchMapping mapping = (PatchMapping) anno;
            return mapping.value();
        } else if (method.equals("PUT") && anno instanceof PutMapping) {
            PutMapping mapping = (PutMapping) anno;
            return mapping.value();
        } else if (method.equals("DELETE") && anno instanceof DeleteMapping) {
            DeleteMapping mapping = (DeleteMapping) anno;
            return mapping.value();
        }

        return null;
    }
}
