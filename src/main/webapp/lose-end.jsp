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
		Integer countedHit = (Integer) request.getAttribute("countHit");
	%>
	<ul>あなたの手札は、
		<% for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
		<li><%=playerInGame.getHandCard(i)%></li>
		<%}%>
	</ul>です
	<br>
	<p>カードの数値の合計は<%=playerInGame.getPoint()%>です</p>
	<ul>ディーラーの初期手札は、
		<li><%=dealer.getHandCard(0)%></li>
		<li><%=dealer.getHandCard(1)%></li>
	</ul>
	<p>で、
		<%if(countedHit.equals(0)){%>
			カードの数値の合計が17点以上だったため、ディーラーはカードを追加しませんでした</p>
		<%}else{%>
			ディーラーは追加で<%=countedHit%>枚カードを引きました</p>
			<ul>追加されたカードは以下の通りです
			<%for(int i = 0; i < (dealer.countHand() -2); i++){%>
				<li><%=dealer.getHandCard(i + 2)%></li>
			<%}%>
			</ul>
		<%}%>
	<p>ディーラーの数値の合計は<%=dealer.getPoint()%>で、ディーラーの方が21に近いため、ディーラーの勝利です</p>
	<p>You lose......</p>
	<p>賭けていたチップは没収されました</p>
	<form action="PlayServlet" method="get">
		<button type="submit">もう一度遊ぶ</button>
	</form>
	<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>
</body>
</html>