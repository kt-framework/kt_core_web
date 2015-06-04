package jp.kt.web.page;

import java.net.URISyntaxException;

/**
 * forward用のPage実装クラス.
 *
 * @author tatsuya.kumon
 */
public class ForwardPage extends MovePage {
	/**
	 * コンストラクタ.
	 *
	 * @param pageUrl
	 *            ForwardするURL
	 * @throws URISyntaxException
	 *             URIの書式が誤っている場合
	 */
	public ForwardPage(String pageUrl) throws URISyntaxException {
		super(pageUrl);
	}
}
