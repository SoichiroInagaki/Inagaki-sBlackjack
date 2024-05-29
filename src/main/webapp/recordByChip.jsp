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
	<h3>ここは戦績表示画面</h3>
	<p>あなたの戦績は以下の通りです</p>
	<% 
		Integer chipOfLoginedPlayer = (Integer) request.getAttribute("chipOfLoginedPlayer"); 
		Player player = (Player) session.getAttribute("player");
	%>
	
	<!-- チップ制の導入 -->
	<p><%=player.getName()%> 保有チップ枚数：<%=chipOfLoginedPlayer%></p>
	<p>保有チップ枚数TOP5のプレイヤーは、以下の通りです</p>
	<%
		Player[] rankedPlayers = (Player[]) request.getAttribute("rankedRecords");
		for(int i = 0; i < 5; i++){
	%>
		<p>【<%=(i + 1)%>】<%=rankedPlayers[i].getName()%>
		 保有チップ枚数：<%=rankedPlayers[i].getCoin() %>
	<% } %>
	
	<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>
</body>
</html>