<%--
  Created by IntelliJ IDEA.
  User: lenovo
  Date: 2019/6/20
  Time: 22:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>start grab redpackets</title>

    <script type="text/javascript" src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>
    <script type="text/javascript">

        $(document).ready(function(){
            var max = 5000;
            $("#grab").click(function(){
                for(var i=1; i<=max; i++){
                    $.post({
                        url: "./grabByRedis.do?redPacketId=1&userId=" + i,
                        success: function(result){console.log("5000次红包抢完！");}
                    });
                }
            });

        });

    </script>
</head>
<body>
    <button id="grab"> 抢红包！ </button>
</body>
</html>
