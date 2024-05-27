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
	<h3>最終確認</h3>
	<p>本当にプレイヤーを削除しますか？</p>
	<form action="DeleteServlet" method="get">
		<button type="submit" name="clicked" value="yes">はい</button>
		<button type="submit" name="clicked" value="no">いいえ</button>
	</form>


</body>
</html>