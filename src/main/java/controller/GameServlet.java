package controller;

import java.io.IOException;

import dao.PlayerDao;
import exception.BlackjackException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Dealer;
import model.Deck;
import model.Player;
import model.PlayerInGame;
import model.Result;

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    //ゲーム開始時に用いるメソッド
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//処理に必要な変数等を準備
		HttpSession session = request.getSession();
		Player player = (Player) session.getAttribute("player");
		int id = player.getId();
		int chip = Integer.valueOf(request.getParameter("bet"));
		String message = null;
		String nextPage = null;
		
		//スプリット時にセッションに保持される情報を初期化
		session.setAttribute("actionAisEnd", null);
		session.setAttribute("actionBisEnd", null);
		session.setAttribute("splitA", null);
		session.setAttribute("splitB", null);
		
		//カードを準備
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		//DB処理を行う
		try {
			PlayerDao playerDao = new PlayerDao();
			playerDao.bet(id, chip);
			
			//スプリット判定
			boolean canSplit = playerInGame.checkSplit();
			if(canSplit) {
				request.setAttribute("canSplit", canSplit);
				int totalChips = playerDao.getChip(player.getId());
				request.setAttribute("totalChips", totalChips);
				boolean pairOfA = playerInGame.checkAPair();
				if(pairOfA) {
					session.setAttribute("pairOfA", pairOfA);
				}
			}
			
			//ブラックジャック判定
			//ブラックジャックが完成していた場合、結果画面に遷移する
			//完成していなければ、ゲーム画面に遷移する
			if(playerInGame.checkBlackjack() && !(dealer.checkBlackjack())) {
				message = "BLACKJACK!!";
				request.setAttribute("blackjackMessageForPlayer", message);
				message = "ディーラーの手札はブラックジャックではなかったので、あなたの勝ちです！";
				request.setAttribute("blackjackMessageForDealer", message);
				double calculatedChip = (chip * 2.5);
				int cashBackedChip = (int)calculatedChip;
				Result.win(request, cashBackedChip);
				playerDao.cashBack(id, cashBackedChip);
				playerDao.updateRecord(player, "win");
				nextPage = "result.jsp";
			}else if(playerInGame.checkBlackjack() && dealer.checkBlackjack()) {
				message = "BLACKJACK!!";
				request.setAttribute("blackjackMessageForPlayer", message);
				message = "ディーラーの手札もブラックジャックでした！このゲームは引き分けです";
				request.setAttribute("blackjackMessageForDealer", message);
				Result.draw(request, chip);
				playerDao.cashBack(id, chip);
				playerDao.updateRecord(player, "draw");
				nextPage = "result.jsp";
			}else if(!(playerInGame.checkBlackjack()) && dealer.checkBlackjack()) {
				message = "ディーラーの手札はブラックジャックでした。お気の毒ですが、あなたの負けです";
				request.setAttribute("blackjackMessageForDealer", message);
				Result.lose(request, chip);
				playerDao.updateRecord(player, "lose");
				nextPage = "result.jsp";
			}else {
				nextPage = "in-game.jsp";
			}
		}catch(BlackjackException e) {
			//エラーメッセージを表示させる
			message = e.getMessage();
			request.setAttribute("message", message);
			//画面遷移先はゲーム開始画面を指定
			nextPage = "play.jsp";
		}
		
		
		//チップ・カード状況をセッションに保持
		session.setAttribute("bettingChip", chip);
		session.setAttribute("deck", deck);
		session.setAttribute("playerInGame", playerInGame);
		session.setAttribute("dealer", dealer);
		
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
	
	//ゲーム進行中に用いるメソッド
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		//処理に必要な変数を用意
		String nextPage = null;
		String message = null;
		HttpSession session = request.getSession();
		Player player = (Player) session.getAttribute("player");
		Dealer dealer = (Dealer) session.getAttribute("dealer");
		PlayerInGame playerInGame = (PlayerInGame) 
				session.getAttribute("playerInGame");
		Deck deck = (Deck) session.getAttribute("deck");
		int chip = (Integer)session.getAttribute("bettingChip");
		Boolean splitting = (Boolean) session.getAttribute("splitting");
		String action = null;
		PlayerInGame splitA = (PlayerInGame) session.getAttribute("splitA");
		PlayerInGame splitB = (PlayerInGame) session.getAttribute("splitB");
		boolean gameEnd = false;
		String gameResult = (String) session.getAttribute("result");
		String gameResultOfB = (String) session.getAttribute("resultOfB");
		boolean actionAisEnd = false;
		boolean actionBisEnd = false;
		Boolean pairOfA = (Boolean) session.getAttribute("pairOfA");
		
		//DB接続を行うため、try文
		try {
			PlayerDao playerDao = new PlayerDao();
			
			//スプリット時は2回、通常時は1回アクション処理の判定を行う
			for(int i = 0; i < 2; i++) {
				//JSPで入力されたアクションを取得する
				action = request.getParameter("clicked");
				if(splitting) {
					if(pairOfA && i == 0) {
						action = "stand";
						playerInGame = splitA;
					}else if(pairOfA && i == 0) {
						action = "stand";
						playerInGame = splitB;
					}else if(i == 0) {
						action = request.getParameter("actionA");
						playerInGame = splitA;
					}else {
						action = request.getParameter("actionB");
						playerInGame = splitB;
					}
				}else {
					i++;
				}
				//アクションがスプリットのとき
				if(action.equals("split")) {
					playerDao.bet(player.getId(), chip);
					splitA = new PlayerInGame();
					splitB = new PlayerInGame();
					splitA.prepareSplit(playerInGame.getHandCard(0), deck);
					splitB.prepareSplit(playerInGame.getHandCard(1), deck);
					session.setAttribute("splitA", splitA);
					session.setAttribute("splitB", splitB);
					splitting = true;
					session.setAttribute("splitting", splitting);
					if(pairOfA) {
						nextPage = "GameServlet";
					}
				//アクションがヒットのとき
				}else if(action.equals("hit")) {
					playerInGame.hit(deck);
					message = "ヒットしました";
					//スプリットした二つ目の手札をヒットした時だけ、それ専用の場所にメッセージを保持
					if(!(action.equals("actionB"))) {
						request.setAttribute("hit", message);
					}else {
						request.setAttribute("splitBHit", message);
					}
					//バーストしたかを判定する
					if(playerInGame.confirmBust()) {
						//バーストしていた場合
						//スプリットしている場合、その手札にこれ以上アクションを行えないようにする
						//敗北したという属性もセッションに付与
						if(splitting) {
							message = "この手札はバーストしています";
							if(action.equals("actionA")) {
								session.setAttribute("actionAisEnd", message);
								actionAisEnd = true;
								gameResult = "lose";
								session.setAttribute("result", "lose");
							}else {
								session.setAttribute("actionBisEnd", message);
								actionBisEnd = true;
								gameResultOfB = "lose";
								session.setAttribute("resultOfB", "lose");
							}
						//通常プレイ時はゲーム終了
						}else {
							message = "21点を超えたのでバーストしてしまいました。あなたの負けです";
							request.setAttribute("bustedPlayer", message);
							gameEnd = true;
							gameResult = "lose";
						}
					}
				//アクションがスタンドのとき、または、スプリットしていて、既にバーストしていたとき
				}else {
					//スプリット時は、その手札にこれ以上アクションできないようにする
					if(splitting) {
						message = "この手札でスタンドしています";
						if(action.equals("actionA")) {
							if(gameResult != null) {
								message = "この手札はバーストしています";
							}
							session.setAttribute("actionAisEnd", message);
							actionAisEnd = true;
						}else {
							if(gameResultOfB != null) {
								message = "この手札はバーストしています";
							}
							session.setAttribute("actionAisEnd", message);
							actionAisEnd = true;
						}
					}
					//通常プレイ時、またはスプリットした手札が両方アクション不可の時はゲーム終了
					if(action.equals("clicked") || (actionAisEnd && actionBisEnd)) {
						//スプリットしていて、両方の手札がバーストしていた場合
						if(gameResult.equals("lose") && gameResultOfB.equals("lose")) {
							message = "両方の手札がバーストしてしまいました。あなたの負けです";
							request.setAttribute("bustedPlayer", message);
							gameResult = "splitWBust";
						}else {
							dealer.hit(deck);
							request.setAttribute("countHit", dealer.countHit());
							if(dealer.confirmBust()) {
								message = "21点を超えたので、ディーラーはバーストしました！";
								request.setAttribute("bustedDealer", message);
								//スプリットした二つ目の手札をスタンドしていた場合
								if(action.equals("actionB") && (!gameResultOfB.equals("lose"))) {
									gameResultOfB = "win";
								//スプリットした一つ目の手札、もしくは、通常プレイでスタンドしていた場合
								}else if((action.equals("actionA") && (!gameResult.equals("lose"))) 
										|| action.equals("clicked")){
									gameResult = "win";
								}
								//上記if文に該当しないパターンは、スプリット時、その手札がバーストしていたパターン
							}else {
								if(dealer.getPoint() < playerInGame.getPoint()) {
									message = "あなたの方が21点に近いため、あなたの勝利です！";
									if(action.equals("actionB")) {
										request.setAttribute("situationMessageOfB", message);
										gameResultOfB = "win";
									}else {
										request.setAttribute("situationMessage", message);
										gameResult = "win";
									}
								}else if(dealer.getPoint() == playerInGame.getPoint()) {
									message = "合計点数が同じなので、このゲームは引き分けです";
									if(action.equals("actionB")) {
										request.setAttribute("situationMessageOfB", message);
										gameResultOfB = "draw";
									}else {
										request.setAttribute("situationMessage", message);
										gameResult = "draw";
									}
								}else {
									message = "ディーラーの方が21点に近いため、ディーラーの勝利です";
									if(action.equals("actionB")) {
										request.setAttribute("situationMessageOfB", message);
										gameResultOfB = "lose";
									}else {
										request.setAttribute("situationMessage", message);
										gameResult = "lose";
									}
								}
							}
						}
					}
				}
			}
			if(gameEnd || (actionAisEnd && actionBisEnd)) {
				switch(gameResult) {
				case "win":
					Result.win(request, (chip * 2));
					playerDao.cashBack(player.getId(), (chip * 2));
					playerDao.updateRecord(player, "win");
					nextPage = "result.jsp";
				case "draw":
					Result.draw(request, chip);
					playerDao.cashBack(player.getId(), chip);
					playerDao.updateRecord(player, "draw");
					nextPage = "result.jsp";
				case "lose":
					Result.lose(request, chip);
					playerDao.updateRecord(player, "lose");
					nextPage = "result.jsp";
				case "splitWBust":
					Result.splitWBust(request, chip);
					playerDao.updateRecord(player, "lose");
					playerDao.updateRecord(player, "lose");
					nextPage = "result.jsp";
				}
			}else if(pairOfA) {
				
			}else {
				nextPage = "in-game.jsp";
			}
		}catch(BlackjackException e) {
			
			//エラーメッセージを表示させる
			message = e.getMessage();
			request.setAttribute("message", message);
			
			//画面遷移先はログイン画面を指定
			nextPage = "play.jsp";
		}
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
}
