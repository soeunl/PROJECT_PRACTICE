<%@ tag body-content="scriptless" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ tag import="org.choongang.member.MemberUtil" %>
<% if (MemberUtil.isLogin(request)) { %>
<jsp:doBody />
<% } %> <%--이렇게 되면 로그인을 한 상태에서만 출력된다--%>