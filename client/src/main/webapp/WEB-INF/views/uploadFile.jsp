<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>头像上传</title>
</head>
<body>
<c:set value="${pageContext.request.contextPath}" var="path" scope="page"/>
<div align="center">
    <form action="${path}/u/upload/image" method="post" enctype="multipart/form-data">
        <input type="file" name="file">
        <br>
        用户:<input type="text" name="username">
        <br>
        <div align="center"><input type="submit" value="点击上传"></div>
        <br>
    </form>
    <br>
    <h2><font color="#a52a2a" size="4">图片显示</font></h2>

    <img src="${data}" width="200px" height="180px">
    <br>
    <h4><font color="#a52a2a" size="4">文件名: ${newFileName}</font></h4>
</div>
</body>
</html>
