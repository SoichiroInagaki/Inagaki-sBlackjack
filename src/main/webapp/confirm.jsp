<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Player"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
<link rel="stylesheet" href="CSS/css.css">
</head>
<body>
	<p class="title">-Blackjack-</p>
	<p class="summary">最終確認</p>
	<% 	Player player = (Player) session.getAttribute("player");
		if(player != null){%>
			<p>本当に「<%=player.getName()%>」のデータを削除しますか？</p>
			<form action="DeleteServlet" method="get">
				<button type="submit" name="clicked" value="yes" class="delete_choice_button">はい</button>
				<button type="submit" name="clicked" value="no" class="delete_choice_button">いいえ</button>
			</form>
	<%	} %>
</body>
</html>