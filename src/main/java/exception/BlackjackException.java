package exception;

//アプリ内で発生した例外の概要を表す例外
public class BlackjackException extends Exception{

	//インスタンス化すると例外概要を表す例外ができる
	public BlackjackException(String message) {
		super(message);
	}
	
	
}
