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
		String hit = (String) request.getAttribute("hit");
		String splitBHit = (String) request.getAttribute("splitBHit");
		Boolean splitting = (Boolean) session.getAttribute("splitting");
		Boolean canSplit = (Boolean)request.getAttribute("canSplit");
		Boolean pairOfA = (Boolean)session.getAttribute("pairOfA");
		PlayerInGame splitA = (PlayerInGame) session.getAttribute("splitA");
		PlayerInGame splitB = (PlayerInGame) session.getAttribute("splitB");
		Integer totalChips = (Integer) request.getAttribute("totalChips");
		Integer bettingChips = (Integer) session.getAttribute("bettingChip");
		String actionAisEnd = (String) session.getAttribute("actionAisEnd");
		String actionBisEnd = (String) session.getAttribute("actionBisEnd");%>
	
	<div class="game_text_area">
		<p>ディーラーの手札は<%=dealer.getHandCardStr(0)%>と裏向きのカード1枚です</p>
		<% if(hit != null){ %>
			<p><%=hit%></p>
		<%}%>
		<p>あなたの
			<% if(splitting != null){ %>
				一組目の
			<% } %>
			手札は、
			<% 	if(splitting != null){
					playerInGame = splitA;
				} 
				for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
					<%=playerInGame.getHandCardStr(i)%>
					<% 	if((i + 1) < playerInGame.countHand()){%>
						・
					<%	}
				}%>
			で、現在のカードの数値の合計は<span style="color: red"><%=playerInGame.getPoint()%></span>です</p>
		<% if(canSplit != null){ %>
			<p style="color: red">また、初期手札のカードが同じ数字のカードのため、スプリットが可能です</p>
			<p>現在保有中のチップ枚数：<%=totalChips%></p>
			<p>スプリット時に再度賭けるチップ枚数：<%=bettingChips %></p>
			<% if(pairOfA != null){ %>
				<p class="alert">※Aのペアをスプリットした場合、追加のヒットは行えません</p>
			<% } %>
		<% } %>
		<% 	if(splitting != null){ %>
				<% 	if(actionAisEnd != null){ %>
						<p style="color: red"><%=actionAisEnd %></p>
				<% 	}else{ %>
						<p>行うアクションを左下のボタンから選んでください</p>
				<% 	}
					if(splitBHit != null){ %>
						<p><%=splitBHit%></p>
				<%	}%>
				<p>あなたの二組目の手札は、
					<% 	playerInGame = splitB;
						for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
							<%=playerInGame.getHandCardStr(i)%>
							<%	if((i + 1) < playerInGame.countHand()){%>
								・
							<%	} 
						}%>
					で、現在のカードの数値の合計は<span style="color: red"><%=playerInGame.getPoint()%></span>です</p>
				<% if(actionBisEnd != null){ %>
					<p style="color: red"><%=actionBisEnd %></p>
				<% }else{ %>
					<p>行うアクションを左下のボタンから選んでください</p>
				<% } %>
		<% }else{ %>
			<p>行うアクションを左下のボタンから選んでください</p>
		<% } %>
	</div>
	<div class="game_dealer_area">
		<p class="game_gambler_name">&lt; DEALER &gt;</p>
		<div style="margin-top: 1em"><br></div>
		<img alt="<%=dealer.getHandCardStr(0)%>" 
				src="<%=request.getContextPath()%>/img/<%=dealer.getHandCardStr(0)%>.png"/>
		<img alt="裏面カード" 
				src="<%=request.getContextPath()%>/img/裏面カード.png"/>
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
		<%	if(splitting == null){ %>
				<div style="margin-top: 1em"><br></div>
				<form action="GameServlet" method="post">
					<button type="submit" name="clicked" value="hit">HIT</button>
					<button type="submit" name="clicked" value="stand">STAND</button>
					<% 	if(canSplit != null){ %>
							<button type="submit" name="clicked" value="split">SPLIT</button>
					<% 	} %>
				</form>
		<%	}else{ %>
				<div class="game_actionA_area">
					<div style="margin-top: 1em"><br></div>
						<input type="radio" name="actionA" value="hit" form="form" id="hitA" required>
							<label class="split_action_label" for="hitA">HIT</label>
						<input type="radio" name="actionA" value="stand" form="form" id="standA">
							<label class="split_action_label" for="standA">STAND</label>
				</div>
				<div class="game_actionB_area">
					<div style="margin-top: 1em"><br></div>
						<input type="radio" name="actionB" value="hit" form="form" id="hitB" required>
							<label class="split_action_label" for="hitB">HIT</label>
						<input type="radio" name="actionB" value="stand" form="form" id="standB">
							<label class="split_action_label" for="standB">STAND</label>
				</div>
				<div class="game_action_split_decision_area">
					<form action="GameServlet" method="post" id="form">
						<button type="submit">
							<%if(actionAisEnd == null && actionBisEnd == null){%>
								それぞれの
							<% } %>
							アクションを確定
						</button>
					</form>
				</div>
		<%	} %>
	</div>
</body>
</html>