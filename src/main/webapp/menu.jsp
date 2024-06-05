<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="model.Player"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blackjack</title>
<link rel="stylesheet" href="CSS/css.css">
</head>
<body>
	<p class="title">-Blackjack-</p>
	<% 	Player player = (Player) session.getAttribute("player");
		if(player != null){%>
			<p>ログインプレイヤー：<%=player.getName()%></p>
	<%	}
		String message = (String) request.getAttribute("message");
		if(message != null){%>
			<p class="alert"><%=message%></p>
	<%	}%>
	<p class="summary">メインメニュー</>
	<form action="MenuServlet" method="get">
		<button type="submit" name="clicked" value="play" class="menu_button">ブラックジャックをプレイ</button>
		<br>
		<button type="submit" name="clicked" value="record" class="menu_button">戦績確認</button>
		<br>
		<button type="submit" name="clicked" value="delete" class="menu_button">退会</button>
		<br>
		<button type="submit" name="clicked" value="logout" class="menu_button">ログアウト</button>
	</form>
</body>
</html>