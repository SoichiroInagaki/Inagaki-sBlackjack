<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Dealer" import="model.PlayerInGame"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
</head>
<body>
	<%
		Dealer dealer = (Dealer) session.getAttribute("dealer");
		PlayerInGame playerInGame = 
			(PlayerInGame) session.getAttribute("playerInGame");
		String hit = (String) request.getAttribute("hit");
		String splitBHit = (Stinrg) request.getAttribute("splitBHit");
		Boolean splitting = (Boolean) session.getAttribute("splitting");
		Boolean canSplit = (Boolean)request.getAttribute("canSplit");
		Boolean pairOfA = (Boolean)session.getAttribute("pairOfA");
		PlayerInGame splitA = (PlayerInGame) session.getAttribute("splitA");
		PlayerInGame splitB = (PlayerInGame) session.getAttribute("splitB");
		Integer totalChips = (Integer) request.getAttribute("totalChips");
		Integer bettingChips = (Integer) request.getAttribute("bettingChip");
		String actionAisEnd = (String) session.getAttribute("actionAisEnd");
		String actionBisEnd = (String) session.getAttribute("actionBisEnd");
	%>
	
	<p>ディーラーの手札は<%=dealer.getHandCardStr(0)%>と裏向きのカード1枚です</p>

	<% if(hit != null){ %>
		<p><%=hit%></p>
	<%}%>
	
	<ul>あなたの<% if(splitting){ %>一組目の<% } %>手札は、
		<% 	playerInGame = splitA
			for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
				<li><%=playerInGame.getHandCardStr(i)%></li>
			<%}%>
	</ul>です
	<p>現在のカードの数値の合計は<%=playerInGame.getPoint()%>です</p>
	<% if(canSplit){ %>
		<p>また、初期手札のカードが同じ数字のカードのため、スプリットが可能です</p>
		<p>現在保有中のチップ枚数：<%=totalChips%></p>
		<p>スプリット時に再度賭けるチップ枚数：<%=bettingChips %></p>
		<% if(pairOfA){ %>
			<p>※Aのペアをスプリットした場合、追加のヒットは行えません</p>
		<% } %>
	<% } %>
	<% if(splitting){ %>
		<% if(actionAisEnd != null){ %>
			<p><%=actionAisEnd %></p>
		<% }else{ %>
			<p>行うアクションを選んでください</p>
			<label><input type="radio" name="actionA" value="hit" form="form" required>HIT</label>
			<label><input type="radio" name="actionA" value="stand" form="form">STAND</label>
		<% } %>
		<% if(splitBHit != null){ %>
			<p><%=splitBHit%></p>
		<%}%>
		<ul>あなたの二組目の手札は、
			<% 	playerInGame = splitB;
				for(int i = 0; i < playerInGame.countHand(); i++ ){%>
					<li><%=playerInGame.getHandCardStr(i)%></li>
		</ul>です
		<p>現在のカードの数値の合計は<%=playerInGame.getPoint()%>です</p>
		<% if(actionBisEnd != null){ %>
			<p><%=actionBisEnd %></p>
		<% }else{ %>
			<p>行うアクションを選んでください</p>
			<form action="GameServlet" method="post" id="form">
				<label><input type="radio" name="actionB" value="hit" required>HIT</label>
				<label><input type="radio" name="actionB" value="stand" >STAND</label>
				<button type="submit">それぞれのアクションを確定</button>
			</form>
		<% } %>
	<% }else{ %>
		<p>行うアクションを選んでください</p>
		<form action="GameServlet" method="post">
			<button type="submit" name="clicked" value="hit">HIT</button>
			<button type="submit" name="clicked" value="stand">STAND</button>
			<% if(canSplit){ %>
				<button type="submit" name="clicked" value="split">SPLIT</button>
			<% } %>
		</form>
	<% } %>
</body>
</html>