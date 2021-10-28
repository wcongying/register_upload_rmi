<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

<c:set value="${pageContext.request.contextPath}" var="path" scope="page"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <script>
        //验证码
        var counts = 1;

        function settime(val) {
            if(counts == 0) {
                val.removeAttribute("disabled");
                val.value = "获取验证码";
                counts = 1;
                return false;
            } else {
                val.setAttribute("disabled", true);
                val.value = "重新发送（" + counts + "）";
                counts--;
            }
            setTimeout(function() {
                settime(val);
            }, 1000);
        }

        $(function(){
            //获取验证码
            $("#verCodeBtn").click(function() {
                var phone = $("input[name='phone']").val();
                $.ajax({
                    async:true,
                    method:"POST",
                    type: "post",
                    url: "${path}/phone/code",
                    data:{
                        phone:phone
                    },
                    //发送到服务器的数据
                    //设置为json返回格式
                    dataType:'json',
                    success:function (data) {
                        alert(data["msg"]);
                    }
                });

            });
        })

        function validate() {
            var pwd1 = document.getElementById("pwd").value;
            var pwd2 = document.getElementById("pwd1").value;

            <!-- 对比两次输入的密码 -->
            if(pwd1 == pwd2)
            {
                document.getElementById("tishi").innerHTML="<font color='green'>两次密码相同</font>";
                document.getElementById("button").disabled = false;
            }
            else {
                document.getElementById("tishi").innerHTML="<font color='red'>两次密码不相同</font>";
                document.getElementById("button").disabled = true;
            }
        }


    </script>
</head>
<body>
<h1 align="center">手机注册</h1>
<div align="center">
    <form action="${path}/phone/regist" method="post">
        <table>
            <tr>
                <td>用户名</td>
                <td><input type="text" name="username"></td>
                <td><span>${msg}</span></td>
            </tr>
            <tr>
                <td>密码</td>
                <td> <input type="password"  name="pwd" id="pwd" placeholder="请设置登录密码"></td>
            </tr>
            <tr>
                <td>确认密码</td>
                <td><input type="password"  name="pwd1" id="pwd1" placeholder="请再次填写密码" onkeyup="validate()"><span id="tishi"></span></td>
            </tr>
            <tr>
                <td>手机号</td>
                <td><input type="text" name="phone" id="" value="" placeholder="请输入+86手机号" maxlength="14" /></td>
                <td><input type="button" name="" id="verCodeBtn" value="获取验证码"  onclick="settime(this);"/></td>
            </tr>
            <tr>
                <td>验证码</td>
                <td><input type="" name="code" id="code" value="" placeholder="请输入验证码" maxlength="6"/></td>
                <td><span>${msgCode}</span></td>
            </tr>
            <tr>
                <td><input type="submit" id="button" value="提交"></td>
            </tr>
        </table>
    </form>
    <a href="${path}/mail/regist" style="color: #5cb85c">点我使用邮箱注册</a>
</div>
</body>
</html>
