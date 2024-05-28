<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Dealer" import="model.PlayerInGame"
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
	<p>ディーラーの手札は<%=dealer.getHandCard(0)%>と裏向きのカード1枚です</p>

	<%
		String message = (String) request.getAttribute("messaeg");
		if(message != null){
	%>
	<p><%=message%></p>
	<%}%>
	<ul>あなたの手札は、
		<% for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
		<li><%=playerInGame.getHandCard(i)%></li>
		<%}%>
	</ul>です
	<br>
	<p>現在のカードの数値の合計は<%=playerInGame.getPoint()%>です</p>
	<p>行うアクションを選んでください</p>
	<form action="GameServlet" method="post">
		<button type="submit" name="clicked" value="hit">HIT</button>
		<button type="submit" name="clicked" value="stand">STAND</button>
	</form>

</body>
</html>