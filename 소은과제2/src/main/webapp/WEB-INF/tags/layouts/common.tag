<%@ tag body-content="scriptless" %>
<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %> <%--페이지 인코딩을 UTF-8로 설정하고 지시문 사이의 공백을 제거--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %> <%--JSTL core 태그 라이브러리 사용을 위한 선언--%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <%--JSTL fmt 태그 라이브러리 사용을 위한 선언--%>
<%@ attribute name="header" fragment="true" %> <%--프래그먼트를 사용할 수 있다는 것을 의미함. 즉, 별도의 JSP 파일을 지정하여 헤더 영역의 내용을 포함할 수 있음--%>
<%@ attribute name="footer" fragment="true" %>
<%@ attribute name="commonCss" fragment="true" %>
<%@ attribute name="commonJs" fragment="true" %>
<%@ attribute name="title" %> <%--name 속성의 이름을 title 로 지정--%>
<fmt:setBundle basename="messages.commons" /> <%--메시지가 messages.commons.properties라는 이름의 리소스 파일에서 로됨--%>
<c:url var="cssUrl" value="/css/" />
<c:url var="jsUrl" value="/js/" />
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>
         <c:if test="${!empty title}">
         ${title} -
         </c:if>
         <fmt:message key="SITE_TITLE" />
        <%-- title 속성이 설정되어 있으면 해당 값을 제목으로 사용하고, 설정되어 있지 않으면 fmt:message 태그를 통해 SITE_TITLE 키에 해당하는 값을 제목으로 사용--%>
        </title>
        <link rel="stylesheet" type="text/css" href="${cssUrl}style.css">
        <jsp:invoke fragment="commonCss" />
        <c:if test="${addCss != null}">
            <c:forEach var="cssFile" items="${addCss}">
                <link rel="stylesheet" type="text/css" href="${cssUrl}${cssFile}.css">
            </c:forEach>
        </c:if>

        <script src="${jsUrl}common.js"></script>
        <jsp:invoke fragment="commonJs" />
        <c:if test="${addScript != null}">
            <c:forEach var="jsFile" items="${addScript}">
                <script src="${jsUrl}${jsFile}.js"></script>
            </c:forEach>
        </c:if>
    </head>
    <body>
        <header>
            <jsp:invoke fragment="header" />
        </header>
        <main>
            <jsp:doBody />
        </main>
        <footer>
            <jsp:invoke fragment="footer" />
        </footer>
    </body>
    <iframe name="ifrmProcess" class="dn"></iframe>
</html>