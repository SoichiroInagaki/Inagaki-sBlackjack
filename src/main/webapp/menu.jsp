<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Player"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
</head>
<body>
	<h1>-Blackjack-</h1>
	<% 
		Player player = (Player) session.getAttribute("player");
		if(player != null){
	%>
	<p>ログインプレイヤー：<%=player.getName()%>
	<%}%>
	
	<h3>ここはメインメニュー</h3>
	<form action="MenuServlet" method="get">
		<button type="submit" name="clicked" value="play">ブラックジャックをプレイ</button>
		<br>
		<button type="submit" name="clicked" value="record">戦績確認</button>
		<br>
		<button type="submit" name="clicked" value="delete">退会</button>
		<br>
		<button type="submit" name="clicked" value="logout">ログアウト</button>
	</form>

</body>
</html>