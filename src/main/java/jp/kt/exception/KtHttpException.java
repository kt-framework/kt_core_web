package jp.kt.exception;

/**
 * HTTPレスポンスコードを指定したエラーをあらわすException.
 *
 * @author tatsuya.kumon
 */
public final class KtHttpException extends RuntimeException {
	/** HTTPレスポンスコード */
	private int httpResponseCode;

	/**
	 * コンストラクタ.
	 *
	 * @param httpResponseCode
	 *            HTTPレスポンスコード.<br>
	 *            HttpServletResponseクラスの定数で指定すること.
	 */
	public KtHttpException(int httpResponseCode) {
		super("HTTPレスポンスコード:" + httpResponseCode);
		this.httpResponseCode = httpResponseCode;
	}

	/**
	 * HTTPレスポンスコードを取得.
	 *
	 * @return HTTPレスポンスコード
	 */
	public int getHttpResponseCode() {
		return httpResponseCode;
	}
}
