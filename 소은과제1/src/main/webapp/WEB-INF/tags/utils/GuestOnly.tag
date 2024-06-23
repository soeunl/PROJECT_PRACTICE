<%@ tag body-content="scriptless" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ tag import="org.choongang.member.MemberUtil" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/utils" %>
<% if (!MemberUtil.isLogin(request)) { %>
<jsp:doBody />
<% } %> <%--이렇게 되면 로그인을 하지 않은 상태에서만 출력된다--%>
<%--(!MemberUtil.isLogin(request)로 적음--%>