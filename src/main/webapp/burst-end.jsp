<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
</head>
<body>
	<p>ヒットしました</p>
	<ul>あなたの手札は、
		<% for(int i = 0; i < playerInGame.countHand(); i++ ){ %>
		<li><%=playerInGame.getHandCard(i)%></li>
		<%}%>
	</ul>です
	<br>
	<p>現在のカードの数値の合計は<%=playerInGame.getPoint()%>で、バーストしてしまいました......</p>
	<p>You lose......</p>
	<form action="GameServlet" method="get">
		<button type="submit">もう一度遊ぶ</button>
	</form>
	<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>
	
	

</body>
</html>