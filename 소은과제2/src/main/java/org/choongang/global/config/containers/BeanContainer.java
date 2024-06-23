package org.choongang.global.config.containers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.choongang.global.config.annotations.Component;
import org.choongang.global.config.annotations.Controller;
import org.choongang.global.config.annotations.RestController;
import org.choongang.global.config.annotations.Service;
import org.choongang.global.config.containers.mybatis.MapperProvider;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanContainer {
    private static BeanContainer instance;

    private Map<String, Object> beans; // key는 문자열 타입, value는 객체 타입을 가짐

    private MapperProvider mapperProvider; // 마이바티스 매퍼 조회

    public BeanContainer() { // 생성자
        beans = new HashMap<>(); // beans 초기화, HashMap 객체 생성
        mapperProvider = MapperProvider.getInstance(); // MapperProvider 클래스의 싱글톤 인스턴스를 가져와 mapperProvider 변수에 할당
    }

    public void loadBeans() {
        // 패키지 경로 기준으로 스캔 파일 경로 조회
        try {
            String rootPath = new File(getClass().getResource("../../../").getPath()).getCanonicalPath(); // 파일 객체 생성. 클래스의 현재 경로를 기준으로 그 경로를 문자열로 가져옴
            // getCanonicalPath(): File 객체의 절대 경로를 문자열로 반환
            String packageName = getClass().getPackageName().replace(".global.config.containers", "");
            // 현재 클래스의 패키지 이름을 문자열로 가져오고, 패키지 이름에서 ".global.config.containers" 문자열을 제거
            List<Class> classNames = getClassNames(rootPath, packageName);
            // 절대 경로와 패키지 이름을 활용하여 객체 목록 생성

            for (Class clazz : classNames) {
                // 인터페이스는 동적 객체 생성을 하지 않으므로 건너띄기
                if (clazz.isInterface()) {
                    continue;
                    // 클래스 정보(clazz)가 인터페이스인지 확인
                    // 인터페이스일 경우 continue 문을 통해 다음 반복으로 넘어감
                }

                // 애노테이션 중 Controller, RestController, Component, Service 등이 TYPE 애노테이션으로 정의된 경우 beans 컨테이너에 객체 생성하여 보관
                // 키값은 전체 클래스명, 값은 생성된 객체
                String key = clazz.getName();

                /**
                 *  이미 생성된 객체라면 생성된 객체로 활용
                 *  매 요청시마다 새로 만들어야 객체가 있는 경우 갱신 처리
                 *
                 *  매 요청시 새로 갱신해야 하는 객체
                 *      - HttpServletRequest
                 *      - HttpServletResponse
                 *      - HttpSession session
                 *      - Mybatis mapper 구현 객체
                 */

                if (beans.containsKey(key)) {
                    updateObject(beans.get(key));
                    continue;
                    // 같은 key를 가진 객체가 존재하면 updateObject 메서드를 호출하여 해당 객체를 업데이트
                }


                Annotation[] annotations = clazz.getDeclaredAnnotations(); // 현재 클래스 정보(clazz)에 선언된 애노테이션 정보를 가져옴

                boolean isBean = false; // 초기화
                for (Annotation anno : annotations) {
                    if (anno instanceof Controller || anno instanceof RestController || anno instanceof Service || anno instanceof Component)  { // 반복문 안에서 anno가 Controller, RestController, Service, Component 중 하나의 인스턴스인지 확인
                        isBean = true;
                        break;
                    }
                }
                // 컨테이너가 관리할 객체라면 생성자 매개변수의 의존성을 체크하고 의존성이 있다면 해당 객체를 생성하고 의존성을 해결한다.
                if (isBean) {
                    Constructor con = clazz.getDeclaredConstructors()[0]; // 현재 클래스의 생성자 정보 가져오기
                    List<Object> objs = resolveDependencies(key, con);
                    if (!beans.containsKey(key)) { // 같은 key를 가진 객체가 이미 beans 필드에 저장되어 있는지 확인하고, 없을 경우에 새로운 객체를 생성
                        Object obj = con.getParameterTypes().length == 0 ? con.newInstance() : con.newInstance(objs.toArray());
                        // getParameterTypes(): 생성자의 매개변수 타입 정보를 배열로 반환
                        // 생성자의 매개변수 개수를 확인하고 매개변수가 없는 생성자일 경우 con.newInstance() 를 호출하여 객체를 생성, 매개변수가 있는 생성자라면 그 생성자를 호출하여 객체 생성
                        beans.put(key, obj); // beans에 key 와 obj 를 키-값 쌍으로 저장
                        // 이를 통해 BeanContainer 클래스는 key 를 통해 객체에 접근할 수 있게 됨
                    }
                }

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BeanContainer getInstance() { // 정적메서드
        if (instance == null) {
            instance = new BeanContainer();
        }

        return instance; // 싱글톤 패턴을 이용하여 BeanContainer 클래스의 인스턴스를 단 하나만 생성
    }

    /**
     * 생성된 객체 조회
     *
     * @param clazz
     * @return
     */
    public <T> T getBean(Class clazz) {
        return (T)beans.get(clazz.getName());
    } // 해당 타입과 일치하는 객체를 반환

    public void addBean(Object obj) {

        beans.put(obj.getClass().getName(), obj);
    } // addBean 메서드를 통해 입력받은 객체를 beans에 저장
      // 키 값으로는 객체의 클래스 이름을 문자열로 사용하며, 값으로는 객체 자체를 저장

    public void addBean(String key, Object obj) {
        beans.put(key, obj);
    }
    // addBean(Object obj) 메서드는 객체의 클래스 이름을 자동으로 키 값으로 사용하는 반면, addBean(String key, Object obj) 메서드는 사용자가 직접 키 값을 설정할 수 있음

    // 전체 컨테이너 객체 반환
    public Map<String, Object> getBeans() {
        return beans;
    }

    /**
     * 의존성의 의존성을 재귀적으로 체크하여 필요한 의존성의 객체를 모두 생성한다.
     *
     * @param con
     */
    private List<Object> resolveDependencies(String key, Constructor con) throws Exception {
        List<Object> dependencies = new ArrayList<>();
        if (beans.containsKey(key)) { // containsKey(key): 특정 키 값이 존재하는지 확인하는 메서드
            dependencies.add(beans.get(key));
            return dependencies;
        }

        Class[] parameters = con.getParameterTypes(); // getParameterTypes(): 생성자의 매개변수 타입 정보를 배열로 반환하는 메서드
        if (parameters.length == 0) {
            Object obj = con.newInstance(); // 만약 생성자의 매개변수가 없다면 con.newInstance() 를 호출하여 해당 클래스의 새로운 객체를 생성
            dependencies.add(obj); // dependencies에 추가
        } else {
            for(Class clazz : parameters) { // 생성자 매개변수 타입을 clazz 변수에 할당
                /**
                 * 인터페이스라면 마이바티스 매퍼일수 있으므로 매퍼로 조회가 되는지 체크합니다.
                 * 매퍼로 생성이 된다면 의존성 주입이 될 수 있도록 dependencies에 추가
                 *
                  */
                if (clazz.isInterface()) { // isInterface(): 해당 클래스가 인터페이스인지 확인하는 메서드
                    Object mapper = mapperProvider.getMapper(clazz); // 인터페이스라면 mapperProvider 객체를 이용하여 해당 인터페이스를 구현하는 클래스 객체를 가져옴
                    if (mapper != null) { // null이 아니라면 dependencies에 추가
                        dependencies.add(mapper);
                        continue;
                    }
                }

                Object obj = beans.get(clazz.getName());
                if (obj == null) { // 만약 null 이라면, 현재 클래스의 생성자 정보를 가지고 옴
                    Constructor _con = clazz.getDeclaredConstructors()[0];
                    // getDeclaredConstructors(): 클래스의 모든 생성자 정보를 배열로 반환하는 메서드
                    if (_con.getParameterTypes().length == 0) {
                        obj = _con.newInstance(); // 매개변수가 없다면 _con.newInstance() 를 호출하여 해당 클래스의 새로운 객체를 생성
                    } else {
                        List<Object> deps = resolveDependencies(clazz.getName(), _con);
                        obj = _con.newInstance(deps.toArray());
                    }
                }
                dependencies.add(obj);
            }
        }


        return dependencies;
    }

    private List<Class> getClassNames(String rootPath, String packageName) {
        List<Class> classes = new ArrayList<>();
        List<File> files = getFiles(rootPath);
        for (File file : files) {
            String path = file.getAbsolutePath();
            String className = packageName + "." + path.replace(rootPath + File.separator, "").replace(".class", "").replace(File.separator, ".");
            try {
                Class cls = Class.forName(className);
                classes.add(cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    private List<File> getFiles(String rootPath) {
        List<File> items = new ArrayList<>();
        File[] files = new File(rootPath).listFiles();
        if (files == null) return items;

        for (File file : files) {
            if (file.isDirectory()) {
                List<File> _files = getFiles(file.getAbsolutePath());
                if (!_files.isEmpty()) items.addAll(_files);
            } else {
                items.add(file);
            }
        }
        return items;
    }

    /**
     * 컨테이너에 이미 담겨 있는 객체에서 매 요청시마다 새로 생성이 필요한 의존성이 있는 경우
     * 갱신 처리
     *  - HttpServletRequest
     *  - HttpServletResponse
     *  - HttpSession session
     *  - Mybatis mapper 구현 객체
     *
     * @param bean
     */
    private void updateObject(Object bean) {
        // 인터페이스인 경우 갱신 배제
        if (bean.getClass().isInterface()) {
            return;
        }

        Class clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class clz = field.getType();
            try {

                /**
                 * 필드가 마이바티스 매퍼 또는 서블릿 기본 객체(HttpServletRequest, HttpServletResponse, HttpSession) 이라면 갱신
                 *
                 */
                
                Object mapper = mapperProvider.getMapper(clz);

                // 그외 서블릿 기본 객체(HttpServletRequest, HttpServletResponse, HttpSession)이라면 갱신
                if (clz == HttpServletRequest.class || clz == HttpServletResponse.class || clz == HttpSession.class || mapper != null) {
                    field.setAccessible(true);
                }

                if (clz == HttpServletRequest.class) {
                    field.set(bean, getBean(HttpServletRequest.class));
                } else if (clz == HttpServletResponse.class) {
                    field.set(bean, getBean(HttpServletResponse.class));
                } else if (clz == HttpSession.class) {
                    field.set(bean, getBean(HttpSession.class));
                } else if (mapper != null) { // 마이바티스 매퍼
                    field.set(bean, mapper);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
