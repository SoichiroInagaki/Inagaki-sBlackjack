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
		<p>-Blackjack-</p>
	</div>
	<div class="summary">
		<p>処理完了</p>
	</div>
	<%	String message = (String) request.getAttribute("process");
		String playerName = (String) request.getAttribute("playerName");%>
	<p class="alert"><%=message%>が完了しました</p>
	<p>プレイヤーネーム：<%=playerName%></p>
	<p style="margin: 2em"><a href="login.jsp">ログイン画面に戻る</a></p>
</body>
</html>