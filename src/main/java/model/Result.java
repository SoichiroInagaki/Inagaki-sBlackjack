package model;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

//結果の定型文をリクエストスコープにセットするstaticメソッド群
public class Result {
	
	//勝敗メッセージをリクエストスコープに保持させるメソッド
	private static void setResult
		(HttpServletRequest request, Map <String, String> map) {
		
		request.setAttribute("resultMessage", map.get("resultMessage"));
		request.setAttribute("chipMessage", map.get("chipMessage"));
		
	}
	
	//勝った時の定型文をセットするメソッド
	public static void win(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessage", "You Win!!");
		map.put("chipMessage", "配当として、" + chip + "枚のチップを獲得しました！");
		setResult(request, map);
	}
		
	//引き分けの時の定型文をセットするメソッド
	public static void draw(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessage", "Draw game!");
		map.put("chipMessage", "引き分けなので、賭けていた" + chip + "枚のチップが手元に戻りました");
		setResult(request, map);
	}
		
	//負けた時の定型文をセットするメソッド
	public static void lose(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessage", "You lose......");
		map.put("chipMessage", "賭けていたチップ" + chip + "枚が没収されました");
		setResult(request, map);
	}
	
	//スプリットした二つ目の手札に関する勝敗情報を保持させるメソッド
	private static void setResultOfB
		(HttpServletRequest request, Map<String, String> map){
		
		request.setAttribute("resultMessageOfB", map.get("resultMessageOfB"));
		request.setAttribute("chipMessageOfB", map.get("chipMessageOfB"));
	}
	
	/* スプリットした二つ目の手札について、
	 * 勝った時の定型文をセットするメソッド*/
		public static void winOfB(HttpServletRequest request, int chip) {
			
			Map<String, String> map = new HashMap<>();
			map.put("resultMessageOfB", "You Win!!");
			map.put("chipMessageOfB", "配当として、" + chip + "枚のチップを獲得しました！");
			setResultOfB(request, map);
		}
		
	/* スプリットした二つ目の手札について、
	 * 引き分けの時の定型文をセットするメソッド*/
	public static void drawOfB(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessageOfB", "Draw game!");
		map.put("chipMessageOfB", "引き分けなので、賭けていた" + chip + "枚のチップが手元に戻りました");
		setResultOfB(request, map);
	}
		
	/* スプリットした二つ目の手札について、
	 * 負けた時の定型文をセットするメソッド */
	public static void loseOfB(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessageOfB", "You lose......");
		map.put("chipMessageOfB", "賭けていたチップ" + chip + "枚が没収されました");
		setResultOfB(request, map);
	}
	
	/* ナチュラルブラックジャックに関するメッセージを
	 * リクエストスコープに保持させるメソッド*/
	private static void setResultOfBlackjack
		(HttpServletRequest request, Map <String, String> map) {
		
		request.setAttribute("blackjackMessageForPlayer", map.get("blackjackMessageForPlayer"));
		request.setAttribute("blackjackMessageForDealer", map.get("blackjackMessageForDealer"));
	}
	
	//ナチュラルブラックジャックで勝った時の定型文をセットするメソッド
	public static void winOfBlackjack
		(HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<>();
		map.put("blackjackMessageForPlayer", "BLACKJACK!!");
		map.put("blackjackMessageForDealer", "ディーラーの手札はブラックジャックではなかったので、あなたの勝ちです！");
		setResultOfBlackjack(request, map);
	}
			
	//ナチュラルブラックジャックで引き分けた時の定型文をセットするメソッド
	public static void drawOfBlackjack
		(HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<>();
		map.put("blackjackMessageForPlayer", "BLACKJACK!!");
		map.put("blackjackMessageForDealer", "ディーラーの手札もブラックジャックでした！このゲームは引き分けです");
		setResultOfBlackjack(request, map);
	}
			
	//ナチュラルブラックジャックで負けた時の定型文をセットするメソッド
	public static void loseOfBlackjack
		(HttpServletRequest request) {
		
		/*ディーラー側のメッセージのみのため、
		 * 直接リクエストスコープにメッセージを保持させる */
		request.setAttribute
			("blackjackMessageForDealer", "ディーラーの手札はブラックジャックでした。お気の毒ですが、あなたの負けです");
	}
	
	//勝敗判定を行うメソッド
	public static void decideWinner
		(Dealer dealer, PlayerInGame playerInGame, HttpServletRequest request) {
		
		String message = null;
		
		//プレイヤーの手札がバーストしている場合
		if(playerInGame.checkBust()) {
			playerInGame.setResult("lose");
			message = "21点を超えてバーストしているため、あなたの負けです";
			if(playerInGame.isSplitA()) {
				request.setAttribute("situationMessage", message);
			}else if(playerInGame.isSplitB()){
				request.setAttribute("situationMessageOfB", message);
			}else{
				message = "21点を超えたのでバーストしてしまいました。あなたの負けです";
				request.setAttribute("bustingPlayer", message);
			}
		//ディーラーの手札がバーストしている場合
		}else if(!playerInGame.checkBust() && dealer.checkBust()) {
			playerInGame.setResult("win");
			message = "スタンドしていたため、あなたの勝利です！";
			if(playerInGame.isSplitB()) {
				request.setAttribute("situationMessageOfB", message);
			}else {
				request.setAttribute("situationMessage", message);
			}
		//それ以外の場合は、手札の点数で勝敗判定
		//勝った場合
		}else if(!playerInGame.checkBust() && dealer.getPoint() < playerInGame.getPoint()) {
			playerInGame.setResult("win");
			message = "あなたの方が21点に近いため、あなたの勝利です！";
			if(playerInGame.isSplitB()) {
				request.setAttribute("situationMessageOfB", message);
			}else {
				request.setAttribute("situationMessage", message);
			}
		//引き分けの場合
		}else if(dealer.getPoint() == playerInGame.getPoint()) {
			playerInGame.setResult("draw");
			message = "合計点数が同じなので、このゲームは引き分けです";
			if(playerInGame.isSplitB()) {
				request.setAttribute("situationMessageOfB", message);
			}else {
				request.setAttribute("situationMessage", message);
			}
		//負けた場合
		}else if(dealer.getPoint() > playerInGame.getPoint()){
			playerInGame.setResult("lose");
			message = "ディーラーの方が21点に近いため、ディーラーの勝利です";
			if(playerInGame.isSplitB()) {
				request.setAttribute("situationMessageOfB", message);
			}else {
				request.setAttribute("situationMessage", message);
			}
		}
	}
	
	//ディーラーのヒット処理を行うメソッド
	public static void actionOfDealer
		(Dealer dealer, Deck deck, HttpServletRequest request) {
		dealer.hit(deck);
		request.setAttribute("countHit", dealer.countHit());
		if(dealer.checkBust()){
			String message = "21点を超えたので、ディーラーはバーストしました！";
			request.setAttribute("bustingDealer", message);
		}
	}
	
	//二組の手札がバーストした際のメッセージをセットするメソッド
	public static void splitWBust
		(HttpServletRequest request, int chip) {
		String message = "両方の手札がバーストしてしまいました。あなたの負けです";
		request.setAttribute("bustingPlayer", message);
		Result.lose(request, (chip * 2));	
	}
	
	//二組の手札がディーラーのバーストで勝った際のリザルトメソッド
	public static void splitWStand(HttpServletRequest request, int chip) {
		Result.win(request, (chip * 4));
	}
}
