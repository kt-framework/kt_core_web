package jp.kt.web.tag;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dateオブジェクトをフォーマットして表示するためのタグクラス.
 *
 * @author tatsuya.kumon
 */
public final class FormatDateTag extends BaseTag {
	/** Date値 */
	private Date value;

	/** フォーマットパターン */
	private String pattern;

	private static final String EMPTY = "";

	/**
	 * タグ作成.
	 */
	protected String createTag() {

		if (value == null) {
			return EMPTY;
		}

		// Dateオブジェクトをフォーマットする
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String dateText = format.format(value);
		// タグを返す
		return dateText;
	}

	/**
	 * フォーマットをセット.
	 *
	 * @param pattern
	 *            フォーマット
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Date値をセット.
	 *
	 * @param value
	 *            Data値
	 */
	public void setValue(Date value) {
		this.value = value;
	}
}
