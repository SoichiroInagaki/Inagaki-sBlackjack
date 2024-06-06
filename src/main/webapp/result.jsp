<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Dealer" import="model.PlayerInGame"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
<link rel="stylesheet" href="CSS/css.css">
</head>
<body>
	<%	Dealer dealer = (Dealer) session.getAttribute("dealer");
		PlayerInGame playerInGame = 
			(PlayerInGame) session.getAttribute("playerInGame");
		String hit = (String) request.getAttribute("Hit");
		String bustedPlayer = (String) request.getAttribute("bustedPlayer");
		String resultMessage = (String) request.getAttribute("resultMessage");
		String resultMessageOfB = (String) request.getAttribute("resultMessageOfB");
		String chipMessage = (String) request.getAttribute("chipMessage");
		String chipMessageOfB = (String) request.getAttribute("chipMessageOfB");
		Integer countedHit = (Integer) request.getAttribute("countHit");
		String blackjackMessageForPlayer = (String) request.getAttribute("blackjackMessageForPlayer");
		String blackjackMessageForDealer = (String) request.getAttribute("blackjackMessageForDealer");
		String bustedDealer = (String) request.getAttribute("bustedDealer");
		String situationMessage = (String) request.getAttribute("situationMessage");
		String situationMessageOfB = (String) request.getAttribute("situationMessageOfB");
		String actionAisEnd = (String) session.getAttribute("actionAisEnd");
		String actionBisEnd = (String) session.getAttribute("actionBisEnd");
		PlayerInGame splitA = (PlayerInGame) session.getAttribute("splitA");
		PlayerInGame splitB = (PlayerInGame) session.getAttribute("splitB");
		Boolean splitting = (Boolean) session.getAttribute("splitting");
		Boolean splitWStand = (Boolean) request.getAttribute("splitWStand");%>
		
	<div class="game_text_area">
		<%	if(hit != null){ %>
			<p><%=hit%></p>
		<%	} %>
		<p>あなたの
			<% if(splitting != null){ %>
				一組目の
			<% } %>
			手札は、
			<% 	if(splitting != null){
						playerInGame = splitA;
					} 
					for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
						<%=	playerInGame.getHandCardStr(i)%>
						<% 	if((i + 1) < playerInGame.countHand()){%>
							・
				<%	}
				}%>
			で、カードの数値の合計は<span style="color: red"><%=playerInGame.getPoint()%></span>です</p>
		<% 	if(splitting != null){ %>
			<p style="color: red"><%=actionAisEnd%></p>
			<p>あなたの二組目の手札は、
				<% 	playerInGame = splitB;
					for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
						<%=	playerInGame.getHandCardStr(i)%>
					<%	if((i + 1) < playerInGame.countHand()){%>
							・
					<%	} 
					}%>
				で、現在のカードの数値の合計は<span style="color: red"><%=playerInGame.getPoint()%></span>です</p>
			<p style="color: red"><%=actionBisEnd%></p>
		<% } %>
		<% if(bustedPlayer != null){ %>
			<p><%=bustedPlayer %></p>
			<p style="color: blue"><%=resultMessage %></p>
			<p><%=chipMessage %></p>
		<% }else{ %>
			<% if(blackjackMessageForPlayer != null){ %>
				<p style="color: red"><%=blackjackMessageForPlayer %></p>
			<% } %>
			<p>ディーラーの初期手札は、<%=dealer.getHandCardStr(0)%>・
				<%=dealer.getHandCardStr(1)%>でした</p>
			<% if(blackjackMessageForDealer != null){ %>
				<p><%=blackjackMessageForDealer %></p>
				<p style="color: blue"><%=resultMessage %></p>
				<p><%=chipMessage %></p>
			<% }else{ %>
				<%if(countedHit.equals(0)){%>
					<p>初めから点数が17点以上だったため、ディーラーはカードの追加を行いませんでした</p>
					<p>ディーラーの数値の合計は<span style="color: red"><%=dealer.getPoint()%></span>です</p>
				<%}else{%>
					<p>ディーラーは点数が17点以上になるよう、追加でカードを<%=countedHit%>枚引きました</p>
					<p>追加されたカードは、
						<%	for(int i = 0; i < (dealer.countHand() -2); i++){%>
								<%=dealer.getHandCardStr(i + 2)%>
							<%	if((i + 3) < dealer.countHand()){ %>
								・
							<%	} 
							}%>
						で、ディーラーの数値の合計は
						<span style="color: red"><%=dealer.getPoint()%></span>となりました</p>
				<% } %>
				<%if((bustedDealer != null && splitWStand != null) || (bustedDealer != null && splitting == null)){%>
					<p><%=bustedDealer%></p>
					<p style="color: blue"><%=resultMessage %></p>
					<p><%=chipMessage %></p>
				<%}else if (bustedDealer != null && splitting != null){%>
					<p><%=bustedDealer %></p>
					<p>一組目の手札について、<%=situationMessage %></p>
					<p style="color: blue"><%=resultMessage %></p>
					<p><%=chipMessage %></p>
					<p>二組目の手札について、<%=situationMessageOfB %></p>
					<p style="color: blue"><%=resultMessageOfB %></p>
					<p><%=chipMessageOfB %></p>
				<%}else if(splitting != null){ %>
					<p><p>一組目の手札について、<%=situationMessage %></p>
					<p style="color: blue"><%=resultMessage %></p>
					<p><%=chipMessage %></p>
					<p>二組目の手札について、<%=situationMessageOfB %></p>
					<p style="color: blue"><%=resultMessageOfB %></p>
					<p><%=chipMessageOfB %></p>
				<%}else{ %>
					<p><%=situationMessage %></p>
					<p style="color: blue"><%=resultMessage %></p>
					<p><%=chipMessage %></p>
				<%}%>
			<%}%>
		<% } %>
	</div>
	<div class="game_dealer_area">
		<p class="game_gambler_name">&lt; DEALER &gt;</p>
		<div style="margin-top: 1em"><br></div>
		<%	for(int i = 0; i < dealer.countHand(); i++ ){ %>
				<img alt="<%=dealer.getHandCardStr(i)%>" 
					src="<%=request.getContextPath()%>/img/<%=dealer.getHandCardStr(i)%>.png"/>
		<%	} %>
	</div>
	<div class="game_player_area">
		<p class="game_gambler_name">&lt; PLAYER &gt;</p>
		<%	if(splitting == null){ %>
				<div style="margin-top: 1em"><br></div>
				<%	for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
						<img alt="<%=playerInGame.getHandCardStr(i)%>" 
								src="<%=request.getContextPath()%>/img/<%=playerInGame.getHandCardStr(i)%>.png"/>
				<%	} 
			}else{%>
				<div class="game_split_area_A">
					<div style="margin-top: 1em"><br></div>
					<%	for(int i = 0; i < splitA.countHand(); i++ ){ %>
							<img alt="<%=splitA.getHandCardStr(i)%>" 
								src="<%=request.getContextPath()%>/img/<%=splitA.getHandCardStr(i)%>.png"/>
					<%	} %>
				</div>
				<div class="game_split_area_B">
					<div style="margin-top: 1em"><br></div>
					<%	for(int i = 0; i < splitB.countHand(); i++ ){ %>
							<img alt="<%=splitB.getHandCardStr(i)%>" 
								src="<%=request.getContextPath()%>/img/<%=splitB.getHandCardStr(i)%>.png"/>
					<%	} %>
				</div>
		<%	} %>
	</div>
	<div class="game_action_area">
		<p class="game_gambler_name">&lt; ACTION &gt;</p>
			<form action="PlayServlet" method="get">
				<button type="submit">もう一度遊ぶ</button>
			</form>
			<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>
	</div>
</body>
</html>