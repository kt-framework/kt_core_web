package jp.kt.web.page;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import jp.kt.tool.UrlCreator;

/**
 * 画面遷移ページ（forward、redirect）を定義する抽象クラス.
 *
 * @author tatsuya.kumon
 */
abstract class MovePage implements Page {
	private UrlCreator urlCreator;

	/**
	 * コンストラクタ.
	 *
	 * @param url
	 *            URL
	 * @throws URISyntaxException
	 *             URIの書式が誤っている場合
	 */
	protected MovePage(String url) throws URISyntaxException {
		this.urlCreator = new UrlCreator(url);
	}

	/**
	 * パラメータの追加.
	 *
	 * @param name
	 *            パラメータ名
	 * @param value
	 *            パラメータ値
	 */
	public void addParam(String name, String value) {
		urlCreator.addParam(name, value);
	}

	/**
	 * 遷移するページのURLを返す.
	 *
	 * @return 遷移するページのURL
	 * @throws UnsupportedEncodingException
	 *             指定されたエンコーディングがサポートされていない場合
	 */
	public String getUrl() throws UnsupportedEncodingException {
		return urlCreator.create();
	}

	/**
	 * フラグメント（ページ内リンク）をセットする.
	 *
	 * @param fragment
	 *            フラグメント（ページ内リンク）の名前
	 */
	public void setFragment(String fragment) {
		urlCreator.setFragment(fragment);
	}
}
