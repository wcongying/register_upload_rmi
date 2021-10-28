<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录页面</title>
</head>
<body>
<c:set value="${pageContext.request.contextPath}" var="path" scope="page"/>
<div align="center">
    <form action="${path}/login" method="post">
        <table>
            <tr>
                <td>用户名</td>
                <td><input type="text" placeholder="用户名/邮箱/手机号" name="username"></td>
            </tr>
            <tr>
                <td>密码</td>
                <td><input type="password" name="password"></td>
                <td><span>${msg}</span></td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="登录">
                </td>
                <td>
                    <a href="${path}/login/regist" style="color: #5cb85c">还没有账号？点我注册</a>
                </td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>
