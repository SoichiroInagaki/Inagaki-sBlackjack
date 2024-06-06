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

	<h1>-Blackjack-</h1>
	<h3>ここは戦績表示画面</h3>
	<p>あなたの戦績は以下の通りです</p>
	<%Player player = (Player) request.getAttribute("playerForRecord");%>
	
	<p><%=player.getName()%> 勝率：
		<%=String.format("%.2f", player.getWinRate())%> 
		試合数：<%=player.getGames()%></p>
	<p>勝率TOP5のプレイヤーは、以下の通りです</p>
	<%
		Player[] rankedPlayers = (Player[]) request.getAttribute("rankedRecords");
		for(int i = 0; i < 5; i++){
	%>
		<p>【<%=(i + 1)%>】<%=rankedPlayers[i].getName()%> 勝率：
			<%=String.format("%.2f", rankedPlayers[i].getWinRate())%> 
			試合数：<%=rankedPlayers[i].getGames()%></p>
		<%}%>
	
	<p class="bottom_link"><a href="menu.jsp">メインメニュー画面に戻る</a></p>
</body>
</html>