<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<c:set value="${pageContext.request.contextPath}" var="path" scope="page"/>
<html>
<head>
    <meta charset="utf-8">
    <title>${pageContext.request.contextPath}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1.0" name="viewport" charset="UTF-8">
    <link rel="stylesheet" href="http://apps.bdimg.com/libs/bootstrap/3.3.4/css/bootstrap.min.css">
    <script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <link href="${path}/css/t11.css" rel="stylesheet" type="text/css">
    <link href="${path}/css/base.css" rel="stylesheet" type="text/css">
    <link href="${path}/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/css/Untitled-3.css" rel="stylesheet" type="text/css">
    <link href="${path}/css/Untitled-1base.css" rel="stylesheet" type="text/css">
    <link href="${path}/css/font.css" rel="stylesheet" type="text/css">
    <link href="${path}/css/dfghrgdffbdfb6base.css" rel="stylesheet" type="text/css">
    <link href="${path}/css/ghjghjghjbnmyhj.css" rel="stylesheet" type="text/css">
</head>
<body>


<tiles:insertAttribute name="header"/>

<tiles:insertAttribute name="body"/>

<tiles:insertAttribute name="footer"/>


</body>
<script src="${path}/js/jquery.min.js"></script>
<script src="${path}/js/bootstrap.min.js"></script>
</html>
