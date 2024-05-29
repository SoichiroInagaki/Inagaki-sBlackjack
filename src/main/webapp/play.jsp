<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
</head>
<body>
	<h1>-Blackjack-</h1>
	<h3>ここはゲーム画面</h3>
	
	<%
		String message = (String) request.getAttribute("message");
		if(message != null){
	%>
	<p><%=message%></p>
	<%}%>
	
	<% Integer chip = (Integer) request.getAttribute("chip");%>
	<p>現在保有中のチップ枚数：<%=chip%></p>
	
	<form action="GameServlet" method="get">
		<p>ベット額</p>
		<select name="bet" required>
			<option value="">選択してください</option>
			<option value="1">1</option>
			<option value="2">2</option>
			<option value="3">3</option>
			<option value="4">4</option>
			<option value="5">5</option>
			<option value="6">6</option>
			<option value="7">7</option>
			<option value="8">8</option>
			<option value="9">9</option>
			<option value="10">10</option>
		</select>
		<br>
		<button type="submit">賭け金を設定してゲームを開始する</button>
	</form>
	<p><a href="menu.jsp">メインメニュー画面に戻る</a></p>


</body>
</html>