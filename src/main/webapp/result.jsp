<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Dealer" import="model.PlayerInGame"
    import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
<link rel="stylesheet" href="CSS/css.css">
</head>
<body>
	<%	Dealer dealer = (Dealer) session.getAttribute("dealer");
		List<PlayerInGame> playerHandList = 
			(List<PlayerInGame>) session.getAttribute("playerHandList");%>
		
	<div class="game_area">
		<div class="game_text_name_area">
			<p class="game_gambler_name">&lt; NAVIGATION &gt;</p>
		</div>
		<div class="game_text_area">
			<div style="margin-top: 1.3em"><br></div>
			<%	String hit = (String) request.getAttribute("hit");
				if(hit != null){ %>
					<p><%=hit%></p>
			<%	} %>
			<p>あなたの
				<% 	if(playerHandList.get(0).isSplitA()){ %>
					一組目の
				<%	 } %>
				手札は、
				<%	for(int i = 0; i < playerHandList.get(0).countHand(); i++ ){ %>
							<%=	playerHandList.get(0).getHandCardStr(i)%>
							<% 	if((i + 1) < playerHandList.get(0).countHand()){%>
								・
							<%	}
					}%>
				で、カードの数値の合計は
				<span class="red_text">
					<%=playerHandList.get(0).getPoint()%>
				</span>です</p>
			<% 	if(playerHandList.get(0).isSplitA()){ %>
					<%	if(playerHandList.get(0).actionIsEnd()){ %>
						<p class="red_text"><%=(String)request.getAttribute("actionAisEnd")%></p>
					<%	} 
						String splitBHit = (String) request.getAttribute("splitBHit");
						if(splitBHit != null){ %>
							<p><%=splitBHit%></p>
					<%	} %>
					<p>あなたの二組目の手札は、
						<% 	for(int i = 0; i < playerHandList.get(1).countHand(); i++ ){ %>
								<%=	playerHandList.get(1).getHandCardStr(i)%>
							<%	if((i + 1) < playerHandList.get(1).countHand()){%>
									・
							<%	} 
							}%>
						で、現在のカードの数値の合計は
						<span class="red_text">
							<%=playerHandList.get(1).getPoint()%>
						</span>です
					</p>
					<%	if(playerHandList.get(1).actionIsEnd()){ %>
							<p class="red_text"><%=(String) request.getAttribute("actionBisEnd")%></p>
					<% 	} 
				}
				String resultMessage = (String) request.getAttribute("resultMessage");
				String chipMessage = (String) request.getAttribute("chipMessage");
				String bustingPlayer = (String) request.getAttribute("bustingPlayer");
				if(bustingPlayer != null){ %>
					<p><%=bustingPlayer %></p>
					<p style="color: blue"><%=resultMessage %></p>
					<p><%=chipMessage %></p>
			<%	 }else{ 
					String blackjackMessageForPlayer = 
						(String) request.getAttribute("blackjackMessageForPlayer");
					if(blackjackMessageForPlayer != null){ %>
							<p class="red_text"><%=blackjackMessageForPlayer %></p>
				<% 	} %>
					<p>ディーラーの初期手札は、<%=dealer.getHandCardStr(0)%>・
						<%=dealer.getHandCardStr(1)%>でした</p>
					<% 	String blackjackMessageForDealer = 
							(String) request.getAttribute("blackjackMessageForDealer");
						if(blackjackMessageForDealer != null){ %>
							<p><%=blackjackMessageForDealer %></p>
							<p style="color: blue"><%=resultMessage %></p>
							<p><%=chipMessage %></p>
					<%	 }else{ 
							Integer countedHit = (Integer) request.getAttribute("countHit");
							if(countedHit.equals(0)){%>
								<p>初めから点数が17点以上だったため、ディーラーはカードの追加を行いませんでした</p>
								<p>ディーラーの数値の合計は
									<span class="red_text">
										<%=dealer.getPoint()%>
									</span>です
								</p>
						<%	}else{%>
							<p>ディーラーは点数が17点以上になるよう、追加でカードを<%=countedHit%>枚引きました</p>
							<p>追加されたカードは、
								<%	for(int i = 0; i < (dealer.countHand() -2); i++){%>
										<%=dealer.getHandCardStr(i + 2)%>
									<%	if((i + 3) < dealer.countHand()){ %>
										・
									<%	} 
									}%>
								で、ディーラーの数値の合計は
								<span class="red_text"><%=dealer.getPoint()%></span>となりました</p>
						<% 	} 
							String bustingDealer = (String) request.getAttribute("bustingDealer");
							String resultMessageOfB = (String) request.getAttribute("resultMessageOfB");
							String chipMessageOfB = (String) request.getAttribute("chipMessageOfB");
							String situationMessage = (String) request.getAttribute("situationMessage");
							String situationMessageOfB = (String) request.getAttribute("situationMessageOfB");
							if((bustingDealer != null && playerHandList.get(0).isSplitWStand())
									|| (bustingDealer != null && !playerHandList.get(0).isSplitA())){%>
								<p><%=bustingDealer%></p>
								<p style="color: blue"><%=resultMessage %></p>
								<p><%=chipMessage %></p>
						<%	}else if (bustingDealer != null && playerHandList.get(0).isSplitA()){%>
								<p><%=bustingDealer %></p>
								<p>一組目の手札について、<%=situationMessage %></p>
								<p style="color: blue"><%=resultMessage %></p>
								<p><%=chipMessage %></p>
								<p>二組目の手札について、<%=situationMessageOfB %></p>
								<p style="color: blue"><%=resultMessageOfB %></p>
								<p><%=chipMessageOfB %></p>
						<%	}else if(playerHandList.get(0).isSplitA()){ %>
								<p>一組目の手札について、<%=situationMessage %></p>
								<p style="color: blue"><%=resultMessage %></p>
								<p><%=chipMessage %></p>
								<p>二組目の手札について、<%=situationMessageOfB %></p>
								<p style="color: blue"><%=resultMessageOfB %></p>
								<p><%=chipMessageOfB %></p>
						<%	}else{ %>
								<p><%=situationMessage %></p>
								<p style="color: blue"><%=resultMessage %></p>
								<p><%=chipMessage %></p>
						<%	}
						}
			 	} %>
		</div>
		<div class="game_dealer_area">
			<p class="game_gambler_name">&lt; DEALER &gt;</p>
			<div style="margin-top: 1.3em"><br></div>
			<%	for(int i = 0; i < dealer.countHand(); i++ ){ %>
					<img alt="<%=dealer.getHandCardStr(i)%>" 
						src="<%=request.getContextPath()%>/img/<%=dealer.getHandCardStr(i)%>.png"/>
			<%	} %>
		</div>
		<div class="game_player_area">
			<p class="game_gambler_name">&lt; PLAYER &gt;</p>
			<%	if(!playerHandList.get(0).isSplitA()){ %>
					<div style="margin-top: 1.3em"><br></div>
					<%	for(int i = 0; i < playerHandList.get(0).countHand(); i++ ){ %>
							<img alt="<%=playerHandList.get(0).getHandCardStr(i)%>" 
									src="<%=request.getContextPath()%>/img/
									<%=playerHandList.get(0).getHandCardStr(i)%>.png"/>
					<%	} 
				}else{%>
					<div class="game_split_area_A">
						<div style="margin-top: 1.3em"><br></div>
						<%	for(int i = 0; i < playerHandList.get(0).countHand(); i++ ){ %>
								<img alt="<%=playerHandList.get(0).getHandCardStr(i)%>" 
									src="<%=request.getContextPath()%>/img/
									<%=playerHandList.get(0).getHandCardStr(i)%>.png"/>
						<%	} %>
					</div>
					<div class="game_split_area_B">
						<div style="margin-top: 1.3em"><br></div>
						<%	for(int i = 0; i < playerHandList.get(1).countHand(); i++ ){ %>
								<img alt="<%=playerHandList.get(1).getHandCardStr(i)%>" 
									src="<%=request.getContextPath()%>/img/
									<%=playerHandList.get(1).getHandCardStr(i)%>.png"/>
						<%	} %>
					</div>
			<%	} %>
		</div>
		<div class="game_action_area">
			<p class="game_gambler_name">&lt; ACTION &gt;</p>
			<div style="margin-top: 1.3em"><br></div>
			<form action="AfterGameServlet" method="get">
				<button type="submit" name="clicked" value="playAgain">もう一度遊ぶ</button>
				<button type="submit" name="clicked" value="backHome">メインメニュー画面に戻る</button>
			</form>
		</div>
	</div>
</body>
</html>