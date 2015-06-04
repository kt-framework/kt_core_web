package jp.kt.web.page;

/**
 * HTTPコードを直接レスポンスする場合のPage.
 *
 * @author tatsuya.kumon
 */
public final class HttpCodePage implements Page {
	/** HTTPレスポンスコード */
	private int httpResponseCode;

	/**
	 * コンストラクタ.
	 *
	 * @param httpResponseCode
	 *            HTTPレスポンスコード.<br>
	 *            HttpServletResponseクラスの定数で指定すること.
	 */
	public HttpCodePage(int httpResponseCode) {
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
