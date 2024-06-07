<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Player"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blacjack</title>
<link rel="stylesheet" href="CSS/css.css">
</head>
<body>
	<p class="title">-Blackjack-</p>
	<% 	Player player = (Player) session.getAttribute("player");
		if(player != null){%>
			<p>ログインプレイヤー：<%=player.getName()%>
	<%	}%>
	<p class="summary">退会</p>
	<p>現在ログイン中のプレイヤーのデータを削除します
	<p class="red_text">※削除したプレイヤーのデータは後から復元することができません</p>
	<p>データの削除を行うためには、ログイン中のプレイヤーのパスワードを入力してください</p>
	<%	String message = (String) request.getAttribute("message");
		if(message != null){%>
			<p class="alert" style="margin-top: 2em"><%=message%></p>
	<%	}%>
	<form action="DeleteServlet" method="post">
		<p style="margin-top: 2em"><label for="delete_password">パスワード</label></p>
		<input type="password" name="delete_password" id="delete_password" required>
		<br>
		<button type="submit">プレイヤーを削除する</button>
	</form>
	<p class="bottom_link"><a href="menu.jsp">メインメニュー画面に戻る</a></p>
</body>
</html>