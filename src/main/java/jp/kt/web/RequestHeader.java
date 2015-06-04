package jp.kt.web;

import javax.servlet.http.HttpServletRequest;

import jp.kt.tool.Validator;

/**
 * HTTPリクエストヘッダ情報を取得するクラス.
 * 
 * @author tatsuya.kumon
 */
public class RequestHeader {
	private RequestHeader() {
	}

	/**
	 * ユーザエージェントを取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return ユーザエージェント
	 */
	public static String getUserAgent(HttpServletRequest req) {
		return req.getHeader("User-Agent");
	}

	/**
	 * リファラーの取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return リファラー
	 */
	public static String getReferer(HttpServletRequest req) {
		return req.getHeader("Referer");
	}

	/**
	 * アクセスしてきたメソッドを取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return アクセスしてきたメソッド
	 */
	public static String getMethod(HttpServletRequest req) {
		return req.getMethod();
	}

	/**
	 * アクセスしてきたスキーム（プロトコル）を取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return アクセスしてきたスキーム（プロトコル）
	 */
	public static String getScheme(HttpServletRequest req) {
		String scheme = req.getHeader("X-Forwarded-Scheme");
		if (Validator.isEmpty(scheme)) {
			scheme = req.getScheme();
		}
		return scheme;
	}

	/**
	 * アクセスしてきたホスト名（ポートがあればポート付き）を取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return アクセスしてきたサーバ名（ホスト）
	 */
	public static String getHost(HttpServletRequest req) {
		return req.getHeader("Host");
	}

	/**
	 * アクセスしてきたURI（クエリストリング付き）を取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return アクセスしてきたURI（クエリストリング付き）
	 */
	public static String getUri(HttpServletRequest req) {
		StringBuilder url = new StringBuilder();
		// URI
		String uri = req.getRequestURI();
		if (!Validator.isEmpty(uri)) {
			url.append(uri);
		}
		// クエリストリング
		String queryString = req.getQueryString();
		if (!Validator.isEmpty(queryString)) {
			url.append("?");
			url.append(queryString);
		}
		return url.toString();
	}

	/**
	 * auの端末固有番号の取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return auの端末固有番号
	 */
	public static String getAuUid(HttpServletRequest req) {
		return req.getHeader("X-Up-Subno");
	}

	/**
	 * softbankの端末固有番号の取得.
	 * 
	 * @param req
	 *            {@link HttpServletRequest} オブジェクト
	 * @return softbankの端末固有番号
	 */
	public static String getSoftbankUid(HttpServletRequest req) {
		return req.getHeader("x-jphone-uid");
	}
}
