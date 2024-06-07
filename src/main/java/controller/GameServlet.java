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
       
	
    //ゲーム開始時はdoGetメソッドを用いる
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		//保持する情報の一部はセッションスコープで保持する
		HttpSession session = request.getSession();
		
		/* ゲームのリメイク時、以前のゲームで
		 * スプリットしていた際にセッションに保持される情報を初期化する*/
		session.setAttribute("actionAisEnd", null);
		session.setAttribute("actionBisEnd", null);
		session.setAttribute("splitA", null);
		session.setAttribute("splitB", null);
		session.setAttribute("splitting", null);
		session.setAttribute("result", null);
		session.setAttribute("resultOfB", null);
		session.setAttribute("pairOfA", null);
		
		//カードを準備し、ディーラーとプレイヤーに手札を配る
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		//チップ・カード状況は複数の画面で共有するため、セッションに保持
		int chip = Integer.valueOf(request.getParameter("bet"));
		session.setAttribute("bettingChip", chip);
		session.setAttribute("deck", deck);
		session.setAttribute("playerInGame", playerInGame);
		session.setAttribute("dealer", dealer);
		
		// 画面遷移用の変数を宣言
		String message = null;
		String nextPage = null;
		
		//DBを利用する処理を行う
		try {
			//DB接続用に、プレイヤーのDaoインスタンスを取得
			Player player = (Player) session.getAttribute("player");
			PlayerDao playerDao = new PlayerDao();
			
			//賭けられたチップ枚数分、DBに登録されているチップ総量を減らす
			playerDao.bet(player.getId(), chip);
			
			//スプリット可能か判定する
			if(playerInGame.checkSplit()) {
				
				/* 同じ数字のペアなら、スプリット可能であるという情報・
				 * プレイヤーの保有チップ枚数をリクエストスコープに保持*/
				request.setAttribute("canSplit", true);
				int totalChips = playerDao.getChip(player.getId());
				request.setAttribute("totalChips", totalChips);
				
				//さらに、Aのペアなら、その旨の情報をセッションスコープに保持
				if(playerInGame.checkAPair()) {
					session.setAttribute("pairOfA", true);
				}
			}
			
			//ブラックジャック判定
			/* プレイヤーかディーラーのどちらかのブラックジャックが
			 * 完成していた場合、結果画面に遷移する*/
			if(playerInGame.checkBlackjack() && !dealer.checkBlackjack()) {
				Result.winOfBlackjack(request);
				double calculatedChip = (chip * 2.5);
				int cashBackedChip = (int)calculatedChip;
				Result.win(request, cashBackedChip);
				playerDao.cashBack(player.getId(), cashBackedChip);
				playerDao.updateRecord(player, "win");
				nextPage = "result.jsp";
			}else if(playerInGame.checkBlackjack() && dealer.checkBlackjack()) {
				Result.drawOfBlackjack(request);
				Result.draw(request, chip);
				playerDao.cashBack(player.getId(), chip);
				playerDao.updateRecord(player, "draw");
				nextPage = "result.jsp";
			}else if(!(playerInGame.checkBlackjack()) && dealer.checkBlackjack()) {
				Result.loseOfBlackjack(request);
				Result.lose(request, chip);
				playerDao.updateRecord(player, "lose");
				nextPage = "result.jsp";
			//ブラックジャックが完成していなければ、ゲーム画面に遷移する
			}else {
				nextPage = "in-game.jsp";
			}
		}catch(BlackjackException e) {
			//エラーメッセージがある時は表示させる
			message = e.getMessage();
			request.setAttribute("message", message);
			//画面遷移先はゲーム開始画面を指定
			nextPage = "play.jsp";
		}
		//処理が終わったら画面遷移を行う
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
	
	
	//ゲーム進行中はdoPostメソッドを用いる
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {		
		
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
		boolean splitWStand = false;
		
		//DB接続を行うため、try文
		try {
			PlayerDao playerDao = new PlayerDao();
			
			//スプリット時は2回、通常時は1回アクション処理の判定を行う
			for(int i = 0; i < 2; i++) {
				//JSPで入力されたアクションを取得する
				action = request.getParameter("clicked");
				if(splitting != null) {
					if(pairOfA != null &&  i == 0) {
						action = "stand";
						playerInGame = splitA;
					}else if(pairOfA != null && i == 1) {
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
				if(action != null && action.equals("split")) {
					playerDao.bet(player.getId(), chip);
					splitA = new PlayerInGame();
					splitB = new PlayerInGame();
					splitA.prepareSplit(playerInGame.getHandCard(0), deck);
					splitB.prepareSplit(playerInGame.getHandCard(1), deck);
					session.setAttribute("splitA", splitA);
					session.setAttribute("splitB", splitB);
					splitting = true;
					session.setAttribute("splitting", splitting);
				//アクションがヒットのとき
				}else if(action != null && action.equals("hit")) {
					playerInGame.hit(deck);
					message = "ヒットしました";
					//スプリットした二つ目の手札をヒットした時だけ、それ専用の場所にメッセージを保持
					if(splitting != null && i == 1){
						request.setAttribute("splitBHit", message);
					}else {
						request.setAttribute("hit", message);
					}
					//バーストしたかを判定する
					if(playerInGame.confirmBust()) {
						//バーストしていた場合
						//スプリットしている場合、その手札にこれ以上アクションを行えないようにする
						//敗北したという属性もセッションに付与
						if(splitting != null) {
							message = "この手札はバーストしています";
							if(i == 0) {
								session.setAttribute("actionAisEnd", message);
								message = "21点を超えてバーストしているため、あなたの負けです";
								request.setAttribute("situationMessage", message);
								actionAisEnd = true;
								gameResult = "lose";
								session.setAttribute("result", "lose");
							}else {
								session.setAttribute("actionBisEnd", message);
								message = "21点を超えてバーストしているため、あなたの負けです";
								request.setAttribute("situationMessageOfB", message);
								actionBisEnd = true;
								gameResultOfB = "loseOfB";
								session.setAttribute("resultOfB", "loseOfB");
							}
							if(gameResult != null && gameResultOfB != null) {
								message = "両方の手札がバーストしてしまいました。あなたの負けです";
								request.setAttribute("bustedPlayer", message);
								gameResult = "splitWBust";
								gameResultOfB = "splitWBust";
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
					if(splitting != null) {
						message = "この手札でスタンドしています";
						if(i == 0) {
							if(gameResult != null) {
								message = "21点を超えてバーストしているため、あなたの負けです";
								request.setAttribute("situationMessage", message);
								message = "この手札はバーストしています";
							}
							session.setAttribute("actionAisEnd", message);
							actionAisEnd = true;
						}else {
							if(gameResultOfB != null) {
								message = "21点を超えてバーストしているため、あなたの負けです";
								request.setAttribute("situationMessageOfB", message);
								message = "この手札はバーストしています";
							}
							session.setAttribute("actionBisEnd", message);
							actionBisEnd = true;
						}
					}
					//通常プレイ時、またはスプリットした手札が両方アクション不可の時はゲーム終了
					if(splitting == null || (actionAisEnd && actionBisEnd)) {
						dealer.hit(deck);
						request.setAttribute("countHit", dealer.countHit());
						do {
							if(splitting != null && gameResult != null && playerInGame.getPoint() > 21) {
								message = "21点を超えてバーストしているため、あなたの負けです";
								request.setAttribute("situationMessage", message);
							}else if(splitting != null && gameResultOfB != null && playerInGame.getPoint() > 21){
								message = "21点を超えてバーストしているため、あなたの負けです";
								request.setAttribute("situationMessageOfB", message);
							}else if(dealer.confirmBust()) {
								message = "21点を超えたので、ディーラーはバーストしました！";
								request.setAttribute("bustedDealer", message);
								//スプリットした両方の手札をスタンドしていた場合
								if(splitting != null && gameResult == null && gameResultOfB == null) {	
									gameResult = "splitWStand";
									gameResultOfB = "splitWStand";
									splitWStand = true;
									request.setAttribute("splitWStand", splitWStand);
								//スプリットした二つ目の手札をスタンドしていた場合
								}else if(splitting != null && gameResultOfB == null) {
									gameResultOfB = "winOfB";
									message = "スタンドしていたため、あなたの勝利です！";
									request.setAttribute("situationMessageOfB", message);
								//スプリットした一つ目の手札、もしくは、通常プレイでスタンドしていた場合
								}else if(splitting != null && gameResult == null){
									gameResult = "win";
									message = "スタンドしていたため、あなたの勝利です！";
									request.setAttribute("situationMessage", message);
								}else {
									gameResult = "win";
									gameEnd = true;
								}
							}else {
								if(dealer.getPoint() < playerInGame.getPoint()) {
									message = "あなたの方が21点に近いため、あなたの勝利です！";
									if(splitting != null && gameResultOfB == null) {
										request.setAttribute("situationMessageOfB", message);
										gameResultOfB = "winOfB";
									}else {
										request.setAttribute("situationMessage", message);
										gameResult = "win";		
										gameEnd = true;
									}
								}else if(dealer.getPoint() == playerInGame.getPoint()) {
									message = "合計点数が同じなので、このゲームは引き分けです";
									if(splitting != null && gameResultOfB == null) {
										request.setAttribute("situationMessageOfB", message);
										gameResultOfB = "drawOfB";
									}else {
										request.setAttribute("situationMessage", message);
										gameResult = "draw";
										gameEnd = true;
									}
								}else {
									message = "ディーラーの方が21点に近いため、ディーラーの勝利です";
									if(splitting != null && gameResultOfB == null) {
										request.setAttribute("situationMessageOfB", message);
										gameResultOfB = "loseOfB";
									}else {
										request.setAttribute("situationMessage", message);
										gameResult = "lose";
										gameEnd = true;
									}
								}
							}
							//スプリット時で、片方のゲーム結果がまだ登録されていない場合
							//参照ハンドを変更して、もう一度勝敗判別を行う
							//通常プレイ時、架空のスプリット手札に結果を登録
							if(splitting == null) {
								gameResultOfB = "notSplitting";
							}else if(splitting != null && gameResult == null) {
								playerInGame = splitA;
							}else if(splitting != null && gameResultOfB == null) {
								playerInGame = splitB;
							}
						//ゲーム結果が両方とも登録されていればDBへの登録へ
						}while(gameResult == null || gameResultOfB == null);			
					}
				}
				if((splitting == null && gameEnd) || (actionAisEnd && actionBisEnd)) {
					//スプリットしている場合、DBへの勝敗登録を2回行う
					if(splitting != null) {
						i = 0;
					}
					do {
						String result;
						if(splitting != null && splitting && i == 1) {
							result = gameResultOfB;
						}else {
							result = gameResult;
						}
						switch(result) {
						case "win":
							Result.win(request, (chip * 2));
							playerDao.cashBack(player.getId(), (chip * 2));
							playerDao.updateRecord(player, "win");
							break;
						case "draw":
							Result.draw(request, chip);
							playerDao.cashBack(player.getId(), chip);
							playerDao.updateRecord(player, "draw");
							break;
						case "lose":
							Result.lose(request, chip);
							playerDao.updateRecord(player, "lose");
							break;
						case "winOfB":
							Result.winOfB(request, (chip * 2));
							playerDao.cashBack(player.getId(), (chip * 2));
							playerDao.updateRecord(player, "win");
							break;
						case "drawOfB":
							Result.drawOfB(request, chip);
							playerDao.cashBack(player.getId(), chip);
							playerDao.updateRecord(player, "draw");
							break;
						case "loseOfB":
							Result.loseOfB(request, chip);
							playerDao.updateRecord(player, "lose");
							break;
						case "splitWBust":
							Result.lose(request, (chip * 2));
							playerDao.updateRecord(player, "lose");
							playerDao.updateRecord(player, "lose");
							nextPage = "result.jsp";
							//2回分の登録が済んでいるため、ループを抜ける
							if(i == 0) {
								i++;
							}
							break;
						case "splitWStand":
							Result.win(request, (chip * 4));
							playerDao.cashBack(player.getId(), (chip * 4));
							playerDao.updateRecord(player, "win");
							playerDao.updateRecord(player, "win");
							nextPage = "result.jsp";
							//2回分の登録が済んでいるため、ループを抜ける
							if(i == 0) {
								i++;
							}
							break;
						default:
							break;
						}
						//繰り返し用にiを加算
						i++;
					}while(i < 2);
					nextPage = "result.jsp";
				}else if(splitting != null && splitting && pairOfA != null && pairOfA) {
					nextPage = "GameServlet";
				}else {
					nextPage = "in-game.jsp";
				}
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
