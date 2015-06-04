package jp.kt.web.page;

import java.net.URISyntaxException;

/**
 * redirect用のPage実装クラス.
 *
 * @author tatsuya.kumon
 */
public class RedirectPage extends MovePage {
	/**
	 * コンストラクタ.
	 *
	 * @param pageUrl
	 *            RedirectするURL
	 * @throws URISyntaxException
	 *             URIの書式が誤っている場合
	 */
	public RedirectPage(String pageUrl) throws URISyntaxException {
		super(pageUrl);
	}
}
