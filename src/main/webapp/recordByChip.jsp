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
	<p class="summary">戦績</p>
	<p>あなたの戦績は以下の通りです</p>
	<% 	Integer chipOfLoginedPlayer = (Integer) request.getAttribute("chipOfLoginedPlayer"); 
		Player player = (Player) session.getAttribute("player");%>
	<!-- チップ制の導入 -->
	<p style="text-decoration: underline;"><%=player.getName()%>...保有チップ枚数：<%=chipOfLoginedPlayer%></p>
	<p style="margin-top: 2em">保有チップ枚数TOP5のプレイヤーは、以下の通りです</p>
	<table style="margin: auto">
		<tr>
			<td>順位</td><td>プレイヤーネーム</td><td>保有チップ枚数</td>
		</tr>
		<%	Player[] rankedPlayers = (Player[]) request.getAttribute("rankedRecords");
			for(int i = 0; i < 5; i++){%>
				<tr>
					<td><span class="circle"><%=(i + 1)%></span></td>
					<td><%=rankedPlayers[i].getName()%></td>
					<td><%=rankedPlayers[i].getCoin() %></td>
				</tr>
		<%	 } %>
	</table>
	<button onclick="location.href='menu.jsp'">メインメニュー画面に戻る</button>
</body>
</html>