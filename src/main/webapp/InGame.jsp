<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Dealer" import="PlayerInGame"
    import="model.Card"%>
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
	%>
	<p>ディーラーの手札は<%=dealer.hand.get(0)%>と裏向きのカード1枚です</p>

	<ul>あなたの手札は、
		<% for(Card card : playerInGame.hand){ %>
		<li><%=card.getCard()%></li>
		<%}%>
	</ul>です
	<br>
	<p>現在のカードの合計は<%=playerInGame.getPoint()%>です</p>
	<p>行うアクションを選んでください</p>
	<form action="GameServlet" method="post">
		<button type="submit" name="clicked" value="hit">HIT</button>
		<button type="submit" name="clicked" value="stand">STAND</button>
	</form>

</body>
</html>