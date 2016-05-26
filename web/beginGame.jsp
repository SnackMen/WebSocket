<%--
  User: laowang
  Date: 2016/5/6
  Time: 23:13
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
  <base href="<%=basePath%>">
  <title>聊天</title>
  <script src="jquery.min.js"></script>
</head>
<body>
<input type="text" id="userMessage"> <br>
<input type="button" id="showUsername" style="width: 50px;"> <br>
<input type="button" id="submit" value="提交" onclick="toUserMessage($('#userMessage').val())"> &nbsp;&nbsp;
<input type="button" id="disconnect" value="断开连接" onclick="disconnection()">
<div id="convo" style="height: 500px;width: 600px;border: 1px solid black;">
</div>
<script>
    function toUserMessage(message){
      var mychat = $("<li></li>")
      mychat.append(message);
      mychat.addClass("ziji");
      $("#convo").append(mychat);
      forwardMessage(message);
    }
</script>
</body>
</html>
<script src="jquery-onepoetry-websocket.js"></script>

