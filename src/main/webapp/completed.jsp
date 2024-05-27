<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>-Blackjack-</h1>
	<h3>処理完了</h3>
	<% 
		String message = (String) request.getAttribute("process");
		String playerName = (String) request.getAttribute("playerName");
	%>
	<p><%=message%>が完了しました</p>
	<p>プレイヤーネーム：<%=playerName%>
	<p><a href="login.jsp">ログイン画面に戻る</a></p>
	

</body>
</html>