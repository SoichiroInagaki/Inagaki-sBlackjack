<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
<link rel="stylesheet" href="CSS/css.css">
</head>
<body>
	<div class="title" style="font-size: 4em">
		<p>-Blackjack-</p>
	</div>
	<div class="summary">
		<p>ログイン</p>
	</div>
	<% String message = (String) request.getAttribute("message");
		if(message != null){%>
			<p class="alert"><%=message%></p>
		<%}%>
	<form action="LoginServlet" method="post">
		<p><label for="login_name">プレイヤーネーム</label></p>
		<input type="text" name="login_name" id="login_name" required>
		<p><label for="login_password">パスワード</label></p>
		<input type="password" name="login_password" id="login_password" required>
		<br>
		<button type="submit">ログイン</button>
	</form>
	<p>新規の方は<a href="new_player.jsp">こちら</a></p>
</body>
</html>