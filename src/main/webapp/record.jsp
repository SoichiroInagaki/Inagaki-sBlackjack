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
	<h3>ここは戦績表示画面</h3>
	<p>あなたの戦績は以下の通りです</p>
	<%Player player = (Player) request.getAttribute(playerForRecord);%>
	<p><%=player.getName()%> 勝率：
		<%=String.format("%.2f", player.getWinRate())%> 
		試合数：<%=player.getGames()%></p>
	<p>勝率TOP5のプレイヤーは、以下の通りです</p>
	<%
		Player[] rankedPlayers = (Player[]) request.getAttrinbute(rankedRecords);
		for(Player player : rankedPlayers){
	%>
		<p><%=player.getName()%> 勝率：
			<%=String.format("%.2f", player.getWinRate())%> 
			試合数：<%=player.getGames()%></p>
		<%}%>
	<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>


</body>
</html>