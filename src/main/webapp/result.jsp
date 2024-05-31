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
		String hit = (String) request.getAttribute("Hit");
		String bustedPlayer = (String) request.getAttribute("bustedPlayer");
		String resultMessage = (String) request.getAttribute("resultMessage");
		String chipMessage = (String) request.getAttribute("chipMessage");
		Integer countedHit = (Integer) request.getAttribute("countHit");
		String blackjackMessageForPlayer = (String) request.getAttribute("blackjackMessageForPlayer");
		String blackjackMessageForDealer = (String) request.getAttribute("blackjackMessageForDealer");
		String bustedDealer = (String) request.getAttribute("bustedDealer");
		String situationMessage = (String) request.getAttribute("situationMessage");
		String actionAisEnd = (String) session.getAttribute("actionAisEnd");
		String actionBisEnd = (String) session.getAttribute("actionBisEnd");
		PlayerInGame splitA = (PlayerInGame) session.getAttribute("splitA");
		PlayerInGame splitB = (PlayerInGame) session.getAttribute("splitB");
	%>
	
	<% if(hit != null){ %>
		<p><%=hit%></p>
	<% } %>
	<% if() %>
	<ul>あなたの手札は、
		<% for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
		<li><%=playerInGame.getHandCardStr(i)%></li>
		<%}%>
	</ul>です
	<p>カードの数値の合計は<%=playerInGame.getPoint()%>です</p>
	<%=actionAisEnd %>
	
	<% if(bustedPlayer != null){ %>
		<p><%=bustedPlayer %></p>
		<p><%=resultMessage %></p>
		<p><%=chipMessage %></p>
	<% }else{ %>
		<% if(blackjackMessageForPlayer != null){ %>
			<p><%=blackjackMessageForPlayer %></p>
		<% } %>
		<ul>ディーラーの初期手札は、
			<li><%=dealer.getHandCardStr(0)%></li>
			<li><%=dealer.getHandCardStr(1)%></li>
		</ul>です
		<% if(blackjackMessageForDealer != null){ %>
			<p>ディーラーの数値の合計は<%=dealer.getPoint()%>です</p>
			<p><%=blackjackMessageForDealer %></p>
			<p><%=resultMessage %></p>
			<p><%=chipMessage %></p>
		<% }else{ %>
			<%if(countedHit.equals(0)){%>
				<p>点数が17点以上だったため、ディーラーはカードを追加しませんでした</p>
			<%}else{%>
				<p>点数が17点以上になるように、ディーラーは追加でカードを<%=countedHit%>枚引きました</p>
				<ul>追加されたカードは以下の通りです
					<%for(int i = 0; i < (dealer.countHand() -2); i++){%>
						<li><%=dealer.getHandCardStr(i + 2)%></li>
					<%}%>
				</ul>
			<% } %>
			<p>ディーラーの数値の合計は<%=dealer.getPoint()%>です</p>
			<%if(!(bustedDealer == null)){%>
				<p><%=bustedDealer%></p>
				<p><%=resultMessage %></p>
				<p><%=chipMessage %></p>
			<%}else{%>
				<p><%=situationMessage %></p>
				<p><%=resultMessage %></p>
				<p><%=chipMessage %></p>
			<%}%>
		<%}%>
	<% } %>
	<form action="PlayServlet" method="get">
		<button type="submit">もう一度遊ぶ</button>
	</form>
	<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>
	
	


</body>
</html>