<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
</head>
<body>
	<h1>-Blackjack-</h1>
	<h3>ログイン</h3>
	
	<%
		String message = (String) request.getAttribute("message");
		if(message != null){
	%>
	<p><%=message%></p>
	<%}%>
	
	<form action="LoginServlet" method="post">
		<p><label for="login_name">プレイヤーネーム</label></p>
		<input type="text" name="login_name" id="login_name" required>
		<p><label for="login_password">パスワード</label></p>
		<input type="password" name="login_password" id="login_password" required>
		<br><br>
		<button type="submit">ログイン</button>
	</form>
	<br><br>
	
	<p>新規の方は<a href="new_player.jsp">こちら</a></p>
	
	



</body>
</html>