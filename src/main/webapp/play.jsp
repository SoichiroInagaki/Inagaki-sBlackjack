<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
<link rel="stylesheet" href="CSS/css.css">
</head>
<body>
	<p class="title">-Blackjack-</p>
	<%	String message = (String) request.getAttribute("message");
		if(message != null){%>
			<p class="alert"><%=message%></p>
	<%	}
		Integer chip = (Integer) request.getAttribute("chip");%>
	<p>現在保有中のチップ枚数：<%=chip%></p>
	<form action="NewGameServlet" method="post">
		<p style="margin-top: 2em">ベット額</p>
		<select name="bet" required>
			<option value="">SELECT</option>
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
	<button onclick="location.href='menu.jsp'">
		メインメニュー画面に戻る
	</button>
</body>
</html>