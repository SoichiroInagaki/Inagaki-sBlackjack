<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Player"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blacjack</title>
</head>
<body>

	<h1>-Blackjack-</h1>
	<% 
		Player player = (Player) session.getAttribute("player");
		if(player != null){
	%>
	<p>ログインプレイヤー：<%=player.getName()%>
	<%}%>
	<h3>ここは退会画面</h3>
	<p>現在ログイン中のプレイヤーのデータを削除します
	<p>削除したプレイヤーのデータは後から復元することができません</p>
	<p>データの削除を行うためには、ログイン中のプレイヤーのパスワードを入力してください</p>
	<%
		String message = (String) request.getAttribute("message");
		if(message != null){
	%>
			<p><%=message%></p>
	<%}%>
	<form action="DeleteServlet" method="post">
		<p><label for="delete_password">パスワード</label></p>
		<input type="password" name="delete_password" id="delete_password" required>
		<br>
		<button type="submit">プレイヤーを削除する</button>
	</form>
	<br>
	<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>

</body>
</html>