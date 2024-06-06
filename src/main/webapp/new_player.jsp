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
	<div class="title">
		<p>-Blackjack-<p>
	</div>
	<div class="summary">
		<p>新規登録<p>
	</div>
	<%	String message = (String)request.getAttribute("message");
		if(message != null){%>
			<p class="alert"><%=message%></p>
	<% 	}%>
	<form action="NewPlayerServlet" method="post">
		<p><label for="new_name">ニックネームを入力(あなたのプレイヤーネームになります)</label></p>
		<input type="text" name="new_name" id="new_name" required>
		<p><label for="new_password1">新規パスワードを入力</label></p>
		<input type="password" name="new_password1" id="new_password1" required>
		<p><label for="new_password2">パスワードを再入力</label></p>
		<input type="password" name="new_password2" id="new_password2" required>
		<br><br>
		<button type="submit">新規登録</button>
	</form>
	<p class="bottom_link"><a href="login.jsp">ログイン画面に戻る</a></p>
</body>
</html>