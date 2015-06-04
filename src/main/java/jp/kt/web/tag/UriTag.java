package jp.kt.web.tag;

import javax.servlet.http.HttpServletRequest;

/**
 * APサーバへのURIを記述するためのタグクラス.
 *
 * @author tatsuya.kumon
 */
public final class UriTag extends BaseTag {
	private String value;

	/**
	 * タグ作成.
	 */
	protected String createTag() {
		// 半角スラッシュから始まっていない場合はそのまま返す
		boolean startsWithSlash = value.startsWith("/");
		// URLエンコード
		StringBuilder outUri = new StringBuilder();
		if (startsWithSlash) {
			// 半角スラッシュから始まっている場合、コンテキストパスを付加
			HttpServletRequest req = (HttpServletRequest) pageContext
					.getRequest();
			String contextPath = req.getContextPath();
			outUri.append(contextPath);
		}
		outUri.append(value);
		// URIを出力する
		return outUri.toString();
	}

	/**
	 * uri属性値のセット.
	 *
	 * @param value
	 *            値
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
