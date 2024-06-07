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
		request.setAttribute("blackjackMessageForDealer", "ディーラーの手札はブラックジャックでした。お気の毒ですが、あなたの負けです");
	}
	
}
