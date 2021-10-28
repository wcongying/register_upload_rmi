<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set value="${pageContext.request.contextPath}" var="path" scope="page"/>
<div class="container  hidden-xs">
    <div class="row header-top">
        <p class="col-xs-12 col-sm-6 col-md-6 col-lg-6">客服电话:010-594-78634</p>

<%--        <div><form method="get" action="${path}/logout">--%>
<%--            <button type="submit">注销</button>--%>
<%--        </form></div>--%>

<%--        <a href="${path}/u/img/page" style="color: #5cb85c">点我上传头像</a>--%>

        <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 text-right">
            <div>
                <a href="#" target="_blank"> <img alt=""  src="${path}/images/54537.png"></a>
                <a href="#" target="_blank"><img alt=""  src="${path}/images/45678678.png"></a>
                <a href="#" target="_blank"> <img alt=""  src="${path}/images/54375483543.png"></a>
            </div>
        </div>

    </div>
</div>
<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <a href="#" class="navbar-brand">
                <img src="${path}/images/logo.png" alt="Brand" class="img-responsive">
            </a>
            <button data-target="#open-nav" data-toggle="collapse" class="navbar-toggle btn-primary collapsed" aria-expanded="false">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <div id="open-nav" class="navbar-collapse collapse" aria-expanded="false" style="height: 1px;">
            <ul class="nav navbar-nav navbar-right text-center list-inline">
                <li><a href="${path}/excellentStudent">首页</a></li>
                <li><a href="${path}/u/profession">职业</a></li>
                <li><a href="">推荐</a></li>
                <li><a href="">关于</a></li>
                <li><a href="${path}/login">登录</a></li>
                <li><a href="${path}/phone/regist">注册</a></li>
            </ul>
        </div>
    </div>
</nav>
