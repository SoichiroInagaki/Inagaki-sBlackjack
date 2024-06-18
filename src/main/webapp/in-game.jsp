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
			(List<PlayerInGame>) session.getAttribute("playerHandList");
		Integer totalChips = (Integer) request.getAttribute("totalChips");
		int bettingChips = playerHandList.get(0).getChip();%>
		
	<div class="game_area">
		<div class="game_text_area">
			<div class="game_gambler_name">&lt; NAVIGATION &gt;</div>
			<div style="margin-top: 1.3em"><br></div>
			<p>ディーラーの手札は<%=dealer.getHandCardStr(0)%>と裏向きのカード1枚です</p>
			<% 	String hit = (String) request.getAttribute("hit");
				if(hit != null){ %>
				<p><%=hit%></p>
			<%	}%>
			<p>あなたの
				<% if(playerHandList.get(0).isSplitA()){ %>
					一組目の
				<% } %>
				手札は、
				<% 	for(int i = 0; i < playerHandList.get(0).countHand(); i++ ){ %>
						<%=	playerHandList.get(0).getHandCardStr(i)%>
						<% 	if((i + 1) < playerHandList.get(0).countHand()){%>
							・
						<%	}
					}%>
				で、現在のカードの数値の合計は
				<span class="red_text">
					<%=playerHandList.get(0).getPoint()%>
				</span>です</p>
			<% 	if(!playerHandList.get(0).isSplitA() && 
					playerHandList.get(0).checkSplittable()){ %>
				<p class="red_text">
					また、初期手札のカードが同じ数字のカードのため、スプリットが可能です
				</p>
				<p>現在保有中のチップ枚数：<%=totalChips%></p>
				<p>スプリット時に再度賭けるチップ枚数：<%=bettingChips %></p>
				<% 	if(playerHandList.get(0).checkPairOfA()){ %>
					<p class="alert">
						※Aのペアをスプリットした場合、追加のヒットは行えません
					</p>
				<% } %>
			<% } %>
			<% 	if(playerHandList.get(0).isSplitA()){ %>
					<% 	if(playerHandList.get(0).actionIsEnd()){ %>
							<p class="red_text">
								<%=(String) request.getAttribute("actionAisEnd")%>
							</p>
					<% 	}else{ %>
							<p>行うアクションを左下のボタンから選んでください▼</p>
					<% 	}
						String splitBHit = (String) request.getAttribute("splitBHit");
						if(splitBHit != null){ %>
							<p><%=splitBHit%></p>
					<%	}%>
					<p>あなたの二組目の手札は、
						<% 	for(int i = 0; i < playerHandList.get(1).countHand(); i++ ){ %>
								<%=	playerHandList.get(1).getHandCardStr(i)%>
								<%	if((i + 1) < playerHandList.get(1).countHand()){%>
									・
								<%	} 
							}%>
						で、現在のカードの数値の合計は
						<span class="red_text">
							<%=	playerHandList.get(1).getPoint()%>
						</span>です</p>
					<% 	if(playerHandList.get(1).actionIsEnd()){ %>
							<p class="red_text">
								<%=(String) request.getAttribute("actionBisEnd")%>
							</p>
					<% 	}else{ %>
						<p>行うアクションを左下のボタンから選んでください▼</p>
					<% 	} %>
			<% 	}else{ %>
				<p>行うアクションを左下のボタンから選んでください▼</p>
			<% 	} %>
		</div>
		<div class="game_dealer_area">
			<p class="game_gambler_name">&lt; DEALER &gt;</p>
			<div style="margin-top: 1.3em"><br></div>
			<img alt="<%=dealer.getHandCardStr(0)%>" 
					src="<%=request.getContextPath()%>/img/
					<%=dealer.getHandCardStr(0)%>.png"/>
			<img alt="裏面カード" 
					src="<%=request.getContextPath()%>/img/裏面カード.png"/>
		</div>
		<div class="game_player_area">
			<p class="game_gambler_name">&lt; PLAYER &gt;</p>
			<%	if(!playerHandList.get(0).isSplitA()){ %>
					<div style="margin-top: 1.3em"><br></div>
					<%	for(int i = 0; i < playerHandList.get(0).countHand(); i++ ){ %>
							<img alt="<%=	playerHandList.get(0).getHandCardStr(i)%>" 
									src="<%=	request.getContextPath()%>/img/
									<%=	playerHandList.get(0).getHandCardStr(i)%>.png"/>
					<%	} 
				}else{%>
					<div class="game_split_area_A">
						<div style="margin-top: 1.3em"><br></div>
						<%	for(int i = 0; i < playerHandList.get(0).countHand(); i++ ){ %>
								<img alt="<%=	playerHandList.get(0).getHandCardStr(i)%>" 
									src="<%=	request.getContextPath()%>/img/
									<%=playerHandList.get(0).getHandCardStr(i)%>.png"/>
						<%	} %>
					</div>
					<div class="game_split_area_B">
						<div style="margin-top: 1.3em"><br></div>
						<%	for(int i = 0; i < playerHandList.get(1).countHand(); i++ ){ %>
								<img alt="<%=	playerHandList.get(1).getHandCardStr(i)%>" 
									src="<%=	request.getContextPath()%>/img/
									<%=	playerHandList.get(1).getHandCardStr(i)%>.png"/>
						<%	} %>
					</div>
			<%	} %>
		</div>
		<div class="game_action_area">
			<p class="game_gambler_name">&lt; ACTION &gt;</p>
			<%	if(!playerHandList.get(0).isSplitA()){ %>
					<div style="margin-top: 1.3em">
						<br>
					</div>
					<form action="GameServlet" method="post">
						<button type="submit" name="clicked" value="hit">HIT</button>
						<button type="submit" name="clicked" value="stand">STAND</button>
						<% 	if(playerHandList.get(0).checkSplittable()){ %>
								<button type="submit" name="clicked" value="split">SPLIT</button>
						<% 	} %>
					</form>
			<%	}else{ %>
					<form action="GameServlet" method="post" id="form">
						<div class="game_actionA_area">
							<div style="margin-top: 1.3em">
								<br>
							</div>
							<% 	if(playerHandList.get(0).actionIsEnd()){ %>
									<p><span class="game_action_end_text">
										CAN'T TAKE ACTION!
									</span></p>
							<%	}else{ %>
									<input type="radio" name="actionA" value="hit" form="form" id="hitA" required>
										<label class="split_action_label" for="hitA">
											HIT
										</label>
									<input type="radio" name="actionA" value="stand" form="form" id="standA">
										<label class="split_action_label" for="standA">
											STAND
										</label>
							<%	} %>
						</div>
						<div class="game_actionB_area">
							<div style="margin-top: 1.3em">
								<br>
							</div>
							<% 	if(playerHandList.get(1).actionIsEnd()){ %>
									<p><span class="game_action_end_text">
										CAN'T TAKE ACTION!
									</span></p>
							<%	}else{ %>
									<input type="radio" name="actionB" value="hit" form="form" id="hitB" required>
										<label class="split_action_label" for="hitB">
											HIT
										</label>
									<input type="radio" name="actionB" value="stand" form="form" id="standB">
										<label class="split_action_label" for="standB">
											STAND
										</label>
							<%	} %>
						</div>
						<div class="game_action_split_decision_area">
							<button type="submit">
									<%	if(!playerHandList.get(0).actionIsEnd() && 
											playerHandList.get(1).actionIsEnd()){%>
										それぞれの
									<% } %>
									アクションを確定
							</button>
						</div>
					</form>
			<%	} %>
		</div>
	</div>
</body>
</html>