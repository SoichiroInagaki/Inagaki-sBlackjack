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
	<h3>新規登録</h3>
	
	<%
		String message = (String)request.getAttribute("message");
		if(message != null){
	%>
		<p><%=message%></p>
	<% }%>
	
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
	<p><a href="login.jsp">ログイン画面に戻る</a></p>


</body>
</html>